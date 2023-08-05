package com.libhelper.lib

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.libhelper.helper.caesar
import com.libhelper.helper.domain.BuildResult
import com.libhelper.helper.domain.LinkBuilder
import com.libhelper.helper.getAfUserID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
