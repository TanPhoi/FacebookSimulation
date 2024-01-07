package com.phoint.facebooksimulation.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.phoint.facebooksimulation.data.local.model.Notification

@Dao
interface NotificationDao {
    @Query("SELECT * FROM Notification WHERE user_id = :userId AND sender_id != :currentUserId ORDER BY id DESC")
    suspend fun getAllNotificationById(userId: Long, currentUserId: Long): List<Notification>

    @Query("SELECT * FROM Notification where sender_id =:senderId and user_id =:userId and post_id =:postId order by id DESC")
    suspend fun getNotificationById(senderId: Long, userId: Long, postId: Long): Notification

    @Query("SELECT * FROM Notification where sender_id =:senderId and user_id =:userId and post_id =:postId and comment_id =:commentId order by id DESC")
    suspend fun getNotificationCommentById(
        senderId: Long,
        userId: Long,
        postId: Long,
        commentId: Long
    ): Notification

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotification(notification: Notification)

    @Update
    suspend fun updateNotification(notification: Notification)

    @Delete
    suspend fun deleteNotification(notification: Notification)
}