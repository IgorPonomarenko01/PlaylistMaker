package com.example.playlistmaker.sharing.domain

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun shareLink(link: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
        }
        val chooserIntent = Intent.createChooser(shareIntent, null).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        ContextCompat.startActivity(context, chooserIntent, null)
    }

    override fun openLink(link: String) {
        Intent(Intent.ACTION_VIEW, Uri.parse(link)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ContextCompat.startActivity(context, this, null)
        }
    }

    override fun openEmail(emailData: EmailData) {
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
            putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
            putExtra(Intent.EXTRA_TEXT, emailData.text)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ContextCompat.startActivity(context, this, null)
        }
    }
}