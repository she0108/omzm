package com.she.omealjomeal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Playlist(var title: String = "", var userName: String = "", var imagePath: String = ""): Parcelable {

    var soundIdList: MutableList<String>

    init {
        this.title = title
        this.userName = userName
        this.imagePath = imagePath
        this.soundIdList = mutableListOf()      // 리스트에 sound id를 저장할까 sound 자체를 저장할까 -> sound id 저장하자
    }

    // playlist에 sound 추가 (sound id 저장)
    fun addSound(sound:Sound) {
        this.soundIdList.add(sound.id)
    }
}