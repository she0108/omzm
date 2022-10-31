package com.she.omealjomeal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Playlist(var title: String, var userName: String, var imagePath: String, var soundIdList: MutableList<String>): Parcelable {


    init {
        this.title = title
        this.userName = userName
        this.imagePath = imagePath
        this.soundIdList = soundIdList
    }

    // playlist에 sound 추가 (sound id 저장)
//    fun addSound(sound:Sound) {
//        this.soundIdList.add(sound.id)
//    }
}