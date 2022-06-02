package com.example.figmamudical

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.figmamudical.databinding.FragmentSongwritingPageBinding

class SongwritingPage : Fragment() {

    private var selectedSong = 0

    private val viewModel: UserdataViewModel by activityViewModels {
        UserdataViewModelFactory(
            (activity?.application as UserdataApplication).database.userdataDao()
        )
    }

    private var _binding: FragmentSongwritingPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSongwritingPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            // Assign the fragment
            songwritingpageFragment = this@SongwritingPage
        }
    }

    fun selectSong(s: Int) {
        fun findColor(sel: Int, idx: Int): Int = run { return ContextCompat.getColor(requireContext(), if(sel==idx) R.color.yellow else R.color.black) }
        binding.songText1!!.setTextColor(findColor(s, 1))
        binding.songText2!!.setTextColor(findColor(s, 2))
        binding.songText3!!.setTextColor(findColor(s, 3))
        binding.songText4!!.setTextColor(findColor(s, 4))
        binding.songText5!!.setTextColor(findColor(s, 5))
        binding.songlistenNextButton!!.background = ResourcesCompat.getDrawable(resources, R.drawable.rectangle_small_selected, null)
        selectedSong = s
    }

    fun confirmSong() {
        viewModel.setSong(selectedSong)
        findNavController().navigate(R.id.action_songwritingPage_to_songwritingListenPage)
    }

}
