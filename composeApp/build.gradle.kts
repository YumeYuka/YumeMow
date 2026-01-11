import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose.hot-reload")
}

val nativeBuildDir = layout.buildDirectory.dir("native")
val coreDir = file("../core")

val buildNativeLib = tasks.register<Exec>("buildNativeLib") {
    group = "build"
    description = "Configure native library with CMake"

    val buildDir = nativeBuildDir.get().asFile
    workingDir = buildDir
    outputs.dir(buildDir)

    doFirst {
        buildDir.mkdirs()
    }

    when {
        OperatingSystem.current().isWindows -> {
            commandLine(
                "cmd", "/c", "cmake",
                "-G", "Ninja",
                "-DCMAKE_BUILD_TYPE=Release",
                "-DCMAKE_MSVC_RUNTIME_LIBRARY=MultiThreadedDLL",
                "-S", coreDir.absolutePath,
                "-B", buildDir.absolutePath
            )
        }
        else -> {
            commandLine(
                "cmake",
                "-DCMAKE_BUILD_TYPE=Release",
                "-S", coreDir.absolutePath,
                "-B", buildDir.absolutePath
            )
        }
    }
}

val buildNativeLibCompile = tasks.register<Exec>("buildNativeLibCompile") {
    group = "build"
    description = "Compile native library"
    dependsOn(buildNativeLib)

    val buildDir = nativeBuildDir.get().asFile
    workingDir = buildDir

    when {
        OperatingSystem.current().isWindows -> {
            commandLine("cmd", "/c", "cmake", "--build", ".", "--config", "Release", "--target", "FolderIconSetter")
        }
        else -> {
            commandLine("cmake", "--build", ".", "--config", "Release", "--target", "FolderIconSetter")
        }
    }
}

val copyNativeLib = tasks.register<Copy>("copyNativeLib") {
    group = "build"
    description = "Copy native library to resources"
    dependsOn(buildNativeLibCompile)

    from(nativeBuildDir) {
        include(
            when {
                OperatingSystem.current().isWindows -> "yumemow_core.dll"
                OperatingSystem.current().isLinux -> "libyumemow_core.so"
                OperatingSystem.current().isMacOsX -> "libyumemow_core.dylib"
                else -> ""
            }
        )
    }
    into(file("src/jvmMain/resources/"))
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation("top.yukonga.miuix.kmp:miuix-desktop:0.7.2")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.9.6")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.9.6")
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(compose.uiTooling)
            implementation("org.jetbrains.jewel:jewel-int-ui-standalone:0.32.1-253.28294.285")
            implementation("org.jetbrains.jewel:jewel-int-ui-decorated-window:0.32.1-253.28294.285")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")
            implementation("dev.chrisbanes.haze:haze-materials:1.7.1")
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

afterEvaluate {
    tasks.matching { it.name == "jvmProcessResources" }.configureEach {
        dependsOn(copyNativeLib)
    }

    tasks.withType<JavaExec>().matching { it.name == "jvmRun" }.configureEach {
        mainClass.set("plus.yumeyuka.yumemow.MainKt")
    }
}

compose.desktop {
    application {
        mainClass = "plus.yumeyuka.yumemow.MainKt"

        jvmArgs(
            "-Xms32m",
            "-Xmx256m",
            "-XX:+UseG1GC",
            "-XX:MaxGCPauseMillis=200",
            "-XX:+UseStringDeduplication",
            "-Xss256k"
        )

        buildTypes.release.proguard {
            isEnabled.set(false)
            configurationFiles.from("proguard-rules.pro")
        }

        nativeDistributions {
            includeAllModules = false
            modules(
                "java.base",
                "java.desktop",
                "java.logging"
            )
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

            packageName = "YumeMow"
            packageVersion = "1.0.0"
            description = "文件夹图标设置工具"
            copyright = "© 2025 YumeYuka"
            vendor = "YumeYuka"

            windows {
                menuGroup = "YumeMow"
                shortcut = true
                dirChooser = true
                perUserInstall = false
                 iconFile = project.file("src/jvmMain/composeResources/drawable/icon.ico")
            }
        }
    }
}
