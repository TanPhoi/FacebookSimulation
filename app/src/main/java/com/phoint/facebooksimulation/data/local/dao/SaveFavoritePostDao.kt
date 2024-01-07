package com.phoint.facebooksimulation.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.phoint.facebooksimulation.data.local.model.SaveFavoritePost

@Dao
interface SaveFavoritePostDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSaveFavoritePost(saveFavoritePost: SaveFavoritePost)

    @Delete
    suspend fun deleteSaveFavoritePost(saveFavoritePost: SaveFavoritePost)

    @Update
    suspend fun updateSaveFavoritePost(saveFavoritePost: SaveFavoritePost)

    @Query("select * from SaveFavoritePost where user_owner_id=:userOwnerId and post_owner_id=:postOwnerId")
    suspend fun getAllSaveFavoritePost(userOwnerId: Long, postOwnerId: Long): SaveFavoritePost

    @Query("select * from SaveFavoritePost where post_owner_id=:postOwnerId")
    suspend fun getAllDataByPost(postOwnerId: Long): SaveFavoritePost

    @Query("select * from SaveFavoritePost where user_owner_id=:userOwnerId")
    suspend fun getSavePostDataByUser(userOwnerId: Long): List<SaveFavoritePost>

    @Query("select * from SaveFavoritePost")
    suspend fun getAllSavePosts(): List<SaveFavoritePost>

}