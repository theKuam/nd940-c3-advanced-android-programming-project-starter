package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var txtFileName: TextView
    private lateinit var txtFileStatus: TextView
    private lateinit var backButton: Button
    private lateinit var showStatusButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelAll()

        txtFileName = findViewById(R.id.txtFileName)
        txtFileStatus = findViewById(R.id.txtFileStatus)
        backButton = findViewById(R.id.backButton)
        showStatusButton = findViewById(R.id.showStatusButton)

        val bundles = intent.extras

        if (bundles != null) {
            bundles.apply {
                txtFileName.text = getString(Constant.EXTRA_FILE_NAME_KEY)

                val status = getString(Constant.EXTRA_FILE_STATUS_KEY)
                if (status == getString(R.string.download_successful)) {
                    txtFileStatus.setTextColor(getColor(R.color.colorPrimary))
                } else {
                    txtFileStatus.setTextColor(getColor(R.color.red))
                }
                txtFileStatus.text = status
            }
        } else {

            txtFileName.text = getString(R.string.unknown)
            txtFileStatus.text = getString(R.string.unknown)
        }

        backButton.setOnClickListener {
            val backIntent = Intent(applicationContext, MainActivity::class.java)
            startActivity(backIntent)
        }
    }

}
