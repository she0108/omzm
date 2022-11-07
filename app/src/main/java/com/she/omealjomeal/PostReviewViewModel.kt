package com.she.omealjomeal

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PostReviewViewModel: ViewModel() {

    var title: String = ""
    var review1: String = ""
    var review2: String = ""
    var review3: String = ""
    var resID: String = ""
    lateinit var imageUri: Uri

    init {
        Log.d("ViewModel", "ViewModel created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("ViewModel", "ViewModel cleared")
    }



}