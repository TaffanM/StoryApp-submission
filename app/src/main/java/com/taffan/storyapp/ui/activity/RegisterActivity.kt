package com.taffan.storyapp.ui.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.taffan.storyapp.R
import com.taffan.storyapp.databinding.ActivityRegisterBinding
import com.taffan.storyapp.ui.model.RegisterViewModel
import com.taffan.storyapp.ui.model.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this@RegisterActivity)
        val viewModel: RegisterViewModel by viewModels {
            factory
        }

        binding.btnRegister.setOnClickListener {
            val nameEmpty = binding.edRegisterName.checkEditTextEmpty()
            val emailEmpty = binding.edRegisterEmail.checkEditTextEmpty()
            val passwordEmpty = binding.edRegisterPassword.checkEditTextEmpty()
            val confirmPasswordEmpty = binding.edConfirmRegisterPassword.checkEditTextEmpty()
            val name = binding.edRegisterName.text.toString().trim()
            val email = binding.edRegisterEmail.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString().trim()
            val confirmPassword = binding.edConfirmRegisterPassword.text.toString().trim()

            if (!nameEmpty && !emailEmpty && !passwordEmpty && !confirmPasswordEmpty) {
                if (password == confirmPassword) {
                    viewModel.isLoading.observe(this) {
                        showLoading(it)
                    }
                    viewModel.register(name, email, password)
                } else {
                    Toast.makeText(this, "Your password and confirm password must be the same", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
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
            showLoading(false)
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
        showLoading(false)
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
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        dispatcher.addCallback(this, onBackPressedCallback)

    }
}