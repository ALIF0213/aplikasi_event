package com.example.appevent.ui.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appevent.data.remote.response.ListEventsItem
import com.example.appevent.data.remote.response.ResponseEvents
import com.example.appevent.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class EventViewModel  : ViewModel() {

    private val _responseEvents = MutableLiveData<ResponseEvents>()
    val responseEvents: LiveData<ResponseEvents> get() = _responseEvents

    private val _eventsActive = MutableLiveData<List<ListEventsItem>>()
    val eventsActive: LiveData<List<ListEventsItem>> get() = _eventsActive

    private val _eventsCompleted = MutableLiveData<List<ListEventsItem>>()
    val eventsCompleted: LiveData<List<ListEventsItem>> get() = _eventsCompleted

    private val _searchedEvents = MutableLiveData<List<ListEventsItem>>()
    val searchedEvents: LiveData<List<ListEventsItem>> get() = _searchedEvents

    private val _isLoadingUpcoming = MutableLiveData<Boolean>()
    val isLoadingUpcoming: LiveData<Boolean> get() = _isLoadingUpcoming

    private val _isLoadingPast = MutableLiveData<Boolean>()
    val isLoadingPast: LiveData<Boolean> get() = _isLoadingPast

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchActiveEvents() {
        _isLoadingUpcoming.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.instance.getEvents(active = 1)
                if (response.isSuccessful && response.body() != null) {
                    _eventsActive.value = response.body()?.listEvents ?: emptyList()
                    _error.value = null
                } else {
                    _error.value = "Gagal memuat data event aktif."
                }
            } catch (e: UnknownHostException) {
                _error.value = "Aplikasi memerlukan koneksi internet."
            } catch (e: Exception) {
                _error.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoadingUpcoming.value = false
            }
        }
    }

    fun fetchCompletedEvents() {
        _isLoadingPast.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.instance.getEvents(active = 0)
                if (response.isSuccessful && response.body() != null) {
                    _eventsCompleted.value = response.body()?.listEvents ?: emptyList()
                    _error.value = null
                } else {
                    _error.value = "Gagal memuat data event selesai."
                }
            } catch (e: UnknownHostException) {
                _error.value = "Aplikasi memerlukan koneksi internet."
            } catch (e: Exception) {
                _error.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoadingPast.value = false
            }
        }
    }

    fun searchEvents(query: String) {
        _isLoadingPast.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.instance.searchEvents(active = 0, query = query)
                if (response.isSuccessful && response.body() != null) {
                    _searchedEvents.value = response.body()?.listEvents ?: emptyList()
                    _error.value = null
                } else {
                    _error.value = "Pencarian gagal memuat data."
                }
            } catch (e: UnknownHostException) {
                _error.value = "Aplikasi memerlukan koneksi internet."
            } catch (e: Exception) {
                _error.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoadingPast.value = false
            }
        }
    }
}


