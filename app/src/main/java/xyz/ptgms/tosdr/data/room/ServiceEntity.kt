package xyz.ptgms.tosdr.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val url: String,
    val rating: String,
    val lastUpdate: Long = System.currentTimeMillis()
) 