package com.phoint.facebooksimulation.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class CurrentProvinceOrCity {
    @PrimaryKey
    @ColumnInfo(name = "current_province_or_city_id")
    var currentProvinceOrCityId: Long? = null

    @ColumnInfo(name = "name_current_province_or_city_id")
    var nameCurrentProvinceOrCity: String? = null

    @ColumnInfo(name = "privacy")
    var privacy: String? = null
}