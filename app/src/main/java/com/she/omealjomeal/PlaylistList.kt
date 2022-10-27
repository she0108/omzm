package com.she.omealjomeal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.she.omealjomeal.databinding.ActivityPlaylistListBinding

class PlaylistList : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding by lazy { ActivityPlaylistListBinding.inflate(layoutInflater) }
        setContentView(binding.root)

        // 리사이클러뷰
        val data:MutableList<Playlist> = loadData_test()
        var adapter = PlaylistRecyclerAdapter()
        adapter.listPlaylist = data
        binding.playlistRecyclerView.adapter = adapter
        binding.playlistRecyclerView.layoutManager = LinearLayoutManager(this)

        // SoundList 액티비티로 이동
        val intent = Intent(this, SoundList::class.java)
    }

    fun loadData_test(): MutableList<Playlist> {
        val data: MutableList<Playlist> = mutableListOf()

        var p1 = Playlist("듣기 좋은 플레이리스트", "유저", "images/temp_1665409715572.jpeg")
        var p2 = Playlist("맛있는 플레이리스트", "다른 유저", "images/temp_1665409715572.jpeg")
        var p3 = Playlist("배고픈 플레이리스트", "또다른 유저", "images/temp_1665409715572.jpeg")

        var c1 = mutableListOf("-NE18u6wncx70OYdxjqp")
        var c2 = mutableListOf("-NE18u6wncx70OYdxjqp", "-NE192PaMztCJEoIKPGU", "-NFJVqVOClthJjdIrrp4")
        var c3 = mutableListOf("-NFJW5Q6kXPNkYqviSF2", "-NFJWT6KprEMBSqWY_KD")

        p1.soundIdList.addAll(c1)
        p2.soundIdList.addAll(c2)
        p3.soundIdList.addAll(c3)

        data.add(p1)
        data.add(p2)
        data.add(p3)

        return data
    }
}