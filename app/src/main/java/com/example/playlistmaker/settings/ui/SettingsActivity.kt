package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    private val themeInteractor: ThemeInteractor = Creator.provideThemeInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings2)

        val navBack = findViewById<MaterialToolbar>(R.id.tool_bar)

        navBack.setNavigationOnClickListener {
            finish()
        }
        setThemeSwitcher()

        val shareLine = findViewById<MaterialTextView>(R.id.shareLine)
        shareLine.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.practicumLink))

            }
            startActivity(shareIntent)
        }

        val supportLine = findViewById<MaterialTextView>(R.id.supportLine)
        supportLine.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.supportEmail)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.supportEmailSubj))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.supportEmailText))
            }
            startActivity(sendIntent)
        }

        val agreementLine = findViewById<MaterialTextView>(R.id.agreementLine)
        agreementLine.setOnClickListener {
            val browseIntent = Intent(Intent.ACTION_VIEW)
            browseIntent.data = Uri.parse(getString(R.string.agreementLink))
            startActivity(browseIntent)
        }

        }
    private fun setThemeSwitcher() {
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.selector)
        themeSwitcher.isChecked = themeInteractor.getCurrentTheme()

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            themeInteractor.switchTheme(checked)
        }
    }
    }
