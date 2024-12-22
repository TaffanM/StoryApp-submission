
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.taffan.storyapp.data.api.ApiServiceStory
import com.taffan.storyapp.data.db.StoryDatabase
import com.taffan.storyapp.data.db.StoryRemoteMediator
import com.taffan.storyapp.data.response.DetailStoryResponse
import com.taffan.storyapp.data.response.GetStoriesResponse
import com.taffan.storyapp.data.response.ListStoryItem
import com.taffan.storyapp.preferences.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StoryRepository private constructor(
    private val storyDatabase: StoryDatabase,
    private val apiServiceStory: ApiServiceStory,
    private val userPreferences: UserPreferences
){

    @OptIn(ExperimentalPagingApi::class)
    fun getStoriesPaging() : LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiServiceStory),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).liveData
    }



    suspend fun getStoriesWithLocation(): GetStoriesResponse {
        return withContext(Dispatchers.IO) {
            apiServiceStory.getStoriesWithLocation()
        }
    }

    suspend fun getDetail(id: String): DetailStoryResponse {
        return withContext(Dispatchers.IO) {
            apiServiceStory.getDetailStories(id)
        }
    }

    companion object {
        fun getInstance(
            storyDatabase: StoryDatabase,
            apiServiceStory: ApiServiceStory,
            userPreferences: UserPreferences
        ) = StoryRepository(storyDatabase, apiServiceStory, userPreferences)
    }
}