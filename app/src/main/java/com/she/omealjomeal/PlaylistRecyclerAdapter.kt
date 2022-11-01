package com.she.omealjomeal

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.she.omealjomeal.databinding.PlaylistRecyclerBinding
import kotlinx.android.synthetic.main.playlist_recycler.view.*
import kotlinx.android.synthetic.main.sound_recycler.view.*

class PlaylistRecyclerAdapter: RecyclerView.Adapter<PlaylistRecyclerAdapter.PlaylistRecyclerHolder>() {
    var listPlaylist = mutableListOf<Playlist>()    // 여기에 Playlist리스트 전달까지는 잘 됨

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistRecyclerHolder {
        val binding = PlaylistRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistRecyclerHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistRecyclerHolder, position: Int) {
        val playlist = listPlaylist.get(position)
        Log.d(TAG, "onBindViewHolder의 playlist변수에 선택한 Playlist instance 저장 -> ${position}, ${playlist}")    // 여기까지도 잘 됨
        holder.setPlaylist(playlist)
    }

    override fun getItemCount(): Int {
        return listPlaylist.size
    }


    class PlaylistRecyclerHolder(val binding: PlaylistRecyclerBinding): RecyclerView.ViewHolder(binding.root) {

        var context: Context
        var playlistS: Playlist? = null
        val storage = Firebase.storage("gs://omzm-84564.appspot.com")   // (storage)

        init {
            context = binding.root.context
            binding.root.setOnClickListener {
                val intentPlaylist = Intent(context, SoundList::class.java)
                intentPlaylist.putExtra("playlist", playlistS)            // selectedPlaylist에 선택한 것 말고 다른, 아예 목록에도 없는 Playlist instance가 전달됨. 왜?
                context.startActivity(intentPlaylist)                          // 여기 이 intent_playlist가 SoundList.kt에서 접근이 안 됨
            }
        }

        fun setPlaylist(playlist: Playlist) {
            Log.d(TAG, "setPlaylist의 playlist변수로 선택된 Playlist 받음 -> ${playlist}")
            playlistS = playlist
            binding.run {
                textTitle.text = playlist.title
            }
            downloadImage(playlist.imagePath)
        }

        fun downloadImage(path: String) {
            storage.getReference(path).downloadUrl.addOnSuccessListener { uri ->
                Glide.with(context).load(uri).into(binding.root.imagePlaylist)
            }.addOnFailureListener {
                Log.e("storage", "download error => ${it.message}")
            }
        }
    }


}

