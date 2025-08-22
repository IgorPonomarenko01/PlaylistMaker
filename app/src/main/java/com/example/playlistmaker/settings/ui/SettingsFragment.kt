package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}