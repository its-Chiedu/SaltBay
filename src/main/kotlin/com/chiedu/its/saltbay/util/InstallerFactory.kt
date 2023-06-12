package com.chiedu.its.saltbay.util

import com.chiedu.its.saltbay.exception.NotSupportedException

sealed class InstallerFactory {
    abstract operator fun invoke(os: String): Installer
}

object InstallerFactoryImpl : InstallerFactory() {

    override operator fun invoke(os: String): Installer {
        return when {
            os.contains("Mac") -> MacInstaller
            os.contains("Windows") -> WindowsInstaller
            os.contains("Windows") -> LinuxInstaller
            else -> {
                throw NotSupportedException()
            }
        }
    }
}
