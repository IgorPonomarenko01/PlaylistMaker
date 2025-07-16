package com.example.playlistmaker.sharing.domain

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(link: String)
    fun openEmail(emailData: EmailData)
}