package com.cs407.safebite.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.safebite.data.Allergen
import com.cs407.safebite.data.AllergenDatabase
import kotlinx.coroutines.launch

class AllergenViewModel(
    private val userUID: String,
    context: Context
) : ViewModel() {

    // ---- MASTER LIST (never changes) ----
    private val master = listOf(
        "Peanut", "Milk", "Egg", "Fish", "Gluten",
        "Lactose", "Nuts", "Sesame", "Shellfish", "Soy"
    )

    // ---- LIVE CHECKED STATE (from DB) ----
    private val dao = AllergenDatabase.getDatabase(context).allergenDao()
    private val _checked = mutableStateListOf<String>()
    val checked: List<String> = _checked

    init { loadChecked() }

    private fun loadChecked() {
        viewModelScope.launch {
            val allergens = dao.getAllForUser(userUID)
            _checked.clear()
            _checked.addAll(allergens.filter { it.isChecked }.map { it.name })
        }
    }

    /** Called from UI when a checkbox toggles */
    fun toggle(name: String) {
        viewModelScope.launch {
            if (name !in master) return@launch

            if (name in _checked) {
                // Uncheck: delete row
                dao.delete(userUID, name)
                _checked.remove(name)
            } else {
                // Check: insert row
                dao.insert(Allergen(userUID = userUID, name = name, isChecked = true))
                _checked.add(name)
            }
        }
    }

    fun isChecked(name: String): Boolean = name in _checked
}