package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettings2Binding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettings2Binding
    private val viewModel: SettingsViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettings2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolBar.setNavigationOnClickListener {
            finish()
        }
        setThemeSwitcher()
        setupClickListeners()
        }
    private fun setThemeSwitcher() {
        binding.selector.isChecked = viewModel.getCurrentTheme()
        binding.selector.setOnCheckedChangeListener { switcher, checked ->
            viewModel.switchTheme(checked)
        }
    }
    private fun setupClickListeners() {
        binding.shareLine.setOnClickListener { viewModel.shareApp() }
        binding.supportLine.setOnClickListener { viewModel.openSupport() }
        binding.agreementLine.setOnClickListener { viewModel.openTerms() }
    }
    }
