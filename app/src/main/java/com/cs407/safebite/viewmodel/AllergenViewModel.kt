package com.cs407.safebite.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel

class AllergenViewModel : ViewModel() {
    // master list of allergens
    val allergens = mutableStateListOf("Peanut", "Milk", "Egg")

    // per-item checked state (true = show on Profile)
    val checkedMap = mutableStateMapOf<String, Boolean>().apply {
        allergens.forEach { this[it] = false }
    }

    fun addAllergen(name: String) {
        val exists = allergens.any { it.equals(name, ignoreCase = true) }
        if (!exists) {
            allergens.add(name)
            checkedMap.putIfAbsent(name, false)
        }
    }

    fun setChecked(name: String, checked: Boolean) {
        if (name in allergens) checkedMap[name] = checked
    }

    fun checkedItems(): List<String> = allergens.filter { checkedMap[it] == true }
}