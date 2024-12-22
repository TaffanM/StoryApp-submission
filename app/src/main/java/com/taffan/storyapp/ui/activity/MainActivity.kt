package com.taffan.storyapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.taffan.storyapp.R
import com.taffan.storyapp.data.response.ListStoryItem
import com.taffan.storyapp.databinding.ActivityMainBinding
import com.taffan.storyapp.preferences.UserPreferences
import com.taffan.storyapp.preferences.dataStore
import com.taffan.storyapp.ui.adapter.StoryAdapter
import com.taffan.storyapp.ui.fragment.DetailFragment
import com.taffan.storyapp.ui.model.StoryViewModel
import com.taffan.storyapp.ui.model.StoryViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var userPreferences: UserPreferences
    private lateinit var factory: StoryViewModelFactory
    private lateinit var viewModel: StoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = ""

        userPreferences = UserPreferences.getInstance(dataStore)

        factory = StoryViewModelFactory.getInstance(applicationContext)
        viewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]

        storyAdapter = StoryAdapter()
        storyAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(story: ListStoryItem) {
                showDetailedStory(story)
            }
        })

        viewModel.storiesPaging.observe(this) { pagingData ->
            lifecycleScope.launch {
                storyAdapter.submitData(pagingData)
            }
        }

        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.error.observe(this) {
            binding.tvErrorMessage.text = it
        }

        binding.buttonAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, AddActivity::class.java)
            startActivity(intent)
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
            R.id.maps -> {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.storiesPaging.observe(this) { pagingData ->
            lifecycleScope.launch {
                storyAdapter.submitData(pagingData)
            }
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            userPreferences.clearUser()
            Toast.makeText(this@MainActivity, "Successfully Logout", Toast.LENGTH_SHORT).show()
            val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
            loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(loginIntent)
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showDetailedStory(story: ListStoryItem) {
        val fragment = DetailFragment().apply {
            arguments = Bundle().apply {
                putString("id", story.id)
            }
        }


        supportFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}