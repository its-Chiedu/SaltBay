import com.chiedu.its.saltbay.exception.DownloadFailedException
import com.chiedu.its.saltbay.exception.InstallFailedException
import com.chiedu.its.saltbay.exception.InvalidOSException
import com.chiedu.its.saltbay.util.Downloader
import com.chiedu.its.saltbay.util.InstallerFactoryImpl
import java.io.File
import java.lang.Exception
import kotlin.io.path.createTempDirectory
import kotlin.system.exitProcess

fun main() {
    getJBToolbox()
}

fun getJBToolbox() {
    val baseUrl = "https://www.jetbrains.com/toolbox-app/download/download-thanks.html?platform="

    val platformAppAndUrl = platformVal(
        os = System.getProperty("os.name"),
        arch = System.getProperty("os.arch")
    )

    downloadAndInstall(
        downloadUrl = baseUrl + platformAppAndUrl.second,
        filename = platformAppAndUrl.first
    )
}

private fun platformVal(os: String, arch: String): Pair<String, String> =
    when {
    os.contains("Mac") -> "Jetbrains Toolbox.app" to arch.macOs()
    os.contains("Windows") -> "Jetbrains_Toolbox.exe" to "windows"
    os.contains("linux") -> "Jetbrains_Toolbox.tar.gz" to "linux"
    else -> throw InvalidOSException("Invalid Operating System: $os")

}

private fun String.macOs() = if (contains("arm") || contains("aarch64")) "macM1" else "intel"

private fun download(filename: String, downloadUrl: String): File {
    val tempDir = createTempDirectory().toFile()
    val result = Downloader.download(downloadUrl, File(tempDir, filename))
    if (result == 0) throw DownloadFailedException("0 bytes")
    return tempDir
}

private fun install(os: String, downloadDir: File, filename: String): Int {
    val installDir = downloadDir.absolutePath
    println("Installing to $installDir")
    try {
        InstallerFactoryImpl(os)(installDir, filename)
    } catch (exception: Exception) {
        throw InstallFailedException(message = "Installation Failed because: ${exception.message} \n Caused by: ${exception.cause}")
    }
    println("Installation completed. Installed to: $installDir")
    downloadDir.deleteOnExit()
    return 0
}

fun downloadAndInstall(downloadUrl: String, filename: String) {
    when (install(
        os = System.getProperty("os.name"),
        downloadDir = download(filename, downloadUrl),
        filename = filename
    )) {
        0 -> println("Install Successful")
        else -> {
            println("Install Failed")
            exitProcess(-1)
        }
    }
}

