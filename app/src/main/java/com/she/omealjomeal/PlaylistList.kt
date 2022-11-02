package com.she.omealjomeal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.she.omealjomeal.databinding.ActivityPlaylistListBinding


class PlaylistList : AppCompatActivity() {

    private val binding by lazy { ActivityPlaylistListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 리사이클러뷰
        val data: MutableList<Playlist> = loadData_test()        // list of Playlist
        Log.d(TAG, "data에 Playlist리스트 저장 -> $data")
        var adapter = PlaylistRecyclerAdapter()
        adapter.listPlaylist = data
        Log.d(TAG, "어댑터의 listPlaylist에 Playlist리스트 전달 -> ${adapter.listPlaylist}")
        binding.playlistRecyclerView.adapter = adapter
        binding.playlistRecyclerView.layoutManager = LinearLayoutManager(this)

        // SoundList 액티비티로 이동

    }

    fun loadData_test(): MutableList<Playlist> {
        val data: MutableList<Playlist> = mutableListOf()



        var c1 = mutableListOf("-NFqN-1SP6ypQVrJ146e")
        var c2 = mutableListOf("-NE18u6wncx70OYdxjqp", "-NE192PaMztCJEoIKPGU", "-NFJVqVOClthJjdIrrp4")
        var c3 = mutableListOf("-NFJW5Q6kXPNkYqviSF2", "-NFJWT6KprEMBSqWY_KD")

        var p1 = Playlist("듣기 좋은 플레이리스트", "유저", "images/temp_1665409715572.jpeg", c1)
        var p2 = Playlist("맛있는 플레이리스트", "다른 유저", "images/temp_1665409715572.jpeg", c2)
        var p3 = Playlist("배고픈 플레이리스트", "또다른 유저", "images/temp_1665409715572.jpeg", c3)

        Log.d(TAG, "듣기 좋은 플레이리스트.soundIdList -> ${p1.soundIdList}")
        Log.d(TAG, "맛있는 플레이리스트.soundIdList -> ${p2.soundIdList}")
        Log.d(TAG, "배고픈 플레이리스트.soundIdList -> ${p3.soundIdList}")

        data.add(p1)
        data.add(p2)
        data.add(p3)
        Log.d(TAG, "[playlist p1, p2, p3 added to data -> ${data}")

        return data
    }
}