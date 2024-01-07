package com.phoint.facebooksimulation.di.module

import com.phoint.facebooksimulation.ui.changeForgetPassword.ChangeForgetPasswordFragment
import com.phoint.facebooksimulation.ui.changePassword.ChangePasswordFragment
import com.phoint.facebooksimulation.ui.changeUserName.ChangeUserNameFragment
import com.phoint.facebooksimulation.ui.contactInfo.ContactInfoFragment
import com.phoint.facebooksimulation.ui.createInformationUser.addStory.AddStoryFragment
import com.phoint.facebooksimulation.ui.createInformationUser.colleges.CollegesFragment
import com.phoint.facebooksimulation.ui.createInformationUser.createDateOfBirth.DataOfBirthFragment
import com.phoint.facebooksimulation.ui.createInformationUser.createGender.GenderFragment
import com.phoint.facebooksimulation.ui.createInformationUser.createUserName.CreateUserNameFragment
import com.phoint.facebooksimulation.ui.createInformationUser.createYearOld.YearOldFragment
import com.phoint.facebooksimulation.ui.createInformationUser.currentCityAndProvince.CurrentCityProvinceFragment
import com.phoint.facebooksimulation.ui.createInformationUser.email.EmailFragment
import com.phoint.facebooksimulation.ui.createInformationUser.highSchool.HighSchoolFragment
import com.phoint.facebooksimulation.ui.createInformationUser.homeTown.HomeTownFragment
import com.phoint.facebooksimulation.ui.createInformationUser.password.PasswordFragment
import com.phoint.facebooksimulation.ui.createInformationUser.phone.PhoneFragment
import com.phoint.facebooksimulation.ui.createInformationUser.relationship.RelationshipFragment
import com.phoint.facebooksimulation.ui.createInformationUser.wordExperience.WorkExperienceFragment
import com.phoint.facebooksimulation.ui.displayImage.DisplayImageFragment
import com.phoint.facebooksimulation.ui.editInformation.EditInformationFragment
import com.phoint.facebooksimulation.ui.editPermissionUser.EditPermissionUserFragment
import com.phoint.facebooksimulation.ui.editPost.EditPostFragment
import com.phoint.facebooksimulation.ui.emailRequired.EmailRequiredFragment
import com.phoint.facebooksimulation.ui.enterOTP.EnterOTPFragment
import com.phoint.facebooksimulation.ui.findAccount.FindAccountFragment
import com.phoint.facebooksimulation.ui.findNewFriends.FindNewFriendsFragment
import com.phoint.facebooksimulation.ui.friend.FriendFragment
import com.phoint.facebooksimulation.ui.home.HomeFragment
import com.phoint.facebooksimulation.ui.informationUserOther.InformationUserOtherFragment
import com.phoint.facebooksimulation.ui.login.LoginFragment
import com.phoint.facebooksimulation.ui.main.MainActivity
import com.phoint.facebooksimulation.ui.menu.MenuFragment
import com.phoint.facebooksimulation.ui.notification.NotificationFragment
import com.phoint.facebooksimulation.ui.otherPeopleComment.OtherPeopleCommentFragment
import com.phoint.facebooksimulation.ui.permission.PermissionFragment
import com.phoint.facebooksimulation.ui.personalAndAccout.PersonalAndAccountFragment
import com.phoint.facebooksimulation.ui.photoPrewiew.PhotoPreviewFragment
import com.phoint.facebooksimulation.ui.posts.PostsAnArticleFragment
import com.phoint.facebooksimulation.ui.profile.ProfileFragment
import com.phoint.facebooksimulation.ui.profileOtherUser.ProfileOtherUserFragment
import com.phoint.facebooksimulation.ui.saveFavoritePosts.SaveFavoritePostsFragment
import com.phoint.facebooksimulation.ui.searchHistory.SearchHistoryFragment
import com.phoint.facebooksimulation.ui.searchOtherUser.SearchOtherUserFragment
import com.phoint.facebooksimulation.ui.securityManagement.SecurityManagementFragment
import com.phoint.facebooksimulation.ui.seeComment.SeeCommentsFragment
import com.phoint.facebooksimulation.ui.seeCommentSharePost.SeeCommentSharePostFragment
import com.phoint.facebooksimulation.ui.seeImages.SeeImagesFragment
import com.phoint.facebooksimulation.ui.seenCommentAvatar.SeenCommentAvatarFragment
import com.phoint.facebooksimulation.ui.seenFriendsOfFriend.SeenFriendsOfFriendFragment
import com.phoint.facebooksimulation.ui.sendInformation.SendInformationFragment
import com.phoint.facebooksimulation.ui.sendProfileOtherUser.SendProfileOtherFragment
import com.phoint.facebooksimulation.ui.setting.SettingFragment
import com.phoint.facebooksimulation.ui.settingPageOtherUser.SettingPageOtherUserFragment
import com.phoint.facebooksimulation.ui.settingPageUser.SettingPageUserFragment
import com.phoint.facebooksimulation.ui.showImagePhoneApp.ShowImageFragment
import com.phoint.facebooksimulation.ui.splash.SplashFragment
import com.phoint.facebooksimulation.ui.userNavigation.UserNavigationFragment
import com.phoint.facebooksimulation.ui.userOfFriend.UserOfFriendFragment
import com.phoint.facebooksimulation.ui.watch.WatchFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindSplashFragment(): SplashFragment

    @ContributesAndroidInjector
    abstract fun bindHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun bindUserNavigationFragment(): UserNavigationFragment

    @ContributesAndroidInjector
    abstract fun bindFriendFragment(): FriendFragment

    @ContributesAndroidInjector
    abstract fun bindWatchFragment(): WatchFragment

    @ContributesAndroidInjector
    abstract fun bindNotificationFragment(): NotificationFragment

    @ContributesAndroidInjector
    abstract fun bindMenuFragment(): MenuFragment

    @ContributesAndroidInjector
    abstract fun bindLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun bindCreateUserNameFragment(): CreateUserNameFragment

    @ContributesAndroidInjector
    abstract fun bindDataOfBirthFragment(): DataOfBirthFragment

    @ContributesAndroidInjector
    abstract fun bindYearOldFragment(): YearOldFragment

    @ContributesAndroidInjector
    abstract fun bindGenderFragment(): GenderFragment

    @ContributesAndroidInjector
    abstract fun bindPhoneFragment(): PhoneFragment

    @ContributesAndroidInjector
    abstract fun bindEmailFragment(): EmailFragment

    @ContributesAndroidInjector
    abstract fun bindPasswordFragment(): PasswordFragment

    @ContributesAndroidInjector
    abstract fun bindProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun bindPostsAnArticleFragment(): PostsAnArticleFragment

    @ContributesAndroidInjector
    abstract fun bindSendInformationFragment(): SendInformationFragment

    @ContributesAndroidInjector
    abstract fun bindWorkExperienceFragment(): WorkExperienceFragment

    @ContributesAndroidInjector
    abstract fun bindCollegesFragment(): CollegesFragment

    @ContributesAndroidInjector
    abstract fun bindHighSchoolFragment(): HighSchoolFragment

    @ContributesAndroidInjector
    abstract fun bindCurrentCityProvinceFragment(): CurrentCityProvinceFragment

    @ContributesAndroidInjector
    abstract fun bindHomeTownFragment(): HomeTownFragment

    @ContributesAndroidInjector
    abstract fun bindRelationshipFragment(): RelationshipFragment

    @ContributesAndroidInjector
    abstract fun bindEditInformationFragment(): EditInformationFragment

    @ContributesAndroidInjector
    abstract fun bindDisplayImageFragment(): DisplayImageFragment

    @ContributesAndroidInjector
    abstract fun bindSettingPageUserFragment(): SettingPageUserFragment

    @ContributesAndroidInjector
    abstract fun bindShowImageFragment(): ShowImageFragment

    @ContributesAndroidInjector
    abstract fun bindPhotoPreviewFragment(): PhotoPreviewFragment

    @ContributesAndroidInjector
    abstract fun bindAddStoryFragment(): AddStoryFragment

    @ContributesAndroidInjector
    abstract fun bindEditPermissionUserFragment(): EditPermissionUserFragment

    @ContributesAndroidInjector
    abstract fun bindPermissionFragment(): PermissionFragment

    @ContributesAndroidInjector
    abstract fun bindSeeCommentsFragment(): SeeCommentsFragment

    @ContributesAndroidInjector
    abstract fun bindEditPostFragment(): EditPostFragment

    @ContributesAndroidInjector
    abstract fun bindSearchOtherUserFragment(): SearchOtherUserFragment

    @ContributesAndroidInjector
    abstract fun bindInformationUserOtherFragment(): InformationUserOtherFragment

    @ContributesAndroidInjector
    abstract fun bindProfileOtherUserFragment(): ProfileOtherUserFragment

    @ContributesAndroidInjector
    abstract fun bindOtherPeopleCommentFragment(): OtherPeopleCommentFragment

    @ContributesAndroidInjector
    abstract fun bindSettingPageOtherUserFragment(): SettingPageOtherUserFragment

    @ContributesAndroidInjector
    abstract fun bindSaveFavoritePostsFragment(): SaveFavoritePostsFragment

    @ContributesAndroidInjector
    abstract fun bindSendProfileOtherFragment(): SendProfileOtherFragment

    @ContributesAndroidInjector
    abstract fun bindSearchHistoryFragment(): SearchHistoryFragment

    @ContributesAndroidInjector
    abstract fun bindUserOfFriendFragment(): UserOfFriendFragment

    @ContributesAndroidInjector
    abstract fun bindFindNewFriendsFragment(): FindNewFriendsFragment

    @ContributesAndroidInjector
    abstract fun bindSettingFragment(): SettingFragment

    @ContributesAndroidInjector
    abstract fun bindPersonalAndAccountFragment(): PersonalAndAccountFragment

    @ContributesAndroidInjector
    abstract fun bindChangeUserNameFragment(): ChangeUserNameFragment

    @ContributesAndroidInjector
    abstract fun bindContactInfoFragment(): ContactInfoFragment

    @ContributesAndroidInjector
    abstract fun bindSecurityManagementFragment(): SecurityManagementFragment

    @ContributesAndroidInjector
    abstract fun bindChangePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector
    abstract fun bindSeenCommentAvatarFragment(): SeenCommentAvatarFragment

    @ContributesAndroidInjector
    abstract fun bindSeenFriendsOfFriendFragment(): SeenFriendsOfFriendFragment

    @ContributesAndroidInjector
    abstract fun bindSeeImagesFragment(): SeeImagesFragment

    @ContributesAndroidInjector
    abstract fun bindEnterOTPFragment(): EnterOTPFragment

    @ContributesAndroidInjector
    abstract fun bindEmailRequiredFragment(): EmailRequiredFragment

    @ContributesAndroidInjector
    abstract fun bindFindAccountFragment(): FindAccountFragment

    @ContributesAndroidInjector
    abstract fun bindChangeForgetPasswordFragment(): ChangeForgetPasswordFragment

    @ContributesAndroidInjector
    abstract fun bindSeeCommentSharePostFragment(): SeeCommentSharePostFragment
}