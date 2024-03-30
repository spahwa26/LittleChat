package com.app.littlechat.utility

/*Base Class for all localised Exceptions*/
open class LocalisedException(message: String?) : Exception(message)

class UnAuthorizedException : LocalisedException(null)
class NoInternetException(message: String?) : LocalisedException(message)
class SomethingWentWrongException() : LocalisedException(null)

class ApiException(message: String?) : LocalisedException(message)
