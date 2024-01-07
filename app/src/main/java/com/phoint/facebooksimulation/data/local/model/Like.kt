package com.phoint.facebooksimulation.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Like {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "likeId")
    var likeId: Long? = null

    @ColumnInfo(name = "like_user_id")
    var likeUserId: Long? = null

    @ColumnInfo(name = "like_post_id")
    var likePostId: Long? = null

    @ColumnInfo(name = "nameUser")
    var nameUser: String? = null
}