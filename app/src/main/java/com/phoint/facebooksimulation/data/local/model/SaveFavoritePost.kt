package com.phoint.facebooksimulation.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class SaveFavoritePost {
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Long? = null

    @ColumnInfo(name = "user_owner_id")
    var userOwnerId: Long? = null

    @ColumnInfo(name = "post_owner_id")
    var postOwnerId: Long? = null

    @ColumnInfo(name = "timestamp")
    var timestamp: Long? = null

    @ColumnInfo(name = "save_post")
    var savePost: Post? = null
}