package com.app.littlechat.utility

import com.app.littlechat.data.model.CustomResult

/*Base Class for all localised Exceptions*/
open class LocalisedException(message: String?) : Exception(message)

class UnAuthorizedException : LocalisedException(null)
class NoInternetException(message: String?) : LocalisedException(message)
class SomethingWentWrongException() : LocalisedException(null)

class ApiException(message: String?) : LocalisedException(message)


fun <T> setError(resultCallback: (CustomResult<List<T>>) -> Unit, e: String? = null) {
    resultCallback.invoke(
        CustomResult.Error(
            exception = LocalisedException(e)
        )
    )
}
