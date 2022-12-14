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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.waterme.R
import com.example.waterme.databinding.FragmentAddPlantBinding
import com.example.waterme.model.Plants
import com.example.waterme.viewmodel.PlantViewModel
import com.google.android.material.chip.Chip
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AddPlantFragment() : Fragment() {

    private var _binding: FragmentAddPlantBinding? = null
    private val binding get() = _binding!!

    private var dateArray = mutableListOf<String>()
    private var timeArray = mutableListOf<Int>()
    private var setAlarm = false
    private var actionArray = mutableListOf<String>()

    private var imageUri: Uri? = null
    private val pickImage = 100

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

      // setHeight()
        plantViewModel.result.observe(viewLifecycleOwner) {
            val message = if (it == null) {
                getString(R.string.added_plants)
            } else {
                getString(R.string.error, it.message)
            }
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
        initChip()
        binding.setPlantImage.setOnClickListener {
            setProfileImage()
        }
        binding.buttonSecond.setOnClickListener {
            addNewPlants()
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
        if (isUserInputValid()) {
           val plants = Plants(
                "",
                getPlantImage(),
                getPlantName(),
                getPlantDescription(),
                getTimeSelection(),
                getAlarmNotificationSelection(),
                getDaySelection(),
                getPlantAction()
            )
            plantViewModel.addPlants(plants)
            if (getAlarmNotificationSelection()){
                setReminder(plants)
            }
            val action = AddPlantFragmentDirections.actionSecondFragmentToFirstFragment()
            findNavController().navigate(action)
        }else{
            Toast.makeText(requireContext(),
                "Please select an image, the days, action and time as to when you want to be reminded",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun isUserInputValid(): Boolean {
        return plantViewModel.isUserInputValid(getPlantName(), getPlantImage(),getDaySelection(), getPlantAction(), getTimeSelection())
    }

    private fun getPlantImage(): String {
        if (imageUri == null){
            return ""
        }
        return imageUri.toString()
    }

    private fun getPlantName(): String {
        val plantName = binding.plantName.editText?.text.toString()
        if (plantName.isBlank()){ binding.plantName.editText?.error = "name cannot be blank"}
        return plantName
    }

    private fun getPlantDescription(): String {
        return binding.plantDescription.editText?.text.toString()
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

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    private var pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK)
        {
            // There are no request codes
            imageUri = result.data?.data
            if (imageUri == null) return@registerForActivityResult
            requireActivity().contentResolver.takePersistableUriPermission(imageUri!! , Intent.FLAG_GRANT_READ_URI_PERMISSION)
            binding.plantsImage.setImageURI(imageUri)
        }
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
}