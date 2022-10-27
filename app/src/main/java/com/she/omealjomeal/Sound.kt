package com.she.omealjomeal

import android.net.Uri
import android.provider.MediaStore

class Sound {

    var id: String = ""
    var title: String = ""
    var restaurantName: String = ""
    var userName: String? = ""
    var review: String? = ""
    var imagePath: String? = ""
    var audioPath: String? = ""
    var duration: Long? = null

    constructor()

    constructor(title:String, restaurantName:String, userName:String? = null, review:String? = null, imagePath:String? = null) {
        this.title = title
        this.restaurantName = restaurantName
        this.userName = userName
        this.review = review
        this.imagePath = imagePath
    }

//    //사운드의 URI를 생성
//    fun getSoundUri(): Uri {
//        return Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
//    }
//
//    //이미지의 URI를 생성
//    fun getImageUri(): Uri {
//
//    }

}