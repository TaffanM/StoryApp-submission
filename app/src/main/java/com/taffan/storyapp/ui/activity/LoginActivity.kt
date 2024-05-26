package com.taffan.storyapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import com.taffan.storyapp.databinding.ActivityLoginBinding
import com.taffan.storyapp.preferences.UserPreferences
import com.taffan.storyapp.preferences.dataStore
import com.taffan.storyapp.ui.model.LoginViewModel
import com.taffan.storyapp.ui.model.ViewModelFactory
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var userPreferences: UserPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this@LoginActivity)
        val viewModel: LoginViewModel by viewModels {
            factory
        }

        userPreferences = UserPreferences.getInstance(dataStore)

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLogin.setOnClickListener {
            val emailEmpty = binding.edLoginEmail.checkEditTextEmpty()
            val passwordEmpty = binding.edLoginPassword.checkEditTextEmpty()
            val emailValid = binding.edLoginEmail.error == null
            val passwordValid = binding.edLoginPassword.error == null

            if (!emailEmpty && !passwordEmpty) {
                if (emailValid && passwordValid) {
                    val email = binding.edLoginEmail.text.toString().trim()
                    val password = binding.edLoginPassword.text.toString().trim()
                    viewModel.isLoading.observe(this) {
                        showLoading(it)
                    }
                    viewModel.login(email, password)
                } else {
                    if (!emailValid) {
                        Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                    } else if (!passwordValid) {
                        Toast.makeText(this, "Your password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }


        }

        viewModel.loginResponse.observe(this) {response ->
            if (!response.error) {
                lifecycleScope.launch {
                    userPreferences.saveUser(response.loginResult)
                }
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.error.observe(this) {errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            showLoading(false)
        }



        onBackPressedCallback()

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun onBackPressedCallback() {
        val dispatcher = onBackPressedDispatcher

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }

        dispatcher.addCallback(this, onBackPressedCallback)

    }
}