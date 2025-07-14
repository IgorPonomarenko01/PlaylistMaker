package com.example.playlistmaker.sharing.domain

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val shareAppLink: String,
    private val termsLink: String,
    private val supportEmailData: EmailData
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(shareAppLink)
    }

    override fun openTerms() {
        externalNavigator.openLink(termsLink)
    }

    override fun openSupport() {
        externalNavigator.openEmail(supportEmailData)
    }
}