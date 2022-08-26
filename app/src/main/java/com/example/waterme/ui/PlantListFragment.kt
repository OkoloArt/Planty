package com.example.waterme.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
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

    private var plantList = arrayListOf<PlantData>()
    private lateinit var pagerAdapter: PlantViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentPlantListBinding.inflate(inflater, container, false)
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
    }

    private fun loadCards() {

        plantList.add(PlantData(R.mipmap.test_image_1,
            "MarshMallow",
            "Description",
            "October 5, 2015"))
        plantList.add(PlantData(R.mipmap.test_image_1,
            "MarshMallow",
            "Description",
            "October 5, 2015"))

        pagerAdapter = PlantViewPagerAdapter(requireContext(), plantList)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.setPadding(100, 0, 100, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}