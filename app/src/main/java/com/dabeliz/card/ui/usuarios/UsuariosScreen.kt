package com.dabeliz.card.ui.usuarios

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dabeliz.card.data.UsuariosRepositorio
import com.dabeliz.card.model.Rol
import com.dabeliz.card.model.Usuario
import com.dabeliz.card.ui.common.DabelizTopBar
import com.dabeliz.card.ui.common.FotoRemota
import kotlinx.coroutines.launch

/**
 * Cuadro de usuarios: toda cuenta que inicia sesión (en esta app o en la futura app
 * del trabajador) aparece aquí. El administrador le asigna el rol y puede desactivarla.
 */
@Composable
fun UsuariosScreen(
    usuarios: List<Usuario>?,
    uidActual: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var error by remember { mutableStateOf<String?>(null) }

    fun ejecutar(accion: suspend () -> Unit) {
        error = null
        scope.launch {
            try {
                accion()
            } catch (e: Exception) {
                error = "No se pudo actualizar: ${e.message}"
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { DabelizTopBar(title = "Usuarios y roles", onBack = onBack) }
    ) { innerPadding ->
        when {
            usuarios == null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = "Las cuentas nuevas entran como Trabajador. Solo los Administradores pueden usar esta app.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth()
                    )
                    error?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .widthIn(max = 720.dp)
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }
                }
                if (usuarios.isEmpty()) {
                    item { Text("Todavía no hay usuarios registrados.") }
                }
                items(usuarios, key = { it.uid }) { usuario ->
                    val esYo = usuario.uid == uidActual
                    UsuarioItem(
                        usuario = usuario,
                        esYo = esYo,
                        onCambiarRol = { rol -> ejecutar { UsuariosRepositorio.cambiarRol(usuario.uid, rol) } },
                        onCambiarActivo = { activo -> ejecutar { UsuariosRepositorio.cambiarActivo(usuario.uid, activo) } }
                    )
                }
            }
        }
    }
}

@Composable
private fun UsuarioItem(
    usuario: Usuario,
    esYo: Boolean,
    onCambiarRol: (Rol) -> Unit,
    onCambiarActivo: (Boolean) -> Unit
) {
    var menuRolAbierto by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .widthIn(max = 720.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (usuario.fotoUrl != null) {
                FotoRemota(
                    imagen = usuario.fotoUrl,
                    contentDescription = usuario.nombre,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(44.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = usuario.nombre.ifBlank { usuario.email } + if (esYo) " (tú)" else "",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = usuario.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box {
                    AssistChip(
                        onClick = { menuRolAbierto = true },
                        enabled = !esYo,
                        label = { Text(usuario.rol.etiqueta) },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    DropdownMenu(expanded = menuRolAbierto, onDismissRequest = { menuRolAbierto = false }) {
                        Rol.entries.forEach { rol ->
                            DropdownMenuItem(
                                text = { Text(rol.etiqueta) },
                                onClick = {
                                    menuRolAbierto = false
                                    if (rol != usuario.rol) onCambiarRol(rol)
                                }
                            )
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Uno mismo no puede quitarse el rol ni desactivarse, para no quedarse fuera de la app.
                Switch(
                    checked = usuario.activo,
                    onCheckedChange = onCambiarActivo,
                    enabled = !esYo
                )
                Text(
                    text = if (usuario.activo) "Activo" else "Inactivo",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
