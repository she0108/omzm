package com.she.omealjomeal

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.she.omealjomeal.databinding.ActivityPostReview2Binding


val storage = Firebase.storage("gs://omzm-84564.appspot.com")   // 버킷(스토리지 주소)에 연결 (storage)
val database = Firebase.database("https://omzm-84564-default-rtdb.asia-southeast1.firebasedatabase.app/")   // (realtime database)
val soundRef = database.getReference("sounds")      // 최상위노드 "sounds"에 연결
val restaurantRef = database.getReference("restaurants")


class PostReview2 : AppCompatActivity() {

    val TAG = "PostReview"

    val binding by lazy { ActivityPostReview2Binding.inflate(layoutInflater) }
    lateinit var context1: Context
    lateinit var restaurantID: String




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        context1 = binding.root.context

        var spinnerData = listOf("-선택하세요-", "가족", "친구", "애인", "소중한 나 자신")
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spinnerData)
        lateinit var selectedValue: String

        binding.spinnerReview1.adapter = adapter
        binding.spinnerReview1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedValue = spinnerData[p2]
            }
        }

        // 입력완료 버튼 클릭 > 입력한 텍스트 데이터베이스에 업로드 (realtime database)
        with(binding) {
            btnComplete.setOnClickListener {

                var sound = Sound()

                // 입력한 텍스트
                sound.title = editTextTextPersonName.text.toString()
                sound.restaurantId = restaurantID   // 가게선택 화면에서 restaurantId 넘겨받기
                //sound.userName = editUserName.text.toString()       // 이건 앱 시작할 때 입력하게 하든가, 아님 그냥 앱 내부에 정해놓든가
                sound.review1 = selectedValue             //이건 가족, 애인 등 고름
                sound.review2 = editText1.text.toString()                //이건 가족 애인 등 고르는 거 아래에 ~곳이다.
                sound.review3 = editTextLongReview.text.toString()      // 선택지형, 주관식, 추가리뷰 다 따로 +해시태그도?

                // 이미지
                uploadImage(saveThings.imageURI)    // 저장해둔 이미지 URI
                sound.imagePath = saveThings.imageFullpath

                //음성파일
//                uploadAudio(saveThings.audioURI)
//                sound.audioPath = saveThings.audioFullpath

                // Sound() 만들기 전에 글자수 확인하는 코드 필요할 듯? -> value.isNotEmpty() 함수 이용
                addItem(sound)      // firebase에 sound 정보 업로드

                // 업로드 성공적 -> Toast 메시지 같은 거 띄우고 화면 초기화
            }
        }


        //
        // 버튼 클릭 > 권한 요청 > 갤러리 오픈
        binding.imageView2.setOnClickListener {
            permissionLauncher_gallery.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        // 갤러리에서 사진 선택 말고 카메라 열어서 사진 찍고 바로 올리는 기능 추가해야 함 -> 나중에 시간 남으면 하는 걸로...

        // 녹음 버튼 클릭 -> 녹음 시작. 한번 더 누르면 녹음 종료 후 녹음파일 & 파일 경로 저장.

        // 가게 정보 선택하는 화면으로 이동
        binding.layoutRes.setOnClickListener {
            val intentSelect = Intent(this, SelectRestaurantActivity::class.java)
            this.startActivity(intentSelect)
        }
    }

    override fun onResume() {
        super.onResume()
        //가게 선택 후 돌아왔을 때 가게정보 표시
        if(intent.getStringExtra("from") == "SelectRestaurant") {
            restaurantID = intent.getStringExtra("restaurant").toString()

            restaurantRef.child(restaurantID?:"").get().addOnSuccessListener {
                it.getValue(Restaurant::class.java)?.let { restaurant ->    // Restaurant 클래스로 가져오는 거 해보고 안되면 일일이 String으로...
                    binding.textResName.text = restaurant.name
                    binding.textResAddress.text = restaurant.address
                    downloadImage(restaurant.imagePath)
                }
            }.addOnFailureListener {
                Log.d("TAG", "error=${it.message}")
            }

            binding.layoutResSelected.visibility = VISIBLE
            binding.textView20.visibility = INVISIBLE
        }
    }

    object saveThings {
        lateinit var imageURI: Uri      // 선택한 사진 storage에 올리기 전에 URI 여기다 저장
        lateinit var imageFullpath: String      // storage에 사진 업로드 후 생성된 파일주소 저장
        lateinit var audioURI: Uri
        lateinit var audioFullpath: String
    }

    // Sound 데이터를 노드에 입력하는 함수 (realtime database)
    fun addItem(sound: Sound) {
        Log.d(TAG, "addItem() called -> true")
        val id = soundRef.push().key!!
        sound.id = id
        soundRef.child(id).setValue(sound)
    }

    // 외부저장소 권한 요청 팝업 띄우고 갤러리 여는 함수 (storage - image)
    val permissionLauncher_gallery = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(baseContext, "외부 저장소 읽기 권한을 승인해야 사용할 수 있습니다", Toast.LENGTH_LONG).show()
        }
    }

    // 갤러리에서 파일을 선택하면 반환되는 URI로 uploadImage() 호출 (storage - image)
    val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri ->
        binding.imageView2.setImageURI(uri)      // imageView에 선택한 사진 띄우기
        if (uri != null) {
            saveThings.imageURI = uri
            Log.d(TAG, "imageURI saved in saveThings.imageURI")
        }
    }

    fun uploadImage(uri: Uri) {
        Log.d(TAG, "uploadeImage() called -> true")

        val fullPath = makeFilePath("images", "userID", uri)  // 경로+사용자ID+밀리초로 파일주소 만들기 (사용자ID는 나중에 수정해야 할 듯)
        saveThings.imageFullpath = fullPath
        val imageRef = storage.getReference(fullPath)
        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnFailureListener {
            Log.d(TAG, "실패 => ${it.message}")
        }.addOnSuccessListener { taskSnapshot ->
            Log.d(TAG, "성공 주소 => ${fullPath}")    // 이미지 다운로드할 때 이 주소(fullPath) 사용 => fullPath를 Sound 프로퍼티로 데이터베이스에 저장할 것.
        }
    }
    // 경로, 사용자ID, 확장자를 조합해서 파일의 전체경로(주소)를 생성하는 함수. 이 주소는 storage에서 해당 이미지를 다시 불러올 때 사용됨.
    // 로그인 기능이 없으면 사용자ID 대신 디바이스ID나 IP주소 사용
    fun makeFilePath(path:String, userID:String, uri:Uri): String {
        val mimeType = contentResolver.getType(uri)?:"/3gp"
        val ext = mimeType.split("/")[1]
        val timeSuffix = System.currentTimeMillis()
        val filename = "${path}/${userID}_${timeSuffix}.${ext}"
        return filename
    }

    fun downloadImage(path: String) {
        storage.getReference(path).downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context1).load(uri).into(binding.imageRes)
        }.addOnFailureListener {
            Log.e("storage", "download error => ${it.message}")
        }
    }
}

