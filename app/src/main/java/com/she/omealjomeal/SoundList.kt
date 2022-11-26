package com.she.omealjomeal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.she.omealjomeal.databinding.ActivitySoundListBinding
import java.nio.file.attribute.UserPrincipalNotFoundException


// 1. 소리 목록

val TAG = "TAG"

class SoundList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding by lazy { ActivitySoundListBinding.inflate(layoutInflater) }
        setContentView(binding.root)

        overridePendingTransition(0, 0)

        val selectedPlaylistId = intent.getStringExtra("playlist")  // PlaylistList에서 선택된 playlist 전달받음 -> playlist.soundIdList에서 sound id 목록 받아오기




        var adapter = SoundRecyclerAdapter()

        val database = Firebase.database("https://omzm-84564-default-rtdb.asia-southeast1.firebasedatabase.app/")   // (realtime database)
        val playlistRef = database.getReference("playlists")

        playlistRef.child(selectedPlaylistId?:"").get().addOnSuccessListener {
            it.getValue(Playlist::class.java)?.let { playlist ->
                adapter.listSoundID = playlist.soundIdList.toString().split("/").toMutableList()
                setFragment(playlist)   //AlbumFragment
            }
        }.addOnFailureListener {
            Toast.makeText(baseContext, "네크워크 상태를 확인해주세요.", Toast.LENGTH_SHORT).show()
            Log.d("TAG", "error=${it.message}")
        }

        binding.soundRecyclerView.adapter = adapter
        binding.soundRecyclerView.layoutManager = LinearLayoutManager(this) // 레이아웃 매니저: 리사이클러뷰를 화면에 보여주는 형태 결정




        // 하단 탭 버튼 -> 리뷰 작성 화면으로
        binding.imageButton7.setOnClickListener {
            val intent = Intent(this, PostReview2::class.java)
            intent.putExtra("from", "other")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            this.startActivity(intent)
        }

        binding.imageButton10.setOnClickListener {
            finish()
        }

        binding.imageButton6.setOnClickListener {
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        overridePendingTransition(0, 0)
    }


    fun setFragment(playlist: Playlist) {
        val albumFragment = AlbumFragment()
        var bundle = Bundle()
        bundle.putString("title", playlist.title)
        bundle.putString("userName", playlist.userName)
        bundle.putString("imagePath", playlist.imagePath)
        albumFragment.arguments = bundle
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragmentView, albumFragment)
        transaction.commit()
    }


    // 테스트용. Sound 클래스에 title, restaurantName만 있음 -> 원래 여기서 직접 추가하는 게 아니라 파이어베이스에서 불러와서 띄워야 함.
//    fun loadData_test(): MutableList<Sound> {
//        val data: MutableList<Sound> = mutableListOf()
//
//        data.add(Sound("아삭아삭 샐러드, 함께 씹어봐요", "맛있는샐러드집"))
//        data.add(Sound("스파게티도 면치기가 되나요?", "맛있는파스타집"))
//        data.add(Sound("사장님표 로스팅 커피의 소리", "커피가맛있는집"))
//        data.add(Sound("동네 바리스타의 라떼아트 소리", "라떼아트가예쁜집"))
//        data.add(Sound("소문난 수제버거집 패티 지글지글", "지글지글집"))
//        data.add(Sound("영국의 맛, 피쉬 앤 칩스 튀겨지는 소리", "영국집"))
//        data.add(Sound("바닷가 카페의 아이스 라떼 속 얼음 소리?", "바닷가에있는집"))
//        data.add(Sound("대형 프렌차이즈 버거집의 분주한 런치타임", "버거집"))
//
//        return data
//    }

    // 선택한 플레이리스트에 포함된 sound들의 정보를 database에서 불러와서 data 리스트에 넣음
//    fun loadData(): MutableList<Sound> {
//        val data: MutableList<Sound> = mutableListOf()
//
//        data.add()
//
//        return data
//    }
}