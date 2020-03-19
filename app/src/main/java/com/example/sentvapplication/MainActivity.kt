package com.example.sentvapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Toast.makeText(this, "Will open Kiosk...", Toast.LENGTH_LONG).show()
        thread {
            Thread.sleep(3000)
            val launchIntent =
                packageManager.getLaunchIntentForPackage("de.ozerov.fully")
            launchIntent?.let { startActivity(it) }
        }
    }
}
