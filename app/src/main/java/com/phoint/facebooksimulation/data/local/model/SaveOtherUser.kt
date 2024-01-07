package com.phoint.facebooksimulation.data.local.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class SaveOtherUser() : Parcelable {
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Long? = null

    @ColumnInfo(name = "user_owner_id")
    var userOwnerId: Long? = null

    @ColumnInfo(name = "name_other")
    var nameOther: String? = null

    @ColumnInfo(name = "id_user_other")
    var idUserOther: Long? = null

    @ColumnInfo(name = "is_avatar")
    var isAvatar: Boolean = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readValue(Long::class.java.classLoader) as? Long
        userOwnerId = parcel.readValue(Long::class.java.classLoader) as? Long
        nameOther = parcel.readString()
        idUserOther = parcel.readValue(Long::class.java.classLoader) as? Long
        isAvatar = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeValue(userOwnerId)
        parcel.writeString(nameOther)
        parcel.writeValue(idUserOther)
        parcel.writeByte(if (isAvatar) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SaveOtherUser> {
        override fun createFromParcel(parcel: Parcel): SaveOtherUser {
            return SaveOtherUser(parcel)
        }

        override fun newArray(size: Int): Array<SaveOtherUser?> {
            return arrayOfNulls(size)
        }
    }
}