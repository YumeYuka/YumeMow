package plus.yumeyuka.yumemow

import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

fun downloadToTemp(urlStr: String): Path {
    val url = URI.create(urlStr).toURL()
    val conn = url.openConnection() as HttpURLConnection
    conn.requestMethod = "GET"
    conn.connectTimeout = 15000
    conn.readTimeout = 15000
    conn.instanceFollowRedirects = true
    conn.connect()
    if (conn.responseCode !in 200..299) throw RuntimeException("HTTP ${conn.responseCode}")
    val tmp = Files.createTempFile("dl-", ".tmp")
    conn.inputStream.use { input ->
        Files.newOutputStream(tmp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use { out ->
            input.copyTo(out)
        }
    }
    conn.disconnect()
    return tmp
}

fun unzipSafely(zipPath: Path, destDir: Path) {
    val entryNames = mutableListOf<String>()
    ZipInputStream(Files.newInputStream(zipPath)).use { zis ->
        var entry: ZipEntry? = zis.nextEntry
        while (entry != null) {
            val name = entry.name.trimStart('/')
            if (name.isNotEmpty()) entryNames.add(name)
            zis.closeEntry()
            entry = zis.nextEntry
        }
    }
    val topLevelPrefix = entryNames
        .map { it.substringBefore("/") }
        .takeIf { it.isNotEmpty() && it.all { n -> n == it[0] && n.isNotEmpty() } }
        ?.get(0)
    val hasSingleTopDir = topLevelPrefix != null && entryNames.all { it.startsWith("$topLevelPrefix/") }

    ZipInputStream(Files.newInputStream(zipPath)).use { zis ->
        var entry: ZipEntry? = zis.nextEntry
        while (entry != null) {
            var outName = entry.name.trimStart('/')
            if (hasSingleTopDir && outName.startsWith("$topLevelPrefix/")) {
                outName = outName.removePrefix("$topLevelPrefix/")
            }
            if (outName.isNotEmpty()) {
                val resolved = destDir.resolve(outName).normalize()
                if (!resolved.startsWith(destDir)) {
                    throw SecurityException("Zip entry is outside target dir: ${entry.name}")
                }
                if (entry.isDirectory) {
                    Files.createDirectories(resolved)
                } else {
                    Files.createDirectories(resolved.parent)
                    Files.newOutputStream(resolved, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
                        .use { out -> zis.copyTo(out) }
                }
            }
            zis.closeEntry()
            entry = zis.nextEntry
        }
    }
}

fun downloadAndUnzip(
    url: String,
    destFolder: String = System.getProperty("user.home") + File.separator + ".yume/IcoPacks"
) {
    val dest = Path.of(destFolder)
    Files.createDirectories(dest)
    val tmp = downloadToTemp(url)
    try {
        val fileName = URI.create(url).path.substringAfterLast('/')
        val isZip = fileName.lowercase().endsWith(".zip") || tmp.toString().lowercase().endsWith(".zip")
        if (isZip) {
            unzipSafely(tmp, dest)
        } else {
            val target = dest.resolve(fileName)
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING)
            return
        }
    } finally {
        try {
            Files.deleteIfExists(tmp)
        } catch (_: Exception) {
        }
    }
}

fun redownloadIcoPack(url: String, destFolder: String) {
    val dest = Path.of(destFolder)
    if (Files.exists(dest)) {
        Files.walk(dest)
            .sorted(Comparator.reverseOrder())
            .forEach {
                try {
                    Files.deleteIfExists(it)
                } catch (_: Exception) {
                }
            }
    }
    downloadAndUnzip(url, destFolder)
}

fun isDirEmptyOrNotExist(path: String): Boolean {
    val dir = File(path)
    return !dir.exists() || !dir.isDirectory || dir.list()?.isEmpty() == true
}