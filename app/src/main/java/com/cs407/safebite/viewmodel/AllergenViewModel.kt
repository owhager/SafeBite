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
    val master = listOf(
        "Peanut", "Milk", "Egg", "Fish", "Gluten",
        "Lactose", "Nuts", "Sesame", "Shellfish", "Soy"
    )

    // ---- LIVE CHECKED STATE (from DB) ----
    private val dao = AllergenDatabase.getDatabase(context).allergenDao()
    private val _checked = mutableStateListOf<String>()
    val checked: List<String> = _checked

    // Get all custom allergens (not in master list)
    val customAllergens: List<String>
        get() = _checked.filter { it !in master }

    init { loadChecked() }

    private fun loadChecked() {
        viewModelScope.launch {
            val allergens = dao.getAllForUser(userUID)
            _checked.clear()
            _checked.addAll(allergens.filter { it.isChecked }.map { it.name })
        }
    }

    /** Called from UI when a checkbox toggles (works for both master and custom allergens) */
    fun toggle(name: String) {
        viewModelScope.launch {
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

    /** Add a custom allergen */
    fun addCustomAllergen(name: String): Boolean {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty() || trimmedName in _checked) {
            return false
        }
        viewModelScope.launch {
            dao.insert(Allergen(userUID = userUID, name = trimmedName, isChecked = true))
            _checked.add(trimmedName)
        }
        return true
    }

    /** Remove a custom allergen */
    fun removeCustomAllergen(name: String) {
        if (name !in master && name in _checked) {
            viewModelScope.launch {
                dao.delete(userUID, name)
                _checked.remove(name)
            }
        }
    }

    fun isChecked(name: String): Boolean = name in _checked
}