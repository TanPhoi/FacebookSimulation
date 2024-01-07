package com.phoint.facebooksimulation.data.local.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
class User() : Parcelable {
    @PrimaryKey
    @ColumnInfo(name = "id_user")
    var idUser: Long? = null

    @ColumnInfo(name = "name_user")
    var nameUser: String? = null

    @ColumnInfo(name = "date_of_birthUser")
    var dateOfBirthUser: String? = null

    @ColumnInfo(name = "gender_user")
    var genderUser: String? = null

    @ColumnInfo(name = "year_old_ser")
    var yearOldUser: String? = null

    @ColumnInfo(name = "phone_user")
    var phoneUser: String? = null

    @ColumnInfo(name = "email_user")
    var emailUser: String? = null

    @ColumnInfo(name = "avatar_user")
    var avatarUser: String? = null

    @ColumnInfo(name = "cover_image_user")
    var coverImageUser: String? = null

    @ColumnInfo(name = "password_user")
    var passwordUser: String? = null

    @ColumnInfo(name = "work_experience")
    var workExperience: Work? = null

    @ColumnInfo(name = "colleges")
    var colleges: Colleges? = null

    @ColumnInfo(name = "high_school")
    var highSchool: HighSchool? = null

    @ColumnInfo(name = "current_city_and_province")
    var currentCityAndProvince: CurrentProvinceOrCity? = null

    @ColumnInfo(name = "home_town")
    var homeTown: HomeTown? = null

    @ColumnInfo(name = "relationship")
    var relationship: Relationship? = null

    @ColumnInfo(name = "story")
    var story: Story? = null

    @ColumnInfo(name = "friends")
    var friends: ArrayList<Friend> = ArrayList()

    constructor(parcel: Parcel) : this() {
        idUser = parcel.readValue(Long::class.java.classLoader) as? Long
        nameUser = parcel.readString()
        dateOfBirthUser = parcel.readString()
        genderUser = parcel.readString()
        yearOldUser = parcel.readString()
        phoneUser = parcel.readString()
        emailUser = parcel.readString()
        avatarUser = parcel.readString()
        coverImageUser = parcel.readString()
        passwordUser = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(idUser)
        parcel.writeString(nameUser)
        parcel.writeString(dateOfBirthUser)
        parcel.writeString(genderUser)
        parcel.writeString(yearOldUser)
        parcel.writeString(phoneUser)
        parcel.writeString(emailUser)
        parcel.writeString(avatarUser)
        parcel.writeString(coverImageUser)
        parcel.writeString(passwordUser)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}