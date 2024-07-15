package com.example.task_manager.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class TodoViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<TodoItem>>(emptyList())
    val items: StateFlow<List<TodoItem>> = _items
    private var currentId = 0
    init {
        loadExistingNotes()
    }

    private fun loadExistingNotes() {
        val existingNotes = listOf(
            TodoItem(1,"Заметка 1", isChecked = false),
            TodoItem(2,"Заметка 2", isChecked = true),
            TodoItem(3,"Заметка 3", isChecked = false)
        )
        _items.value = existingNotes
        if (existingNotes.isNotEmpty()) {
            currentId = existingNotes.maxOf { it.id } + 1
        }
    }
    fun addItem(text: String) {

        _items.update { currentItems ->
            val newItem = TodoItem(id = currentId, text = text)
            currentId++
            val newItems = currentItems + newItem
            newItems

        }
    }
    fun toggleItemChecked(id: Int) {
        _items.update { currentItems ->
            val updatedItems = currentItems.map { item ->
                if (item.id == id) item.copy(isChecked = !item.isChecked) else item
            }
            //prefsManager.saveNotes(updatedItems)
            updatedItems
        }
    }
    fun removeItem(id: Int) {
        _items.update { currentItems ->
            val updatedItems = currentItems.filter { it.id != id }
            updatedItems
        }
    }
}