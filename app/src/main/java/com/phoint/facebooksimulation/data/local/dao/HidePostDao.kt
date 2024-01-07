package com.phoint.facebooksimulation.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phoint.facebooksimulation.data.local.model.HidePost

@Dao
interface HidePostDao {

    @Query("SELECT id_post FROM HidePost WHERE id_user_current = :userId")
    suspend fun getHiddenPostIds(userId: Long): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHidePost(hidePost: HidePost)
}