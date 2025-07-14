package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivitySettings2Binding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettings2Binding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettings2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        viewModel.initSharingInteractor(applicationContext)

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
