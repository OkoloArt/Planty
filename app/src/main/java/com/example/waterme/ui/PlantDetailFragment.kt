package com.example.waterme.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.waterme.R
import com.example.waterme.databinding.FragmentPlantDetailBinding
import com.example.waterme.model.Plants
import com.example.waterme.viewmodel.PlantViewModel
import com.example.waterme.viewmodel.PlantViewModelFactory
import com.google.android.material.chip.Chip
import com.squareup.picasso.Picasso

/**
 * A simple [Fragment] subclass.
 * Use the [PlantDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlantDetailFragment : Fragment() {

    private val defaultTime = "00 : 00 AM"

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
        initAction(id)

    }

    private fun initAction(plants: Plants) {
        for (date in plants.plantAction!!) {

            // Create TextView programmatically.
            val textView =
                layoutInflater.inflate(R.layout.action_text, binding.rootLayout, false) as TextView

            textView.gravity = Gravity.CENTER
            textView.text = date

            // Add TextView to LinearLayout
            binding.rootLayout.addView(textView)
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
            (requireActivity() as AppCompatActivity).supportActionBar?.title =  plants.plantTitle
            if (plants.plantReminderTime.isNullOrEmpty()){
                plantReminderTime.text = if (plants.plantReminderTime!![0] < 12){
                    "${plants.plantReminderTime?.get(0)} : ${plants.plantReminderTime?.get(1)} AM"
                }else{
                    "${plants.plantReminderTime?.get(0)} : ${plants.plantReminderTime?.get(1)} PM"
                }
            }else{
                plantReminderTime.text = defaultTime
            }
            plantReminderDays.text = plants.plantReminderDays?.joinToString(". ")
        }
    }
}