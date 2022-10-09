package com.she.omealjomeal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.she.omealjomeal.databinding.ActivityMainBinding

// 1. 소리 목록

class MainActivity : AppCompatActivity() {

    lateinit var storagePermission: ActivityResultLauncher<String>      // 외부저장소 권한 런처
    val storage = Firebase.storage("gs://omzm-84564.appspot.com")   //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //firebase realtime database test
//        val database = Firebase.database
//        val myRef = database.getReference("message")
//        myRef.setValue("Hello, Firebase!")

        val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

        setContentView(binding.root)

        val data: MutableList<Sound> = loadData()   // <Sound>타입 데이터들 가져와서 변수 data에 저장
        var adapter = SoundRecyclerAdapter()
        adapter.listSound = data    // 어댑터의 listSound에 방금 불러온 data 넣어주기
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this) // 레이아웃 매니저: 리사이클러뷰를 화면에 보여주는 형태 결정
    }

    // 테스트용. Sound 클래스에 title, restaurantName만 있음 -> 원래 여기서 직접 추가하는 게 아니라 파이어베이스에서 불러와서 띄워야 함.
    fun loadData(): MutableList<Sound> {
        val data: MutableList<Sound> = mutableListOf()

        data.add(Sound("아삭아삭 샐러드, 함께 씹어봐요", "맛있는샐러드집"))
        data.add(Sound("스파게티도 면치기가 되나요?", "맛있는파스타집"))
        data.add(Sound("사장님표 로스팅 커피의 소리", "커피가맛있는집"))
        data.add(Sound("동네 바리스타의 라떼아트 소리", "라떼아트가예쁜집"))
        data.add(Sound("소문난 수제버거집 패티 지글지글", "지글지글집"))
        data.add(Sound("영국의 맛, 피쉬 앤 칩스 튀겨지는 소리", "영국집"))
        data.add(Sound("바닷가 카페의 아이스 라떼 속 얼음 소리?", "바닷가에있는집"))
        data.add(Sound("대형 프렌차이즈 버거집의 분주한 런치타임", "버거집"))

        return data
    }
}