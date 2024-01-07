package com.phoint.facebooksimulation.data.local.dao

import androidx.room.*
import com.phoint.facebooksimulation.data.local.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM user")
    suspend fun getAllUserById(): List<User>

    @Query(
        "SELECT * FROM user WHERE id_user != :currentUserId AND id_user NOT IN " +
                "(SELECT DISTINCT CASE " +
                "WHEN sender_id = :currentUserId THEN receiver_id " +
                "WHEN receiver_id = :currentUserId THEN sender_id " +
                "END " +
                "FROM friend_request " +
                "WHERE (sender_id = :currentUserId OR receiver_id = :currentUserId) AND status = 0 or status = 1) " +
                "AND id_user NOT IN (SELECT user_id_is_removed FROM RemoveUser WHERE user_id_removed = :currentUserId)"
    )
    suspend fun getUsersWithFriendRequests(currentUserId: Long): List<User>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("select * from user where id_user =:id")
    suspend fun getIdUser(id: Long): User

    @Query("select * from user where id_user =:id and password_user =:password")
    suspend fun changePasswordById(id: Long, password: String): User

    @Query("select * from user where phone_user =:phone")
    suspend fun changePasswordByPhone(phone: String): User

    @Query("select * from user where phone_user=:phone and password_user=:password")
    suspend fun login(phone: String, password: String): User

    @Query("select * from user where phone_user=:s")
    suspend fun getFindAccount(s: String): User

    @Query("UPDATE user SET work_experience = NULL WHERE id_user = :id")
    suspend fun deleteWork(id: Long)

    @Query("UPDATE user SET high_school = NULL WHERE id_user = :id")
    suspend fun deleteHighSchool(id: Long)

    @Query("UPDATE user SET colleges = NULL WHERE id_user = :id")
    suspend fun deleteColleges(id: Long)

    @Query("UPDATE user SET current_city_and_province = NULL WHERE id_user = :id")
    suspend fun deleteCurrentCity(id: Long)

    @Query("UPDATE user SET home_town = NULL WHERE id_user = :id")
    suspend fun deleteHomeTown(id: Long)

    @Query("UPDATE user SET relationship = NULL WHERE id_user = :id")
    suspend fun deleteRelationship(id: Long)

    @Query("select * from user order by id_user desc")
    suspend fun getAllFriend(): List<User>

    @Query("SELECT * FROM user where name_user LIKE :searchQuery")
    suspend fun searchNameUser(searchQuery: String): List<User>
}