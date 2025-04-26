package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
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

    private val iTunesbaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesbaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ItunesApi::class.java)

    private val tracks = ArrayList<Track>()
    private val adapter = TrackAdapter(tracks)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val recycler = findViewById<RecyclerView>(R.id.trackList)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

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
            tracks.clear()
            adapter.notifyDataSetChanged()
        }

        val searchInputTextWatcher = object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchInputClear.visibility = inputClearVisibility(s)
                inputText = searchInput.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}

        }

        searchInput.addTextChangedListener(searchInputTextWatcher)

        searchInput.setOnEditorActionListener { _, actionId, _, ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
            var text = searchInput.text.toString()
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
        Log.d(TAG, "В onSaveInstanceState сохранен текст: $inputText")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(INPUT_TEXT, DEF_TEXT)
        searchInput.setText(inputText)
        Log.d(TAG, "В onRestoreInstanceState восстановлен текст: $inputText")
    }

    companion object {
        private const val INPUT_TEXT = "INPUT_TEXT"
        private const val DEF_TEXT = ""
        private const val TAG = "SEARCH_TEST"
    }

    private fun searchTracks(text: String) {
        if(text.isNotEmpty()) {
            iTunesService.search(text).enqueue(object : Callback<ItunesResponse> {
                override fun onResponse(call: Call<ItunesResponse>,
                                        response: Response<ItunesResponse>) {
                    if(response.code() == 200) {
                        Log.d(TAG, "Sent text: $text Response is 200: ${response.code()}")
                        tracks.clear()
                        if(response.body()?.results?.isNotEmpty() == true) {
                            Log.d(TAG, "Tracks got: ${response.body()?.results}")
                            tracks.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }

                override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}