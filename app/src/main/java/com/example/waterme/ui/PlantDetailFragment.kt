package com.example.waterme.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.waterme.databinding.FragmentPlantDetailBinding
import com.example.waterme.model.Plants
import com.example.waterme.viewmodel.PlantViewModel
import com.example.waterme.viewmodel.PlantViewModelFactory
import com.squareup.picasso.Picasso

/**
 * A simple [Fragment] subclass.
 * Use the [PlantDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlantDetailFragment : Fragment() {

    private val safeArgs: PlantDetailFragmentArgs by navArgs()

    private var _binding: FragmentPlantDetailBinding? = null
    private val binding get() = _binding!!

    private val plantViewModel: PlantViewModel by activityViewModels {
        PlantViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPlantDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val id = arguments?.getSerializable(bundleKey) as? Plants
        val id = safeArgs.plants
        bind(id)

//        if () {
//            plantViewModel.currentPlant.observe(viewLifecycleOwner) { plants ->
//                plants?.let {
//                    bind(plants)
//                }
//            }
//        } else {
//            Toast.makeText(requireContext(), "Plant Not Empty", Toast.LENGTH_SHORT).show()
//        }

    }

    /**
     * Binds views with the passed in item data.
     */
    private fun bind(plants: Plants) {
        binding.apply {
            Picasso.get().load(plants.plantImage).into(plantsImage)
            plantTitle.text = plants.plantTitle
            plantReminderTime.text =
                "${plants.plantReminderHour} : ${plants.plantReminderMinute} PM"
            plantReminderDays.text = plants.plantReminderDays?.joinToString(". ")
            // editItem.setOnClickListener { editItem() }
        }
    }
}