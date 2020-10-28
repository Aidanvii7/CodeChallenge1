package com.aidanvii.codechallenge.utils

import java.math.BigInteger
import java.security.MessageDigest

val String.md5: String
    get() {
        val messageDigest = MessageDigest.getInstance("MD5")
        return BigInteger(1, messageDigest.digest(toByteArray())).toString(16).padStart(32, '0')
    }