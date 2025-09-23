package com.hg.qynnlauncher.utils

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "collectAsStateInVM"

// derivedStateOf doesn't work with StateFlow but we can convert a StateFlow as a State and that gets picked up so that's what we do here
fun <TValue> ViewModel.collectAsStateButInViewModel(flow: StateFlow<TValue>): State<TValue>
{
    return mutableStateOf(flow.value).apply {
        viewModelScope.launch {
            flow.collectLatest {
                value = it
            }
        }
    }
}

// derivedStateOf doesn't work with StateFlow but we can convert a StateFlow as a State and that gets picked up so that's what we do here
fun <TValue> ViewModel.collectAsStateButInViewModel(flow: Flow<TValue>, initialValue: TValue): State<TValue>
{
    return mutableStateOf(initialValue).apply {
        viewModelScope.launch {
            flow.collectLatest {
                value = it
            }
        }
    }
}