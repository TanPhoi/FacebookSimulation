package com.phoint.facebooksimulation.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phoint.facebooksimulation.data.local.model.ChildSupport
import com.phoint.facebooksimulation.data.local.model.Colleges
import com.phoint.facebooksimulation.data.local.model.Comment
import com.phoint.facebooksimulation.data.local.model.CurrentProvinceOrCity
import com.phoint.facebooksimulation.data.local.model.Friend
import com.phoint.facebooksimulation.data.local.model.HighSchool
import com.phoint.facebooksimulation.data.local.model.HomeTown
import com.phoint.facebooksimulation.data.local.model.Like
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.Relationship
import com.phoint.facebooksimulation.data.local.model.Reply
import com.phoint.facebooksimulation.data.local.model.Story
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.data.local.model.Work

class Converter {
    @TypeConverter
    fun fromJson(json: String): List<ChildSupport> {
        return Gson().fromJson(json, object : TypeToken<List<ChildSupport>>() {}.type)
    }

    @TypeConverter
    fun toJson(childSupports: List<ChildSupport>): String {
        return Gson().toJson(childSupports)
    }

    @TypeConverter
    fun fromJsonReply(json: String): ArrayList<Reply> {
        return Gson().fromJson(json, object : TypeToken<ArrayList<Reply>>() {}.type)
    }

    @TypeConverter
    fun toJsonReply(replies: ArrayList<Reply>): String {
        return Gson().toJson(replies)
    }

    @TypeConverter
    fun fromWork(work: Work?): String? {
        return work?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toWork(json: String?): Work? {
        return json?.let { Gson().fromJson(it, Work::class.java) }
    }

    @TypeConverter
    fun fromColleges(colleges: Colleges?): String? {
        return colleges?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toColleges(json: String?): Colleges? {
        return json?.let { Gson().fromJson(it, Colleges::class.java) }
    }

    @TypeConverter
    fun fromHighSchool(highSchool: HighSchool?): String? {
        return highSchool?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toHighSchool(json: String?): HighSchool? {
        return json?.let { Gson().fromJson(it, HighSchool::class.java) }
    }

    @TypeConverter
    fun fromCurrentProvinceOrCity(currentProvinceOrCity: CurrentProvinceOrCity?): String? {
        return currentProvinceOrCity?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toCurrentProvinceOrCity(json: String?): CurrentProvinceOrCity? {
        return json?.let { Gson().fromJson(it, CurrentProvinceOrCity::class.java) }
    }

    @TypeConverter
    fun fromHomeTown(homeTown: HomeTown?): String? {
        return homeTown?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toHomeTown(json: String?): HomeTown? {
        return json?.let { Gson().fromJson(it, HomeTown::class.java) }
    }

    @TypeConverter
    fun fromRelationship(relationship: Relationship?): String? {
        return relationship?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toRelationship(json: String?): Relationship? {
        return json?.let { Gson().fromJson(it, Relationship::class.java) }
    }

    @TypeConverter
    fun fromPost(post: Post?): String? {
        return post?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toPost(json: String?): Post? {
        return json?.let { Gson().fromJson(it, Post::class.java) }
    }

    @TypeConverter
    fun fromStory(story: Story?): String? {
        return story?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toStory(json: String?): Story? {
        return json?.let { Gson().fromJson(it, Story::class.java) }
    }

    @TypeConverter
    fun fromString(value: String): ArrayList<Long> {
        val listType = object : TypeToken<ArrayList<Long>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toString(list: ArrayList<Long>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromJsonLike(json: String): ArrayList<Like> {
        return Gson().fromJson(json, object : TypeToken<ArrayList<Like>>() {}.type)
    }

    @TypeConverter
    fun toJsonLike(likes: ArrayList<Like>): String {
        return Gson().toJson(likes)
    }

    @TypeConverter
    fun fromJsonComment(json: String): ArrayList<Comment> {
        return Gson().fromJson(json, object : TypeToken<ArrayList<Comment>>() {}.type)
    }

    @TypeConverter
    fun toJsonComment(comments: ArrayList<Comment>): String {
        return Gson().toJson(comments)
    }

    @TypeConverter
    fun fromUsers(json: String): ArrayList<User> {
        return Gson().fromJson(json, object : TypeToken<ArrayList<User>>() {}.type)
    }

    @TypeConverter
    fun toUsers(users: ArrayList<User>): String {
        return Gson().toJson(users)
    }

    @TypeConverter
    fun fromFriendList(friends: ArrayList<Friend>): String {
        val gson = Gson()
        val json = gson.toJson(friends)
        return json
    }

    @TypeConverter
    fun toFriendList(json: String): ArrayList<Friend> {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<Friend>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromUser(user: User?): String? {
        return user?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toUser(json: String?): User? {
        return json?.let { Gson().fromJson(it, User::class.java) }
    }

//    @TypeConverter
//    fun fromStringList(list: ArrayList<String>?): String? {
//        return list?.joinToString(separator = ",")
//    }
//
//    @TypeConverter
//    fun toStringList(data: String?): ArrayList<String>? {
//        return data?.split(",")?.map { it.trim() }?.toCollection(ArrayList())
//    }
}