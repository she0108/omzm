package com.she.omealjomeal

import android.Manifest
import android.R
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.she.omealjomeal.databinding.FragmentRecordBinding
import com.she.omealjomeal.databinding.FragmentReviewBinding
import java.io.File

class ReviewFragment : Fragment() {
    var mainActivity: MainActivity? = null
    private lateinit var binding: FragmentReviewBinding
    var restaurantID: String? = null
    lateinit var selectedValue: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MainActivity) mainActivity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewBinding.inflate(inflater, container, false)

        // 녹음 프래그먼트
        setRecordFragment()

        // 스피너 (선택지형 리뷰)
        var spinnerData = listOf("   ", "가족", "친구", "애인", "나 자신")
        var adapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1, spinnerData)

        binding.spinnerReview1.adapter = adapter
        binding.spinnerReview1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                SaveThings.saveReview1 = p2
                selectedValue = spinnerData[p2]
            }
        }

        // 완료 버튼
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

                if (textInput && resSelected && imageSelected && PostReview2.saveThings.audioRecorded) {
                    postReview(sound)
                    // 업로드 성공적 -> Toast 메시지 같은 거 띄우고 화면 초기화
                    if (PostReview2.saveThings.uploadSuccess) {
                        Toast.makeText(requireContext(), "리뷰 업로드가 완료되었습니다.", Toast.LENGTH_LONG).show()
                        resetLayout()
                    } else {
                        Toast.makeText(requireContext(), "리뷰 업로드에 실패했습니다. 네트워크 연결 상태 확인 후 다시 시도해주세요.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "제목, 가게정보, 리뷰1, 리뷰2, 사진 등록과 녹음을 모두 완료해야 리뷰를 업로드할 수 있습니다.", Toast.LENGTH_LONG).show()
                }
            }
        }

        // 갤러리에서 사진 선택
        binding.imageView3.setOnClickListener {
            permissionLauncher_gallery.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        // 가게 정보 선택하는 fragment로 이동
        binding.layoutRes.setOnClickListener {
            Log.d("tag", "ReviewFragment - layoutRes clicked")
            (parentFragment as PencilFragment).setRestaurantFragment()
        }



        return binding.root
    }

    override fun onResume() {
        super.onResume()

        Log.d("test", "ReviewFragment onResume() called")

        if (SaveThings.selectedRestaurantID.isNotBlank()) {
            Log.d("test", "SaveThings.selectedRestaurantID is not Blank -> ${SaveThings.selectedRestaurantID}")
            setRestaurant(SaveThings.selectedRestaurantID)
        }

        binding.editTextTitle.setText(SaveThings.saveTitle)
        binding.spinnerReview1.setSelection(SaveThings.saveReview1)
        binding.editText1.setText(SaveThings.saveReview2)
        binding.editTextLongReview.setText(SaveThings.saveReview3)
        PostReview2.saveThings.imageFilePath = SaveThings.saveImageFilePath
        if (SaveThings.saveImageFilePath.isNotEmpty()) {
            binding.imageView3.setScaleType(ImageView.ScaleType.CENTER_CROP)
            binding.imageView3.setImageURI(Uri.fromFile(File(PostReview2.saveThings.imageFilePath)))
        }
        PostReview2.saveThings.audioFile = SaveThings.saveAudioFile?:null
    }

    override fun onStop() {
        super.onStop()

        with(SaveThings) {
            saveTitle = binding.editTextTitle.text.toString()
            saveReview2 = binding.editText1.text.toString()
            saveReview3 = binding.editTextLongReview.text.toString()
        }
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
            Toast.makeText(requireContext(), "사진을 등록하려면 권한을 승인해야 합니다.", Toast.LENGTH_LONG).show()
        }
    }

    // 갤러리에서 파일을 선택하면 반환되는 URI로 uploadImage() 호출 (storage - image)
    val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri ->
        if (uri != null) {
            SaveThings.saveImageFilePath = getFullPathFromUri(requireContext(), uri)!!
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

        val fullPath = makeImageFilePath("images", "userID", uri)  // 경로+사용자ID+밀리초로 파일주소 만들기 (사용자ID는 나중에 수정해야 할 듯)
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
    fun makeImageFilePath(path:String, userID:String, uri: Uri): String {
        val mimeType = requireContext().contentResolver.getType(uri)?:"/jpg"
        val ext = mimeType.split("/")[1]
        val timeSuffix = System.currentTimeMillis()
        val filename = "${path}/${userID}_${timeSuffix}.${ext}"
        return filename
    }

    fun makeAudioFilePath(path:String, userID:String, uri: Uri): String {
        val mimeType = requireContext().contentResolver.getType(uri)?:"/3gp"
        val ext = mimeType.split("/")[1]
        val timeSuffix = System.currentTimeMillis()
        val filename = "${path}/${userID}_${timeSuffix}.${ext}"
        return filename
    }

    fun downloadImage(path: String) {
        storage.getReference(path).downloadUrl.addOnSuccessListener { uri ->
            Glide.with(requireContext()).load(uri).into(binding.imageRes)
        }.addOnFailureListener {
            Log.e("storage", "download error => ${it.message}")
        }
    }

    // Record Fragment + 녹음&재생 기능


    fun setRecordFragment() {
        Log.d(TAG, "setRecordFragment()")
        val recordFragment = RecordFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(com.she.omealjomeal.R.id.recordLayout, recordFragment)
        transaction.commit()
    }

    fun removeRecordFragment() {
        val transaction = childFragmentManager.beginTransaction()
        val frameLayout = childFragmentManager.findFragmentById(com.she.omealjomeal.R.id.recordLayout)
        transaction.remove(frameLayout!!)
        transaction.commit()
    }

    // 녹음파일 업로드
    val externalCacheFile by lazy { File(requireContext().externalCacheDir, "recording.3gp") }

    fun uploadAudio(uri: Uri) {
        val fullPath = makeAudioFilePath("audio", "userID", uri)  // 경로+사용자ID+밀리초로 파일주소 만들기 (사용자ID는 나중에 수정해야 할 듯)
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
            File(requireContext().filesDir, "recording.3gp").delete()    //업로드 후 지우기
            Log.d("Upload", "오디오 업로드 성공 주소 => ${fullPath}")    // 이미지 다운로드할 때 이 주소(fullPath) 사용 => fullPath를 Sound 프로퍼티로 데이터베이스에 저장할 것.
        }
    }

    fun checkInput(s: String): Boolean {
        return !(s.isNullOrBlank())
    }

    fun resetLayout() {     // 텍스트 입력한 거, 가게 선택한 거, 사진 선택한 거, 스피너 선택한 거, 녹음한 거 다 비우기!
        binding.editTextTitle.setText("")
        restaurantID = null     // 가게 선택 초기화
        binding.layoutResSelected.visibility = View.INVISIBLE
        binding.textView20.visibility = View.VISIBLE
        binding.spinnerReview1.setSelection(0)  // 스피너 선택된 값 0으로 초기화
        binding.editText1.setText("")
        binding.imageView3.setImageResource(com.she.omealjomeal.R.drawable.ic_bytesize_photo)   // 사진 선택 초기화
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

        SaveThings.saveTitle = ""
        SaveThings.saveReview1 = 0
        SaveThings.saveReview2 = ""
        SaveThings.saveReview3 = ""
        SaveThings.saveImageFilePath = ""
        SaveThings.saveAudioFile = null
        SaveThings.selectedRestaurantID = ""
    }

    fun setRestaurant(restaurantID: String) {
        Log.d("test", "ReviewFragment - setRestaurant() called")

        restaurantRef.child(restaurantID?:"").get().addOnSuccessListener {
            it.getValue(Restaurant::class.java)?.let { restaurant ->    // Restaurant 클래스로 가져오는 거 해보고 안되면 일일이 String으로...
                binding.textResName.text = restaurant.name
                binding.textResAddress.text = restaurant.address
                downloadImage(restaurant.imagePath)
            }
        }.addOnFailureListener {
            Log.d(TAG, "error=${it.message}")
        }

        binding.layoutResSelected.visibility = View.VISIBLE
        binding.textView20.visibility = View.INVISIBLE
    }
}