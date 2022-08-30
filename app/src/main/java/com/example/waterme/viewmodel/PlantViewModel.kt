package com.example.waterme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.waterme.NODE_PLANTS
import com.example.waterme.model.Plants
import com.google.firebase.database.FirebaseDatabase

class PlantViewModel : ViewModel() {

    private val dbPlants = FirebaseDatabase.getInstance().getReference(NODE_PLANTS)

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?> get() = _result


    fun addPlants(plants: Plants) {
        plants.plantId = dbPlants.push().key

        dbPlants.child(plants.plantId!!).setValue(plants).addOnCompleteListener {
            if (it.isSuccessful) {
                _result.value = null
            } else {
                _result.value = it.exception
            }
        }
    }
}