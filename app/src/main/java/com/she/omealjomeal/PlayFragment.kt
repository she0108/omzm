package com.she.omealjomeal

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.she.omealjomeal.databinding.FragmentPlayBinding
import kotlinx.android.synthetic.main.activity_play2.view.*
import java.text.SimpleDateFormat


class PlayFragment : Fragment() {
    lateinit var binding: FragmentPlayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayBinding.inflate(inflater, container, false)

        val selectedSoundId = arguments?.getString("sound")      // 리뷰목록(SoundList)에서 선택된 리뷰(Sound) 인스턴스가 selectedSound에 저장됨
        val selectedRestaurantId = arguments?.getString("restaurant")

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
                binding.textOneLineReview.text = "이곳은 " + sound.review1 + "와/과 함께 " + sound.review2 + " 곳이다."
//                binding.textView23.text = 해시태그!
                binding.textView22.text = sound.review3

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
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build()
                        )
                        setDataSource(requireContext(), uri)     // lateinit property audioUri has not been initialized
                        setWakeMode(requireContext(), PowerManager.PARTIAL_WAKE_LOCK)
                        prepareAsync()
                        var preparedListener: MediaPlayer.OnPreparedListener =
                            MediaPlayer.OnPreparedListener { player ->
                                Log.d(TAG, "OnPreparedListener called")
                                startPlaying()
                            }
                        setOnPreparedListener(preparedListener)
                        Log.d(TAG, "player prepared")
                    }
                    Log.d(TAG, "player created -> ${player != null}")
                }.addOnFailureListener {
                    Log.e(TAG, "download error => ${it.message}")
                }
            }
        }

        restaurantRef.child(selectedRestaurantId?:"").get().addOnSuccessListener {
            it.getValue(Restaurant::class.java)?.let { restaurant ->    // Restaurant 클래스로 가져오는 거 해보고 안되면 일일이 String으로...
                binding.textRestaurantName2.text = restaurant.name

                // 가게이름 눌렀을 때 가게정보 화면으로 이동
                binding.textRestaurantName2.setOnClickListener {
                    /*val intentRestaurant = Intent(this, MapsActivity::class.java)        // MapsActivity -> 가게정보 화면으로 수정
                    intentRestaurant.putExtra("latitude", restaurant.latitude)
                    intentRestaurant.putExtra("longitude", restaurant.longitude)
                    this.startActivity(intentRestaurant)*/
                }
            }
        }.addOnFailureListener {
            Log.d("TAG", "error=${it.message}")
        }



        // 사진 눌렀을 때 리뷰 창 visible, clickable
        binding.imageSound2.setOnClickListener {
            if (binding.layoutLongReview.visibility == View.INVISIBLE) {
                binding.layoutLongReview.visibility = View.VISIBLE
                binding.layoutLongReview.isClickable = true
            }
        }

        // 리뷰 창 떠있는 상태에서 누르면 다시 invisible, not clickable
        binding.layoutLongReview.setOnClickListener {
            binding.layoutLongReview.visibility = View.INVISIBLE
            binding.layoutLongReview.isClickable = false
        }

        // 재생, 일시정지 등 구현
        binding.root.btnPlay3.setOnClickListener {  // 재생/일시정지 버튼 눌렀을 때
            when (state_) {
                State2.PLAY -> {
                    stopPlaying()
                }
                State2.PAUSE -> {
                    startPlaying()
                }
            }

            object : Thread() {
                var timeFormat = SimpleDateFormat("mm:ss")  //"분:초"를 나타낼 수 있도록 포멧팅
                override fun run() {
                    super.run()
                    if (player == null)
                        return
                    binding.seekBar3.max = player!!.duration  // player.duration : 음악 총 시간
                    while (player!!.isPlaying) {
                        run {
                            binding.seekBar3.progress = player!!.currentPosition
                            binding.textCurrentTime.text = timeFormat.format(player!!.currentPosition)
                            binding.textTotalTime.text = timeFormat.format(player!!.duration)
                        }
                        SystemClock.sleep(200)
                    }

                    //1. 음악이 종료되면 자동으로 초기상태로 전환
                    if(player!!.isPlaying){
                        player!!.stop()      //음악 정지
                        player!!.reset()
                        binding.seekBar3.progress = 0
                    }
                }
            }.start()
        }

        return binding.root
    }

    private var player: MediaPlayer? = null

    var state_ = State2.PLAY
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
        Log.d(TAG, "startPlaying() called -> true")
        state_ = State2.PLAY
        player?.start()
        Log.d(TAG, "player playing -> ${player?.isPlaying}")
    }

    fun stopPlaying() {
        state_ = State2.PAUSE
        player?.pause()
    }

}