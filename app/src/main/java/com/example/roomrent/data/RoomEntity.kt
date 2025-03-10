package com.example.roomrent.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_table")
data class RoomEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val roomNo: String,
    val month: String,
    val roomRent: Int,
    val electricityBill: Int,
    val total: Int,
    val amountPaid: Int,
    val balance: Int
)
