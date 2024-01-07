package com.phoint.facebooksimulation.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.phoint.facebooksimulation.data.local.model.Post

@Dao
interface PostDao {

    @Query("SELECT * FROM Post")
    suspend fun getAllPost(): List<Post>

    @Query("SELECT * FROM post")
    suspend fun getAllPosts(): List<Post>

    @Query("SELECT * FROM post WHERE id_post = :postId")
    suspend fun getPostById(postId: Long): Post

    @Insert
    suspend fun insertPost(post: Post)

    @Update
    suspend fun updatePost(post: Post)

    @Delete
    suspend fun deletePost(post: Post)

    @Query("select * from post where is_pinned")
    suspend fun getIsPinned(): Post

    @Query("UPDATE post SET likes = likes + 1 WHERE id_post = :postId")
    suspend fun incrementLikes(postId: Long)

    @Query("SELECT * FROM post WHERE user_id = :userId AND (is_avatar = 0 OR is_avatar = 1) ORDER BY id_post DESC")
    suspend fun getAllPosts(userId: Long): List<Post>

    @Query("SELECT * FROM post WHERE user_id = :userId order by id_post DESC")
    suspend fun getJoinDataPost(userId: Long): List<Post>

    @Query("SELECT * FROM post WHERE user_id = :userId and privacy_post = 'Công khai' order by id_post DESC")
    suspend fun getJoinDataPostPrivacy(userId: Long): List<Post>

    @Query("SELECT * FROM post WHERE user_id = :userId and id_post = :postId")
    suspend fun getPostDataByUser(userId: Long, postId: Long): Post

    @Query("UPDATE post SET date_time_post = :newDateTime")
    suspend fun updateDateTimePost(newDateTime: Long)
}