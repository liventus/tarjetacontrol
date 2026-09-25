package com.dabeliz.card.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.GetCredentialUnsupportedException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.dabeliz.card.R
import com.dabeliz.card.data.UsuariosRepositorio
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/** Resultado de un intento de inicio de sesión con Google. */
sealed interface ResultadoLogin {
    data class Exito(val usuario: FirebaseUser) : ResultadoLogin
    data object Cancelado : ResultadoLogin
    /** El dispositivo no soporta Credential Manager: la pantalla debe lanzar [AutenticacionGoogle.intentFlujoClasico]. */
    data object UsarFlujoClasico : ResultadoLogin
    data class Error(val mensaje: String) : ResultadoLogin
}

/**
 * Inicio de sesión real con Google usando Credential Manager + Firebase Authentication.
 * Firebase guarda la sesión, así que al reabrir la app el usuario sigue logueado
 * hasta que presione "Cerrar sesión".
 */
object AutenticacionGoogle {

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

    val usuarioActual: FirebaseUser? get() = auth.currentUser

    /** Emite el usuario actual cada vez que cambia la sesión (null = sin sesión). */
    fun escucharSesion(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun iniciarSesion(context: Context): ResultadoLogin {
        val webClientId = obtenerWebClientId(context)
            ?: return ResultadoLogin.Error(
                "Falta el Web client ID de Google: no viene en google-services.json " +
                    "ni en strings.xml (google_web_client_id)."
            )

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(GetSignInWithGoogleOption.Builder(webClientId).build())
            .build()

        return try {
            val respuesta = CredentialManager.create(context).getCredential(context, request)
            val credencial = respuesta.credential
            if (credencial !is CustomCredential ||
                credencial.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                return ResultadoLogin.Error("Tipo de credencial no reconocido.")
            }
            entrarConIdToken(context, GoogleIdTokenCredential.createFrom(credencial.data).idToken)
        } catch (e: GetCredentialCancellationException) {
            ResultadoLogin.Cancelado
        } catch (e: GetCredentialProviderConfigurationException) {
            // Google Play Services antiguo (o emulador desactualizado): Credential Manager no
            // tiene proveedor, así que se usa el login clásico de Google Sign-In.
            ResultadoLogin.UsarFlujoClasico
        } catch (e: GetCredentialUnsupportedException) {
            ResultadoLogin.UsarFlujoClasico
        } catch (e: NoCredentialException) {
            ResultadoLogin.Error("No hay cuentas de Google disponibles en este dispositivo.")
        } catch (e: GetCredentialException) {
            ResultadoLogin.Error(e.message ?: "No se pudo iniciar sesión con Google.")
        } catch (e: Exception) {
            ResultadoLogin.Error(e.message ?: "Error al iniciar sesión.")
        }
    }

    /** Intent del selector de cuentas clásico, para dispositivos sin soporte de Credential Manager. */
    fun intentFlujoClasico(context: Context): Intent? {
        val webClientId = obtenerWebClientId(context) ?: return null
        return clienteClasico(context, webClientId).signInIntent
    }

    /** Procesa el resultado del selector clásico (lo que devuelve [intentFlujoClasico]). */
    suspend fun completarFlujoClasico(context: Context, data: Intent?): ResultadoLogin = try {
        val idToken = GoogleSignIn.getSignedInAccountFromIntent(data).await().idToken
        if (idToken == null) {
            ResultadoLogin.Error("Google no devolvió el token de la cuenta.")
        } else {
            entrarConIdToken(context, idToken)
        }
    } catch (e: ApiException) {
        if (e.statusCode == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
            ResultadoLogin.Cancelado
        } else {
            ResultadoLogin.Error("No se pudo iniciar sesión con Google (código ${e.statusCode}).")
        }
    } catch (e: Exception) {
        ResultadoLogin.Error(e.message ?: "Error al iniciar sesión.")
    }

    /** Paso común a ambos flujos: entra a Firebase con el token de Google y valida el rol. */
    private suspend fun entrarConIdToken(context: Context, idToken: String): ResultadoLogin {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        val usuario = auth.signInWithCredential(firebaseCredential).await().user
            ?: return ResultadoLogin.Error("No se pudo obtener el usuario de Firebase.")
        val perfil = try {
            UsuariosRepositorio.registrarAcceso(usuario)
        } catch (e: Exception) {
            cerrarSesion(context)
            return ResultadoLogin.Error("No se pudo leer tu perfil en Firestore: ${e.message}")
        }
        if (!perfil.puedeUsarAppAdmin) {
            cerrarSesion(context)
            return ResultadoLogin.Error(
                if (!perfil.activo) {
                    "La cuenta ${perfil.email} está desactivada."
                } else {
                    "La cuenta ${perfil.email} no tiene rol de administrador. " +
                        "Pide a un administrador que te lo asigne."
                }
            )
        }
        return ResultadoLogin.Exito(usuario)
    }

    suspend fun cerrarSesion(context: Context) {
        auth.signOut()
        try {
            CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
        } catch (_: Exception) {
            // Si falla, la sesión de Firebase ya está cerrada; solo no se limpia la cuenta sugerida.
        }
        obtenerWebClientId(context)?.let { webClientId ->
            // Para que el selector clásico vuelva a preguntar la cuenta la próxima vez.
            runCatching { clienteClasico(context, webClientId).signOut().await() }
        }
    }

    private fun clienteClasico(context: Context, webClientId: String) = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
    )

    // El plugin google-services genera default_web_client_id solo cuando el
    // google-services.json trae el cliente OAuth web; se busca por nombre para
    // que la app compile aunque no esté. Si falta, se usa el que se haya puesto
    // a mano en strings.xml (google_web_client_id).
    @SuppressLint("DiscouragedApi")
    private fun obtenerWebClientId(context: Context): String? {
        val id = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        if (id != 0) return context.getString(id)
        return context.getString(R.string.google_web_client_id).takeIf { it.isNotBlank() }
    }
}
