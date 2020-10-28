package com.aidanvii.codechallenge.utils

import androidx.activity.result.ActivityResultLauncher

operator fun ActivityResultLauncher<Unit>.invoke() = launch(null)