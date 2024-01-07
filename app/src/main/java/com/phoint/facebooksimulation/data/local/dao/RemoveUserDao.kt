package com.phoint.facebooksimulation.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.phoint.facebooksimulation.data.local.model.RemoveUser

@Dao
interface RemoveUserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRemoveUser(removeUser: RemoveUser)

    @Update
    suspend fun updateRemoveUser(removeUser: RemoveUser)

    @Query("select * from RemoveUser where user_id_removed =:userIdRemoved")
    suspend fun getRemoveUserById(userIdRemoved: Long): RemoveUser
}