package com.example.waterme.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.waterme.R
import com.example.waterme.databinding.FragmentAddPlantBinding
import com.example.waterme.model.Plants
import com.example.waterme.viewmodel.PlantViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AddPlantFragment() : BottomSheetDialogFragment() {

    private var _binding: FragmentAddPlantBinding? = null
    private val binding get() = _binding!!

    private var dateArray = mutableListOf<String>()
    private var hours = 0
    private var minutes = 0
    private var setAlarm = false
    private var plantName = ""
    private var plantImage = ""
    private var plantAction = mutableListOf("Water", "Fertilize")

    private val plantViewModel: PlantViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAddPlantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        plantViewModel.result.observe(viewLifecycleOwner) {
            val message = if (it == null) {
                getString(R.string.added_plants)
            } else {
                getString(R.string.error, it.message)
            }
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
        initChip()
        getAlarmNotificationSelection()
        binding.chipGroupDay.forEach { child ->
            (child as? Chip)?.setOnCheckedChangeListener { _, _ ->
                getDaySelection()
            }
        }
        binding.buttonSecond.setOnClickListener {
            addNewPlants()
        }
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

    private fun addNewPlants() {

        val plants = Plants(
            "",
            "https://cdn.dribbble.com/userupload/3370424/file/original-661f45e4fbb55e46808ff29e152b0641.png?compress=1&resize=752x564",
            "Herbicus",
            "sibbbii",
            15,
            30,
            true,
            dateArray,
            plantAction
        )
        plantViewModel.addPlants(plants)
    }

    private fun getPlantName(): String {
        return binding.plantName.editText?.text.toString()
    }

    private fun getAlarmNotificationSelection() {
        binding.alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setAlarm = isChecked
            }
        }
    }

    private fun getTimeSelection(): List<Int> {
        binding.timePicker.setOnTimeChangedListener { _, hour, minute ->
            hours = hour
            minutes = minute
        }
        return listOf(hours,minutes)
    }

    private fun setHeight() {
        val bottomSheet = dialog?.findViewById(R.id.modal_bottomSheet) as RelativeLayout
        val standardBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        standardBottomSheetBehavior.peekHeight = RelativeLayout.LayoutParams.MATCH_PARENT
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}