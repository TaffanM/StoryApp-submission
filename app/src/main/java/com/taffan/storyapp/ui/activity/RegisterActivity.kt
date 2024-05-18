package com.taffan.storyapp.ui.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.taffan.storyapp.R
import com.taffan.storyapp.data.api.ApiService
import com.taffan.storyapp.databinding.ActivityRegisterBinding
import com.taffan.storyapp.repository.RegisterLoginRepository
import com.taffan.storyapp.ui.model.RegisterViewModel
import com.taffan.storyapp.ui.model.ViewModelFactory
import com.taffan.storyapp.utils.AppExecutors

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance()
        val viewModel: RegisterViewModel by viewModels {
            factory
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString().trim()
            val email = binding.edRegisterEmail.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString().trim()
            val confirmPassword = binding.edConfirmRegisterPassword.text.toString().trim()

            if (password == confirmPassword) {
                viewModel.register(name, email, password)
            } else {
                Toast.makeText(this, "Your password and confirm password must be the same", Toast.LENGTH_SHORT).show()
            }

        }

        viewModel.registerResponse.observe(this) { response ->
            if (!response.error) {
                showSuccesDialog()
            } else {
                showErrorDialog(response.message)
            }
        }

        viewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

        onBackPressedCallback()
    }

    private fun showSuccesDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_register_success)
        dialog.setCancelable(false)

        val btnOk = dialog.findViewById<Button>(R.id.btn_ok)
        btnOk.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        dialog.show()
    }

    private fun showErrorDialog(errorMessage: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_register_failed)
        dialog.setCancelable(false)

        val errorText = dialog.findViewById<TextView>(R.id.tv_error)
        errorText.text = errorMessage

        val btnOk = dialog.findViewById<Button>(R.id.btn_ok)
        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun onBackPressedCallback() {
        val dispatcher = onBackPressedDispatcher

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        dispatcher.addCallback(this, onBackPressedCallback)

    }
}