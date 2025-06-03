package com.example.orchidease00.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [MyOrchid::class], version = 8)
@TypeConverters(Converters::class)
abstract class MyOrchidDatabase : RoomDatabase() {
    abstract fun myOrchidDao(): MyOrchidDao


    companion object {
        @Volatile private var INSTANCE: MyOrchidDatabase? = null

        fun getDatabase(context: Context): MyOrchidDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyOrchidDatabase::class.java,
                    "orchid_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
