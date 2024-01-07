package com.phoint.facebooksimulation.data.local.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend_request")
class FriendRequest() : Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "request_id")
    var requestId: Long? = null

    @ColumnInfo(name = "sender_id")
    var senderId: Long? = null

    @ColumnInfo(name = "receiver_id")
    var receiverId: Long? = null

    @ColumnInfo(name = "time")
    var time: Long? = null

    @ColumnInfo(name = "is_notification")
    var isNotification: Boolean? = false

    @ColumnInfo(name = "status")
    var status: Int = FRIEND_REQUEST_PENDING // Trạng thái lời mời (đã gửi, chấp nhận, từ chối...)

    constructor(parcel: Parcel) : this() {
        requestId = parcel.readValue(Long::class.java.classLoader) as? Long
        senderId = parcel.readValue(Long::class.java.classLoader) as? Long
        receiverId = parcel.readValue(Long::class.java.classLoader) as? Long
        time = parcel.readValue(Long::class.java.classLoader) as? Long
        isNotification = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        status = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(requestId)
        parcel.writeValue(senderId)
        parcel.writeValue(receiverId)
        parcel.writeValue(time)
        parcel.writeValue(isNotification)
        parcel.writeInt(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FriendRequest> {
        const val FRIEND_REQUEST_PENDING = 0
        const val FRIEND_REQUEST_ACCEPTED = 1
        const val FRIEND_REQUEST_REJECTED = 2
        override fun createFromParcel(parcel: Parcel): FriendRequest {
            return FriendRequest(parcel)
        }

        override fun newArray(size: Int): Array<FriendRequest?> {
            return arrayOfNulls(size)
        }
    }


}