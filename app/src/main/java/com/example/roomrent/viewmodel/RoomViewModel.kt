package com.example.roomrent.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.roomrent.data.RoomDatabaseApp
import com.example.roomrent.data.RoomEntity
import com.example.roomrent.repository.RoomRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RoomViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        RoomDatabaseApp::class.java,
        "room_database"
    ).build()

    private val repository = RoomRepository(db.roomDao())

    val roomData = repository.allRoomData.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun insertRoomData(roomEntity: RoomEntity) = viewModelScope.launch {
        repository.insertRoomData(roomEntity)
    }

    fun deleteRoomData(roomEntity: RoomEntity) = viewModelScope.launch {
        repository.deleteRoomData(roomEntity)
    }
}
