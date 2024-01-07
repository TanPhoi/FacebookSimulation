package com.phoint.facebooksimulation.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Shortcut {
    @PrimaryKey
    var title: String = ""
    var url: Int? = null
}