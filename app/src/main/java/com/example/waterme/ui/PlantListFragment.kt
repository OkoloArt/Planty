package com.example.waterme.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.example.waterme.R
import com.example.waterme.adapter.PlantViewPagerAdapter
import com.example.waterme.databinding.FragmentPlantListBinding
import com.example.waterme.model.Plants
import com.example.waterme.viewmodel.PlantViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class PlantListFragment : Fragment() {

    private var _binding: FragmentPlantListBinding? = null
    private val binding get() = _binding!!

    private var plantList = arrayListOf<Plants>()
    private lateinit var pagerAdapter: PlantViewPagerAdapter
    private val plantViewModel: PlantViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentPlantListBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadCards()
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                (requireActivity() as AppCompatActivity).supportActionBar?.title =
                    plantList[position].plantTitle
            }

            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })

        binding.fab.setOnClickListener {
            val modalBottomSheet = AddPlantFragment()
            fragmentManager?.let {
                modalBottomSheet.show(it, AddPlantFragment.TAG)
            }
        }

    }

    private fun loadCards() {
        plantViewModel.plants.observe(viewLifecycleOwner) {
            it?.let {
                addPlants(it)
            }
            pagerAdapter = PlantViewPagerAdapter(requireContext(), plantList, findNavController(),
                requireFragmentManager(),plantViewModel) { plants ->
                plants.let {
                    plantViewModel.setCurrent(plants)
                }
            }

            binding.viewPager.adapter = pagerAdapter
            binding.viewPager.setPadding(100, 0, 100, 0)
            pagerAdapter.notifyDataSetChanged()

        }
        plantViewModel.getRealTimeUpdate()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addPlants(plants: Plants) {
        if (!plantList.contains(plants)) {
            plantList.add(plants)
        }else{
            if (plants.isDeleted){
                plantList.remove(plants)
            }
        }
    }
}