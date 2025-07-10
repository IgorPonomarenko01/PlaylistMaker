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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.App
import com.example.playlistmaker.Constants
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.player.AudioPlayer
import com.example.playlistmaker.search.domain.TrackAdapter
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private var inputText: String = DEF_TEXT
    private val tracks = ArrayList<Track>()
    private lateinit var adapter : TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener
    private val searchRunnable = Runnable {
        val currentText = binding.searchInput.text.toString()
        if (currentText.isNotEmpty()) {
            searchTracks(inputText)
        }
    }
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private lateinit var historyInteractor: SearchHistoryInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        historyInteractor = Creator.provideSearchHistoryInteractor()


        adapter = TrackAdapter(tracks) { clickedTrack ->
            historyInteractor.addToHistory(clickedTrack)
            playTrack(clickedTrack)
        }

        historyAdapter = TrackAdapter(ArrayList()) { clickedTrack ->
            historyInteractor.addToHistory(clickedTrack)
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
            searchTracks(binding.searchInput.text.toString())
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
            historyInteractor.clearHistory()
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
                searchDebounce()
               if(s.isNullOrEmpty()) {
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
            searchTracks(text)
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

    private val trackInteractor: TracksInteractor = Creator.provideTracksInteractor()

    private fun searchTracks(text: String) {
        if (text.isNotEmpty()) {
            binding.placeHolderImage.visibility = View.GONE
            binding.placeHolderMessage.visibility = View.GONE
            binding.refreshBtn.visibility = View.GONE
            binding.trackList.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE

            trackInteractor.searchTracks(text, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>) {
                    this@SearchActivity.runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        tracks.clear()

                        if (foundTracks.isNotEmpty()) {
                            binding.trackList.visibility = View.VISIBLE
                            tracks.addAll(foundTracks)
                            adapter.notifyDataSetChanged()
                            hideHistory()
                        } else {
                            showPlaceHolder(
                                getString(R.string.not_found),
                                R.drawable.empty_library,
                                false
                            )
                        }
                    }
                }

                override fun onError(error: String) {
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        showPlaceHolder(
                            getString(R.string.no_connection),
                            R.drawable.no_connection,
                            true
                        )
                    }
                }
            })
        }
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
        val historyTracks = historyInteractor.getHistory()
        Log.d(TAG, "historyInteractor")
        if (binding.searchInput.text.isEmpty() && historyTracks.isNotEmpty()) {
            historyAdapter.updateTracks(historyTracks)
            binding.searchHistoryLayout.visibility = View.VISIBLE
            binding.trackList.visibility = View.GONE
            hidePlaceholder()
        } else {
            hideHistory()
        }
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
        if (clickDebounce()) {
            val playerIntent = Intent(this, AudioPlayer::class.java).apply {
                putExtra(Constants.TRACK_KEY, track)
            }
            startActivity(playerIntent)
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({isClickAllowed = true}, CLICK_DEBOUNCE_DELAY)
        }
        Log.d(TAG, "Click allowed: $current")
        return current
    }

    override fun onDestroy() {
        super.onDestroy()
        App.getSharedPreferences()
            .unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
}
