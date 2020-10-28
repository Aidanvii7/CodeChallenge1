package com.aidanvii.codechallenge.utils

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

fun Bitmap.toBase64String(): String =
    ByteArrayOutputStream().use { outputStream ->
        compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val imageInByteArray = outputStream.toByteArray()
        Base64.encodeToString(imageInByteArray, Base64.DEFAULT)
    }
