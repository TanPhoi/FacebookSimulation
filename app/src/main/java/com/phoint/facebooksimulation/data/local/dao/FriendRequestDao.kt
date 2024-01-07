package com.phoint.facebooksimulation.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.phoint.facebooksimulation.data.local.model.FriendRequest

@Dao
interface FriendRequestDao {
    @Query("SELECT * FROM friend_request WHERE receiver_id = :id or sender_id = :id and status == 1")
    suspend fun getAllFriend(id: Long): List<FriendRequest>

    @Query("SELECT * FROM friend_request WHERE receiver_id = :id or sender_id = :id and status == 1")
    suspend fun getAllFriendCurrentUser(id: Long): List<FriendRequest>

    @Query("SELECT * FROM friend_request WHERE sender_id = :senderId and receiver_id =:receiverId")
    suspend fun getFriendRequestById(senderId: Long, receiverId: Long): FriendRequest

    @Query("SELECT * FROM friend_request WHERE sender_id = :senderId and receiver_id =:receiverId or sender_id =:receiverId and receiver_id =:senderId")
    suspend fun getFriendRequestByIdDelete(senderId: Long, receiverId: Long): FriendRequest

    @Query("SELECT * FROM friend_request WHERE sender_id = :senderId and receiver_id =:receiverId or sender_id =:receiverId and receiver_id =:senderId")
    suspend fun getFriendOfUser(senderId: Long, receiverId: Long): List<FriendRequest>

    @Query("SELECT * FROM friend_request WHERE sender_id = :senderId and receiver_id =:receiverId")
    suspend fun getFriendRequestByIdReceiver(senderId: Long, receiverId: Long): FriendRequest

    @Query("SELECT * FROM friend_request WHERE sender_id = :senderId")
    suspend fun getSenderId(senderId: Long): FriendRequest

    @Query("SELECT * FROM friend_request WHERE receiver_id = :receiverId")
    suspend fun getReceiverId(receiverId: Long): FriendRequest

    @Query("SELECT * FROM friend_request WHERE receiver_id = :receiverId")
    suspend fun getAllReceiverId(receiverId: Long): List<FriendRequest>

    @Query("SELECT * FROM friend_request WHERE sender_id = :senderId and receiver_id =:receiverId")
    suspend fun getFriendRequest(senderId: Long, receiverId: Long): FriendRequest

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFriendRequest(friendRequest: FriendRequest)

    @Update
    suspend fun updateFriendRequest(friendRequest: FriendRequest)

    @Delete
    suspend fun deleteFriendRequest(friendRequest: FriendRequest)

    @Query("SELECT * FROM friend_request WHERE sender_id = :userId OR receiver_id = :userId")
    suspend fun getFilteredFriends(userId: Long): List<FriendRequest>
}