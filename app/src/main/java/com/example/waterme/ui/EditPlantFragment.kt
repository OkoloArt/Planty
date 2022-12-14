package com.example.waterme.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.waterme.R
import com.example.waterme.databinding.FragmentEditPlantBinding
import com.example.waterme.model.Plants
import com.example.waterme.viewmodel.PlantViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import com.squareup.picasso.Picasso
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 * Use the [EditPlantFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditPlantFragment(private val plants: Plants) : BottomSheetDialogFragment() {

    private var _binding: FragmentEditPlantBinding? = null
    private val binding get() = _binding!!

    private var dateArray = mutableListOf<String>()
    private var timeArray = mutableListOf<Int>()
    private var setAlarm = false
    private var actionArray = mutableListOf<String>()

    private var imageUri: Uri? = null

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
        binding.plantsImage.setOnClickListener {
            setProfileImage()
        }
        bind(plants)
        binding.updatePlants.setOnClickListener {
            updatePlantsData()
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

    private fun checkDaySelectionFromFirebase(child: View, plants: Plants) {
        val text = (child as? Chip)?.text.toString()
        for (i in plants.plantReminderDays!!.indices){
            when(text){
                plants.plantReminderDays!![i] -> { (child as? Chip)?.isChecked = true}
            }
        }
    }

    private fun checkActionSelectionFromFirebase(plants: Plants) {
        val ids = binding.plantAction.checkedButtonIds
        if (ids.size == 0) {
                for (i in plants.plantAction!!.indices) {
                    when (plants.plantAction!![i]) {
                        "Water" -> {
                            binding.plantAction.check(R.id.water)
                        }
                        "Fertilize" -> {
                            binding.plantAction.check(R.id.fertilize)
                        }
                        "Prune" -> {
                            binding.plantAction.check(R.id.prune)
                        }
                        "Harvest" -> {
                            binding.plantAction.check(R.id.harvest)
                        }
                    }
                }
        }
    }

    /**
     * Binds views with the passed in item data.
     */
    private fun bind(plants: Plants) {
        binding.apply {
            if (plants.plantImage != null) {
                Picasso.get().load(plants.plantImage).into(plantsImage)
            } else {
                Picasso.get().load(R.drawable.plants).into(plantsImage)
            }
            timePicker.apply {
                hour = plants.plantReminderTime!![0]
                minute = plants.plantReminderTime!![1]
            }
            plantName.editText?.setText(plants.plantTitle!!)
            alarmSwitch.isChecked = plants.plantAlarmChoice!!


            chipGroupDay.forEach { child ->
                checkDaySelectionFromFirebase(child, plants)
            }
            checkActionSelectionFromFirebase(plants)
        }
    }

    private fun updatePlantsData() {
        if (isUserInputValid()) {
            plants.plantImage = getPlantImage()
            plants.plantTitle = getPlantName()
            plants.plantDescription = getPlantDescription()
            plants.plantReminderTime = getTimeSelection()
            plants.plantAlarmChoice = getAlarmNotificationSelection()
            plants.plantReminderDays = getDaySelection()
            plants.plantAction = getPlantAction()

            plantViewModel.updatePlant(plants)
            if (getAlarmNotificationSelection()) {
                setReminder(plants)
            }
            dismiss()
        } else {
            Toast.makeText(requireContext(),
                "Please Select the days, action and time as to when you want to be reminded",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun isUserInputValid(): Boolean {
        return plantViewModel.isUserInputValid(getPlantName(), getPlantImage(), getDaySelection(), getPlantAction(), getTimeSelection())
    }

    private fun getPlantImage(): String {
        if (imageUri == null){
            return plants.plantImage!!
        }
        return imageUri.toString()
    }

    private fun getPlantDescription(): String {
        return binding.plantDescription.editText?.text.toString()
    }

    private fun getPlantName(): String {
        val plantName = binding.plantName.editText?.text.toString()
        if (plantName.isBlank()) {
            binding.plantName.editText?.error = "name cannot be blank"
        }
        return plantName
    }

    private fun getDaySelection(): MutableList<String> {
        val ids = binding.chipGroupDay.checkedChipIds
        dateArray.clear()
        ids.forEach { id ->
            dateArray.add(binding.chipGroupDay.findViewById<Chip>(id).text.toString())
        }
        return dateArray
    }

    private fun getPlantAction(): MutableList<String> {
        val ids = binding.plantAction.checkedButtonIds
        actionArray.clear()
        ids.forEach { id ->
            actionArray.add(binding.plantAction.findViewById<Button>(id).text.toString())
        }
        return actionArray
    }

    private fun getTimeSelection(): MutableList<Int> {
        timeArray.clear()
        timeArray.add(binding.timePicker.hour)
        timeArray.add(binding.timePicker.minute)

        binding.timePicker.setOnTimeChangedListener { _, hour, minute ->
            timeArray.add(hour)
            timeArray.add(minute)
        }
        return timeArray
    }

    private fun getAlarmNotificationSelection(): Boolean {
        setAlarm = binding.alarmSwitch.isChecked
        binding.alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            setAlarm = isChecked
        }
        return setAlarm
    }

    private fun setReminder(plants: Plants) {
        val duration = TimeUnit.MILLISECONDS.toSeconds(difference())
        Toast.makeText(requireContext(), "$duration", Toast.LENGTH_SHORT)
            .show()
        plantViewModel.scheduleReminder(duration, TimeUnit.SECONDS, plants)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SimpleDateFormat")
    private fun difference(): Long {

        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinutes = calendar.get(Calendar.MINUTE)
        val currentSeconds = calendar.get(Calendar.SECOND)
        val selectedHour = getTimeSelection()[0]
        val selectedMinutes = getTimeSelection()[1]

        val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
        val startDate = simpleDateFormat.parse("$currentHour:$currentMinutes:$currentSeconds")
        val endDate = simpleDateFormat.parse("$selectedHour:$selectedMinutes:00")

        return endDate.time - startDate.time
    }

    private fun setProfileImage() {
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val gallery =
                        Intent(Intent.ACTION_OPEN_DOCUMENT,
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    gallery.flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                  pickImageLauncher.launch(gallery)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(requireContext(), "Permission failed", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: com.karumi.dexter.listener.PermissionRequest?,
                    p1: PermissionToken?,
                ) {
                    TODO("Not yet implemented")
                }

            }).check()
    }

    private var pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK)
        {
            imageUri = result.data?.data
            if (imageUri == null) return@registerForActivityResult
            requireActivity().contentResolver.takePersistableUriPermission(imageUri!! , Intent.FLAG_GRANT_READ_URI_PERMISSION)
            binding.plantsImage.setImageURI(imageUri)
        }
    }

    companion object {
        const val TAG = "EditBottomSheet"
    }
}