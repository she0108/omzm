package com.she.omealjomeal


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.she.omealjomeal.databinding.ActivityPostReview2Binding


class PostReview2 : AppCompatActivity() {

    private val binding by lazy { ActivityPostReview2Binding.inflate(layoutInflater) }
    lateinit var context1: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        context1 = binding.root.context

        var spinnerData = listOf("", "가족", "친구", "애인", "나 자신")
        var spinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spinnerData)
        binding.spinnerReview1.adapter = spinnerAdapter

        setRecordFragment()     // 하단 녹음부분

//        // SelectRestaurant에서 선택한 가게 정보 받기
//        val activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (it.resultCode == RESULT_OK) {
//                var restaurantID = it.data?.getStringExtra("restaurant")
//            }
//        }

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
            var restaurantID = intent.getStringExtra("restaurant")

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

    // 하단 녹음 부분
    fun setRecordFragment() {
        val recordFragment = RecordFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.recordLayout, recordFragment)
        transaction.commit()
    }

    val database = Firebase.database("https://omzm-84564-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val restaurantRef = database.getReference("restaurants")
    private val storage = Firebase.storage("gs://omzm-84564.appspot.com")

    // 가게 선택 후 setRestaurantFragment()


    fun downloadImage(path: String) {
        storage.getReference(path).downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context1).load(uri).into(binding.imageRes)
        }.addOnFailureListener {
            Log.e("storage", "download error => ${it.message}")
        }
    }
}