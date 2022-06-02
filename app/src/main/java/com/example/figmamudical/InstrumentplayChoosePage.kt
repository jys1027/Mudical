package com.example.figmamudical

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.figmamudical.databinding.FragmentInstrumentplayChoosePageBinding


class InstrumentplayChoosePage : Fragment() {

    private val chosen = mutableListOf(false,false,false,false,false,false,false,false,false)
    private var instrument1MediaPlayer: MediaPlayer? = null
    private var instrument2MediaPlayer: MediaPlayer? = null
    private var instrument3MediaPlayer: MediaPlayer? = null
    private var instrument4MediaPlayer: MediaPlayer? = null
    private var instrument5MediaPlayer: MediaPlayer? = null
    private var instrument6MediaPlayer: MediaPlayer? = null
    private var instrument7MediaPlayer: MediaPlayer? = null
    private var instrument8MediaPlayer: MediaPlayer? = null

    private val viewModel: UserdataViewModel by activityViewModels {
        UserdataViewModelFactory(
            (activity?.application as UserdataApplication).database.userdataDao()
        )
    }

    private var _binding: FragmentInstrumentplayChoosePageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInstrumentplayChoosePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            // Assign the fragment
            instrumentplaychoosepageFragment = this@InstrumentplayChoosePage
        }
    }

    private fun offAllInstruments() {
        instrument1MediaPlayer?.reset()
        instrument1MediaPlayer?.release()
        instrument1MediaPlayer = null
        instrument2MediaPlayer?.reset()
        instrument2MediaPlayer?.release()
        instrument2MediaPlayer = null
        instrument3MediaPlayer?.reset()
        instrument3MediaPlayer?.release()
        instrument3MediaPlayer = null
        instrument4MediaPlayer?.reset()
        instrument4MediaPlayer?.release()
        instrument4MediaPlayer = null
        instrument5MediaPlayer?.reset()
        instrument5MediaPlayer?.release()
        instrument5MediaPlayer = null
        instrument6MediaPlayer?.reset()
        instrument6MediaPlayer?.release()
        instrument6MediaPlayer = null
        instrument7MediaPlayer?.reset()
        instrument7MediaPlayer?.release()
        instrument7MediaPlayer = null
        instrument8MediaPlayer?.reset()
        instrument8MediaPlayer?.release()
        instrument8MediaPlayer = null
    }

    fun toggleChoose(idx: Int) {
        //글자는 toggle
        //악기소리는 마지막으로 고른것만 나게
        chosen[idx] = !chosen[idx]

        var totalCnt = 0
        var now = 0
        repeat(8) {
            now ++
            totalCnt += if(chosen[now]) 1 else 0
        }
        binding.songlistenNextButton.background = ResourcesCompat.getDrawable(resources, if(totalCnt == 5) R.drawable.rectangle_small_selected else R.drawable.gray_button_frame, null)

        offAllInstruments()

        fun getColor(sel: Boolean): Int = run { return ContextCompat.getColor(requireContext(), if(sel) R.color.yellow else R.color.black) }
        when(idx) {
            1->binding.instrument1Text
            2->binding.instrument2Text
            3->binding.instrument3Text
            4->binding.instrument4Text
            5->binding.instrument5Text
            6->binding.instrument6Text
            7->binding.instrument7Text
            else->binding.instrument8Text
        }.setTextColor(getColor(chosen[idx]))

        if(chosen[idx]){
            val soundFile = when (idx) {
                1 -> R.raw.instrument1_sound
                2 -> R.raw.instrument2_sound
                3 -> R.raw.instrument3_sound
                4 -> R.raw.instrument4_sound
                5 -> R.raw.instrument5_sound
                6 -> R.raw.instrument6_sound
                7 -> R.raw.instrument7_sound
                else -> R.raw.instrument8_sound
            }
            when (idx) {
                1 -> instrument1MediaPlayer = MediaPlayer.create(context, soundFile)
                2 -> instrument2MediaPlayer = MediaPlayer.create(context, soundFile)
                3 -> instrument3MediaPlayer = MediaPlayer.create(context, soundFile)
                4 -> instrument4MediaPlayer = MediaPlayer.create(context, soundFile)
                5 -> instrument5MediaPlayer = MediaPlayer.create(context, soundFile)
                6 -> instrument6MediaPlayer = MediaPlayer.create(context, soundFile)
                7 -> instrument7MediaPlayer = MediaPlayer.create(context, soundFile)
                else -> instrument8MediaPlayer = MediaPlayer.create(context, soundFile)
            }
            val mediaPlayer = when (idx) {
                1 -> instrument1MediaPlayer
                2 -> instrument2MediaPlayer
                3 -> instrument3MediaPlayer
                4 -> instrument4MediaPlayer
                5 -> instrument5MediaPlayer
                6 -> instrument6MediaPlayer
                7 -> instrument7MediaPlayer
                else -> instrument8MediaPlayer
            }
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        }

    }

    fun confirmChoose() {
        var totalCnt = 0
        var now = 0
        repeat(8) {
            now ++
            totalCnt += if(chosen[now]) 1 else 0
        }
        if(totalCnt == 5) {
            offAllInstruments()
            viewModel.setInstrument(chosen)
            findNavController().navigate(R.id.action_instrumentplayChoosePage_to_instrumentplayPracticePage)
        }
        else {
            Toast.makeText(activity, "5개의 악기를 골라주세요", Toast.LENGTH_LONG).show()
        }
    }

}