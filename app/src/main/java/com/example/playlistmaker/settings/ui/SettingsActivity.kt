package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettings2Binding
import com.example.playlistmaker.settings.domain.ThemeInteractor

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettings2Binding
    private val themeInteractor: ThemeInteractor = Creator.provideThemeInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettings2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolBar.setNavigationOnClickListener {
            finish()
        }
        setThemeSwitcher()

        binding.shareLine.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.practicumLink))

            }
            startActivity(shareIntent)
        }
        binding.supportLine.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.supportEmail)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.supportEmailSubj))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.supportEmailText))
            }
            startActivity(sendIntent)
        }
        binding.agreementLine.setOnClickListener {
            val browseIntent = Intent(Intent.ACTION_VIEW)
            browseIntent.data = Uri.parse(getString(R.string.agreementLink))
            startActivity(browseIntent)
        }
        }
    private fun setThemeSwitcher() {
        binding.selector.isChecked = themeInteractor.getCurrentTheme()
        binding.selector.setOnCheckedChangeListener { switcher, checked ->
            themeInteractor.switchTheme(checked)
        }
    }
    }
