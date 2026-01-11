package plus.yumeyuka.yumemow

import java.io.File

data class IcoData(
    val name: String,
    val path: String
)

fun scanIcoFiles(dirPath: String): List<IcoData> {
    val dir = File(dirPath)
    if (!dir.exists() || !dir.isDirectory) return emptyList()

    return dir.walkTopDown()
        .filter { it.isFile && it.extension.equals("ico", ignoreCase = true) }
        .map { file ->
            IcoData(
                name = file.nameWithoutExtension,
                path = file.absolutePath
            )
        }
        .toList()
}

fun getDefaultIcoPackDir(): String {
    return System.getProperty("user.home") + File.separator + ".yume${File.separator}IcoPacks${File.separator}ico"
}
