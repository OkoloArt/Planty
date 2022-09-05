package com.example.waterme.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.waterme.R
import com.example.waterme.databinding.FragmentAddPlantBinding
import com.example.waterme.model.Plants
import com.example.waterme.viewmodel.PlantViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AddPlantFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddPlantBinding? = null
    private val binding get() = _binding!!


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
        binding.buttonSecond.setOnClickListener {

            val plants = Plants( "", "https://cdn.dribbble.com/userupload/3370424/file/original-661f45e4fbb55e46808ff29e152b0641.png?compress=1&resize=752x564",
                "Herbicus","sibbbii","dqdqdqdqd")
            plantViewModel.addPlants(plants)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}