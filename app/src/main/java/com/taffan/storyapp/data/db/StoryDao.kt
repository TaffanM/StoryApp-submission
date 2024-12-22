package com.taffan.storyapp.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taffan.storyapp.data.response.ListStoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<ListStoryItem>)

    @Query("SELECT * FROM stories")
    fun getAllStories(): PagingSource<Int, ListStoryItem>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}