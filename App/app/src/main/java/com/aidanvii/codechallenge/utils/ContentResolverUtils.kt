package com.aidanvii.codechallenge.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

fun ContentResolver.bitmapFrom(imageUri: Uri): Bitmap =
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(this, imageUri)
    } else {
        val source = ImageDecoder.createSource(this, imageUri)
        ClosableBitmap(ImageDecoder.decodeBitmap(source)).useUnwrapped {
            it.copy(Bitmap.Config.ARGB_8888, false)
        }
    }