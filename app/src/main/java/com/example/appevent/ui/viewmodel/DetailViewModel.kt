package com.example.appevent.ui.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appevent.data.remote.response.Event
import com.example.appevent.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    private val _eventDetail = MutableLiveData<Event?>()
    val eventDetail: LiveData<Event?> get() = _eventDetail

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val logTag = "DetailViewModel"

    fun fetchEventDetail(eventId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiConfig.instance.getEventDetail(eventId)

                if (response.isSuccessful && response.body() != null) {
                    _eventDetail.value = response.body()?.event
                    _error.value = null
                } else {
                    _eventDetail.value = null
                    _error.value = "Data tidak tersedia. Coba lagi nanti."
                }
            } catch (e: Exception) {
                _eventDetail.value = null
                _error.value = "Koneksi gagal. Periksa jaringan Anda dan coba lagi."
            } finally {
                _isLoading.value = false
            }
        }
    }
}

