package edu.oregonstate.cs492.githubsearch

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.squareup.moshi.JsonAdapter
import edu.oregonstate.cs492.githubsearch.data.GitHubRepo
import edu.oregonstate.cs492.githubsearch.data.GitHubService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.squareup.moshi.Moshi
import edu.oregonstate.cs492.githubsearch.data.GitHubRepoSearchResults

class MainActivity : AppCompatActivity() {
    private val githubService = GitHubService.create()
    private val repoListAdapter = GitHubRepoListAdapter()

    private lateinit var searchResultsListRV: RecyclerView
    private lateinit var searchErrorTV: TextView
    private lateinit var loadingIndicator: CircularProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val searchBoxET: EditText = findViewById(R.id.et_search_box)
        val searchBtn: Button = findViewById(R.id.btn_search)

        searchErrorTV = findViewById(R.id.tv_search_error)
        loadingIndicator = findViewById(R.id.loading_indicator)

        searchResultsListRV = findViewById(R.id.rv_search_results)
        searchResultsListRV.layoutManager = LinearLayoutManager(this)
        searchResultsListRV.setHasFixedSize(true)

        searchResultsListRV.adapter = repoListAdapter

        searchBtn.setOnClickListener {
            val query = searchBoxET.text.toString()
            if (!TextUtils.isEmpty(query)) {
                doGitHubSearch(query)
                searchResultsListRV.scrollToPosition(0)
            }
        }
    }

    private fun doGitHubSearch(query: String) {
        searchResultsListRV.visibility = View.INVISIBLE
        searchErrorTV.visibility = View.INVISIBLE
        loadingIndicator.visibility = View.VISIBLE
        githubService.searchRepositories(query).enqueue(object : Callback<GitHubRepoSearchResults> {
            override fun onResponse(call: Call<GitHubRepoSearchResults?>, response: Response<GitHubRepoSearchResults?>) {
                Log.d("MainActivity", "Status code: ${response.code()}")
                Log.d("MainActivity", "Response body: ${response.body()}")
                if (response.isSuccessful) {
//                    val moshi = Moshi.Builder().build()
//                    val jsonAdapter: JsonAdapter<GitHubRepoSearchResults> = moshi.adapter(
//                        GitHubRepoSearchResults::class.java
//                    )
//                    val searchResults = jsonAdapter.fromJson(response.body() ?: "")
                    repoListAdapter.updateRepoList(response.body()?.items)
                    searchResultsListRV.visibility = View.VISIBLE
                    searchErrorTV.visibility = View.INVISIBLE
                    loadingIndicator.visibility = View.INVISIBLE
                } else {
                    searchErrorTV.text = getString(
                        R.string.search_error,
                        response.errorBody()?.string() ?: "unknown error")
                    searchResultsListRV.visibility = View.INVISIBLE
                    searchErrorTV.visibility = View.VISIBLE
                    loadingIndicator.visibility = View.INVISIBLE
                }
            }

            override fun onFailure(call: Call<GitHubRepoSearchResults?>, t: Throwable) {
                Log.e("MainActivity", "Error making API call: ${t.message}")
                searchErrorTV.text = getString(R.string.search_error, t.message)
                searchResultsListRV.visibility = View.INVISIBLE
                searchErrorTV.visibility = View.VISIBLE
                loadingIndicator.visibility = View.INVISIBLE
            }
        })
    }
}