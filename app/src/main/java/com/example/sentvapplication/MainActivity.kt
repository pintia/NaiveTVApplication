package com.example.sentvapplication

import android.app.ActivityManager
import android.app.ActivityManager.RunningTaskInfo
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    private val TAG = "SenTVApplication"
    private val PACKAGE_KIOSK = "de.ozerov.fully"
    private val PACKAGE_OXYGEN = "com.yangqi.rom.launcher.free"
    private val PACKAGE_SETTINGS = "com.xiaomi.mitv.settings"

    @Volatile
    private var autoOpen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnSettings).setOnClickListener {
            try {
                openSettings()
            } catch (e: Exception) {
                // Sometimes it needs two clicks.
                openApp(PACKAGE_SETTINGS)
                openApp(PACKAGE_SETTINGS)
            }
        }
        findViewById<Button>(R.id.btnOxygen).setOnClickListener { openApp(PACKAGE_OXYGEN) }
        findViewById<Button>(R.id.btnKiosk).setOnClickListener { openApp(PACKAGE_KIOSK) }

        if (!isTaskRoot) {
            val intentAction = intent.action
            if (intent.hasCategory(Intent.CATEGORY_HOME) && intentAction != null && intentAction == Intent.ACTION_MAIN) {
                finish()
                return
            }
        }

        if (savedInstanceState == null && !checkAppRunning(PACKAGE_KIOSK)) {
            Toast.makeText(this, getString(R.string.will_open_kiosk), Toast.LENGTH_SHORT).show()
            thread {
                Thread.sleep(5000)
                if (autoOpen) {
                    openApp(PACKAGE_KIOSK)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!autoOpen) {
            return
        }
        autoOpen = false
        Toast.makeText(this, getString(R.string.cancelled_auto_open), Toast.LENGTH_SHORT).show()
        val textView = findViewById<TextView>(R.id.notice)
        textView.text = ""
    }

    private fun openApp(packageName: String) {
        val launchIntent =
            packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.let { startActivity(it) } ?:
        runOnUiThread {
            Toast.makeText(this, getString(R.string.no_app, packageName), Toast.LENGTH_LONG).show()
            val textView = findViewById<TextView>(R.id.notice)
            textView.text = getString(R.string.no_app, packageName)
        }
    }

    private fun checkAppRunning(packageName: String): Boolean {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks: List<RunningTaskInfo> = am.getRunningTasks(10)
        tasks.forEach {
            Log.i(TAG, "checkAppRunning: ${it.topActivity!!.className}")
            if (it.topActivity!!.packageName.startsWith(packageName)) {
                return true
            }
        }
        return false
    }

    private fun openSettings() {
        startActivityForResult(Intent(Settings.ACTION_SETTINGS), 0)
    }
}
