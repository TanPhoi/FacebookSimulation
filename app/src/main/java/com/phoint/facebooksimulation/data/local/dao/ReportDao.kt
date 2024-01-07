package com.phoint.facebooksimulation.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.phoint.facebooksimulation.data.local.model.Report

@Dao
interface ReportDao {
    @Insert
    suspend fun insertReport(report: Report)

    @Delete
    suspend fun deleteReport(report: Report)

    @Update
    suspend fun updateReport(report: Report)

    @Query("select * from report where comment_id =:commentId and user_id =:userId")
    suspend fun getAllReportComment(commentId: Long, userId: Long): Report

    @Query("select * from report where id =:reportId and post_id =:postId and user_id =:userId")
    suspend fun getAllReportPost(reportId: Long, postId: Long, userId: Long): Report
}