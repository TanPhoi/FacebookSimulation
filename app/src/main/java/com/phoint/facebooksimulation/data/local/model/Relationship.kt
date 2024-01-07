package com.phoint.facebooksimulation.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Relationship {
    @PrimaryKey
    @ColumnInfo(name = "relationship_id")
    var relationshipId: Long? = null

    @ColumnInfo(name = "name_relationship_id")
    var nameRelationship: String? = null

    @ColumnInfo(name = "privacy")
    var privacy: String? = null
}