package com.app.littlechat.model

import com.app.littlechat.utility.LocalisedException


sealed class CustomResult<out R> {
    data class Success<out T>(val data: T, val tag: String? = null) : CustomResult<T>()
    data class Error(
        val errorCode : Int=-1,
        val exception: LocalisedException,
    ) : CustomResult<Nothing>()
}
