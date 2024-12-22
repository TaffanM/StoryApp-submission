package com.taffan.storyapp

import com.taffan.storyapp.data.response.ListStoryItem

object DataDummy {
    fun generateDummyStoriesResponse(): List<ListStoryItem> {
        val items : MutableList<ListStoryItem> = arrayListOf()

        for (i in 0..100) {
            val story = ListStoryItem(
                id = i.toString(),
                name = "Name $i",
                description = "Description $i",
                photoUrl = "https://example.com/image$i.jpg",
                createdAt = "2023-08-01T00:00:00Z",
                lat = -6.2,
                lon = 106.8
            )
            items.add(story)
        }
        return items
    }
}