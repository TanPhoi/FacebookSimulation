package com.phoint.facebooksimulation.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ParentSupport(
    @PrimaryKey
    var title: String,
    var url: Int,
    var childSupport: List<ChildSupport>,
    var isExpandable: Boolean = false
)

@Entity
class ChildSupport(
    @PrimaryKey
    var title: String,
    var url: Int
)