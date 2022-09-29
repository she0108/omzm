package com.she.omealjomeal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.she.omealjomeal.databinding.ActivityMainBinding

// 1. 소리 목록

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

        setContentView(binding.root)

        // ????? 왜 Sound랑 SoundRecyclerAdapter 클래스 만들어놨는데 여기에 안 떠 왜 ??????
        val data: MutableList<Sound> = loadData()   // <Sound>타입 데이터들 가져와서 변수 data에 저장
        var adapter = SoundRecyclerAdapter()
        adapter.listSound = data    // 어댑터의 listSound에 방금 불러온 data 넣어주기
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)  // 레이아웃 매니저: 리사이클러뷰를 화면에 보여주는 형태 결정
    }

    // 테스트용. Sound에 title, restaurantName만 있음
    fun loadData(): MutableList<Sound> {
        val data: MutableList<Sound> = mutableListOf()

        data.add(Sound("아삭아삭 샐러드, 함께 씹어봐요", "맛있는샐러드집"))


    }
}