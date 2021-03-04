package com.dardev.prototypemusicallly.fragments

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.dardev.prototypemusicallly.R
import com.dardev.prototypemusicallly.databinding.FragmentMusicPlayerBinding
import com.dardev.prototypemusicallly.helper.Constants
import com.dardev.prototypemusicallly.helper.Constants.toast
import com.dardev.prototypemusicallly.model.Music


class MusicPlayerFrag : Fragment(R.layout.fragment_music_player) {

    private var _binding :FragmentMusicPlayerBinding?=null
    private val binding get()=_binding!!
    private val args:MusicPlayerFragArgs by navArgs()
    private lateinit var music: Music
    private var mediaPlayer: MediaPlayer?=null
    private var seekLength: Int = 0
    private val seekForwardTime = 5 * 1000
    private val seekBackwardTime = 5 * 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding= FragmentMusicPlayerBinding.inflate(
                inflater,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        music=args.music!!
        mediaPlayer= MediaPlayer()

        binding.tvAuthor.text=music.Artist
        binding.tvTitle.text=music.Titre
        binding.tvDuration.text=music.duree

        binding.ibPlay.setOnClickListener {
            playmusic()
        }

        binding.ibForwardSong.setOnClickListener{
            forwardMusic()
        }

        binding.ibBackwardSong.setOnClickListener{
            backwardMusic()
        }

        binding.ibRepeat.setOnClickListener{
            repeatMusic()
        }

        binding.ibShuffle.setOnClickListener{
            shuffleMusic()
        }

        displayMusicArt()
    }

    private fun displayMusicArt(){
        val mediaMetadataRetriever=MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(music.musicUri)
        val data=mediaMetadataRetriever.embeddedPicture
        if(data!=null){
            val bitmap=BitmapFactory.decodeByteArray(
                data,0,data.size
            )
            binding.ibCover.setImageBitmap(bitmap)
        }
    }
    private fun forwardMusic(){
        if(mediaPlayer!=null){
            val aPosition:Int=mediaPlayer!!.currentPosition

            if(aPosition +seekForwardTime<=mediaPlayer!!.duration){
                mediaPlayer!!.seekTo(aPosition+seekForwardTime)
            }else{
                mediaPlayer!!.seekTo(mediaPlayer!!.duration)
            }

        }
    }

    private fun backwardMusic(){
        if(mediaPlayer!=null){
            val aPosition:Int=mediaPlayer!!.currentPosition

            if(aPosition - seekBackwardTime>=0){
                mediaPlayer!!.seekTo(aPosition-seekBackwardTime)
            }else{
                mediaPlayer!!.seekTo(0)
            }

        }
    }
    private fun repeatMusic(){
        if(!mediaPlayer!!.isLooping){

            mediaPlayer!!.isLooping=true
            binding.ibRepeat.setImageDrawable(
                ContextCompat.getDrawable(
                    activity?.applicationContext!!,
                    R.drawable.ic_repeat_red
                )
            )
        }else{
            mediaPlayer!!.isLooping=false
            binding.ibRepeat.setImageDrawable(
                ContextCompat.getDrawable(
                    activity?.applicationContext!!,
                    R.drawable.ic_repeat
                )
            )
        }
    }
    private fun shuffleMusic(){
            activity?.toast("Prototype ")
    }

    private fun updateSeekBar(){
        if(mediaPlayer!=null){
            binding.tvCurrentTime.text=Constants.dureeConversion(
                mediaPlayer!!.currentPosition.toLong()
            )
        }

        seekBarSetup()
        Handler().postDelayed(runnable,50)
    }

    var runnable= Runnable { updateSeekBar() }

    private fun seekBarSetup(){
        if (mediaPlayer!=null){
            binding.seekBar.progress=mediaPlayer!!.currentPosition
            binding.seekBar.max=mediaPlayer!!.duration

        }

        binding.seekBar.setOnSeekBarChangeListener(
            @SuppressLint("AppCompatCustomView")
        object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if(fromUser){
                    mediaPlayer!!.seekTo(progress)
                    binding.tvCurrentTime.text=
                        Constants.dureeConversion(progress.toLong())
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if(mediaPlayer!=null&&mediaPlayer!!.isPlaying){

                    if (seekBar!=null){
                        mediaPlayer!!.seekTo(seekBar.progress)
                    }
                }
            }

        })
    }

    private fun playmusic(){
        if(!mediaPlayer!!.isPlaying){
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(music.musicUri)
            mediaPlayer!!.prepare()
            mediaPlayer!!.seekTo(seekLength)
            mediaPlayer!!.start()

            binding.ibPlay.setImageDrawable(
                    ContextCompat.getDrawable(
                            activity?.applicationContext!!,
                            R.drawable.ic_pause
                    )
            )
            updateSeekBar()

        }else{

            mediaPlayer!!.pause()
            seekLength = mediaPlayer!!.currentPosition
            binding.ibPlay.setImageDrawable(
                    ContextCompat.getDrawable(
                            activity?.applicationContext!!,
                            R.drawable.ic_play
                    )
            )
        }

    }

    private fun clearMediaPlayer(){
        if(mediaPlayer!!.isPlaying){
            mediaPlayer!!.stop()
        }

        mediaPlayer!!.release()
        mediaPlayer=null

    }

    override fun onDestroy() {
        super.onDestroy()
        clearMediaPlayer()
    }
}