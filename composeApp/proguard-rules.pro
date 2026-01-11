# Jewel UI macOS 窗口工具依赖 JNA，但仅在 macOS 上需要
# 在其他平台构建时忽略这些警告
-dontwarn org.jetbrains.jewel.window.utils.macos.**
-dontwarn org.jetbrains.jewel.window.utils.JnaLoader

# 保留 Compose Desktop 必要的类
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# 保留 Main 类和入口点
-keep class plus.yumeyuka.yumemow.MainKt { *; }
-keep class plus.yumeyuka.yumemow.FolderIconJni { *; }

# 保留 JNI native 方法
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留 Compose 相关
-keep class androidx.compose.** { *; }
-keep class org.jetbrains.compose.** { *; }
-keep class org.jetbrains.skia.** { *; }

# 保留 Miuix
-keep class top.yukonga.miuix.kmp.** { *; }

# 保留 Lifecycle
-keep class org.jetbrains.androidx.lifecycle.** { *; }
