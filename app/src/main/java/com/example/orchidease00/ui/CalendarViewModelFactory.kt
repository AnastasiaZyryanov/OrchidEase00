package com.example.orchidease00.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.orchidease00.data.local.MyOrchidDao

class CalendarViewModelFactory(
    private val myOrchidDao: MyOrchidDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            return CalendarViewModel(myOrchidDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
