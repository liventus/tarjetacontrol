import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
import java.util.Properties

plugins {
    id("com.google.gms.google-services")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.firebase.appdistribution)

}

// El número de versión vive en version.properties (no en este archivo) para
// poder incrementarlo automáticamente cada vez que se despliega a Firebase
// App Distribution, sin tener que tocar el build.gradle a mano.
val versionPropsFile = file("version.properties")
val versionProps = Properties().apply {
    if (versionPropsFile.exists()) {
        versionPropsFile.inputStream().use { load(it) }
    } else {
        setProperty("VERSION_CODE", "1")
        setProperty("VERSION_NAME", "1.0")
    }
}
val appVersionCode = versionProps.getProperty("VERSION_CODE").toInt()
val appVersionName = versionProps.getProperty("VERSION_NAME")

android {
    namespace = "com.dabeliz.card"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.dabeliz.card"
        minSdk = 24
        targetSdk = 36
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // AGP 9.x no deja el APK siempre en el mismo lugar (a veces en
            // intermediates/, a veces en outputs/), así que apuntamos a una
            // ubicación fija que nosotros mismos preparamos (ver prepareApkForDistribution).
            firebaseAppDistribution {
                artifactType = "APK"
                artifactPath = layout.buildDirectory.file("distribution/app-debug.apk").get().asFile.path
            }
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            firebaseAppDistribution {
                artifactType = "APK"
                artifactPath = layout.buildDirectory.file("distribution/app-release.apk").get().asFile.path
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    //noinspection WrongGradleMethod
    firebaseAppDistributionDefault {
        releaseNotes = "Primera versión de prueba"
        groups = "testers"
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(platform("com.google.firebase:firebase-bom:34.19.0"))
    implementation("com.google.firebase:firebase-analytics")
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.4")

}

// El plugin de Firebase App Distribution ya no encadena automáticamente la
// tarea de ensamblado con AGP 9.x, y AGP 9.x tampoco deja el APK en una
// ubicación fija. Copiamos el APK real (donde sea que haya quedado) a una
// ruta conocida antes de subirlo.
fun registerPrepareApk(variant: String, apkFileName: String, sourceCandidates: List<String>) {
    val prepareTask = tasks.register("prepareApkForDistribution${variant.replaceFirstChar { it.uppercase() }}") {
        dependsOn("assemble${variant.replaceFirstChar { it.uppercase() }}")
        doLast {
            val source = sourceCandidates
                .map { layout.buildDirectory.file(it).get().asFile }
                .firstOrNull { it.exists() }
                ?: throw GradleException(
                    "No se encontró el APK de $variant. Rutas revisadas: $sourceCandidates"
                )
            val dest = layout.buildDirectory.file("distribution/$apkFileName").get().asFile
            dest.parentFile.mkdirs()
            source.copyTo(dest, overwrite = true)
        }
    }
    tasks.findByName("appDistributionUpload${variant.replaceFirstChar { it.uppercase() }}")
        ?.dependsOn(prepareTask)
}

afterEvaluate {
    registerPrepareApk(
        variant = "debug",
        apkFileName = "app-debug.apk",
        sourceCandidates = listOf(
            "intermediates/apk/debug/app-debug.apk",
            "outputs/apk/debug/app-debug.apk"
        )
    )
    registerPrepareApk(
        variant = "release",
        apkFileName = "app-release.apk",
        sourceCandidates = listOf(
            "intermediates/apk/release/app-release-unsigned.apk",
            "intermediates/apk/release/app-release.apk",
            "outputs/apk/release/app-release-unsigned.apk",
            "outputs/apk/release/app-release.apk"
        )
    )
}

// Cada vez que se sube una build a Firebase App Distribution, el número de
// build (versionCode) sube en 1 automáticamente para la SIGUIENTE build. El
// versionName (1.0, 1.1, ...) se sigue controlando a mano en version.properties
// cuando quieras marcar un cambio mayor.
tasks.matching { it.name.startsWith("appDistributionUpload") }.configureEach {
    doLast {
        val props = Properties()
        versionPropsFile.inputStream().use { props.load(it) }
        val siguiente = props.getProperty("VERSION_CODE").toInt() + 1
        props.setProperty("VERSION_CODE", siguiente.toString())
        versionPropsFile.outputStream().use { props.store(it, "Auto-incrementado al desplegar") }
    }
}