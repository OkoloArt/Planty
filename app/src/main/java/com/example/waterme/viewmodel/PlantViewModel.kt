package com.example.waterme.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.waterme.NODE_PLANTS
import com.example.waterme.model.Plants
import com.example.waterme.worker.WaterReminderWorker
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class PlantViewModel(application: Application) : ViewModel() {

    private val dbPlants = FirebaseDatabase.getInstance().getReference(NODE_PLANTS)
    private val workManager = WorkManager.getInstance(application)

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?> get() = _result

    private val _plants = MutableLiveData<Plants>()
    val plants: LiveData<Plants> get() = _plants

    private val _currentPlant = MutableLiveData<Plants>()
    val currentPlant: LiveData<Plants> get() = _currentPlant

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

    fun setCurrent(plants: Plants?) {
        _currentPlant.value = plants!!
    }

    private val childEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val plants = snapshot.getValue(Plants::class.java)
            plants?.plantId = snapshot.key
            _plants.value = plants!!
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val plants = snapshot.getValue(Plants::class.java)
            plants?.plantId = snapshot.key
            _plants.value = plants!!
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val plants = snapshot.getValue(Plants::class.java)
            plants?.plantId = snapshot.key
            plants?.isDeleted = true
            _plants.value = plants!!
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    fun getRealTimeUpdate() {
        dbPlants.addChildEventListener(childEventListener)
    }

    /**
     * Retrieve an item from the repository.
     */
    fun retrieveItem(plants: Plants): Plants {
        return plants
    }

    fun isUserInputValid(plantName: String): Boolean {
        if (plantName.isBlank()) {
            return false
        }
        return true
    }

    fun updatePlant(plants: Plants) {
        dbPlants.child(plants.plantId!!).setValue(plants).addOnCompleteListener {
            if (it.isSuccessful) {
                _result.value = null
            } else {
                _result.value = it.exception
            }
        }
    }

    fun deletePlant(plants: Plants) {
        dbPlants.child(plants.plantId!!).setValue(null).addOnCompleteListener {
            if (it.isSuccessful) {
                _result.value = null
            } else {
                _result.value = it.exception
            }
        }
    }

    internal fun scheduleReminder(
        duration: Long,
        unit: TimeUnit,
        plantName: String,
    ) {
        // TODO: create a Data instance with the plantName passed to it
        val data = createInputData(plantName)

        // TODO: Generate a OneTimeWorkRequest with the passed in duration, time unit, and data
        //  instance
        val request = OneTimeWorkRequestBuilder<WaterReminderWorker>()
            .setInputData(data)
            .setInitialDelay(duration, unit)
            .build()

        // TODO: Enqueue the request as a unique work request
        workManager.enqueue(request)
    }

    private fun createInputData(plantName: String): Data {
        val data = Data.Builder()
        data.putString(WaterReminderWorker.nameKey, plantName)
        return data.build()
    }

    override fun onCleared() {
        super.onCleared()
        dbPlants.removeEventListener(childEventListener)
    }
}

class PlantViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(PlantViewModel::class.java)) {
            PlantViewModel(application) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}