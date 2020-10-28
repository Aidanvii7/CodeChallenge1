package com.aidanvii.codechallenge.effects

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.core.content.FileProvider.getUriForFile
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.aidanvii.codechallenge.BuildConfig
import com.aidanvii.codechallenge.state.*
import com.aidanvii.codechallenge.state.Action.Intent.ChooseAvatarPhoto
import com.aidanvii.codechallenge.state.Action.Result.GetAvatarPhoto
import com.aidanvii.codechallenge.utils.*
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.math.floor
import kotlin.math.sqrt

class ChooseAvatarPhotoSideEffect(
    activity: ComponentActivity,
    private val coroutineDispatchers: CoroutineDispatchers,
    actionsProvider: ActionsProvider,
    dispatchResultAsync: DispatchResultAsync,
) : SideEffect<LifecycleCoroutineScope, ChooseAvatarPhoto>(
    actionsProvider = actionsProvider,
    dispatchResultAsync = dispatchResultAsync,
    coroutineScope = activity.lifecycleScope,
) {

    private val applicationContext = activity.applicationContext

    override fun transform(action: Action) = action as? ChooseAvatarPhoto

    override suspend fun handle(action: ChooseAvatarPhoto) {
        when (action) {
            ChooseAvatarPhoto.FromCamera -> getPhotoFromCamera()
            ChooseAvatarPhoto.FromGallery -> getExistingPhoto()
        }
    }

    private val imagesDirectory = File(activity.getExternalFilesDir(null), "images").apply {
        if (!exists()) mkdirs()
    }

    private val avatarImageFilePath = createFileForAvatarImage(
        prefix = "DEFAULT",
    )

    private fun createFileForAvatarImage(prefix: String) = createTempFile(
        prefix = prefix,
        suffix = ".jpg",
        directory = imagesDirectory,
    )

    private val avatarImageContentUri = getUriFor(avatarImageFilePath)

    private fun getUriFor(file: File) = getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.fileprovider", file)

    private val getExistingPhoto = activity.registerForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        input = "image/*",
    ) { selectedPhotoUri: Uri? ->
        coroutineScope.launchWhenCreated {
            val contentResolver = applicationContext.contentResolver
            withContext(coroutineDispatchers.default) {
                dispatchResultAsync {
                    val bitmap = selectedPhotoUri?.path?.let { imagePath ->
                        if (imagePath.isNotNullAndNotEmpty()) {
                            contentResolver.bitmapFrom(selectedPhotoUri)
                        } else null
                    }
                    bitmap?.toGetAvatarPhotoResult() ?: GetAvatarPhoto.Failure
                }
            }
        }
    }

    private val getPhotoFromCamera = activity.registerForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        input = avatarImageContentUri,
    ) { originalSavedSuccessfully: Boolean ->
        coroutineScope.launchWhenCreated {
            val contentResolver = applicationContext.contentResolver
            withContext(coroutineDispatchers.default) {
                dispatchResultAsync {
                    if (originalSavedSuccessfully) {
                        contentResolver.bitmapFrom(avatarImageContentUri).toGetAvatarPhotoResult()
                    } else GetAvatarPhoto.Failure
                }
            }
        }
    }

    private suspend fun Bitmap.toGetAvatarPhotoResult(): GetAvatarPhoto =
        croppedToSquareWithInvertedColorsAndCompressed().save()?.let { savedImageFileUri ->
            GetAvatarPhoto.Success(
                uri = savedImageFileUri,
            )
        }?: GetAvatarPhoto.Failure

    // TODO: Optimise this, there's multiple bitmap creations happening here
    private fun Bitmap.croppedToSquareWithInvertedColorsAndCompressed() =
        croppedToSquare().withInvertedFilter().compressed()

    private fun Bitmap.compressed() = transform {
        val uncompressed = copy(Bitmap.Config.ARGB_8888, false)
        val currentWidth = uncompressed.width
        val currentHeight = uncompressed.height
        val currentPixels = currentWidth * currentHeight
        val maxPixels = ONE_MEGABYTE_IN_BYTES_FLOORED
        if (currentPixels > maxPixels) {
            val scaleFactor = sqrt(maxPixels / currentPixels.toDouble())
            val newWidthPx = floor(currentWidth * scaleFactor).toInt()
            val newHeightPx = floor(currentHeight * scaleFactor).toInt()
            uncompressed.transform {
                Bitmap.createScaledBitmap(uncompressed, newWidthPx, newHeightPx, true)
            }

        } else uncompressed
    }

    private suspend fun Bitmap.save(): Uri? =
        withContext(coroutineDispatchers.io) {
            val copiedFile = createFileForAvatarImage(
                prefix = "avatar_${millisSinceEpoch}",
            )
            avatarImageFilePath.copyTo(copiedFile)
            copiedFile.outputStream().use { outputStream ->
                try {
                    if (!compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                        throw IllegalStateException()
                    }
                    getUriFor(copiedFile)
                } catch (e: IllegalStateException) {
                    null
                } finally {
                    @Suppress("BlockingMethodInNonBlockingContext")
                    outputStream.flush()
                }
            }
        }

    private val millisSinceEpoch: Long
        get() = Calendar.getInstance().time.time

    private companion object {
        const val ONE_MEGABYTE_IN_BYTES_FLOORED = (1024 * 1024) / 4
    }
}