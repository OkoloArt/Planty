package com.example.waterme.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.waterme.R
import com.example.waterme.databinding.FragmentAddPlantBinding
import com.example.waterme.model.Plants
import com.example.waterme.viewmodel.PlantViewModel
import com.example.waterme.viewmodel.PlantViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit


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
    private var plantAction = mutableListOf<String>()
    private var imageUri: Uri? = null
    private val pickImage = 100

    private val plantViewModel: PlantViewModel by activityViewModels {
        PlantViewModelFactory(requireActivity().application)
    }

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
        binding.plantsImage.setOnClickListener{
            setProfileImage()
        }
        binding.buttonSecond.setOnClickListener {
            Toast.makeText(requireContext(),"${getPlantAction()}",Toast.LENGTH_SHORT).show()
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

    private fun isUserInputValid(): Boolean{
        val plantName = binding.plantName.editText?.text.toString()
        return plantViewModel.isUserInputValid(plantName)
    }

    private fun getPlantName(): String {
        val plantName = binding.plantName.editText?.text.toString()
        plantViewModel.isUserInputValid(plantName)
        return plantName
    }

    private fun getPlantImage(): String{
        return imageUri.toString()
    }

    private fun getDaySelection(): MutableList<String> {
        val ids = binding.chipGroupDay.checkedChipIds
        dateArray.clear()
        ids.forEach { id ->
            dateArray.add(binding.chipGroupDay.findViewById<Chip>(id).text.toString())
        }
        return dateArray
    }

    private fun getPlantAction(): MutableList<String>{
        val ids = binding.plantAction.checkedButtonIds
        plantAction.clear()
        ids.forEach { id ->
            plantAction.add(binding.plantAction.findViewById<Button>(id).text.toString())
        }
        return plantAction
    }

    private fun getTimeSelection(): List<Int> {
        hours = binding.timePicker.hour
        minutes = binding.timePicker.minute
        binding.timePicker.setOnTimeChangedListener { _, hour, minute ->
            hours = hour
            minutes = minute
        }
        return listOf(hours, minutes)
    }

    private fun getAlarmNotificationSelection(): Boolean {
        binding.alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setAlarm = isChecked
                setReminder()
            }
        }
        return setAlarm
    }

    private fun setReminder() {
        val duration = TimeUnit.MILLISECONDS.toSeconds(difference())
        Toast.makeText(requireContext(), "$duration", Toast.LENGTH_SHORT)
            .show()
        plantViewModel.scheduleReminder(duration, TimeUnit.SECONDS, getPlantName(),dateArray.toTypedArray())
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
                    startActivityForResult(gallery, pickImage)
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            if (imageUri == null) return
            requireActivity().contentResolver.takePersistableUriPermission(imageUri!!,
                Intent.FLAG_GRANT_READ_URI_PERMISSION)
            binding.plantsImage.setImageURI(imageUri)
        }
    }
}