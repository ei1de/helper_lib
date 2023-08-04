package com.libhelper.helper.domain

sealed interface BuildResult{
    class Success(val url:String):BuildResult
    object Error:BuildResult
}