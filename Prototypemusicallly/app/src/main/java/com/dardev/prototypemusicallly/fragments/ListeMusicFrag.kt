package com.dardev.prototypemusicallly.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dardev.prototypemusicallly.R
import com.dardev.prototypemusicallly.adapter.MusicAdapter
import com.dardev.prototypemusicallly.databinding.FragmentListeMusicBinding
import com.dardev.prototypemusicallly.helper.Constants
import com.dardev.prototypemusicallly.helper.Constants.toast
import com.dardev.prototypemusicallly.model.Music
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration


class ListeMusicFrag : Fragment(R.layout.fragment_liste_music) {

    private var _binding:FragmentListeMusicBinding?=null
    private val binding get()=_binding!!
    private var musicList:MutableList<Music> = ArrayList()
    private lateinit var  musicAdapter: MusicAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentListeMusicBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setActionBar(binding.toolbarlm)
        chargeMusic()
        setupRecyclerView()
        VerifUserPermission()

    }

    private fun setupRecyclerView(){
        musicAdapter= MusicAdapter()
        binding.rvListeMusic.apply {
            layoutManager=LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter=musicAdapter
            addItemDecoration(object :DividerItemDecoration(
                    activity,LinearLayout.VERTICAL
            ){})

        }

        musicAdapter.differ.submitList(musicList)
        musicList.clear()
    }

    //music load
    private fun chargeMusic(){
        val allMusicURI=MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection=MediaStore.Audio.Media.IS_MUSIC+"!=0"
        val sortOrder="${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        val cursor=activity?.applicationContext?.contentResolver!!.query(
            allMusicURI,null,selection,null,sortOrder
        )

        if(cursor!=null){

            while(cursor.moveToNext()){
                val musicUri=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val musicAuteur=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val musicTitre=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                val musicDuree=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))


                val musicDureeLong=musicDuree.toLong()
                musicList.add(
                    Music(
                        musicTitre,musicAuteur,musicUri,
                        Constants.dureeConversion(musicDureeLong)
                    )
                )
            }

            cursor.close()
        }
    }



    //Verif permission

    private fun VerifUserPermission(){
        if(activity?.let{
            ActivityCompat.checkSelfPermission(
                it,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.REQUEST_CODE_FOR_PERMISSIONS
            )
            return
        }
        chargeMusic()
    }

    //request permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            Constants.REQUEST_CODE_FOR_PERMISSIONS->
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    chargeMusic()
                }else{
                    activity?.toast("Permission Refusé")
                }
            else->{
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

}