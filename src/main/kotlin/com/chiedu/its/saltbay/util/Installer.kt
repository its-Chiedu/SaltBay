package com.chiedu.its.saltbay.util

import com.chiedu.its.saltbay.exception.InstallFailedException
import java.io.File

sealed class Installer {
    abstract operator fun invoke(installDir: String, appName: String)
    internal fun executeProcess(processBuilder: ProcessBuilder?) {
        val exitCode = processBuilder?.start()?.waitFor()

        if (exitCode == 0) {
            println("Installation completed successfully.")
        } else {
            throw InstallFailedException("Installation failed with exit code $exitCode.")
        }
    }
}

object MacInstaller : Installer() {

    private fun getMountDir(): String = "/Volumes/"

    override operator fun invoke(installDir: String, appName: String) {

        val appPath = File(installDir, appName).toString()
        // Mount the DMG file
        println("Installing $appName...")

        ProcessBuilder(
            "hdiutil",
            "attach",
            appPath,
            "-quiet"
        ).also { executeProcess(it) }

        // Copy the application to the desired installation directory
        ProcessBuilder(
            "cp",
            "-R",
            getMountDir(),
            installDir
        ).also { executeProcess(it) }

        // Unmount the DMG file
        ProcessBuilder(
            "hdiutil",
            "detach",
            "${getMountDir()}Jetbrains Toolbox",
            "-quiet"
        ) .also { executeProcess(it) }

    }

}

object WindowsInstaller : Installer() {
    override fun invoke(installDir: String, appName: String) {
        println("Installing $appName...")
        ProcessBuilder(installDir)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .also { executeProcess(it) }
    }

}

object LinuxInstaller : Installer() {
    override fun invoke(installDir: String, appName: String) {
        println("Installing $appName...")
        ProcessBuilder("sh", installDir)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .also { executeProcess(it) }
    }
}
