package com.she.omealjomeal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.she.omealjomeal.databinding.ActivityPlaylistListBinding


class PlaylistList : AppCompatActivity() {

    private val binding by lazy { ActivityPlaylistListBinding.inflate(layoutInflater) }
    private val database = Firebase.database("https://omzm-84564-default-rtdb.asia-southeast1.firebasedatabase.app/")   // (realtime database)
    private val playlistRef = database.getReference("playlists")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)




        // 리사이클러뷰
        /*val data: MutableList<Playlist> = loadData_test()        // 플레이리스트 정보 데이터베이스에서 불러올 수 있도록 코드 수정
        Log.d(TAG, "data에 Playlist리스트 저장 -> $data")*/
        var adapter = PlaylistRecyclerAdapter()

        playlistRef.child("idList").get().addOnSuccessListener {
            adapter.listPlaylistID = it.value.toString().split("/").toMutableList()
            Log.d("playlist", "${adapter.listPlaylistID}")
            binding.playlistRecyclerView.adapter = adapter
            binding.playlistRecyclerView.layoutManager = LinearLayoutManager(this)
        }.addOnFailureListener {
            Toast.makeText(baseContext, "네크워크 상태를 확인해주세요.", Toast.LENGTH_SHORT).show()
            Log.d("TAG", "error=${it.message}")
        }



        // SoundList 액티비티로 이동


        // 하단 탭 버튼 -> 리뷰 작성 화면으로
        binding.imageButton7.setOnClickListener {
            val intent = Intent(this, PostReview2::class.java)
            intent.putExtra("from", "other")
            this.startActivity(intent)
        }
    }

/*    override fun onBackPressed() {
        super.onBackPressed()
        ActivityCompat.finishAffinity(this)
        System.runFinalization()
        System.exit(0)
    }*/

    /*fun loadData_test(): MutableList<Playlist> {
        val data: MutableList<Playlist> = mutableListOf()



        var c1 = mutableListOf("-NFqN-1SP6ypQVrJ146e", "-NFwjaa1ci-x1Kxtp1aA")
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
    }*/
}