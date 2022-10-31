package com.she.omealjomeal

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.she.omealjomeal.databinding.PlaylistRecyclerBinding

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

        var playlistS: Playlist? = null

        init {
            binding.root.setOnClickListener {
                val intentPlaylist = Intent(binding.root.context, SoundList::class.java)
                intentPlaylist.putExtra("playlist", playlistS)            // selectedPlaylist에 선택한 것 말고 다른, 아예 목록에도 없는 Playlist instance가 전달됨. 왜?
                binding.root.context.startActivity(intentPlaylist)                          // 여기 이 intent_playlist가 SoundList.kt에서 접근이 안 됨
            }
        }

        fun setPlaylist(playlist: Playlist) {
            Log.d(TAG, "setPlaylist의 playlist변수로 선택된 Playlist 받음 -> ${playlist}")
            playlistS = playlist
            binding.run {
                textTitle.text = playlist.title
//            imagePlaylist.setImageURI()
            }
        }
    }


}

