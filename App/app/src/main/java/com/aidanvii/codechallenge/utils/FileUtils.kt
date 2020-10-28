package com.aidanvii.codechallenge.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

fun File.copyTo(destination: File) {
    FileInputStream(this).use { fileInputStream ->
        FileOutputStream(destination).use { fileOutputStream ->
            val fileInputChannel = fileInputStream.channel
            val fileOutputChannel = fileOutputStream.channel
            fileInputChannel.transferTo(0, fileInputChannel.size(), fileOutputChannel)
        }
    }
}