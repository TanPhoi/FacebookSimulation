package com.phoint.facebooksimulation.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Reply {
    @PrimaryKey
    @ColumnInfo(name = "reply_id")
    var replyId: Long? = null

    @ColumnInfo(name = "comment_id")
    var commentId: Long = 0

    @ColumnInfo(name = "user_id_comment")
    var userIdComment: Long? = null

    @ColumnInfo(name = "post_id_comment")
    var postIdComment: Long? = null

    @ColumnInfo(name = "user_name")
    var userName: String? = null

    @ColumnInfo(name = "content_reply")
    var contentReply: String? = null

    @ColumnInfo(name = "image_reply")
    var imageReply: String? = null

    @ColumnInfo(name = "times_tamp")
    var timestamp: Long? = null

    @ColumnInfo(name = "liked")
    var liked: Boolean? = null

    @ColumnInfo(name = "likes")
    var likes: ArrayList<Like> = ArrayList()

    @ColumnInfo(name = "is_hidden")
    var hiddenUsers: ArrayList<User> = ArrayList()
}
