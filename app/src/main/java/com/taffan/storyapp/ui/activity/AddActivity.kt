package com.taffan.storyapp.ui.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.taffan.storyapp.databinding.ActivityAddBinding
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.taffan.storyapp.R
import com.taffan.storyapp.data.api.ApiConfigStory
import com.taffan.storyapp.data.response.FileUploadResponse
import com.taffan.storyapp.preferences.UserPreferences
import com.taffan.storyapp.preferences.dataStore
import com.taffan.storyapp.ui.activity.CameraActivity.Companion.CAMERAX_RESULT
import com.taffan.storyapp.utils.reduceFileImage
import com.taffan.storyapp.utils.uriToFile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private lateinit var userPreferences: UserPreferences
    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences.getInstance(dataStore)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        if (!locationPermissionsGranted()) {
            requestPermissionLauncher.launch(LOCATION_PERMISSION)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCameraX() }
        binding.uploadButton.setOnClickListener {
            uploadImage()

        }

    }

    private fun startCameraX() {
        val intent = Intent(this@AddActivity, CameraActivity::class.java)
       launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image file", "showImage : ${imageFile.path}")
            val desc = binding.edAddDescription.text.toString().trim()
            showLoading(true)

            val requestBody = desc.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)

            if (binding.locationSwitch.isChecked) {
                getCurrentLocation { location ->
                    val lat = location.latitude.toString().toRequestBody("text/plain".toMediaType())
                    val lon = location.longitude.toString().toRequestBody("text/plain".toMediaType())
                    sendUploadRequest(multipartBody, requestBody, lat, lon)
                }
            } else {
                sendUploadRequest(multipartBody, requestBody, null, null)
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun sendUploadRequest(
        multipartBody: MultipartBody.Part,
        requestBody: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ) {
        lifecycleScope.launch {
            val token = userPreferences.getUser().first()?.token
            val apiService = token?.let { ApiConfigStory.getApiService(userPreferences) }
            try {
                val call = apiService?.uploadImage(multipartBody, requestBody, lat, lon)
                showToast(call!!.message)
                showLoading(false)
                val intent = Intent(this@AddActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
                showToast(errorResponse.message)
                showLoading(false)
            }
        }
    }

    private fun getCurrentLocation(onLocationReceived: (Location) -> Unit) {
        if (ActivityCompat.checkSelfPermission(this, LOCATION_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(LOCATION_PERMISSION)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let { onLocationReceived(it) }
        }
    }


    private fun locationPermissionsGranted() =
        ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }

        }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    }


}