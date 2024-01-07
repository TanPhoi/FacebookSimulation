package com.phoint.facebooksimulation.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Story {
    @PrimaryKey
    @ColumnInfo(name = "story_id")
    var storyId: Long? = null

    @ColumnInfo(name = "name_story_id")
    var nameStory: String? = null

    @ColumnInfo(name = "privacy")
    var privacy: String? = null
}