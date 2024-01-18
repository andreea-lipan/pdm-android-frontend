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
import com.example.myapp.core.Result
import com.example.myapp.core.TAG
import com.example.myapp.todo.data.Item
import com.example.myapp.todo.data.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ItemUiState(
    val itemId: String? = null,
    val item: Item = Item(),
    var loadResult: Result<Item>? = null,
    var submitResult: Result<Item>? = null,
)

class ItemViewModel(private val itemId: String?, private val itemRepository: ItemRepository) : ViewModel() {

    var uiState: ItemUiState by mutableStateOf(ItemUiState(loadResult = Result.Loading))
        private set

    init {
        Log.d(TAG, "init")
        loadItem()
    }

    fun loadItem() {
        viewModelScope.launch {
            itemRepository.itemStream.collect { beehives ->
                if (!(uiState.loadResult is Result.Loading)) {
                    return@collect
                }
                val beehive = beehives.find { it._id == itemId } ?: Item()
                uiState = uiState.copy(item = beehive, loadResult = Result.Success(beehive))
            }
        }
    }


    fun saveOrUpdateItem(managerName: String, index: Int, autumnTreatment: Boolean, dateCreated: String) {
        viewModelScope.launch {
            Log.d(TAG, "saveOrUpdateItem...");
            try {
                uiState = uiState.copy(submitResult = Result.Loading)
                val item = uiState.item.copy(managerName = managerName, autumnTreatment = autumnTreatment, index = index, dateCreated = dateCreated)
                val savedItem: Item;
                if (itemId == null) {
                    savedItem = itemRepository.save(item)
                } else {
                    savedItem = itemRepository.update(item)
                }
                Log.d(TAG, "saveOrUpdateItem succeeeded");
                uiState = uiState.copy(submitResult = Result.Success(savedItem))
            } catch (e: Exception) {
                Log.d(TAG, "saveOrUpdateItem failed");
                uiState = uiState.copy(submitResult = Result.Error(e))
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
