package com.example.playlistmaker

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
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<ItunesResponse>
}

class ItunesResponse (
    val resultCount: Int,
    val results: List<Track>
)

class SearchActivity : AppCompatActivity() {

    private var inputText: String = DEF_TEXT
    private lateinit var searchInput: EditText
    private lateinit var placeHolderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var refreshBtn: Button
    private lateinit var searchHistoryLayout: LinearLayout
    private lateinit var trackHistoryList: RecyclerView
    private lateinit var clearHistoryBtn: Button

    private val iTunesbaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesbaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ItunesApi::class.java)

    private val tracks = ArrayList<Track>()
    private lateinit var adapter : TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchHistory: SearchHistory
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var trackList: RecyclerView
    private lateinit var preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener
    private val searchRunnable = Runnable {
        val currentText = searchInput.text.toString()
        if (currentText.isNotEmpty()) {
            searchTracks(inputText)
        }
    }
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchHistory = (applicationContext as App).searchHistory
        sharedPrefs = (applicationContext as App).sharedPrefs

        adapter = TrackAdapter(tracks) { clickedTrack ->
            searchHistory.addTrackToHistory(clickedTrack)
            playTrack(clickedTrack)
        }

        historyAdapter = TrackAdapter(ArrayList()) { clickedTrack ->
            searchHistory.addTrackToHistory(clickedTrack)
            playTrack(clickedTrack)
        }

        preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener{ _, key ->
            if (key == TRACK_HISTORY_KEY) {
                updateHistoryView()
            }
        }
        (applicationContext as App).sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

        trackList = findViewById(R.id.trackList)
        trackList.layoutManager = LinearLayoutManager(this)
        trackList.adapter = adapter

        trackHistoryList = findViewById(R.id.trackHistoryList)
        trackHistoryList.layoutManager = LinearLayoutManager(this)
        trackHistoryList.adapter = historyAdapter

        placeHolderImage = findViewById(R.id.placeHolderImage)
        placeholderMessage = findViewById(R.id.placeHolderMessage)
        refreshBtn = findViewById(R.id.refreshBtn)
        searchHistoryLayout = findViewById(R.id.searchHistoryLayout)
        clearHistoryBtn = findViewById(R.id.clearHistoryBtn)
        progressBar = findViewById(R.id.progressBar)

        refreshBtn.setOnClickListener {
            hidePlaceholder()
            searchTracks(searchInput.text.toString())
        }

        val navBack = findViewById<MaterialToolbar>(R.id.tool_bar)

        navBack.setNavigationOnClickListener {
            finish()
        }
        searchInput = findViewById(R.id.searchInput)
        val searchInputClear = findViewById<ImageView>(R.id.searchInputClear)

        searchInputClear.setOnClickListener {
            searchInput.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchInput.windowToken, 0)
            hidePlaceholder()
            tracks.clear()
            adapter.notifyDataSetChanged()
            showHistoryIfEmpty()
        }

        clearHistoryBtn.setOnClickListener {
            searchHistory.clearHistory()
            updateHistoryView()
        }

        searchInput.setOnFocusChangeListener{ _, hasFocus ->
            if(hasFocus && searchInput.text.isNullOrEmpty()) {
                updateHistoryView()
            } else {
                hideHistory()
            }
        }

        val searchInputTextWatcher = object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchInputClear.visibility = inputClearVisibility(s)
                inputText = searchInput.text.toString()
                searchDebounce()
               if(s.isNullOrEmpty()) {
                    showHistoryIfEmpty()
                } else {
                    hideHistory()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        }

        searchInput.addTextChangedListener(searchInputTextWatcher)

        searchInput.setOnEditorActionListener { _, actionId, _, ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
            val text = searchInput.text.toString()
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
        searchInput.setText(inputText)
    }

    companion object {
        private const val INPUT_TEXT = "INPUT_TEXT"
        private const val DEF_TEXT = ""
        private const val TAG = "SEARCH_TEST"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private fun searchTracks(text: String) {
        if(text.isNotEmpty()) {
            placeHolderImage.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
            refreshBtn.visibility = View.GONE
            trackList.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            iTunesService.search(text).enqueue(object : Callback<ItunesResponse> {
                override fun onResponse(call: Call<ItunesResponse>,
                                        response: Response<ItunesResponse>) {
                    progressBar.visibility = View.GONE
                    if(response.code() == 200) {
                        Log.d(TAG, "Sent text: $text Response is 200: ${response.code()}")
                        tracks.clear()
                        if(response.body()?.results?.isNotEmpty() == true) {
                            trackList.visibility = View.VISIBLE
                            Log.d(TAG, "Tracks got: ${response.body()?.results}")
                            tracks.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                            hideHistory()
                        } else {
                            showPlaceHolder(
                                getString(R.string.not_found),
                                R.drawable.empty_library,
                                false
                            )
                        }
                    } else {
                        Log.d(TAG, "Error: ${response.code()}")
                        showPlaceHolder(
                            getString(R.string.no_connection),
                            R.drawable.no_connection,
                            true
                        )
                    }
                }

                override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                    Log.e(TAG, "Error:", t)
                    showPlaceHolder(
                        getString(R.string.no_connection),
                        R.drawable.no_connection,
                        true
                    )
                }
            })
        }
    }

    private fun showPlaceHolder(text: String, imageId: Int, showRefresh: Boolean) {
        progressBar.visibility = View.GONE
        if (text.isNotEmpty()) {
            placeHolderImage.setImageResource(imageId)
            placeholderMessage.text = text

            placeHolderImage.visibility = View.VISIBLE
            placeholderMessage.visibility = View.VISIBLE
            refreshBtn.visibility = if (showRefresh) View.VISIBLE else View.GONE

            tracks.clear()
            adapter.notifyDataSetChanged()
            hideHistory()
        }
    }

    private fun hidePlaceholder() {
        placeHolderImage.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        refreshBtn.visibility = View.GONE
    }

    private fun updateHistoryView() {
        val historyTracks = searchHistory.getTrackHistory()
        if (searchInput.text.isEmpty() && historyTracks.isNotEmpty()) {
            historyAdapter.updateTracks(historyTracks)
            searchHistoryLayout.visibility = View.VISIBLE
            trackList.visibility = View.GONE
            hidePlaceholder()
        } else {
            hideHistory()
        }
    }

    private fun showHistoryIfEmpty() {
        if (searchInput.text.isNullOrEmpty()) {
            updateHistoryView()
        } else {
            hideHistory()
        }
    }
    private fun hideHistory() {
        searchHistoryLayout.visibility = View.GONE
        trackList.visibility = View.VISIBLE
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
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
}
