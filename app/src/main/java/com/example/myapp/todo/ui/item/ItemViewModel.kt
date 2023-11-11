package com.example.myapp.todo.ui.item

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapp.MyApplication
import com.example.myapp.core.TAG
import com.example.myapp.todo.data.Item
import com.example.myapp.todo.data.ItemRepository
import kotlinx.coroutines.launch

data class ItemUiState(
    val itemId: String? = null,
    val item: Item,
    val isSaving: Boolean = false,
    val savingCompleted: Boolean = false,
    val savingError: Exception? = null,
)

class ItemViewModel(private val itemId: String?, private val itemRepository: ItemRepository) :
    ViewModel() {
    init {
        Log.d(TAG, "init")
    }

    var uiState: ItemUiState by mutableStateOf(
        ItemUiState(item = itemRepository.getItem(itemId)?.copy() ?: Item())
    )
        private set

    fun saveOrUpdateItem(text: String) {
        viewModelScope.launch {
            Log.d(TAG, "saveOrUpdateItem...");
            try {
                uiState = uiState.copy(isSaving = true, savingError = null)
                val item = uiState.item.copy(text = text)
                if (itemId == null) {
                    itemRepository.save(item)
                } else {
                    itemRepository.update(item)
                }
                Log.d(TAG, "saveOrUpdateItem succeeeded");
                uiState = uiState.copy(isSaving = false, savingCompleted = true)
            } catch (e: Exception) {
                Log.d(TAG, "saveOrUpdateItem failed");
                uiState = uiState.copy(isSaving = false, savingError = e)
            }
        }
    }

    companion object {
        fun Factory(itemId: String?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)
                ItemViewModel(itemId, app.container.itemRepository)
            }
        }
    }
}
