package com.example.figmamudical

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.figmamudical.databinding.FragmentSongwritingActPageBinding
import java.util.concurrent.Executors


class SongwritingActPage : Fragment() {

    private var songFinished = false
    private var actState = 0
    private var songMediaPlayer: MediaPlayer? = null

    private val viewModel: UserdataViewModel by activityViewModels {
        UserdataViewModelFactory(
            (activity?.application as UserdataApplication).database.userdataDao()
        )
    }

    private var _binding: FragmentSongwritingActPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSongwritingActPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        putThings()
        binding.apply {
            // Assign the fragment
            songwritingactpageFragment = this@SongwritingActPage
        }

        binding.questionButton.setOnClickListener {
            val fbDialogue = Dialog(this.context!!)
            fbDialogue.window?.setBackgroundDrawable(ColorDrawable(Color.argb(100, 0, 0, 0)))
            val mudiQuestionLayout = MainActivity.getDialogContext(1)
            fbDialogue.setContentView(mudiQuestionLayout)
            fbDialogue.setCancelable(true)
            fbDialogue.show()
        }

        MainActivity.initEmotion()

        MainActivity.startCameraOnFragment(this.context, this.viewLifecycleOwner)
        MainActivity.cameraExecutor = Executors.newSingleThreadExecutor()
        MainActivity.toggleAudioThreading(this.context)
    }

    private fun putThings() {
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
        val songTag = when(viewModel.getSong()){
            1->R.string.song1_tag
            2->R.string.song2_tag
            3->R.string.song3_tag
            4->R.string.song4_tag
            else->R.string.song5_tag
        }
        binding.songCover.background = ResourcesCompat.getDrawable(resources, songImage, null)
        binding.songTitle.text = resources.getString(songName)
        binding.tagText.text = resources.getString(songTag)
        val stub = binding.songBoxChosen.viewStub
        stub?.layoutResource = when(viewModel.getSong()){
            1->R.layout.songwriting_song1_box
            2->R.layout.songwriting_song2_box
            3->R.layout.songwriting_song3_box
            4->R.layout.songwriting_song4_box
            else->R.layout.songwriting_song5_box
        }
        stub?.inflate()
    }

    fun playSong() {
        if(songMediaPlayer == null) {
            val songFile = when(viewModel.getSong()) {
                1->R.raw.song1_inst
                2->R.raw.song2_inst
                3->R.raw.song3_inst
                4->R.raw.song4_inst
                else->R.raw.song5_inst
            }
            songMediaPlayer = MediaPlayer.create(context, songFile)
            songMediaPlayer?.start()
            songMediaPlayer?.setOnCompletionListener {
                songFinished = true
                if(actState == 1) binding.songactNextButton.background = ResourcesCompat.getDrawable(resources, R.drawable.rectangle_small_selected, null)
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
        songMediaPlayer?.reset()
        songMediaPlayer?.release()
        songMediaPlayer = null
        if(actState == 0) {
            songFinished = false
            actState = 1
            binding.marker4.background = null
            binding.text4.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
            binding.marker5.background = ResourcesCompat.getDrawable(resources, R.drawable.mudi_marker, null)
            binding.text5.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
            binding.songactNextText.text = resources.getString(R.string.finish)
            songFinished = true
        }
        else if(songFinished){
            val username = viewModel.getCurUsername()
            viewModel.getUser(username).observe(this.viewLifecycleOwner) { selectedUser ->
                viewModel.done(
                    selectedUser.id,
                    selectedUser.userName,
                    selectedUser.userPassword,
                    selectedUser.userEmail,
                    selectedUser.userChoice1,
                    selectedUser.userChoice2,
                    selectedUser.joinDate,
                    selectedUser.segmentsDone
                )
                MainActivity.toggleAudioThreading(this.context)
                findNavController().navigate(R.id.action_songwritingActPage_to_emotionAnalysisPage)
            }
        }
    }
}