package com.she.omealjomeal

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import com.she.omealjomeal.databinding.ActivityPostReview2Binding

class PostReview2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPostReview2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        var spinnerData = listOf("", "가족", "친구", "애인", "나 자신")
        var spinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spinnerData)
        binding.spinnerReview1.adapter = spinnerAdapter

        setRecordFragment()     // 하단 녹음부분

        // SelectRestaurant에서 선택한 가게 정보 받기
        val activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val message = it.data?.getStringExtra("returnValue")
            }
        }

        // 가게 정보 선택하는 화면으로로
        binding.layoutRes.setOnClickListener {
            val intent = Intent(this, SelectRestaurantActivity::class.java)

        }


    }

    fun setRecordFragment() {
        val recordFragment: RecordFragment = RecordFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.recordLayout, recordFragment)
        transaction.commit()
    }

    // 가게 선택 후 setRestaurantFragment()
    fun setRestaurantFragment() {

    }
}