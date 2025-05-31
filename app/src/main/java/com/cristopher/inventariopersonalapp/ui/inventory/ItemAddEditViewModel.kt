package com.cristopher.inventariopersonalapp.ui.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cristopher.inventariopersonalapp.data.model.Item
import com.cristopher.inventariopersonalapp.data.repository.AuthRepository
import com.cristopher.inventariopersonalapp.data.repository.ItemRepository
import kotlinx.coroutines.launch

class ItemAddEditViewModel(
    private val itemRepository: ItemRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _saveResult = MutableLiveData<Result<Boolean>>()
    val saveResult: LiveData<Result<Boolean>> = _saveResult

    fun saveItem(item: Item) {
        val currentUserId = authRepository.getCurrentUserId()
        if (currentUserId == null) {
            _saveResult.postValue(Result.failure(Exception("User not authenticated. Cannot save item.")))
            return
        }

        val itemToSave = item.copy(ownerId = currentUserId)

        viewModelScope.launch {
            val result = if (itemToSave.id.isEmpty()) {
                itemRepository.addItem(itemToSave)
            } else {
                itemRepository.updateItem(itemToSave)
            }
            _saveResult.postValue(result)
        }
    }
}