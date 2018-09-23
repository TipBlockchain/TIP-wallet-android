package io.tipblockchain.kasakasa.utils

import android.util.Log
import io.tipblockchain.kasakasa.app.App
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import java.io.*
import java.util.*

class FileUtilsTest {

    lateinit var fileUtils: FileUtils

    val testFile = "filename"

    @Before
    fun setUp() {
        fileUtils = FileUtils()
    }

    @After
    fun tearDown() {
        val file = File(fileUtils.filesDir(), testFile)
        file.delete()
    }

    @Test
    fun filesDir() {
        val dir = fileUtils.filesDir()
        Assert.assertTrue(dir.exists())
    }

    @Test
    fun walletsDir() {
        val dir = fileUtils.walletsDir()
        Assert.assertTrue(dir.exists())
    }

    @Test
    fun walletFiles() {
    }

    @Test
    fun createDir() {
        val dirname = "dir-${Date().time}"
        val dir = fileUtils.createDir(dirname)

        Assert.assertTrue(dir.exists())
        Assert.assertTrue(dir.isDirectory)
    }

    @Test
    fun createFile() {
        val filename = "testfile ${Date().time}"
        val filesDir = fileUtils.filesDir()
        val newFile = fileUtils.createFile(filesDir, filename)

        println("File created: $newFile")

        Assert.assertTrue(File(filesDir, filename).exists())
        Assert.assertEquals(newFile?.name, filename)
    }

    @Test
    fun deleteFile() {
    }

    @Test
    fun testGetWordList() {
        val inputStream = App.applicationContext().assets.open("en-mnemonic-word-list.txt")
        val wordList = readAllLines(inputStream)
        Assert.assertNotNull(wordList)
    }

    @Throws(IOException::class)
    fun readAllLines(inputStream: InputStream): List<String> {
        val br = BufferedReader(InputStreamReader(inputStream))
        val data = ArrayList<String>()
        var line: String? = null

        do {
            line = br.readLine()
            if (line != null) {
                data.add(line!!)
            }
        } while (line != null)

        return data
    }
}