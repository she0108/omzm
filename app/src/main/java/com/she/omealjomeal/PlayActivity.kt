package com.she.omealjomeal

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.she.omealjomeal.databinding.ActivityPlay2Binding
import kotlinx.android.synthetic.main.activity_play2.view.*

class PlayActivity : AppCompatActivity() {

    private val binding by lazy { ActivityPlay2Binding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val selectedSoundId = intent.getStringExtra("sound")      // 리뷰목록(SoundList)에서 선택된 리뷰(Sound) 인스턴스가 selectedSound에 저장됨
        val selectedRestaurantId = intent.getStringExtra("restaurant")
        Log.d("TAG", "selectedSoundId -> $selectedSoundId")
        Log.d("TAG", "selectedRestaurantId -> $selectedRestaurantId")

        val database = Firebase.database("https://omzm-84564-default-rtdb.asia-southeast1.firebasedatabase.app/")   // (realtime database)
        val storage = Firebase.storage("gs://omzm-84564.appspot.com")
        val soundRef = database.getReference("sounds")
        val restaurantRef = database.getReference("restaurants")

        lateinit var audioUri: Uri

        // sound 관련된 부분
        soundRef.child(selectedSoundId?:"").get().addOnSuccessListener {
            it.getValue(Sound::class.java)?.let { sound ->
                binding.textSoundTitle.text = sound.title     //  sound title
                binding.textUserName.text = sound.userName     // sound userName

                // soundImage2 설정
                storage.getReference(sound.imagePath).downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this).load(uri).into(binding.imageSound2)     // imageSound 대신 사진 보여줄 이미지뷰
                }.addOnFailureListener {
                    Log.e("storage", "download error => ${it.message}")
                }

                // 녹음 파일 storage에서 불러오기
                Log.d("storage", "sound.audioPath -> ${sound.audioPath}")
                storage.getReference(sound.audioPath?:"").downloadUrl.addOnSuccessListener { uri ->
                    Log.d("storage", "set audio uri -> ${uri != null}")
                    audioUri = uri      // 녹음파일 uri 저장
                    Log.d("storage", "set audio uri -> ${audioUri != null}")
                    player = MediaPlayer().apply {
                        setAudioStreamType(AudioManager.STREAM_MUSIC)
                        setDataSource(applicationContext, uri)     // lateinit property audioUri has not been initialized
                        prepare()
                    }
                    Log.d("storage", "create player -> ${player != null}")
                }.addOnFailureListener {
                    Log.e("storage", "download error => ${it.message}")
                    Log.d("storage", "download error => ${it.message}")
                }
            }
        }





        // restaurant 관련된 부분
        restaurantRef.child(selectedRestaurantId?:"").child("name").get().addOnSuccessListener {
            binding.textRestaurantName2.text = it.value.toString()
        }.addOnFailureListener {
            Log.d("TAG", "error=${it.message}")
        }





        // 가게이름 눌렀을 때 가게정보 화면으로 이동
        binding.textRestaurantName2.setOnClickListener {
            Log.d("click", "textView3 clicked")
            val intentRestaurant = Intent(this, MapsActivity::class.java)        // MapsActivity -> 가게정보 화면으로 수정
            intentRestaurant.putExtra("restaurant", selectedRestaurantId)
            this.startActivity(intentRestaurant)
        }

        // 사진 눌렀을 때 리뷰 창 visible, clickable
        binding.imageSound2.setOnClickListener {
            if (binding.layoutLongReview.visibility == INVISIBLE) {
                binding.layoutLongReview.visibility = VISIBLE
                binding.layoutLongReview.isClickable = true
            }
        }

        // 리뷰 창 떠있는 상태에서 누르면 다시 invisible, not clickable
        binding.layoutLongReview.setOnClickListener {
            binding.layoutLongReview.visibility = INVISIBLE
            binding.layoutLongReview.isClickable = false
        }

        // 재생, 일시정지 등 구현

        startPlaying()  // 처음 화면 실행될 때 '재생'으로 시작
        binding.root.btnPlay3.setOnClickListener {  // 재생/일시정지 버튼 눌렀을 때
            when (state) {
                State2.PLAY -> {
                    stopPlaying()
                }
                State2.PAUSE -> {
                    startPlaying()
                }
            }
        }
    }


    private var player: MediaPlayer? = null

    private var state = State2.PLAY
        set(value) {
            field = value
            binding.root.btnPlay3.updateIconWithState(value)
        }

/*    fun createPlayer(uri: Uri) {
        player = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(applicationContext, uri)     // lateinit property audioUri has not been initialized
            prepare()
        }
    }*/

    fun startPlaying() {
        state = State2.PLAY
        player?.start()
    }

    fun stopPlaying() {
        state = State2.PAUSE
        player?.pause()
    }
}