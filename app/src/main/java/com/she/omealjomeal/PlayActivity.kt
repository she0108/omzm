package com.she.omealjomeal

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.she.omealjomeal.databinding.ActivityPlayBinding
import kotlinx.android.synthetic.main.activity_play.view.*
import kotlinx.android.synthetic.main.sound_recycler.view.*

class PlayActivity : AppCompatActivity() {

    private val binding by lazy { ActivityPlayBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val intentSound = getIntent()
        val selectedSoundId = intentSound.getStringExtra("sound")      // 리뷰목록(SoundList)에서 선택된 리뷰(Sound) 인스턴스가 selectedSound에 저장됨
        val selectedRestaurantId = intentSound.getStringExtra("restaurant")
        Log.d("TAG", "selectedSoundId -> $selectedSoundId")
        Log.d("TAG", "selectedRestaurantId -> $selectedRestaurantId")



/*      *binding 쓰는 코드로 전부 수정*
        val button : ImageButton = findViewById(R.id.imageBut)
        val text : TextView = findViewById(R.id.textView6)
        button.setOnClickListener{
            text.setText("오밀조밀은 기존의 정량적 리뷰만으로는 파악할 수 없는 정보들을 음성 리뷰 및 텍스트 리뷰들로 보완하여 어플을 개발하였습니다.")
        }*/



        val database = Firebase.database("https://omzm-84564-default-rtdb.asia-southeast1.firebasedatabase.app/")   // (realtime database)
        val soundRef = database.getReference("sounds")
        val restaurantRef = database.getReference("restaurants")
        val storage = Firebase.storage("gs://omzm-84564.appspot.com")
        val context = binding.root.context


        // sound 관련된 부분
        soundRef.child(selectedSoundId?:"").get().addOnSuccessListener {
            it.getValue(Sound::class.java)?.let { sound ->
                binding.root.title_text_view.text = sound.title     //  sound title
                binding.root.textView.text = sound.userName     // sound userName

                // soundImage 설정
//                storage.getReference(sound.imagePath).downloadUrl.addOnSuccessListener { uri ->
//                    Glide.with(context).load(uri).into(binding.root.imageSound)     // imageSound 대신 사진 보여줄 이미지뷰
//                }.addOnFailureListener {
//                    Log.e("storage", "download error => ${it.message}")
//                }
            }
        }

        // restaurant 관련된 부분  *지금 restaurant 정보 전달 안 됨
        restaurantRef.child(selectedRestaurantId?:"").child("name").get().addOnSuccessListener {
            binding.root.textView3.text = it.value.toString()
        }.addOnFailureListener {
            Log.d("TAG", "error=${it.message}")
        }


        // 가게이름 눌렀을 때 가게정보 화면으로 이동
        binding.root.textView3.setOnClickListener {
            Log.d("click", "textView3 clicked")
            val intentRestaurant = Intent(context, MapsActivity::class.java)        // MapsActivity -> 가게정보 화면으로 수정
            intentRestaurant.putExtra("restaurant", selectedRestaurantId)
            context.startActivity(intentRestaurant)
        }
    }
}