package com.example.waterme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.waterme.NODE_PLANTS
import com.example.waterme.model.Plants
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class PlantViewModel : ViewModel() {

    private val dbPlants = FirebaseDatabase.getInstance().getReference(NODE_PLANTS)

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?> get() = _result

    private val _plants = MutableLiveData<Plants>()
    val plants: LiveData<Plants> get() = _plants

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

    private val childEventListener = object: ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
           val plants = snapshot.getValue(Plants::class.java)
            plants?.plantId = snapshot.key
            _plants.value = plants!!
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    fun getRealTimeUpdate(){
        dbPlants.addChildEventListener(childEventListener)
    }

    override fun onCleared() {
        super.onCleared()
        dbPlants.removeEventListener(childEventListener)
    }
}