package com.example.orchidease00.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MyOrchidDao {
    @Query("SELECT * FROM my_orchids")
    fun getAll(): Flow<List<MyOrchid>>

    @Insert
    suspend fun insert(orchid: MyOrchid)

    @Delete
    suspend fun delete(orchid: MyOrchid)

    @Query("SELECT * FROM my_orchids WHERE id = :id")
    fun getById(id: Int): Flow<MyOrchid?>

    @Update
    suspend fun update(orchid: MyOrchid)
}
