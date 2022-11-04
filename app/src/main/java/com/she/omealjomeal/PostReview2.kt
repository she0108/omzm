package com.she.omealjomeal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.she.omealjomeal.databinding.ActivityPostReview2Binding

class PostReview2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPostReview2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setRecordFragment()

    }

    fun setRecordFragment() {
        val recordFragment: RecordFragment = RecordFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.recordLayout, recordFragment)
        transaction.commit()
    }
}