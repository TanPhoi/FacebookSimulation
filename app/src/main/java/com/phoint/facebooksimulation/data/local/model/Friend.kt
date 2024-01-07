package com.phoint.facebooksimulation.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Friend {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "friend_id")
    var friendId: Long? = null

    @ColumnInfo(name = "user_owner_id")
    var userOwnerId: Long? = null

    @ColumnInfo(name = "friend_user_id")
    var friendUserId: Long? = null // ID của bạn bè

    @ColumnInfo(name = "user_friend")
    var userFriend: User? = null
}