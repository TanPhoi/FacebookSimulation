package com.phoint.facebooksimulation.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.phoint.facebooksimulation.data.local.model.SaveOtherUser

@Dao
interface SaveOtherUserDao {
    @Query("select * from SaveOtherUser where user_owner_id=:userOwnerId ORDER BY id DESC")
    suspend fun getAllSaveOtherOwnerId(userOwnerId: Long): List<SaveOtherUser>

    @Query("select * from SaveOtherUser where user_owner_id=:userOwnerId ORDER BY id DESC")
    suspend fun getSaveOtherOwnerId(userOwnerId: Long): SaveOtherUser

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSaveOtherUser(saveOtherUser: SaveOtherUser)

    @Update
    suspend fun updateSaveOtherUser(saveOtherUser: SaveOtherUser)

    @Delete
    suspend fun deleteSaveOtherUser(saveOtherUser: SaveOtherUser)

    @Query("SELECT * FROM SaveOtherUser WHERE user_owner_id =:idUserCurrent and id_user_other =:idUserOther and is_avatar = 1")
    suspend fun getSaveOtherUserByName(idUserCurrent: Long, idUserOther: Long): SaveOtherUser
}