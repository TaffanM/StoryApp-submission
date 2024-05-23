package com.taffan.storyapp.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.taffan.storyapp.R
import com.taffan.storyapp.data.response.ListStoryItem
import com.taffan.storyapp.databinding.ActivityLoginBinding
import com.taffan.storyapp.databinding.ActivityMainBinding
import com.taffan.storyapp.preferences.UserPreferences
import com.taffan.storyapp.preferences.dataStore
import com.taffan.storyapp.ui.adapter.StoryAdapter
import com.taffan.storyapp.ui.model.LoginViewModel
import com.taffan.storyapp.ui.model.StoryViewModel
import com.taffan.storyapp.ui.model.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = ""
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this@MainActivity)
        val viewModel: StoryViewModel by viewModels {
            factory
        }

        userPreferences = UserPreferences.getInstance(dataStore)

        storyAdapter = StoryAdapter()
        storyAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(story: ListStoryItem) {
                showDetailedStory(story)
            }
        })

        viewModel.stories.observe(this) {stories ->
            setStoryData(stories)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        binding.buttonAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, AddActivity::class.java)
            startActivity(intent)
        }

        lifecycleScope.launch {
            val user = userPreferences.getUser().first()
            user!!.token.let { viewModel.fetchStories(it) }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                logout()
                true
            }
            R.id.language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun logout() {
        lifecycleScope.launch {
            userPreferences.clearUser()
            val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
            loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(loginIntent)
            finish()
        }
    }

    private fun setStoryData(stories: List<ListStoryItem>) {
        storyAdapter.submitList(stories)
        binding.rvStory.adapter = storyAdapter
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showDetailedStory(story: ListStoryItem) {

    }

}