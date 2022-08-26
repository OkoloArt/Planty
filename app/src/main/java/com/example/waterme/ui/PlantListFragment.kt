package com.example.waterme.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.waterme.R
import com.example.waterme.adapter.PlantViewPagerAdapter
import com.example.waterme.databinding.FragmentPlantListBinding
import com.example.waterme.model.PlantData

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class PlantListFragment : Fragment() {


    private var _binding: FragmentPlantListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var plantList : ArrayList<PlantData>
    private lateinit var pagerAdapter: PlantViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPlantListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(position: Int,
                                        positionOffset: Float,
                                        positionOffsetPixels: Int) {
                (requireActivity() as AppCompatActivity).supportActionBar?.title = plantList[position].plantTitle
            }
        })
    }

    private fun loadCards(){

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}