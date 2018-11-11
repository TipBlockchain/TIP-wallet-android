package io.tipblockchain.kasakasa.utils

import android.content.Context
import io.tipblockchain.kasakasa.app.App
import java.io.File

class FileUtils {

    fun filesDir(): File {
        return App.applicationContext().filesDir
    }

    fun walletsDir(): File {
        val dir = App.applicationContext().getDir("wallets", Context.MODE_PRIVATE)
        return dir
    }

    fun walletFiles(): Array<File> {
        val dir = walletsDir()
        return dir.listFiles()
    }

    fun createDir(name: String, mode: Int = Context.MODE_PRIVATE): File {
        return App.applicationContext().getDir(name, mode)
    }

    fun createFile(dir: File, filename: String): File? {
        val newFile = File(dir, filename)
        try {
            val created = newFile.createNewFile()
            if (created) {
                return newFile
            }
        } catch (e: Exception) { }
        return null
    }

    fun fileForWalletFilename(filename: String): File? {
        val file = File(walletsDir(), filename)
        if (file.exists()) {
            return file
        }
        return null
    }

    fun fileFromPath(path: String): File? {
        val file = File(path)
        if (file.exists()) {
            return file
        }
        return null
    }

    fun imageFileWithName(filename: String): File? {
        return File(App.applicationContext().cacheDir, filename)
    }

    fun deleteFile(file: File) : Boolean {
        return file.delete()
    }
}
