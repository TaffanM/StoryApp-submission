package com.taffan.storyapp.ui.model

import StoryRepository
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.taffan.storyapp.DataDummy
import com.taffan.storyapp.MainDispatcherRule
import com.taffan.storyapp.data.paging.StoryPagingSource
import com.taffan.storyapp.data.response.ListStoryItem
import com.taffan.storyapp.getOrAwaitValue
import com.taffan.storyapp.ui.adapter.StoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    
    

    @Test
    fun `when getStoriesPaging Should Not Null and Return Success`() = runBlocking {
        val dummyStories = DataDummy.generateDummyStoriesResponse()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStories)
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data

        Mockito.`when`(storyRepository.getStoriesPaging()).thenReturn(expectedStory)
        val storyViewModel = StoryViewModel(storyRepository)
        val actualStory: PagingData<ListStoryItem> = storyViewModel.storiesPaging.getOrAwaitValue()


        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])

    }

    @Test
    fun `when getStoriesPaging Should Return Empty`() = runBlocking {
        val data: PagingData<ListStoryItem> = PagingData.empty()
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data

        Mockito.`when`(storyRepository.getStoriesPaging()).thenReturn(expectedStory)
        val storyViewModel = StoryViewModel(storyRepository)
        val actualStory: PagingData<ListStoryItem> = storyViewModel.storiesPaging.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)
        Assert.assertNotNull(differ.snapshot())
        Assert.assertTrue(differ.snapshot().isEmpty())

    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}

        override fun onRemoved(position: Int, count: Int) {}

        override fun onMoved(fromPosition: Int, toPosition: Int) {}

        override fun onChanged(position: Int, count: Int, payload: Any?) {}

    }
}