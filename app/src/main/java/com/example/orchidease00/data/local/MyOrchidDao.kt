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

    @Query("UPDATE my_orchids SET wateringNotified = 1 WHERE id = :orchidId")
    suspend fun markWateringAsNotified(orchidId: Int)

    @Query("UPDATE my_orchids SET fertilizingNotified = 1 WHERE id = :orchidId")
    suspend fun markFertilizingAsNotified(orchidId: Int)

    @Query("SELECT * FROM my_orchids WHERE nextWatering <= :currentTime AND wateringNotified = 0")
    suspend fun getWateringEventsToNotify(currentTime: Long): List<MyOrchid>

    @Query("SELECT * FROM my_orchids WHERE nextFertilizing <= :currentTime AND fertilizingNotified = 0")
    suspend fun getFertilizingEventsToNotify(currentTime: Long): List<MyOrchid>

}
