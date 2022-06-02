package com.example.figmamudical

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.figmamudical.databinding.FragmentSongwritingListenPageBinding
import java.util.concurrent.Executors

class SongwritingListenPage : Fragment() {

    private var listenState = 0
    private var songFinished = false
    private var songMediaPlayer: MediaPlayer? = null

    private val viewModel: UserdataViewModel by activityViewModels {
        UserdataViewModelFactory(
            (activity?.application as UserdataApplication).database.userdataDao()
        )
    }

    private var _binding: FragmentSongwritingListenPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSongwritingListenPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSong()
        binding.apply {
            // Assign the fragment
            songwritinglistenpageFragment = this@SongwritingListenPage
        }

        binding.questionButton.setOnClickListener {
            val fbDialogue = Dialog(this.context!!)
            fbDialogue.window?.setBackgroundDrawable(ColorDrawable(Color.argb(100, 0, 0, 0)))
            fbDialogue.setContentView(R.layout.segment_question_box)
            fbDialogue.setCancelable(true)
            fbDialogue.show()
        }
    }

    private fun setSong() {
        val songImage = when(viewModel.getSong()){
            1->R.drawable.song_cover1
            2->R.drawable.song_cover2
            3->R.drawable.song_cover3
            4->R.drawable.song_cover4
            else->R.drawable.song_cover5
        }
        val songName = when(viewModel.getSong()){
            1->R.string.song1_title
            2->R.string.song2_title
            3->R.string.song3_title
            4->R.string.song4_title
            else->R.string.song5_title
        }
        val songFullText = when(viewModel.getSong()){
            1->R.string.song1_full_text
            2->R.string.song2_full_text
            3->R.string.song3_full_text
            4->R.string.song4_full_text
            else->R.string.song5_full_text
        }
        val songTag = when(viewModel.getSong()){
            1->R.string.song1_tag
            2->R.string.song2_tag
            3->R.string.song3_tag
            4->R.string.song4_tag
            else->R.string.song5_tag
        }
        binding.songCover.background = ResourcesCompat.getDrawable(resources, songImage, null)
        binding.songTitle.text = resources.getString(songName)
        binding.songFullText.text = resources.getString(songFullText)
        binding.tagText.text = resources.getString(songTag)
    }

    fun playSong() {
        if(songMediaPlayer == null) {
            val songFile = when(viewModel.getSong()) {
                1->R.raw.song1
                2->R.raw.song2
                3->R.raw.song3
                4->R.raw.song4
                else->R.raw.song5
            }
            songMediaPlayer = MediaPlayer.create(context, songFile)
            songMediaPlayer?.start()
            songMediaPlayer?.setOnCompletionListener {
                songFinished = true
                binding.songlistenNextButton.background = ResourcesCompat.getDrawable(resources, R.drawable.rectangle_small_selected, null)
            }
        }
        else {
            songMediaPlayer?.start()
        }
    }
    fun stopSong() {
        songMediaPlayer?.reset()
        songMediaPlayer?.release()
        songMediaPlayer = null
    }
    fun pauseSong() {
        songMediaPlayer?.pause()
    }

    fun done() {



        songFinished = true
        if(songFinished) {
            songMediaPlayer?.reset()
            songMediaPlayer?.release()
            songMediaPlayer = null
            if(listenState == 0) {
                songFinished = false
                binding.songlistenNextButton.background = ResourcesCompat.getDrawable(resources, R.drawable.gray_button_frame, null)
                listenState = 1
                binding.marker2.background = null
                binding.text2.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
                binding.marker3.background = ResourcesCompat.getDrawable(resources, R.drawable.mudi_marker, null)
                binding.text3.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
            }
            else{
                findNavController().navigate(R.id.action_songwritingListenPage_to_songwritingActPage)
            }
        }
    }

}
