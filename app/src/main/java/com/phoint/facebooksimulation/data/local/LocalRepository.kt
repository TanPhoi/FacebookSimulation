package com.phoint.facebooksimulation.data.local

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
import com.phoint.facebooksimulation.data.local.model.Comment
import com.phoint.facebooksimulation.data.local.model.FriendRequest
import com.phoint.facebooksimulation.data.local.model.HidePost
import com.phoint.facebooksimulation.data.local.model.Notification
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.RemoveUser
import com.phoint.facebooksimulation.data.local.model.Reply
import com.phoint.facebooksimulation.data.local.model.Report
import com.phoint.facebooksimulation.data.local.model.SaveFavoritePost
import com.phoint.facebooksimulation.data.local.model.SaveOtherUser
import com.phoint.facebooksimulation.data.local.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalRepository @Inject constructor(
    private val userDao: UserDao,
    private val postDao: PostDao,
    private val commentDao: CommentDao,
    private val replyDao: ReplyDao,
    private val reportDao: ReportDao,
    private val saveFavoritePostDao: SaveFavoritePostDao,
    private val saveOtherUserDao: SaveOtherUserDao,
    private val friendRequestDao: FriendRequestDao,
    private val removeUserDao: RemoveUserDao,
    private val hidePostDao: HidePostDao,
    private val notificationDao: NotificationDao
) {

    //User
    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun getId(id: Long): User {
        return userDao.getIdUser(id)
    }

    suspend fun login(phone: String, password: String): User {
        return userDao.login(phone, password)
    }

    suspend fun getFindAccount(s: String): User {
        return userDao.getFindAccount(s)
    }

    suspend fun changePasswordById(id: Long, password: String): User {
        return userDao.changePasswordById(id, password)
    }

    suspend fun changePasswordByPhone(phone: String): User {
        return userDao.changePasswordByPhone(phone)
    }

    suspend fun deleteWork(id: Long) {
        return userDao.deleteWork(id)
    }

    suspend fun deleteHighSchool(id: Long) {
        return userDao.deleteHighSchool(id)
    }

    suspend fun deleteColleges(id: Long) {
        return userDao.deleteColleges(id)
    }

    suspend fun deleteCurrentCity(id: Long) {
        return userDao.deleteCurrentCity(id)
    }

    suspend fun deleteHomeTown(id: Long) {
        return userDao.deleteHomeTown(id)
    }

    suspend fun deleteRelationship(id: Long) {
        return userDao.deleteRelationship(id)
    }

    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }

    suspend fun getAllFriend(): List<User> {
        return userDao.getAllFriend()
    }

    suspend fun getAllUserById(): List<User> {
        return userDao.getAllUserById()
    }

    suspend fun searchNameUser(searchQuery: String): List<User> {
        return userDao.searchNameUser("%$searchQuery%")
    }

    suspend fun getUsersWithFriendRequests(currentUserId: Long): List<User> {
        return userDao.getUsersWithFriendRequests(currentUserId)
    }

    //Post
    suspend fun getAllPosts(): List<Post> {
        return postDao.getAllPosts()
    }

    suspend fun getPostById(postId: Long): Post {
        return postDao.getPostById(postId)
    }

    suspend fun insertPost(post: Post) {
        postDao.insertPost(post)
    }

    suspend fun updatePost(post: Post) {
        postDao.updatePost(post)
    }

    suspend fun deletePost(post: Post) {
        postDao.deletePost(post)
    }

    suspend fun getAllPost(): List<Post> {
        return postDao.getAllPost()
    }

    suspend fun getJoinDataPost(userId: Long): List<Post> {
        return postDao.getJoinDataPost(userId)
    }

    suspend fun getJoinDataPostPrivacy(userId: Long): List<Post> {
        return postDao.getJoinDataPostPrivacy(userId)
    }

    suspend fun getPostDataByUser(userId: Long, postId: Long): Post {
        return postDao.getPostDataByUser(userId, postId)
    }

    suspend fun getIsPinned(): Post {
        return postDao.getIsPinned()
    }

    // Comment
    suspend fun getJoinDataComment(postId: Long): List<Comment> {
        return commentDao.getJoinDataComment(postId)
    }

    suspend fun getAllComment(): List<Comment> {
        return commentDao.getAllComment()
    }

    //Reply
    suspend fun getAllReply(): List<Reply> {
        return replyDao.getAllReply()
    }

    //Report
    suspend fun insertReport(report: Report) {
        reportDao.insertReport(report)
    }

    suspend fun deleteReport(report: Report) {
        reportDao.deleteReport(report)
    }

    suspend fun updateReport(report: Report) {
        reportDao.updateReport(report)
    }

    suspend fun getAllReportComment(commentId: Long, userId: Long): Report {
        return reportDao.getAllReportComment(commentId, userId)
    }

    suspend fun getAllReportPost(reportId: Long, postId: Long, userId: Long): Report {
        return reportDao.getAllReportPost(reportId, postId, userId)
    }

    //SaveFavoritePost
    suspend fun insertSaveFavoritePost(saveFavoritePost: SaveFavoritePost) {
        saveFavoritePostDao.insertSaveFavoritePost(saveFavoritePost)
    }

    suspend fun deleteSaveFavoritePost(saveFavoritePost: SaveFavoritePost) {
        saveFavoritePostDao.deleteSaveFavoritePost(saveFavoritePost)
    }

    suspend fun updateSaveFavoritePost(saveFavoritePost: SaveFavoritePost) {
        saveFavoritePostDao.updateSaveFavoritePost(saveFavoritePost)
    }

    suspend fun getAllSaveFavoritePost(userOwnerId: Long, postOwnerId: Long): SaveFavoritePost {
        return saveFavoritePostDao.getAllSaveFavoritePost(userOwnerId, postOwnerId)
    }

    suspend fun getAllDataByPost(postOwnerId: Long): SaveFavoritePost {
        return saveFavoritePostDao.getAllDataByPost(postOwnerId)
    }

    suspend fun getSavePostDataByUser(userOwnerId: Long): List<SaveFavoritePost> {
        return saveFavoritePostDao.getSavePostDataByUser(userOwnerId)
    }

    suspend fun getAllSavePosts(): List<SaveFavoritePost> {
        return saveFavoritePostDao.getAllSavePosts()
    }

    //SaveOtherUser
    suspend fun getAllSaveOtherOwnerId(userOwnerId: Long): List<SaveOtherUser> {
        return saveOtherUserDao.getAllSaveOtherOwnerId(userOwnerId)
    }

    suspend fun getSaveOtherUserByName(idUserCurrent: Long, idUserOther: Long): SaveOtherUser {
        return saveOtherUserDao.getSaveOtherUserByName(idUserCurrent, idUserOther)
    }

    suspend fun insertSaveOtherUser(saveOtherUser: SaveOtherUser) {
        saveOtherUserDao.insertSaveOtherUser(saveOtherUser)
    }

    suspend fun deleteSaveOtherUser(saveOtherUser: SaveOtherUser) {
        saveOtherUserDao.deleteSaveOtherUser(saveOtherUser)
    }

    //FriendRequest
    suspend fun insertFriendRequest(friendRequest: FriendRequest) {
        friendRequestDao.insertFriendRequest(friendRequest)
    }

    suspend fun deleteFriendRequest(friendRequest: FriendRequest) {
        friendRequestDao.deleteFriendRequest(friendRequest)
    }

    suspend fun updateFriendRequest(friendRequest: FriendRequest) {
        friendRequestDao.updateFriendRequest(friendRequest)
    }

    suspend fun getAllFriend(id: Long): List<FriendRequest> {
        return friendRequestDao.getAllFriend(id)
    }

    suspend fun getAllFriendCurrentUser(id: Long): List<FriendRequest> {
        return friendRequestDao.getAllFriendCurrentUser(id)
    }

    suspend fun getFilteredFriends(id: Long): List<FriendRequest> {
        return friendRequestDao.getFilteredFriends(id)
    }

    suspend fun getFriendRequestById(senderId: Long, receiverId: Long): FriendRequest {
        return friendRequestDao.getFriendRequestById(senderId, receiverId)
    }

    suspend fun getFriendRequestByIdDelete(senderId: Long, receiverId: Long): FriendRequest {
        return friendRequestDao.getFriendRequestByIdDelete(senderId, receiverId)
    }

    suspend fun getFriendOfUser(senderId: Long, receiverId: Long): List<FriendRequest> {
        return friendRequestDao.getFriendOfUser(senderId, receiverId)
    }

    suspend fun getFriendRequestByIdReceiver(senderId: Long, receiverId: Long): FriendRequest {
        return friendRequestDao.getFriendRequestByIdReceiver(senderId, receiverId)
    }

    suspend fun getFriendRequest(senderId: Long, receiverId: Long): FriendRequest {
        return friendRequestDao.getFriendRequest(senderId, receiverId)
    }

    suspend fun getSenderId(senderId: Long): FriendRequest {
        return friendRequestDao.getSenderId(senderId)
    }

    suspend fun getReceiverId(receiverId: Long): FriendRequest {
        return friendRequestDao.getReceiverId(receiverId)
    }

    suspend fun getAllReceiverId(receiverId: Long): List<FriendRequest> {
        return friendRequestDao.getAllReceiverId(receiverId)
    }

    //RemoveUser
    suspend fun insertRemoveUser(removeUser: RemoveUser) {
        removeUserDao.insertRemoveUser(removeUser)
    }

    //HidePost
    suspend fun insertHidePost(hidePost: HidePost) {
        hidePostDao.insertHidePost(hidePost)
    }

    suspend fun getHiddenPostIds(idUser: Long): List<Long> {
        return hidePostDao.getHiddenPostIds(idUser)
    }

    //Notification
    suspend fun getAllNotificationById(userId: Long, currentUserId: Long): List<Notification> {
        return notificationDao.getAllNotificationById(userId, currentUserId)
    }

    suspend fun insertNotification(notification: Notification) {
        notificationDao.insertNotification(notification)
    }

    suspend fun updateNotification(notification: Notification) {
        notificationDao.updateNotification(notification)
    }

    suspend fun deleteNotification(notification: Notification) {
        notificationDao.deleteNotification(notification)
    }

    suspend fun getNotificationById(senderId: Long, userId: Long, postId: Long): Notification {
        return notificationDao.getNotificationById(senderId, userId, postId)
    }

    suspend fun getNotificationCommentById(
        senderId: Long,
        userId: Long,
        postId: Long,
        commentId: Long
    ): Notification {
        return notificationDao.getNotificationCommentById(senderId, userId, postId, commentId)
    }
}