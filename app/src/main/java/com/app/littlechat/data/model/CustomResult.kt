package com.app.littlechat.data.model

import com.app.littlechat.utility.LocalisedException
import com.app.littlechat.utility.SomethingWentWrongException


sealed class CustomResult<out R> {
    data class Success<out T>(val data: T, val tag: String? = null) : CustomResult<T>()
    data class Error(
        val errorCode: Int = -1,
        val exception: LocalisedException = SomethingWentWrongException(),
    ) : CustomResult<Nothing>()
}
