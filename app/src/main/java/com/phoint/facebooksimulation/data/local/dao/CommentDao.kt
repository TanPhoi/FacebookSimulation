package com.phoint.facebooksimulation.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.phoint.facebooksimulation.data.local.model.Comment

@Dao
interface CommentDao {
    @Query("SELECT * FROM comment WHERE post_id_comment = :postId order by id_comment DESC")
    suspend fun getJoinDataComment(postId : Long) : List<Comment>

    @Query("SELECT * FROM comment WHERE id_comment = :commentId")
    suspend fun getIdComment(commentId : Long) : Comment

    @Query("SELECT * FROM comment")
    suspend fun getAllComment() : List<Comment>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertComment(comment: Comment)

    @Update
    suspend fun updateComment(comment: Comment)

    @Update
    suspend fun updateComment(comment: List<Comment>)
}