package com.dardev.prototypemusicallly.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dardev.prototypemusicallly.databinding.MusicBinding
import com.dardev.prototypemusicallly.fragments.ListeMusicFrag
import com.dardev.prototypemusicallly.fragments.ListeMusicFragDirections
import com.dardev.prototypemusicallly.model.Music

class MusicAdapter:RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    inner class MusicViewHolder(val binding: MusicBinding):
            RecyclerView.ViewHolder(binding.root)

    private val differCallback= object : DiffUtil.ItemCallback<Music>(){
        override fun areItemsTheSame(oldItem: Music, newItem: Music): Boolean {
            return oldItem.musicUri==newItem.musicUri
        }

        override fun areContentsTheSame(oldItem: Music, newItem: Music): Boolean {
            return oldItem==newItem
        }
    }
    val differ=AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        return MusicViewHolder(
                MusicBinding.inflate(
                        LayoutInflater.from(parent.context),parent,false
                )
        )
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val currMusic=differ.currentList[position]

        holder.binding.apply {
            tvDuration.text=currMusic.duree
            titreMusic.text=currMusic.Titre
            artisteMusic.text=currMusic.Artist
            tvOrder.text="${position+1}"
        }

        holder.itemView.setOnClickListener {mView ->
            val direction = ListeMusicFragDirections
                    .actionListeMusicFragToMusicPlayerFrag(currMusic)
            mView.findNavController().navigate(direction)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}