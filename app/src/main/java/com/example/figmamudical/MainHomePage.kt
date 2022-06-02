package com.example.figmamudical

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.figmamudical.databinding.FragmentMainHomePageBinding
import java.util.*

class MainHomePage : Fragment() {

    private val viewModel: UserdataViewModel by activityViewModels {
        UserdataViewModelFactory(
            (activity?.application as UserdataApplication).database.userdataDao()
        )
    }

    private var _binding: FragmentMainHomePageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainHomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        putWeekScheduleText()
        binding.apply {
            // Assign the fragment
            mainhomepageFragment = this@MainHomePage
        }
    }

    private fun putWeekScheduleText() {
        var weekString: MutableList<String>
        var segmentString: MutableList<String>
        var nowWeek: Int

        val curUsername = viewModel.getCurUsername()
        viewModel.getUser(curUsername).observe(this.viewLifecycleOwner) { selectedUser ->
            val (first, second) = getWeekString(selectedUser.joinDate)
            weekString = first
            nowWeek = second
            segmentString = getSegmentString(selectedUser.userChoice1, selectedUser.userChoice2)

            //여기도 마찬가지로 이쪽 block 밖에서 업데이트하려하면 concurrency 문제로 터진다
            binding.box63TextTop.text = weekString[0]
            binding.box64TextTop.text = weekString[1]
            binding.box65TextTop.text = weekString[2]
            binding.box66TextTop.text = weekString[3]
            binding.box67TextTop.text = weekString[4]
            binding.thisWeekText.text = weekString[nowWeek]
            binding.box63TextBottom.text = segmentString[0]
            binding.box64TextBottom.text = segmentString[1]
            binding.box65TextBottom.text = segmentString[2]
            binding.box66TextBottom.text = segmentString[3]
            binding.box67TextBottom.text = segmentString[4]

            val grayCircle = ResourcesCompat.getDrawable(resources, R.drawable.circle_gray, null)
            val yellowCircle = ResourcesCompat.getDrawable(resources, R.drawable.circle_yellow, null)
            fun findCircle(w: Int): Drawable? = run { return if(nowWeek == w) yellowCircle else grayCircle }
            binding.ellipse63.background = findCircle(0)
            binding.ellipse64.background = findCircle(1)
            binding.ellipse65.background = findCircle(2)
            binding.ellipse66.background = findCircle(3)
            binding.ellipse67.background = findCircle(4)

            fun findColor(c: Boolean): Int = run { return ContextCompat.getColor(requireContext(), if(c) R.color.yellow else R.color.black) }
            binding.box63TextBottom.setTextColor(findColor(false))
            binding.box64TextBottom.setTextColor(findColor(false))
            binding.box65TextBottom.setTextColor(findColor(false))
            binding.box66TextBottom.setTextColor(findColor(false))
            binding.box67TextBottom.setTextColor(findColor(false))
            binding.box63TextBottom.setShadowLayer(0F, 0F,0F,findColor(false))
            binding.box64TextBottom.setShadowLayer(0F, 0F,0F,findColor(false))
            binding.box65TextBottom.setShadowLayer(0F, 0F,0F,findColor(false))
            binding.box66TextBottom.setShadowLayer(0F, 0F,0F,findColor(false))
            binding.box67TextBottom.setShadowLayer(0F, 0F,0F,findColor(false))
            val thisWeekText = when(nowWeek) {
                0->binding.box63TextBottom
                1->binding.box64TextBottom
                2->binding.box65TextBottom
                3->binding.box66TextBottom
                else->binding.box67TextBottom
            }
            thisWeekText.setTextColor(findColor(true))
            thisWeekText.setShadowLayer(4.0F,4.0F,4.0F,findColor(false))
            binding.thisWeekSegment.text = segmentString[nowWeek]
        }
    }

    fun goToSegment() {
        var weekString: MutableList<String>
        var segmentString: MutableList<String>
        var nowWeek: Int
        val curUsername = viewModel.getCurUsername()
        viewModel.getUser(curUsername).observe(this.viewLifecycleOwner) { selectedUser ->
            val (first, second) = getWeekString(selectedUser.joinDate)
            weekString = first
            nowWeek = second
            segmentString = getSegmentString(selectedUser.userChoice1, selectedUser.userChoice2)

            if(weekString.isEmpty()) return@observe
            if(segmentString[nowWeek].contains("송라이팅"))
                findNavController().navigate(R.id.action_mainHomePage_to_songwritingPage)
            else if(segmentString[nowWeek].contains("즉흥악기연주"))
                findNavController().navigate(R.id.action_mainHomePage_to_instrumentplayPage)
        }
    }

    fun barBelow(c: Int) {
        when(c) {
            0 -> findNavController().navigate(R.id.action_mainHomePage_to_mainMudicalPage)
            2 -> findNavController().navigate(R.id.action_mainHomePage_to_mainMyPage)
        }
    }

}