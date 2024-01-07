package com.phoint.facebooksimulation.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.phoint.facebooksimulation.converter.Converter
import com.phoint.facebooksimulation.data.local.dao.CommentDao
import com.phoint.facebooksimulation.data.local.dao.FriendRequestDao
import com.phoint.facebooksimulation.data.local.dao.HidePostDao
import com.phoint.facebooksimulation.data.local.dao.NotificationDao
import com.phoint.facebooksimulation.data.local.dao.PostDao
import com.phoint.facebooksimulation.data.local.dao.RemoveUserDao
import com.phoint.facebooksimulation.data.local.dao.ReplyDao
import com.phoint.facebooksimulation.data.local.dao.ReportDao
import com.phoint.facebooksimulation.data.local.dao.SaveFavoritePostDao
import com.phoint.facebooksimulation.data.local.dao.SaveOtherUserDao
import com.phoint.facebooksimulation.data.local.dao.UserDao
import com.phoint.facebooksimulation.data.local.dao.WorkDao
import com.phoint.facebooksimulation.data.local.model.ChildSupport
import com.phoint.facebooksimulation.data.local.model.Colleges
import com.phoint.facebooksimulation.data.local.model.Comment
import com.phoint.facebooksimulation.data.local.model.CurrentProvinceOrCity
import com.phoint.facebooksimulation.data.local.model.Friend
import com.phoint.facebooksimulation.data.local.model.FriendRequest
import com.phoint.facebooksimulation.data.local.model.HidePost
import com.phoint.facebooksimulation.data.local.model.HighSchool
import com.phoint.facebooksimulation.data.local.model.HomeTown
import com.phoint.facebooksimulation.data.local.model.Like
import com.phoint.facebooksimulation.data.local.model.Notification
import com.phoint.facebooksimulation.data.local.model.ParentSupport
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.Relationship
import com.phoint.facebooksimulation.data.local.model.RemoveUser
import com.phoint.facebooksimulation.data.local.model.Reply
import com.phoint.facebooksimulation.data.local.model.Report
import com.phoint.facebooksimulation.data.local.model.SaveFavoritePost
import com.phoint.facebooksimulation.data.local.model.SaveOtherUser
import com.phoint.facebooksimulation.data.local.model.Shortcut
import com.phoint.facebooksimulation.data.local.model.Sticker
import com.phoint.facebooksimulation.data.local.model.Story
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.data.local.model.Work

@Database(
    entities = [User::class, Shortcut::class,
        ParentSupport::class, ChildSupport::class,
        Post::class, Comment::class, Reply::class,
        Work::class, Colleges::class, HighSchool::class,
        CurrentProvinceOrCity::class, HomeTown::class,
        Relationship::class, Story::class, Like::class,
        Report::class, Sticker::class, SaveFavoritePost::class,
        SaveOtherUser::class, Friend::class, FriendRequest::class,
        RemoveUser::class, HidePost::class, Notification::class],
    version = 22,
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun replyDao(): ReplyDao
    abstract fun workDao(): WorkDao
    abstract fun reportDao(): ReportDao
    abstract fun saveFavoritePostDao(): SaveFavoritePostDao
    abstract fun saveOtherUserDao(): SaveOtherUserDao
    abstract fun friendRequestDao(): FriendRequestDao
    abstract fun removeUserDao(): RemoveUserDao
    abstract fun hidePostDao(): HidePostDao
    abstract fun notificationDao(): NotificationDao
}