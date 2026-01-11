package plus.yumeyuka.yumemow

import java.io.File
import java.nio.file.Files

enum class FolderIconError(val code: Int) {
    Success(0),
    FolderNotFound(1),
    IconNotFound(2),
    InvalidPath(3),
    WriteFailed(4),
    NotSupported(5);

    companion object {
        fun fromCode(code: Int): FolderIconError {
            return entries.firstOrNull { it.code == code } ?: NotSupported
        }
    }
}

object FolderIconJni {
    private var loaded = false

    init {
        loadLibrary()
    }

    private fun loadLibrary() {
        if (loaded) return

        try {
            System.loadLibrary("yumemow_core")
            loaded = true
        } catch (_: UnsatisfiedLinkError) {
            loadFromResource()
        }
    }

    private fun loadFromResource() {
        val libName = when {
            System.getProperty("os.name").lowercase().contains("win") -> "yumemow_core.dll"
            System.getProperty("os.name").lowercase().contains("mac") -> "libyumemow_core.dylib"
            else -> "libyumemow_core.so"
        }

        val tempFile = File.createTempFile(
            "yumemow_core", when {
                System.getProperty("os.name").lowercase().contains("win") -> ".dll"
                System.getProperty("os.name").lowercase().contains("mac") -> ".dylib"
                else -> ".so"
            }
        ).apply {
            deleteOnExit()
        }

        javaClass.classLoader?.getResourceAsStream(libName)?.use { input ->
            Files.copy(input, tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING)
        } ?: throw RuntimeException("Native library not found in resources: $libName")

        System.load(tempFile.absolutePath)
        loaded = true
    }

    fun setFolderIcon(folderPath: String, iconPath: String, iconIndex: Int = 0): FolderIconError {
        val code = nativeSetFolderIcon(folderPath, iconPath, iconIndex)
        return FolderIconError.fromCode(code)
    }

    fun selectFolder(): String? = nativeSelectFolder()

    fun openFile(filter: String? = null): String? = nativeOpenFile(filter)

    private external fun nativeSetFolderIcon(folderPath: String, iconPath: String, iconIndex: Int): Int
    private external fun nativeSelectFolder(): String?
    private external fun nativeOpenFile(filter: String?): String?
}