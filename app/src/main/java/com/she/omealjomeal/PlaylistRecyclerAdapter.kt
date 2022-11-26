package com.she.omealjomeal

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.she.omealjomeal.databinding.PlaylistRecyclerBinding
import kotlinx.android.synthetic.main.playlist_recycler.view.*
import kotlinx.android.synthetic.main.sound_recycler.view.*

class PlaylistRecyclerAdapter: RecyclerView.Adapter<PlaylistRecyclerAdapter.PlaylistRecyclerHolder>() {

    var listPlaylistID = mutableListOf<String>()    // 여기에 Playlist리스트 전달까지는 잘 됨

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistRecyclerHolder {
        val binding = PlaylistRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistRecyclerHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistRecyclerHolder, position: Int) {
        val playlistID = listPlaylistID.get(position)
        holder.setPlaylist(playlistID)
    }

    override fun getItemCount(): Int {
        return listPlaylistID.size
    }


    inner class PlaylistRecyclerHolder(val binding: PlaylistRecyclerBinding): RecyclerView.ViewHolder(binding.root) {

        var context: Context
        var playlistS_id: String = ""


        init {
            context = binding.root.context
            binding.root.setOnClickListener {
                SaveThings.userFragment.setPlaylistFragment(playlistS_id)
            }
        }

        private val database = Firebase.database("https://omzm-84564-default-rtdb.asia-southeast1.firebasedatabase.app/")   // (realtime database)
        private val playlistRef = database.getReference("playlists")
        val storage = Firebase.storage("gs://omzm-84564.appspot.com")   // (storage)

        fun setPlaylist(playlistID: String) {
            playlistS_id = playlistID
            playlistRef.child(playlistID).get().addOnSuccessListener {
                it.getValue(Playlist::class.java)?.let { playlist ->
                    Log.d("playlist", "playlist -> ${playlist}, ${playlist.id}, ${playlist.title}, ${playlist.imagePath}")
                    binding.textTitle.setText(playlist.title)
                    downloadImage(playlist.imagePath)
                }
            }.addOnFailureListener {
                Toast.makeText(context, "네크워크 상태를 확인해주세요.", Toast.LENGTH_SHORT).show()
                Log.d("playlist", "error=${it.message}")
            }
        }

        fun downloadImage(path: String) {
            storage.getReference(path).downloadUrl.addOnSuccessListener { uri ->
                Glide.with(context).load(uri).into(binding.root.imagePlaylist)
            }.addOnFailureListener {
                Log.e("playlist", "download error => ${it.message}")
            }
        }
    }
}

