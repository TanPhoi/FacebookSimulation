package com.phoint.facebooksimulation.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.phoint.facebooksimulation.data.local.model.Reply

@Dao
interface ReplyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReply(reply: Reply)

    @Update
    suspend fun updateReply(reply: Reply)

    @Query("select * from Reply where comment_id =:commentId and post_id_comment =:postIdComment order by reply_id asc")
    suspend fun getJoinDataReplyForComment(commentId: Long, postIdComment: Long): Reply

    @Query("SELECT * FROM Reply")
    suspend fun getAllReply(): List<Reply>
}