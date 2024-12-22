package com.taffan.storyapp.ui.activity

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.taffan.storyapp.R
import com.taffan.storyapp.data.response.ListStoryItem
import com.taffan.storyapp.databinding.ActivityMapsBinding
import com.taffan.storyapp.ui.model.MapsViewModel
import com.taffan.storyapp.ui.model.StoryViewModelFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var factory: StoryViewModelFactory
    private lateinit var viewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = StoryViewModelFactory.getInstance(applicationContext)
        viewModel = ViewModelProvider(this, factory)[MapsViewModel::class.java]

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel.stories.observe(this) {stories ->
            updateMapWithStories(stories)
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.error.observe(this) {error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }

        }



        viewModel.fetchStoriesWithLocation()

        onBackPressedCallback()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        setMapStyle()
    }

    private fun updateMapWithStories(stories: List<ListStoryItem>) {
        stories.forEach { data ->
            val latLng = LatLng(data.lat, data.lon)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(data.name)
                    .snippet(data.description))
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this@MapsActivity, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    private fun onBackPressedCallback() {
        val dispatcher = onBackPressedDispatcher

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@MapsActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        dispatcher.addCallback(this, onBackPressedCallback)

    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}