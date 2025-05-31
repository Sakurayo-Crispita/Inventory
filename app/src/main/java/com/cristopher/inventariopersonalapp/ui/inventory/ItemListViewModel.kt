package com.cristopher.inventariopersonalapp.ui.inventory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cristopher.inventariopersonalapp.data.model.Item
import com.cristopher.inventariopersonalapp.data.repository.AuthRepository
import com.cristopher.inventariopersonalapp.data.repository.ItemRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ItemListViewModel(
    private val itemRepository: ItemRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _item = MutableLiveData<Result<List<Item>>>()
    val item: LiveData<Result<List<Item>>> = _item

    private val _deleteResult = MutableLiveData<Result<Boolean>>()
    val deleteResult: LiveData<Result<Boolean>> = _deleteResult

    private val _isUserNotAuthenticated = MutableLiveData<Boolean>()
    val isUserNotAuthenticated: LiveData<Boolean> = _isUserNotAuthenticated

    fun loadItem() {
        val currentUserId = authRepository.getCurrentUserId()

        Log.d("ItemListViewModel", "Cargando item para ownerId: $currentUserId")

        if (currentUserId == null) {
            _isUserNotAuthenticated.value = true
            _item.value = Result.failure(Exception("User not authenticated"))
            Log.e("ItemListViewModel", "Usuario no autenticado al cargar item.")
            return
        }

        viewModelScope.launch {
            itemRepository.getItemForOwner(currentUserId).collectLatest { result ->
                result.onSuccess { item ->
                    Log.d("ItemListViewModel", "Flow emitió SUCCESS: ${item.size} item.")
                    _item.postValue(Result.success(item))
                }.onFailure { exception ->
                    Log.e(
                        "ItemListViewModel",
                        "Flow emitió FAILURE: ${exception.message}",
                        exception
                    )
                    _item.postValue(Result.failure(exception))
                }
            }
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            val result = itemRepository.deleteItem(itemId)

            result.onSuccess {
                Log.d("ItemListViewModel", "Item eliminada exitosamente: $itemId")
            }.onFailure { exception ->
                Log.e("ItemListViewModel", "Error al eliminar item: ${exception.message}", exception)
            }
            _deleteResult.postValue(result)
            loadItem()
        }
    }
}