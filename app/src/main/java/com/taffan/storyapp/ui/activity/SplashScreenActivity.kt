package com.taffan.storyapp.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.taffan.storyapp.R
import com.taffan.storyapp.preferences.UserPreferences
import com.taffan.storyapp.preferences.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var userPreferences: UserPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferences = UserPreferences.getInstance(dataStore)

        lifecycleScope.launch {
            val user = userPreferences.getUser().first()
            if (user != null) {
                val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            finish()
        }
    }
}