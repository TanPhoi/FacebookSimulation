package com.phoint.facebooksimulation.di.module

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.phoint.facebooksimulation.data.local.LocalDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(appContext: Context): LocalDatabase {
        return Room
            .databaseBuilder(
                appContext.applicationContext,
                LocalDatabase::class.java,
                "localDb"
            )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideAppPreference(appContext: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(appContext)
    }

    @Singleton
    @Provides
    fun provideUserDao(db: LocalDatabase) =
        db.userDao()

    @Singleton
    @Provides
    fun providePostDao(db: LocalDatabase) =
        db.postDao()

    @Singleton
    @Provides
    fun provideWorkDao(db: LocalDatabase) =
        db.workDao()

    @Singleton
    @Provides
    fun provideCommentDao(db: LocalDatabase) =
        db.commentDao()

    @Singleton
    @Provides
    fun provideReplyDao(db: LocalDatabase) =
        db.replyDao()

    @Singleton
    @Provides
    fun provideReportDao(db: LocalDatabase) =
        db.reportDao()

    @Singleton
    @Provides
    fun provideSaveFavoritePostDao(db: LocalDatabase) =
        db.saveFavoritePostDao()

    @Singleton
    @Provides
    fun provideSaveOtherUserDao(db: LocalDatabase) =
        db.saveOtherUserDao()

    @Singleton
    @Provides
    fun provideFriendRequestDao(db: LocalDatabase) =
        db.friendRequestDao()

    @Singleton
    @Provides
    fun provideRemoveUserDao(db: LocalDatabase) =
        db.removeUserDao()

    @Singleton
    @Provides
    fun provideHidePostDao(db: LocalDatabase) =
        db.hidePostDao()

    @Singleton
    @Provides
    fun provideNotificationDao(db: LocalDatabase) =
        db.notificationDao()
}