package com.example.roomrent.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoomData(roomEntity: RoomEntity)

    @Delete
    suspend fun deleteRoomData(roomEntity: RoomEntity)

    @Query("SELECT * FROM room_table ORDER BY roomNo ASC")
    fun getAllRoomData(): Flow<List<RoomEntity>>
}
