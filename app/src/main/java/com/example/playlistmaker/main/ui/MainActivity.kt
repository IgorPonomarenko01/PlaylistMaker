package com.example.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.LibraryActivity
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.search.ui.SearchActivity
import com.example.playlistmaker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchIntent)
            }
        }
        binding.searchButton.setOnClickListener(searchButtonClickListener)

        binding.libraryButton.setOnClickListener {
            val libraryIntent = Intent(this, LibraryActivity::class.java)
            startActivity(libraryIntent)
        }

        binding.settingsButton.setOnClickListener {
        val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}