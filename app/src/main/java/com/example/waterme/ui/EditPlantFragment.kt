package com.example.waterme.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.waterme.R
import com.example.waterme.databinding.FragmentEditPlantBinding
import com.example.waterme.model.Plants
import com.example.waterme.viewmodel.PlantViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.squareup.picasso.Picasso

/**
 * A simple [Fragment] subclass.
 * Use the [EditPlantFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditPlantFragment(private val plants: Plants) : BottomSheetDialogFragment() {

    private var _binding: FragmentEditPlantBinding? = null
    private val binding get() = _binding!!

    private var dateArray = mutableListOf<String>()
    private var hours = 0
    private var minutes = 0
    private var setAlarm = true
    private var plantName = ""
    private var plantImage = ""
    private var plantAction = mutableListOf("Water", "Fertilize")

    private val plantViewModel: PlantViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditPlantBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initChip()
        //  preLoadCurrentPlantData()
        bind(plants)
    }

    private fun initChip() {
        val dateArr = resources.getStringArray(R.array.daysOfTheWeek)
        binding.chipGroupDay.isSingleSelection = false
        for (date in dateArr) {
            val chip =
                layoutInflater.inflate(R.layout.day_chip_group, binding.chipGroupDay, false) as Chip
            chip.text = date
            chip.isCloseIconVisible = false
            binding.chipGroupDay.addView(chip)
        }
    }

    private fun checkDaySelectionFromFirebase(child: View) {
        val text = (child as? Chip)?.text.toString()
        if (dateArray.isNotEmpty()) {
            for (i in dateArray.indices) {
                if (text == dateArray[i]) {
                    (child as? Chip)?.isChecked = true
                }
            }
        }
    }

    private fun preLoadCurrentPlantData() {
        plantViewModel.currentPlant.observe(viewLifecycleOwner) {
            it?.let {
                bind(it)
            }
        }
    }

    private fun checkAlarmNotificationSelectionFromFirebase() {

    }

    private fun getDaySelection() {
        val ids = binding.chipGroupDay.checkedChipIds
        dateArray.clear()
        ids.forEach { id ->
            dateArray.add(binding.chipGroupDay.findViewById<Chip>(id).text.toString())
        }
        val text = if (dateArray.isNotEmpty()) {
            dateArray.joinToString(", ")
        } else {
            "No Choice"
        }

    }

    /**
     * Binds views with the passed in item data.
     */
    private fun bind(plants: Plants) {
        binding.apply {
            Picasso.get().load(plants.plantImage).into(plantsImage)
            timePicker.apply {
                hour = plants.plantReminderHour!!
                minute = plants.plantReminderMinute!!
            }
            dateArray = if (plants.plantReminderDays.isNullOrEmpty()) {
                mutableListOf()
            } else {
                plants.plantReminderDays!!
            }
            binding.chipGroupDay.forEach { child ->
                checkDaySelectionFromFirebase(child)
                (child as? Chip)?.setOnCheckedChangeListener { _, _ ->
                    getDaySelection()
                }
            }
            updatePlants.setOnClickListener { updatePlantsData() }
        }
    }


    private fun updatePlantsData() {
        plants.plantImage =
            "https://cdn.dribbble.com/userupload/3511028/file/original-59f0713ce9ecd28bab4d7c053b8321b7.jpg?compress=1&resize=1024x768"
        plants.plantReminderDays = dateArray
        plantViewModel.updatePlant(plants)
    }

    companion object {
        const val TAG = "EditBottomSheet"
    }
}