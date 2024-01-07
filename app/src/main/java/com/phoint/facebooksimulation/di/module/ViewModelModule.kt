package com.phoint.facebooksimulation.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phoint.facebooksimulation.di.viewmodel.ViewModelFactory
import com.phoint.facebooksimulation.di.viewmodel.ViewModelKey
import com.phoint.facebooksimulation.ui.changeForgetPassword.ChangeForgetPasswordViewModel
import com.phoint.facebooksimulation.ui.changePassword.ChangePasswordViewModel
import com.phoint.facebooksimulation.ui.changeUserName.ChangeUserNameViewModel
import com.phoint.facebooksimulation.ui.contactInfo.ContactInfoViewModel
import com.phoint.facebooksimulation.ui.createInformationUser.addStory.AddStoryViewModel
import com.phoint.facebooksimulation.ui.createInformationUser.colleges.CollegesViewModel
import com.phoint.facebooksimulation.ui.createInformationUser.createDateOfBirth.DataOfBirthViewModel
import com.phoint.facebooksimulation.ui.createInformationUser.createGender.GenderViewModel
import com.phoint.facebooksimulation.ui.createInformationUser.createUserName.CreateUserNameViewModel
import com.phoint.facebooksimulation.ui.createInformationUser.createYearOld.YearOldViewModel
import com.phoint.facebooksimulation.ui.createInformationUser.currentCityAndProvince.CurrentCityProvinceViewModel
import com.phoint.facebooksimulation.ui.createInformationUser.email.EmailViewModel
import com.phoint.facebooksimulation.ui.createInformationUser.highSchool.HighSchoolViewModel
import com.phoint.facebooksimulation.ui.createInformationUser.homeTown.HomeTownViewModel
import com.phoint.facebooksimulation.ui.createInformationUser.password.PasswordViewModel
import com.phoint.facebooksimulation.ui.createInformationUser.phone.PhoneViewModel
import com.phoint.facebooksimulation.ui.createInformationUser.relationship.RelationshipViewModel
import com.phoint.facebooksimulation.ui.createInformationUser.wordExperience.WorkExperienceViewModel
import com.phoint.facebooksimulation.ui.displayImage.DisplayImageViewModel
import com.phoint.facebooksimulation.ui.editInformation.EditInformationViewModel
import com.phoint.facebooksimulation.ui.editPermissionUser.EditPermissionUserViewModel
import com.phoint.facebooksimulation.ui.editPost.EditPostViewModel
import com.phoint.facebooksimulation.ui.emailRequired.EmailRequiredViewModel
import com.phoint.facebooksimulation.ui.enterOTP.EnterOTPViewModel
import com.phoint.facebooksimulation.ui.findAccount.FindAccountViewModel
import com.phoint.facebooksimulation.ui.findNewFriends.FindNewFriendsViewModel
import com.phoint.facebooksimulation.ui.friend.FriendViewModel
import com.phoint.facebooksimulation.ui.home.HomeViewModel
import com.phoint.facebooksimulation.ui.informationUserOther.InformationUserOtherViewModel
import com.phoint.facebooksimulation.ui.login.LoginViewModel
import com.phoint.facebooksimulation.ui.main.MainViewModel
import com.phoint.facebooksimulation.ui.menu.MenuViewModel
import com.phoint.facebooksimulation.ui.notification.NotificationViewModel
import com.phoint.facebooksimulation.ui.otherPeopleComment.OtherPeopleCommentViewModel
import com.phoint.facebooksimulation.ui.permission.PermissionViewModel
import com.phoint.facebooksimulation.ui.personalAndAccout.PersonalAndAccountViewModel
import com.phoint.facebooksimulation.ui.photoPrewiew.PhotoPreviewViewModel
import com.phoint.facebooksimulation.ui.posts.PostsAnArticleViewModel
import com.phoint.facebooksimulation.ui.profile.ProfileViewModel
import com.phoint.facebooksimulation.ui.profileOtherUser.ProfileOtherUserViewModel
import com.phoint.facebooksimulation.ui.saveFavoritePosts.SaveFavoritePostsViewModel
import com.phoint.facebooksimulation.ui.searchHistory.SearchHistoryViewModel
import com.phoint.facebooksimulation.ui.searchOtherUser.SearchOtherUserViewModel
import com.phoint.facebooksimulation.ui.securityManagement.SecurityManagementViewModel
import com.phoint.facebooksimulation.ui.seeComment.SeeCommentsViewModel
import com.phoint.facebooksimulation.ui.seeCommentSharePost.SeeCommentSharePostViewModel
import com.phoint.facebooksimulation.ui.seeImages.SeeImagesViewModel
import com.phoint.facebooksimulation.ui.seenCommentAvatar.SeenCommentAvatarViewModel
import com.phoint.facebooksimulation.ui.seenFriendsOfFriend.SeenFriendsOfFriendViewModel
import com.phoint.facebooksimulation.ui.sendInformation.SendInformationViewModel
import com.phoint.facebooksimulation.ui.sendProfileOtherUser.SendProfileOtherViewModel
import com.phoint.facebooksimulation.ui.setting.SettingViewModel
import com.phoint.facebooksimulation.ui.settingPageOtherUser.SettingPageOtherUserViewModel
import com.phoint.facebooksimulation.ui.settingPageUser.SettingPageUserViewModel
import com.phoint.facebooksimulation.ui.showImagePhoneApp.ShowImageViewModel
import com.phoint.facebooksimulation.ui.splash.SplashViewModel
import com.phoint.facebooksimulation.ui.userNavigation.UserNavigationViewModel
import com.phoint.facebooksimulation.ui.userOfFriend.UserOfFriendViewModel
import com.phoint.facebooksimulation.ui.watch.WatchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun mainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    internal abstract fun splashViewModel(viewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun homeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserNavigationViewModel::class)
    internal abstract fun userNavigationViewModel(viewModel: UserNavigationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FriendViewModel::class)
    internal abstract fun friendViewModel(viewModel: FriendViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WatchViewModel::class)
    internal abstract fun watchViewModel(viewModel: WatchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotificationViewModel::class)
    internal abstract fun notificationViewModel(viewModel: NotificationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MenuViewModel::class)
    internal abstract fun menuViewModel(viewModel: MenuViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    internal abstract fun loginViewModel(viewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateUserNameViewModel::class)
    internal abstract fun createUserNameViewModel(viewModel: CreateUserNameViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DataOfBirthViewModel::class)
    internal abstract fun dataOfBirthViewModel(viewModel: DataOfBirthViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(YearOldViewModel::class)
    internal abstract fun yearOldViewModel(viewModel: YearOldViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GenderViewModel::class)
    internal abstract fun genderViewModel(viewModel: GenderViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PhoneViewModel::class)
    internal abstract fun phoneViewModel(viewModel: PhoneViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EmailViewModel::class)
    internal abstract fun emailViewModel(viewModel: EmailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PasswordViewModel::class)
    internal abstract fun passwordViewModel(viewModel: PasswordViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    internal abstract fun profileViewModel(viewModel: ProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PostsAnArticleViewModel::class)
    internal abstract fun postsAnArticleViewModel(viewModel: PostsAnArticleViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SendInformationViewModel::class)
    internal abstract fun sendInformationViewModel(viewModel: SendInformationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WorkExperienceViewModel::class)
    internal abstract fun workExperienceViewModel(viewModel: WorkExperienceViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CollegesViewModel::class)
    internal abstract fun collegesViewModel(viewModel: CollegesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HighSchoolViewModel::class)
    internal abstract fun highSchoolViewModel(viewModel: HighSchoolViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CurrentCityProvinceViewModel::class)
    internal abstract fun currentCityProvinceViewModel(viewModel: CurrentCityProvinceViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeTownViewModel::class)
    internal abstract fun homeTownViewModel(viewModel: HomeTownViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RelationshipViewModel::class)
    internal abstract fun relationshipViewModel(viewModel: RelationshipViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditInformationViewModel::class)
    internal abstract fun editInformationViewModel(viewModel: EditInformationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DisplayImageViewModel::class)
    internal abstract fun displayImageViewModel(viewModel: DisplayImageViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingPageUserViewModel::class)
    internal abstract fun settingPageUserViewModel(viewModel: SettingPageUserViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ShowImageViewModel::class)
    internal abstract fun showImageViewModel(viewModel: ShowImageViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PhotoPreviewViewModel::class)
    internal abstract fun photoPreviewViewModel(viewModel: PhotoPreviewViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddStoryViewModel::class)
    internal abstract fun addStoryViewModel(viewModel: AddStoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditPermissionUserViewModel::class)
    internal abstract fun editPermissionUserViewModel(viewModel: EditPermissionUserViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PermissionViewModel::class)
    internal abstract fun permissionViewModel(viewModel: PermissionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SeeCommentsViewModel::class)
    internal abstract fun seeCommentsViewModel(viewModel: SeeCommentsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditPostViewModel::class)
    internal abstract fun editPostViewModel(viewModel: EditPostViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchOtherUserViewModel::class)
    internal abstract fun searchOtherUserViewModel(viewModel: SearchOtherUserViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InformationUserOtherViewModel::class)
    internal abstract fun informationUserOtherViewModel(viewModel: InformationUserOtherViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileOtherUserViewModel::class)
    internal abstract fun profileOtherUserViewModel(viewModel: ProfileOtherUserViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OtherPeopleCommentViewModel::class)
    internal abstract fun otherPeopleCommentViewModel(viewModel: OtherPeopleCommentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingPageOtherUserViewModel::class)
    internal abstract fun settingPageOtherUserViewModel(viewModel: SettingPageOtherUserViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SaveFavoritePostsViewModel::class)
    internal abstract fun saveFavoritePostsViewModel(viewModel: SaveFavoritePostsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SendProfileOtherViewModel::class)
    internal abstract fun sendProfileOtherViewModel(viewModel: SendProfileOtherViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchHistoryViewModel::class)
    internal abstract fun searchHistoryViewModel(viewModel: SearchHistoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserOfFriendViewModel::class)
    internal abstract fun userOfFriendViewModel(viewModel: UserOfFriendViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FindNewFriendsViewModel::class)
    internal abstract fun findNewFriendsViewModel(viewModel: FindNewFriendsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingViewModel::class)
    internal abstract fun settingViewModel(viewModel: SettingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PersonalAndAccountViewModel::class)
    internal abstract fun personalAndAccountViewModel(viewModel: PersonalAndAccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChangeUserNameViewModel::class)
    internal abstract fun changeUserNameViewModel(viewModel: ChangeUserNameViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContactInfoViewModel::class)
    internal abstract fun contactInfoViewModel(viewModel: ContactInfoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SecurityManagementViewModel::class)
    internal abstract fun securityManagementViewModel(viewModel: SecurityManagementViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChangePasswordViewModel::class)
    internal abstract fun changePasswordViewModel(viewModel: ChangePasswordViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SeenCommentAvatarViewModel::class)
    internal abstract fun seenCommentAvatarViewModel(viewModel: SeenCommentAvatarViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SeenFriendsOfFriendViewModel::class)
    internal abstract fun seenFriendsOfFriendViewModel(viewModel: SeenFriendsOfFriendViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SeeImagesViewModel::class)
    internal abstract fun seeImagesViewModel(viewModel: SeeImagesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EnterOTPViewModel::class)
    internal abstract fun enterOTPViewModel(viewModel: EnterOTPViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EmailRequiredViewModel::class)
    internal abstract fun emailRequiredViewModel(viewModel: EmailRequiredViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FindAccountViewModel::class)
    internal abstract fun findAccountViewModel(viewModel: FindAccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChangeForgetPasswordViewModel::class)
    internal abstract fun changeForgetPasswordViewModel(viewModel: ChangeForgetPasswordViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SeeCommentSharePostViewModel::class)
    internal abstract fun seeCommentSharePostViewModel(viewModel: SeeCommentSharePostViewModel): ViewModel
}