package com.she.omealjomeal

import android.Manifest
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.she.omealjomeal.databinding.ActivityPostReview2Binding
import java.io.File


val storage = Firebase.storage("gs://omzm-84564.appspot.com")   // 버킷(스토리지 주소)에 연결 (storage)
val database = Firebase.database("https://omzm-84564-default-rtdb.asia-southeast1.firebasedatabase.app/")   // (realtime database)
val soundRef = database.getReference("sounds")      // 최상위노드 "sounds"에 연결
val restaurantRef = database.getReference("restaurants")
val userRef = database.getReference("users")


class PostReview2 : AppCompatActivity() {

    val TAG = "Record"

    val binding by lazy { ActivityPostReview2Binding.inflate(layoutInflater) }
    lateinit var context1: Context
    var restaurantID: String? = null
    lateinit var selectedValue: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called -> true")
        setContentView(binding.root)
        context1 = binding.root.context

        overridePendingTransition(0, 0)

/*        // 가게 선택으로 이동하면 onSaveIstanceState() 호출 -> 선택하고 돌아오면 onCreate() 호출, but *savedInstance = null* 왜지?
        Log.d(TAG3, "savedInstance -> ${savedInstanceState != null}")
        if (savedInstanceState != null) {
            Log.d(TAG3, "savedInstance -> not null")
            binding.editTextTitle.setText(savedInstanceState.getString("title"))
            binding.editText1.setText(savedInstanceState.getString("review2"))
            binding.editTextLongReview.setText(savedInstanceState.getString("review3"))
        }
*/

        setRecordFragment()

