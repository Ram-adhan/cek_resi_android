package com.inbedroom.couriertracking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inbedroom.couriertracking.data.PreferencesManager
import com.inbedroom.couriertracking.data.entity.HistoryEntity
import com.inbedroom.couriertracking.data.room.HistoryRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val repository: HistoryRepository,
    private val local: PreferencesManager
) : ViewModel() {

    val historiesData: LiveData<List<HistoryEntity>> = repository.getHistories()
    private val _isChanged = MutableLiveData<Boolean>()
    val isChanged: LiveData<Boolean> = _isChanged

    fun deleteHistory(awb: String) {
        viewModelScope.launch {
            repository.deleteHistory(awb)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    fun editHistoryTitle(awb: String, title: String?) {
        if (title.isNullOrEmpty()) {
            _isChanged.postValue(false)
        } else {
            viewModelScope.launch {
                repository.changeTitle(awb, title)
                _isChanged.postValue(true)
            }

        }
    }
}