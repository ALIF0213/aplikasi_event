package com.example.appevent.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.appevent.data.local.entity.FavoriteEvent
import com.example.appevent.data.local.room.AppDatabase
import com.example.appevent.data.local.room.FavoriteEventDao
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private val favoriteEventDao: FavoriteEventDao = AppDatabase.getDatabase(application).favoriteEventDao()


    val favoriteEvents: LiveData<List<FavoriteEvent>> = favoriteEventDao.getAllFavorites()

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private fun addFavorite(event: FavoriteEvent) {
        viewModelScope.launch {
            favoriteEventDao.insertFavorite(event)
            _isFavorite.postValue(true)
        }
    }

    private fun removeFavorite(event: FavoriteEvent) {
        viewModelScope.launch {
            favoriteEventDao.deleteFavorite(event)
            _isFavorite.postValue(false)
        }
    }


    fun checkFavoriteStatus(eventId: Int) {
        viewModelScope.launch {
            _isFavorite.postValue(favoriteEventDao.getFavoriteById(eventId) != null)
        }
    }


    fun toggleFavoriteStatus(event: FavoriteEvent) {
        viewModelScope.launch {
            val existingFavorite = favoriteEventDao.getFavoriteById(event.id)
            if (existingFavorite == null) {
                addFavorite(event)
            } else {
                removeFavorite(event)
            }
        }
    }
}
