package com.phoint.facebooksimulation.data.local.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Post() : Parcelable {
    @PrimaryKey
    @ColumnInfo(name = "id_post")
    var idPost: Long? = null

    @ColumnInfo(name = "user_id")
    var userIdPost: Long? = null

    @ColumnInfo(name = "user_id_share")
    var userIdPostShare: Long? = null

    @ColumnInfo(name = "content_post")
    var contentPost: String? = null

    @ColumnInfo(name = "date_time_post")
    var dateTimePost: Long? = null

    @ColumnInfo(name = "src_post")
    var srcPost: String? = null

    @ColumnInfo(name = "likes")
    var likes: ArrayList<Like> = ArrayList()

    @ColumnInfo(name = "liked")
    var liked: Boolean? = null

    @ColumnInfo(name = "privacy_post")
    var privacyPost: String? = null // permission: "public", "friends", "private"

    @ColumnInfo(name = "is_pinned")
    var isPinned: Boolean = false

    @ColumnInfo(name = "is_avatar")
    var isAvatar: Boolean = false

    @ColumnInfo(name = "comments")
    var comments: ArrayList<Comment> = ArrayList()

    @ColumnInfo(name = "shared_from_post_id")
    var sharedFromPostId: Long? = null

    @ColumnInfo(name = "is_shared")
    var isShared: Boolean = false

    constructor(parcel: Parcel) : this() {
        idPost = parcel.readValue(Long::class.java.classLoader) as? Long
        userIdPost = parcel.readValue(Long::class.java.classLoader) as? Long
        contentPost = parcel.readString()
        dateTimePost = parcel.readValue(Long::class.java.classLoader) as? Long
        srcPost = parcel.readString()
        liked = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        privacyPost = parcel.readString()
        isPinned = parcel.readByte() != 0.toByte()
        isAvatar = parcel.readByte() != 0.toByte()
        sharedFromPostId = parcel.readValue(Long::class.java.classLoader) as? Long
        isShared = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(idPost)
        parcel.writeValue(userIdPost)
        parcel.writeString(contentPost)
        parcel.writeValue(dateTimePost)
        parcel.writeString(srcPost)
        parcel.writeValue(liked)
        parcel.writeString(privacyPost)
        parcel.writeByte(if (isPinned) 1 else 0)
        parcel.writeByte(if (isAvatar) 1 else 0)
        parcel.writeValue(sharedFromPostId)
        parcel.writeByte(if (isShared) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}