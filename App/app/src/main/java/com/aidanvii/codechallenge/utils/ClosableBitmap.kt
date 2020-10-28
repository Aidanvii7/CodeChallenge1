package com.aidanvii.codechallenge.utils

import android.graphics.Bitmap
import java.io.Closeable

fun Bitmap.toClosable() = ClosableBitmap(this)

inline class ClosableBitmap(
    private val bitmap: Bitmap
) : Closeable, Wrapper<Bitmap> {
    override fun unwrap() = bitmap
    override fun close() {
        if (!bitmap.isRecycled) {
            bitmap.recycle()
        }
    }
}