package com.app.littlechat.interfaces

import androidx.annotation.Nullable

interface AppInterface {

    fun handleEvent(pos: Int, act:Int, map: Map<String, Any>?)

}