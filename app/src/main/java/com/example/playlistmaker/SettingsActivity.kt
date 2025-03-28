package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings2)

        val navBack = findViewById<MaterialToolbar>(R.id.tool_bar)
        val shareLine = findViewById<MaterialTextView>(R.id.shareLine)
        val supportLine = findViewById<MaterialTextView>(R.id.supportLine)
        val agreementLine = findViewById<MaterialTextView>(R.id.agreementLine)
        navBack.setNavigationOnClickListener {
            finish()
        }
        shareLine.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.practicumLink))
            startActivity(shareIntent)
        }

        supportLine.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.supportEmail)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.supportEmailSubj))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.supportEmailText))
            }
            startActivity(sendIntent)
        }

        agreementLine.setOnClickListener {
            val browseIntent = Intent(Intent.ACTION_VIEW)
            browseIntent.data = Uri.parse(getString(R.string.agreementLink))
            startActivity(browseIntent)
        }
        }
    }
