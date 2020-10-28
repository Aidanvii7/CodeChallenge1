package com.aidanvii.codechallenge.data

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.aidanvii.codechallenge.domain.UserCredentials

internal class UserDetailsStore(
    applicationContext: Context,
) {
    private val sharedPreferences = EncryptedSharedPreferences.create(
        "secret_shared_prefs",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        applicationContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    val userCredentials: UserCredentials?
        get() {
            return UserCredentials(
                emailAddress = sharedPreferences.getString(USER_EMAIL_PREF_KEY, null) ?: return null,
                password = sharedPreferences.getString(USER_PASSWORD_PREF_KEY, null) ?: return null,
            )
        }

    fun updateUserCredentials(userCredentials: UserCredentials) {
        val (emailAddress, password) = userCredentials
        sharedPreferences.edit(commit = true) {
            putString(USER_EMAIL_PREF_KEY, emailAddress)
            putString(USER_PASSWORD_PREF_KEY, password)
        }
    }

    @Volatile
    var temporaryUserDetails: TemporaryUserDetails? = null
        private set

    fun updateTemporaryUserDetails(temporaryUserDetails: TemporaryUserDetails) {
        this.temporaryUserDetails = temporaryUserDetails
    }

    fun clear() {
        sharedPreferences.edit(commit = true) {
            putString(USER_EMAIL_PREF_KEY, null)
            putString(USER_PASSWORD_PREF_KEY, null)
        }
        temporaryUserDetails = null
    }

    private companion object {
        const val USER_EMAIL_PREF_KEY = "USER_EMAIL_PREF_KEY"
        const val USER_PASSWORD_PREF_KEY = "PASSWORD_PREF_KEY"
    }
}

