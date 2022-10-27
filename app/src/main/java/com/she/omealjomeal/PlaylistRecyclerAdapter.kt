package com.she.omealjomeal

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.she.omealjomeal.databinding.PlaylistRecyclerBinding

class PlaylistRecyclerAdapter: RecyclerView.Adapter<PlaylistRecyclerHolder>() {
    var listPlaylist = mutableListOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistRecyclerHolder {
        val binding = PlaylistRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistRecyclerHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistRecyclerHolder, position: Int) {
        val playlist = listPlaylist.get(position)
        holder.setPlaylist(playlist)
    }

    override fun getItemCount(): Int {
        return listPlaylist.size
    }


}

class PlaylistRecyclerHolder(val binding: PlaylistRecyclerBinding): RecyclerView.ViewHolder(binding.root) {

//    init {
//        binding.root.setOnClickListener {
//            // 선택한 플레이리스트의 sound 목록 화면으로 이동 -> 그러려면 선택된 Playlist를 받아서 다음 액티비티로 같이 넘겨줘야 되는데.. 어떻게?
//        }
//
//        binding.btnPlay.setOnClickListener {
//            // 해당 사운드의 재생화면으로 이동
//        }
//    }

    fun setPlaylist(playlist: Playlist) {
        binding.run {
            textTitle.text = playlist.title
//            imagePlaylist.setImageURI()
        }

        // 아이템 클릭 시 SoundList 액티비티로 전환, 동시에 intent를 통해 선택한 playlist 전달
        val context = binding.root.context
        itemView.setOnClickListener {
            val intent = Intent(context, SoundList::class.java)
            intent.putExtra("playlist", playlist);
            intent.run { context.startActivity(this) }
        }
    }
}