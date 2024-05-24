package com.taffan.storyapp.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.taffan.storyapp.R
import com.taffan.storyapp.databinding.FragmentDetailBinding
import com.taffan.storyapp.preferences.UserPreferences
import com.taffan.storyapp.preferences.dataStore
import com.taffan.storyapp.ui.model.DetailStoryViewModel
import com.taffan.storyapp.ui.model.ViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.taffan.storyapp.data.response.Story
import com.taffan.storyapp.ui.model.StoryViewModel

class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private lateinit var userPreferences: UserPreferences
    private lateinit var factory: ViewModelFactory
    private lateinit var viewModel: DetailStoryViewModel


    override fun onAttach(context: Context) {
        super.onAttach(context)
        factory = ViewModelFactory.getInstance(requireContext())
        viewModel = ViewModelProvider(this, factory)[DetailStoryViewModel::class.java]
        userPreferences = UserPreferences.getInstance(requireContext().dataStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.fade)
        val storyId = arguments?.getString("id")

        viewModel.story.observe(viewLifecycleOwner) {detail ->
            detail?.let { displayStoryDetails(it) }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        if (storyId != null) {

            viewModel.getStory(storyId)
        }
        return binding.root
    }
    private fun displayStoryDetails(story: Story?) {
        if (story != null) {
            binding.tvDetailName.text = story.name
            binding.tvDetailDescription.text = story.description
            Glide.with(this)
                .load(story.photoUrl)
                .into(binding.ivDetailPhoto)
        } else {
            // Handle the case where the story is null
            binding.tvDetailName.text = "N/A"
            binding.tvDetailDescription.text = "N/A"
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {

    }
}