        var spinnerData = listOf("   ", "가족", "친구", "애인", "나 자신")
        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, spinnerData)

        binding.spinnerReview1.adapter = adapter
        binding.spinnerReview1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                SaveThings.saveReview1 = p2
                selectedValue = spinnerData[p2]
            }
        }


        // 입력완료 버튼 클릭 > 입력한 텍스트 데이터베이스에 업로드 (realtime database)
        with(binding) {
            btnComplete.setOnClickListener {

                var sound = Sound()

                // 입력한 텍스트
                sound.title = editTextTitle.text.toString()
                sound.restaurantId = restaurantID?:""   // 가게선택 화면에서 restaurantId 넘겨받기
                sound.userName = SaveThings.userID      // 이건 앱 시작할 때 입력하게 하든가, 아님 그냥 앱 내부에 정해놓든가
                sound.review1 = selectedValue             //이건 가족, 애인 등 고름
                sound.review2 = editText1.text.toString()                //이건 가족 애인 등 고르는 거 아래에 ~곳이다.
                sound.review3 = editTextLongReview.text.toString()      // 선택지형, 주관식, 추가리뷰 다 따로 +해시태그도?


                if (checkInput(sound.title) && checkInput(sound.review1) && checkInput(sound.review2)) { textInput = true }
                if (!(restaurantID.isNullOrBlank())) { resSelected = true }

                if (textInput && resSelected && imageSelected && saveThings.audioRecorded) {
                    postReview(sound)
                    // 업로드 성공적 -> Toast 메시지 같은 거 띄우고 화면 초기화
                    if (saveThings.uploadSuccess) {
                        Toast.makeText(baseContext, "리뷰 업로드가 완료되었습니다.", Toast.LENGTH_LONG).show()
                        resetLayout()
                    } else {
                        Toast.makeText(baseContext, "리뷰 업로드에 실패했습니다. 네트워크 연결 상태 확인 후 다시 시도해주세요.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(baseContext, "제목, 가게정보, 리뷰1, 리뷰2, 사진 등록과 녹음을 모두 완료해야 리뷰를 업로드할 수 있습니다.", Toast.LENGTH_LONG).show()
                }
            }
        }

        //호준-이게 글자수 확인
        binding.editText1.addTextChangedListener(object : TextWatcher {
            // addTextChangedListener 텍스트가 입력에 따라 변경될 때마다 확인하는 기능
            // TextWatcher 텍스트가 변경될 때마다 발생하는 이벤트 처리하는 인터페이스
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val filter = arrayOfNulls<InputFilter>(1)
                filter[0] = InputFilter.LengthFilter(20)
                // 쓸 수 있는 글자 수 최대 80자로 제한
                binding.editText1.filters = filter
//                val currentBytes = s.toString().toByteArray().size // 텍스트 내용을 받아와서 바이트 수를 가져온다.
//                val txt = "$currentBytes / 80 바이트"
//                binding.byteConfirm.setText(txt) // 텍스트뷰에 현재 바이트수 표시
            }
            override fun afterTextChanged(s: Editable) {}
        })


        //
        // 버튼 클릭 > 권한 요청 > 갤러리 오픈
        binding.imageView3.setOnClickListener {
            permissionLauncher_gallery.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        // 갤러리에서 사진 선택 말고 카메라 열어서 사진 찍고 바로 올리는 기능 추가해야 함 -> 나중에 시간 남으면 하는 걸로...

        // 녹음 버튼 클릭 -> 녹음 시작. 한번 더 누르면 녹음 종료 후 녹음파일 & 파일 경로 저장.

        // 가게 정보 선택하는 화면으로 이동
        binding.layoutRes.setOnClickListener {
            val intentSelect = Intent(this, SelectRestaurantActivity::class.java)
            this.startActivity(intentSelect)
        }

        // 하단 탭 버튼 -> (마이페이지) 플레이리스트 화면으로
        binding.imageButton10.setOnClickListener {
            val intent = Intent(this, PlaylistList::class.java)
            intent.putExtra("from", "other")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            this.startActivity(intent)
        }

        binding.imageButton6.setOnClickListener {
            finish()
        }
    }


    override fun onStop() {
        super.onStop()

        // 입력해둔 것들
        with(SaveThings) {
            saveTitle = binding.editTextTitle.text.toString()
            saveReview2 = binding.editText1.text.toString()
            saveReview3 = binding.editTextLongReview.text.toString()
        }

        // 녹음파일 재생되고 있다면 stop
    }

/*    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // 제목, review1, 2, 3, 선택한 사진 uri, 녹음파일? 저장
        Log.d(TAG3, "onSaveInstanceState called -> true")
        outState.putString("title", binding.editTextTitle.text.toString())
        outState.putString("review1", selectedValue)
        outState.putString("review2", binding.editText1.text.toString())
        outState.putString("review3", binding.editTextLongReview.text.toString())
        outState.putBoolean("recorded", externalCacheFile.isFile())
        // putURI는 없는데 imageUri는 어떻게 보관하지?
    }*/

    //얘는 액티비티가 강제종료된 경우에만 onCreate에서 호출됨 -> 용도에 맞지 않음
/*    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG3, "onRestoreInstanceState called -> true")
        binding.editTextTitle.setText(savedInstanceState.getString("title"))
        binding.editText1.setText(savedInstanceState.getString("review2"))
        binding.editTextLongReview.setText(savedInstanceState.getString("review3"))
    }*/

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume() called -> true")

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
                Log.d(TAG, "error=${it.message}")
            }

            binding.layoutResSelected.visibility = VISIBLE
            binding.textView20.visibility = INVISIBLE

            binding.editTextTitle.setText(SaveThings.saveTitle)
            binding.spinnerReview1.setSelection(SaveThings.saveReview1)
            binding.editText1.setText(SaveThings.saveReview2)
            binding.editTextLongReview.setText(SaveThings.saveReview3)
            saveThings.imageFilePath = SaveThings.saveImageFilePath
            if (SaveThings.saveImageFilePath.isNotEmpty()) {
                binding.imageView3.setScaleType(ImageView.ScaleType.CENTER_CROP)
                binding.imageView3.setImageURI(Uri.fromFile(File(saveThings.imageFilePath)))
            }
            saveThings.audioFile = SaveThings.saveAudioFile?:null

            // 녹음 상태만 추가하면 됨!
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()

    }


    var textInput = false
    var resSelected= false
    var imageSelected = false

    object saveThings {
        var imageFilePath: String = ""    // 선택한 사진 storage에 올리기 전에 URI 여기다 저장
        lateinit var imageFullpath: String      // storage에 사진 업로드 후 생성된 파일주소 저장
        var audioFile: File? = null
        lateinit var audioFullpath: String
        var audioRecorded = false
        var uploadSuccess = true
    }

    // Sound 데이터를 노드에 입력하는 함수 (realtime database)
    fun addItem(sound: Sound) {
        Log.d(TAG, "addItem() called -> true")
        val id = soundRef.push().key!!
        sound.id = id
        soundRef.child(id).setValue(sound).addOnFailureListener {
            saveThings.uploadSuccess = false
            Log.d("Upload", "addItem Failed")
        }
    }

    // 외부저장소 권한 요청 팝업 띄우고 갤러리 여는 함수 (storage - image)
    val permissionLauncher_gallery = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(baseContext, "사진을 등록하려면 권한을 승인해야 합니다.", Toast.LENGTH_LONG).show()
        }
    }

    // 갤러리에서 파일을 선택하면 반환되는 URI로 uploadImage() 호출 (storage - image)
    val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri ->
        if (uri != null) {
            SaveThings.saveImageFilePath = getFullPathFromUri(baseContext, uri)!!
            saveThings.imageFilePath = SaveThings.saveImageFilePath
            binding.imageView3.setScaleType(ImageView.ScaleType.CENTER_CROP)
            binding.imageView3.setImageURI(uri)      // imageView에 선택한 사진 띄우기
            Log.d(TAG, "saveThings.imageFile -> ${saveThings.imageFilePath}")
            imageSelected = true
        }
    }

    fun getFullPathFromUri(ctx: Context, fileUri: Uri?): String? {
        var fullPath: String? = null
        val column = "_data"
        var cursor: Cursor? = ctx.contentResolver.query(fileUri!!, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            var document_id: String = cursor.getString(0)
            if (document_id == null) {
                for (i in 0 until cursor.getColumnCount()) {
                    if (column.equals(cursor.getColumnName(i), ignoreCase = true)) {
                        fullPath = cursor.getString(i)
                        break
                    }
                }
            } else {
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
                cursor.close()
                val projection = arrayOf(column)
                try {
                    cursor = ctx.contentResolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        MediaStore.Images.Media._ID + " = ? ",
                        arrayOf(document_id),
                        null
                    )
                    if (cursor != null) {
                        cursor.moveToFirst()
                        fullPath = cursor.getString(cursor.getColumnIndexOrThrow(column))
                    }
                } finally {
                    if (cursor != null) cursor.close()
                }
            }
        }
        return fullPath
    }

    fun uploadImage(uri: Uri) {
        Log.d(TAG, "uploadeImage() called -> true")

        val fullPath = makeFilePath("images", "userID", uri)  // 경로+사용자ID+밀리초로 파일주소 만들기 (사용자ID는 나중에 수정해야 할 듯)
        saveThings.imageFullpath = fullPath
        val imageRef = storage.getReference(fullPath)
        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnFailureListener {
            saveThings.uploadSuccess = false
            Log.d("Upload", "이미지 업로드 실패 => ${it.message}")
        }.addOnSuccessListener { taskSnapshot ->
            File(saveThings.imageFilePath).delete()
            Log.d("Upload", "이미지 업로드 성공 주소 => ${fullPath}")    // 이미지 다운로드할 때 이 주소(fullPath) 사용 => fullPath를 Sound 프로퍼티로 데이터베이스에 저장할 것.
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

    // Record Fragment + 녹음&재생 기능


    fun setRecordFragment() {
        Log.d(TAG, "setRecordFragment()")
        val recordFragment = RecordFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.recordLayout, recordFragment)
        transaction.commit()
    }

    fun removeRecordFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        val frameLayout = supportFragmentManager.findFragmentById(R.id.recordLayout)
        transaction.remove(frameLayout!!)
        transaction.commit()
    }

    // 녹음파일 업로드
    val externalCacheFile by lazy { File(externalCacheDir, "recording.3gp") }

    fun uploadAudio(uri: Uri) {
        val fullPath = makeFilePath("audio", "userID", uri)  // 경로+사용자ID+밀리초로 파일주소 만들기 (사용자ID는 나중에 수정해야 할 듯)
        Log.d("PostReview", "uploadAudio - fullPath defined")
        saveThings.audioFullpath = fullPath
        Log.d("PostReview", "uploadAudio - fullPath saved in saveThings.audioFullpath")
        val audioRef = storage.getReference(fullPath)
        val uploadTask = audioRef.putFile(uri)
        Log.d("PostReview", "uploadAudio - uploadTask defined")

        uploadTask.addOnFailureListener {
            saveThings.uploadSuccess = false
            Log.d("Upload", "오디오 업로드 실패 => ${it.message}")
        }.addOnSuccessListener { taskSnapshot ->
            File(baseContext.filesDir, "recording.3gp").delete()    //업로드 후 지우기
            Log.d("Upload", "오디오 업로드 성공 주소 => ${fullPath}")    // 이미지 다운로드할 때 이 주소(fullPath) 사용 => fullPath를 Sound 프로퍼티로 데이터베이스에 저장할 것.
        }
    }

    fun checkInput(s: String): Boolean {
        return !(s.isNullOrBlank())
    }

    fun resetLayout() {     // 텍스트 입력한 거, 가게 선택한 거, 사진 선택한 거, 스피너 선택한 거, 녹음한 거 다 비우기!
        binding.editTextTitle.setText("")
        restaurantID = null     // 가게 선택 초기화
        binding.layoutResSelected.visibility = INVISIBLE
        binding.textView20.visibility = VISIBLE
        binding.spinnerReview1.setSelection(0)  // 스피너 선택된 값 0으로 초기화
        binding.editText1.setText("")
        binding.imageView3.setImageResource(R.drawable.ic_bytesize_photo)   // 사진 선택 초기화
        binding.imageView3.setScaleType(ImageView.ScaleType.CENTER_INSIDE)
        binding.editTextLongReview.setText("")
        removeRecordFragment()
        setRecordFragment()
        textInput = false
        resSelected= false
        imageSelected = false
        saveThings.audioRecorded = false
    }

    fun postReview(sound: Sound) {
        // 이미지 storage에 업로드
        uploadImage(Uri.fromFile(File(saveThings.imageFilePath)))    // 저장해둔 이미지 URI
        sound.imagePath = saveThings.imageFullpath

        //음성파일 storage에 업로드
        Log.d("Record", "audioFile initialized -> ${saveThings.audioFile}")
        uploadAudio(Uri.fromFile(saveThings.audioFile!!))
        sound.audioPath = saveThings.audioFullpath

        // users에도 업로드
        lateinit var user_soundIdList: String
        userRef.child(SaveThings.userID).child("soundIdList").get().addOnSuccessListener {
            user_soundIdList = it.value.toString()
            userRef.child(SaveThings.userID).child("soundIdList").setValue(user_soundIdList + "/" + sound.id)
        }



        // 데이터베이스에 업로드
        addItem(sound)      // firebase에 sound 정보 업로드
    }


}