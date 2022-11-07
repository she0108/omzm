package com.she.omealjomeal

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.she.omealjomeal.databinding.ActivityPostReviewBinding
import java.io.File


class PostReview : AppCompatActivity() {

    private val binding by lazy { ActivityPostReviewBinding.inflate(layoutInflater) }
    lateinit var this_context: Context

    val storage = Firebase.storage("gs://omzm-84564.appspot.com")   // 버킷(스토리지 주소)에 연결 (storage)
    val database = Firebase.database("https://omzm-84564-default-rtdb.asia-southeast1.firebasedatabase.app/")   // (realtime database)
    val soundRef = database.getReference("sounds")      // 최상위노드 "sounds"에 연결
    val restaurantRef = database.getReference("restaurants")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        this_context = binding.root.context

        // 입력완료 버튼 클릭 > 입력한 텍스트 데이터베이스에 업로드 (realtime database)
        with(binding) {
            btnPostReview.setOnClickListener {

                var sound = Sound()

                // 입력한 텍스트
                sound.title = editTitle.text.toString()
                sound.restaurantId = editRestaurantName.text.toString()     // 가게선택 화면에서 restaurantId 넘겨받기
                sound.userName = editUserName.text.toString()       // 이건 앱 시작할 때 입력하게 하든가, 아님 그냥 앱 내부에 정해놓든가
                sound.review3 = editReview.text.toString()      // 선택지형, 주관식, 추가리뷰 다 따로 +해시태그도?

                // 이미지
                uploadImage(saveThings.imageURI)    // 저장해둔 이미지 URI
                sound.imagePath = saveThings.imageFullpath

                //음성파일
                uploadAudio(saveThings.audioURI)
                sound.audioPath = saveThings.audioFullpath

                // Sound() 만들기 전에 글자수 확인하는 코드 필요할 듯? -> value.isNotEmpty() 함수 이용
                addItem(sound)      // firebase에 sound 정보 업로드

                // 업로드 후 액티비티 종료
            }
        }

