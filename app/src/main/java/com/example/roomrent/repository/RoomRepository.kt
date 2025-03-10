package com.example.roomrent.repository

import com.example.roomrent.data.RoomDao
import com.example.roomrent.data.RoomEntity
import kotlinx.coroutines.flow.Flow

class RoomRepository(private val roomDao: RoomDao) {

    val allRoomData: Flow<List<RoomEntity>> = roomDao.getAllRoomData()

    suspend fun insertRoomData(roomEntity: RoomEntity) {
        roomDao.insertRoomData(roomEntity)
    }

    suspend fun deleteRoomData(roomEntity: RoomEntity) {
        roomDao.deleteRoomData(roomEntity)
    }
}
