package com.phoint.facebooksimulation.data.local.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Notification() : Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null

    @ColumnInfo(name = "user_id")
    var userId: Long? = null

    @ColumnInfo(name = "sender_id")
    var senderId: Long? = null

    @ColumnInfo(name = "post_id")
    var postId: Long? = null

    @ColumnInfo(name = "comment_id")
    var commentId: Long? = null

    @ColumnInfo(name = "notification_type")
    var notificationType: String? = null

    @ColumnInfo(name = "content")
    var content: String? = null

    @ColumnInfo(name = "profile_picture")
    var profilePicture: Int? = null

    @ColumnInfo(name = "timestamp")
    var timestamp: Long? = null

    @ColumnInfo(name = "status")
    var status: String? = null

    @ColumnInfo(name = "notification_id")
    var notificationId: Long? = null

    @ColumnInfo(name = "is_notification")
    var isNotification: Boolean? = null

    @ColumnInfo(name = "viewed")
    var viewed: Boolean? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readValue(Long::class.java.classLoader) as? Long
        userId = parcel.readValue(Long::class.java.classLoader) as? Long
        senderId = parcel.readValue(Long::class.java.classLoader) as? Long
        postId = parcel.readValue(Long::class.java.classLoader) as? Long
        commentId = parcel.readValue(Long::class.java.classLoader) as? Long
        notificationType = parcel.readString()
        content = parcel.readString()
        profilePicture = parcel.readValue(Int::class.java.classLoader) as? Int
        timestamp = parcel.readValue(Long::class.java.classLoader) as? Long
        status = parcel.readString()
        notificationId = parcel.readValue(Long::class.java.classLoader) as? Long
        isNotification = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        viewed = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeValue(userId)
        parcel.writeValue(senderId)
        parcel.writeValue(postId)
        parcel.writeValue(commentId)
        parcel.writeString(notificationType)
        parcel.writeString(content)
        parcel.writeValue(profilePicture)
        parcel.writeValue(timestamp)
        parcel.writeString(status)
        parcel.writeValue(notificationId)
        parcel.writeValue(isNotification)
        parcel.writeValue(viewed)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Notification> {
        override fun createFromParcel(parcel: Parcel): Notification {
            return Notification(parcel)
        }

        override fun newArray(size: Int): Array<Notification?> {
            return arrayOfNulls(size)
        }
    }
}