        // 버튼 클릭 > 권한 요청 > 갤러리 오픈
        binding.btnSelectImage.setOnClickListener {
            permissionLauncher_gallery.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        // 갤러리에서 사진 선택 말고 카메라 열어서 사진 찍고 바로 올리는 기능 추가해야 함 -> 나중에 시간 남으면 하는 걸로...

        // 녹음 버튼 클릭 -> 녹음 시작. 한번 더 누르면 녹음 종료 후 녹음파일 & 파일 경로 저장.
        binding.btnRecord.setOnClickListener {
            when (state) {
                State.BEFORE_RECORDING -> { // 녹음 시작.
                    requestAudioPermission()    // 권한 요청 띄우는 거 -> 수락하면 startRecording -> startCountup, state = ON_RECORDING
                }
                State.ON_RECORDING -> { // 녹음 완료
                    stopRecording()
                }
                State.AFTER_RECORDING -> { // 녹음한 거 재생
                    playRecordedFile()
                }
                State.ON_PLAYING -> { // 재생 일시정지
                    pauseRecordedFile()
                }
            }
        }

        binding.btnClear.setOnClickListener {
            clearRecordedFile()
        }

        binding.btnCheck.setOnClickListener {
            // check 버튼 누른 뒤에는 clear, check 버튼을 없애야 하는데 그럼 state 외에 다른 변수 하나 더 필요함.
            check = Check.AFTER_CHECK
            state = state
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
        binding.imageView.setImageURI(uri)      // imageView에 선택한 사진 띄우기
        if (uri != null) {
            saveThings.imageURI = uri
            Log.d("PostReview", "imageURI saved in saveThings.imageURI")
        }
    }

    fun uploadImage(uri: Uri) {
        val fullPath = makeFilePath("images", "userID", uri)  // 경로+사용자ID+밀리초로 파일주소 만들기 (사용자ID는 나중에 수정해야 할 듯)
        Log.d("PostReview", "uploadImage - fullPath defined")
        saveThings.imageFullpath = fullPath
        Log.d("PostReview", "uploadImage - fullPath saved in saveThings.imageFullpath")
        val imageRef = storage.getReference(fullPath)
        val uploadTask = imageRef.putFile(uri)
        Log.d("PostReview", "uploadImage - uploadTask defined")

        uploadTask.addOnFailureListener {
            Log.d("PostReview", "실패 => ${it.message}")
        }.addOnSuccessListener { taskSnapshot ->
            Log.d("PostReview", "성공 주소 => ${fullPath}")    // 이미지 다운로드할 때 이 주소(fullPath) 사용 => fullPath를 Sound 프로퍼티로 데이터베이스에 저장할 것.
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


    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private val recordingFilePath: String by lazy { "${externalCacheDir?.absolutePath}/recording.3gp"}  //외부저장소-고유영역-캐시
    val externalCacheFile by lazy { File(this_context.externalCacheDir, "recording.3gp") }

    private val requiredPermissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)

    private var check = Check.BEFORE_CHECK      // 녹음파일 확정 전/후
    private var state = State.BEFORE_RECORDING  // 녹음전/녹음중/녹음후/재생중
        set(value) {            // state 변화에 따라 실행할 코드들 여기에 작성
            field = value
            binding.btnRecord.updateIconWithState(value)    //아이콘 모양 변경
            if ((value == State.AFTER_RECORDING || value == State.ON_PLAYING) && (check == Check.BEFORE_CHECK))  {  //btnClear, btnCheck 나타나게/사라지게
                binding.btnClear.isEnabled = true
                binding.btnCheck.isEnabled = true
                binding.btnClear.visibility = View.VISIBLE
                binding.btnCheck.visibility = View.VISIBLE
            } else {
                binding.btnClear.isEnabled = false
                binding.btnCheck.isEnabled = false
                binding.btnClear.visibility = View.INVISIBLE
                binding.btnCheck.visibility = View.INVISIBLE
            }
        }

    companion object { const val REQUEST_RECORD_AUDIO_PERMISSION = 201 }

    fun requestAudioPermission() {
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val audioRecordPermissionGranted = (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) && (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED)
        if (!audioRecordPermissionGranted) {
            Toast.makeText(baseContext, "녹음 권한을 승인해야 이 기능을 사용할 수 있습니다", Toast.LENGTH_LONG).show()
        }     // 권한 거부하면 앱 종료 -> Toast 메시지 띄운다거나 그런 걸로 수정할 것.
        else {
            startRecording()    // 권한 요청 -> 한번만 수락하면 오류 나는데 그냥 수락하니까 오류 안 남 (?)
        }
    }

    fun startRecording() {
        recorder = MediaRecorder(this).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)   // 포멧?
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)      // 엔코더
            setOutputFile(recordingFilePath)    //캐시에 저장?
            Log.d("PostReview", "${externalCacheDir?.absolutePath}/recording.3gp") // 앞에가 파일 경로, recording.3gp가 파일명
            prepare()
        }
        recorder?.start()       // 녹음기 실행
        binding.recordTimeTextView.startCountup()       // 녹음시간 시작
        state = State.ON_RECORDING      // state 변경
    }

    var prepared = false

    fun stopRecording() {
        recorder?.run {
            stop()
            reset()
            release()
        }
        recorder = null
        binding.recordTimeTextView.stopCountup()    // 녹음 시간 저장해야 됨
        state = State.AFTER_RECORDING
        Log.d("PostReview", "외부저장소 캐시에 파일 존재 -> ${externalCacheFile.isFile}")
        saveThings.audioURI = Uri.fromFile(File(recordingFilePath))
        player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setDataSource(applicationContext, saveThings.audioURI)
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
            prepareAsync()
            var preparedListener: MediaPlayer.OnPreparedListener = MediaPlayer.OnPreparedListener { player ->
                Log.d(TAG, "OnPreparedListener called")
                prepared = true
            }
            setOnPreparedListener(preparedListener)
        }
    }

    fun playRecordedFile() {    // prepare 완료되면 실행
        Log.d(TAG, "$prepared")
        if (prepared) {
            state = State.ON_PLAYING
            player?.start()
        }
    }

    fun pauseRecordedFile() {
        state = State.AFTER_RECORDING
        player?.pause()
    }

    fun clearRecordedFile() {
        state = State.BEFORE_RECORDING
        externalCacheFile.delete()
        Log.d("PostReview", "외부저장소 캐시에 저장된 파일 삭제 -> 파일 존재 여부 ${externalCacheFile.isFile}")
        player?.release()
        player = null
    }


    // 녹음파일 업로드
    fun uploadAudio(uri: Uri) {
        val fullPath = makeFilePath("audio", "userID", uri)  // 경로+사용자ID+밀리초로 파일주소 만들기 (사용자ID는 나중에 수정해야 할 듯)
        Log.d("PostReview", "uploadAudio - fullPath defined")
        saveThings.audioFullpath = fullPath
        Log.d("PostReview", "uploadAudio - fullPath saved in saveThings.audioFullpath")
        val audioRef = storage.getReference(fullPath)
        val uploadTask = audioRef.putFile(uri)
        Log.d("PostReview", "uploadAudio - uploadTask defined")

        uploadTask.addOnFailureListener {
            Log.d("PostReview", "실패 => ${it.message}")
        }.addOnSuccessListener { taskSnapshot ->
            Log.d("PostReview", "성공 주소 => ${fullPath}")    // 이미지 다운로드할 때 이 주소(fullPath) 사용 => fullPath를 Sound 프로퍼티로 데이터베이스에 저장할 것.
        }
        externalCacheFile.delete()
    }
}
