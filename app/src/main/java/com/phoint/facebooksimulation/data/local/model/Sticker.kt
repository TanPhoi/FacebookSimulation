package com.phoint.facebooksimulation.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Sticker {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
    var srcSticker: Int? = null
}