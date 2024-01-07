package com.phoint.facebooksimulation.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Report {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null

    @ColumnInfo(name = "user_id")
    var userId: Long? = null

    @ColumnInfo(name = "comment_id")
    var commentId: Long? = null

    @ColumnInfo(name = "post_id")
    var postId: Long? = null

    @ColumnInfo(name = "content")
    var content: String? = null

    @ColumnInfo(name = "reason")
    var reason: String? = null

    @ColumnInfo(name = "timestamp")
    var timestamp: Long? = null
}