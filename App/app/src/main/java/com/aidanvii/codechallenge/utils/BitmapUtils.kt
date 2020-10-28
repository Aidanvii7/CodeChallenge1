package com.aidanvii.codechallenge.utils

import android.graphics.*

inline fun Bitmap.transform(transform: Bitmap.() -> Bitmap): Bitmap =
    toClosable().useUnwrapped {
        transform().let { transformed ->
            if (this === transformed) {
                // In the case that transform() returns the same instance, we need to make a copy as the original will get recycled.
                transformed.deepCopy()
            } else transformed
        }
    }

fun Bitmap.deepCopy(
    config: Bitmap.Config = Bitmap.Config.ARGB_8888,
): Bitmap = Bitmap.createBitmap(width, height, config).also { copiedBitmap ->
    Canvas(copiedBitmap).drawBitmap(this, 0f, 0f, Paint())
}

fun Bitmap.deepCopyWith(
    startX: Int = 0,
    startY: Int = 0,
    width: Int = this.width,
    height: Int = this.height,
    matrix: Matrix? = null,
    filter: Boolean = false,
): Bitmap = Bitmap.createBitmap(deepCopy(), startX, startY, width, height, matrix, filter)

fun Bitmap.croppedToSquare(): Bitmap = transform {
    when {
        width > height -> {
            deepCopyWith(
                startX = (width - height) / 2,
                startY = 0,
                width = height,
                height = height,
            )
        }
        height > width -> {
            deepCopyWith(
                startX = 0,
                startY = (height - width) / 2,
                width = width,
                height = width,
            )
        }
        else -> this
    }
}

fun Bitmap.withColorFilter(filter: ColorMatrixColorFilter): Bitmap = transform {
    val coloredBitmap = deepCopy()
    val canvas = Canvas(coloredBitmap)
    val paint = Paint()
    paint.colorFilter = filter
    canvas.drawBitmap(this, 0f, 0f, paint)
    coloredBitmap
}

fun Bitmap.withInvertedFilter(): Bitmap = transform { withColorFilter(inverted) }

private val inverted: ColorMatrixColorFilter
    get() {
        val colorMatrix = ColorMatrix(
            floatArrayOf(
                -1f, 0f, 0f, 0f, 255f,
                0f, -1f, 0f, 0f, 255f,
                0f, 0f, -1f, 0f, 255f,
                0f, 0f, 0f, 1f, 0f
            ))
        return ColorMatrixColorFilter(colorMatrix)
    }