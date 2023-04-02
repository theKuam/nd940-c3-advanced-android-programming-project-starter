package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var url = EMPTY_URL

    private lateinit var notificationManager: NotificationManager
    private lateinit var radioGroup: RadioGroup
    private var fileName = ""
    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        radioGroup = findViewById(R.id.radioGroupDownload)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioGlide -> {
                    url = GLIDE_URL
                    fileName = getString(R.string.title_glide)
                }
                R.id.radioLoadApp -> {
                    url = LOAD_APP_URL
                    fileName = getString(R.string.title_load_app)
                }
                R.id.radioRetrofit -> {
                    url = RETROFIT_URL
                    fileName = getString(R.string.title_retrofit)
                }
            }
        }

        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        customButton.setOnClickListener {
            download()
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            id?.let { downloadId ->
                val downloadStatus = checkDownloadStatus(downloadId)
                context?.let {
                    notificationManager.sendNotification(
                        getString(R.string.notification_description),
                        it,
                        fileName,
                        downloadStatus
                    )
                }
            }
        }
    }

    private fun checkDownloadStatus(downloadId: Long): String {
        val downloadManagerQuery = DownloadManager.Query()
        downloadManagerQuery.setFilterById(downloadId)
        val cursor = downloadManager.query(downloadManagerQuery)

        return if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            when (cursor.getInt(columnIndex)) {
                DownloadManager.STATUS_SUCCESSFUL -> getString(R.string.download_successful)
                else -> getString(R.string.download_failed)
            }
        } else getString(R.string.unknown)
    }

    private fun download() {
        if (url == EMPTY_URL) {
            Toast.makeText(
                applicationContext,
                getString(R.string.unselected_radio_message),
                Toast.LENGTH_LONG
            ).show()
        } else {
            val request =
                DownloadManager.Request(Uri.parse(url))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        }
    }

    companion object {
        private const val EMPTY_URL = ""
        private const val GLIDE_URL = "https://github.com/bumptech/glide"
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val RETROFIT_URL = "https://github.com/square/retrofit"
    }

}
