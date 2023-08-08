package com.libhelper.helper.domain

sealed interface BuildResult{
    class Success(val url:String, val push:String):BuildResult
    object Error:BuildResult
}