package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.App
import com.example.playlistmaker.Constants
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.AudioPlayer
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.SearchState
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TracksInteractor

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel

    private var inputText: String = DEF_TEXT
    private val tracks = ArrayList<Track>()
    private lateinit var adapter : TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SearchViewModelFactory(
                Creator.provideTracksInteractor(),
                Creator.provideSearchHistoryInteractor()
            )
        )[SearchViewModel::class.java]

        viewModel.historyState.observe(this) { historyTracks ->
            if (binding.searchInput.text.isEmpty() && historyTracks.isNotEmpty()) {
                historyAdapter.updateTracks(historyTracks)
                binding.searchHistoryLayout.visibility = View.VISIBLE
                binding.trackList.visibility = View.GONE
                hidePlaceholder()
            } else {
                hideHistory()
            }
        }

        viewModel.searchState.observe(this) { searchState ->
            when (searchState) {
                is SearchState.Loading -> {
                    binding.placeHolderImage.visibility = View.GONE
                    binding.placeHolderMessage.visibility = View.GONE
                    binding.refreshBtn.visibility = View.GONE
                    binding.trackList.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }
                is SearchState.Content -> {
                    binding.progressBar.visibility = View.GONE
                    tracks.clear()
                    binding.trackList.visibility = View.VISIBLE
                    tracks.addAll(searchState.tracks)
                    adapter.notifyDataSetChanged()
                    hideHistory()
                }
                is SearchState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    tracks.clear()
                    showPlaceHolder(
                        getString(R.string.not_found),
                        R.drawable.empty_library,
                        false
                    )
                }
                is SearchState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    tracks.clear()
                    showPlaceHolder(
                        getString(R.string.no_connection),
                        R.drawable.no_connection,
                        true
                    )
                }
            }
        }

        adapter = TrackAdapter(tracks) { clickedTrack ->
            viewModel.addToHistory(clickedTrack)
            playTrack(clickedTrack)
        }

        historyAdapter = TrackAdapter(ArrayList()) { clickedTrack ->
            viewModel.addToHistory(clickedTrack)
            playTrack(clickedTrack)
        }

        preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener{ _, key ->
            if (key == SearchHistoryRepositoryImpl.KEY_HISTORY) {
                updateHistoryView()
            }
        }
        App.getSharedPreferences()
            .registerOnSharedPreferenceChangeListener(preferenceChangeListener)

        binding.trackList.layoutManager = LinearLayoutManager(this)
        binding.trackList.adapter = adapter
        binding.trackHistoryList.layoutManager = LinearLayoutManager(this)
        binding.trackHistoryList.adapter = historyAdapter
        binding.refreshBtn.setOnClickListener {
            hidePlaceholder()
            viewModel.searchTracks(binding.searchInput.text.toString())
        }

        binding.toolBar.setNavigationOnClickListener {
            finish()
        }

        binding.searchInputClear.setOnClickListener {
            binding.searchInput.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
            hidePlaceholder()
            tracks.clear()
            adapter.notifyDataSetChanged()
            showHistoryIfEmpty()
        }

        binding.clearHistoryBtn.setOnClickListener {
            viewModel.clearHistory()
            updateHistoryView()
        }

        binding.searchInput.setOnFocusChangeListener{ _, hasFocus ->
            if(hasFocus && binding.searchInput.text.isNullOrEmpty()) {
                updateHistoryView()
            } else {
                hideHistory()
            }
        }

        val searchInputTextWatcher = object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.searchInputClear.visibility = inputClearVisibility(s)
                inputText = binding.searchInput.text.toString()
                viewModel.searchDebounce(inputText)
                if (s.isNullOrEmpty()) {
                    showHistoryIfEmpty()
                } else {
                    hideHistory()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        }

        binding.searchInput.addTextChangedListener(searchInputTextWatcher)

        binding.searchInput.setOnEditorActionListener { _, actionId, _, ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                val text = binding.searchInput.text.toString()
                viewModel.searchTracks(text)
                true
            }
            false
        }
    }

    private fun inputClearVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, inputText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(INPUT_TEXT, DEF_TEXT)
        binding.searchInput.setText(inputText)
    }

    companion object {
        private const val INPUT_TEXT = "INPUT_TEXT"
        private const val DEF_TEXT = ""
        private const val TAG = "SEARCH_TEST"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private fun showPlaceHolder(text: String, imageId: Int, showRefresh: Boolean) {
        binding.progressBar.visibility = View.GONE
        if (text.isNotEmpty()) {
            binding.placeHolderImage.setImageResource(imageId)
            binding.placeHolderMessage.text = text

            binding.placeHolderImage.visibility = View.VISIBLE
            binding.placeHolderMessage.visibility = View.VISIBLE
            binding.refreshBtn.visibility = if (showRefresh) View.VISIBLE else View.GONE

            tracks.clear()
            adapter.notifyDataSetChanged()
            hideHistory()
        }
    }

    private fun hidePlaceholder() {
        binding.placeHolderImage.visibility = View.GONE
        binding.placeHolderMessage.visibility = View.GONE
        binding.refreshBtn.visibility = View.GONE
    }

    private fun updateHistoryView() {
        viewModel.getHistory()
    }

    private fun showHistoryIfEmpty() {
        if (binding.searchInput.text.isNullOrEmpty()) {
            updateHistoryView()
        } else {
            hideHistory()
        }
    }
    private fun hideHistory() {
        binding.searchHistoryLayout.visibility = View.GONE
        binding.trackList.visibility = View.VISIBLE
    }

    private fun playTrack(track: Track) {
        if (viewModel.clickDebounce()) {
            val playerIntent = Intent(this, AudioPlayer::class.java).apply {
                putExtra(Constants.TRACK_KEY, track)
            }
            startActivity(playerIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        App.getSharedPreferences()
            .unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
}