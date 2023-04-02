package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 0
private const val FLAG = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

fun NotificationManager.sendNotification(
    messageBody: String,
    applicationContext: Context,
    downloadedFileName: String,
    downloadedFileStatus: String
) {

    applicationContext.apply {
        val detailIntent = Intent(this, DetailActivity::class.java)
        detailIntent.putExtra(Constant.EXTRA_FILE_NAME_KEY, downloadedFileName)
        detailIntent.putExtra(Constant.EXTRA_FILE_STATUS_KEY, downloadedFileStatus)

        val detailPendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID,
            detailIntent,
            FLAG
        )

        val notificationBuilder = NotificationCompat.Builder(
            this,
            getString(R.string.notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_assistant_black_24dp,
                getString(R.string.notification_button),
                detailPendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notify(NOTIFICATION_ID, notificationBuilder.build())
    }

}