package com.phoint.facebooksimulation.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Comment {
    @PrimaryKey
    @ColumnInfo(name = "id_comment")
    var idComment: Long? = null

    @ColumnInfo(name = "user_id_comment")
    var userIdComment: Long? = null

    @ColumnInfo(name = "post_id_comment")
    var postIdComment: Long? = null

    @ColumnInfo(name = "name_user")
    var nameUser: String? = null

    @ColumnInfo(name = "content_comment")
    var contentComment: String? = null

    @ColumnInfo(name = "image_comment")
    var imageComment: String? = null

    @ColumnInfo(name = "timestamp")
    var timestamp: Long? = null

    @ColumnInfo(name = "likes")
    var likes: ArrayList<Like> = ArrayList()

    @ColumnInfo(name = "liked")
    var liked: Boolean? = null

    @ColumnInfo(name = "is_hidden")
    var hiddenUsers: ArrayList<User> = ArrayList()

    @ColumnInfo(name = "replies")
    var replies: ArrayList<Reply> = ArrayList()
}