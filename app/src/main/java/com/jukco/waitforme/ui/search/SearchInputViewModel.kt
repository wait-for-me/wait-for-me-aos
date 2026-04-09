package com.jukco.waitforme.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class SearchInputViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _query: String = checkNotNull(savedStateHandle["query"])
    var query by mutableStateOf("")
        private set

    init {
        reset()
    }

    fun changeQuery(text: String) {
        query = text
    }

    fun reset() {
        query = _query
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = this.createSavedStateHandle()
                SearchInputViewModel(savedStateHandle)
            }
        }
    }
}