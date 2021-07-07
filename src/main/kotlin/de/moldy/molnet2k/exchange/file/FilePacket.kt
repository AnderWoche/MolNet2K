package de.moldy.molnet2k.exchange.file

import java.io.File
import java.nio.file.Path
import java.nio.file.Files
import java.util.*

class FilePacket(path: Path) {
    private val files: MutableList<File> = ArrayList()
    private val unmodifiableFiles = Collections.unmodifiableList(files)
    private val relativeFilePath: MutableList<String> = ArrayList()
    private val relativeFilePathReadOnly = Collections.unmodifiableList(relativeFilePath)
    var totalTransferSize: Long = 0
        private set

    init {
        if (Files.isRegularFile(path)) {
            addFileWithRelativePath(path.toFile(), path.fileName.toString())
        } else {
            Files.walk(path).filter { Files.isRegularFile(it) }.forEach { filePath: Path ->
                val pathString = path.toString()
                val eachPathString = filePath.toString()
                val relativePath = eachPathString.substring(pathString.length)
                addFileWithRelativePath(filePath.toFile(), relativePath)
            }
        }
    }

    private fun addFileWithRelativePath(file: File, relativePath: String) {
        totalTransferSize += file.length()
        files.add(file)
        relativeFilePath.add(relativePath)
    }

    fun getFiles(): List<File> {
        return unmodifiableFiles
    }

    fun getRelativeFilePath(): List<String> {
        return relativeFilePathReadOnly
    }
}