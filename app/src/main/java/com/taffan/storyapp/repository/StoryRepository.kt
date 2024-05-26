
import com.taffan.storyapp.data.api.ApiServiceStory
import com.taffan.storyapp.data.response.DetailStoryResponse
import com.taffan.storyapp.data.response.GetStoriesResponse
import com.taffan.storyapp.preferences.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StoryRepository private constructor(
    private val apiServiceStory: ApiServiceStory,
    private val userPreferences: UserPreferences
){

    suspend fun getStories(): GetStoriesResponse {
        return withContext(Dispatchers.IO) {
            apiServiceStory.getStories()
        }
    }

    suspend fun getDetail(id: String): DetailStoryResponse {
        return withContext(Dispatchers.IO) {
            apiServiceStory.getDetailStories(id)
        }
    }

    companion object {
        fun getInstance(
            apiServiceStory: ApiServiceStory,
            userPreferences: UserPreferences
        ) = StoryRepository(apiServiceStory, userPreferences)
    }
}