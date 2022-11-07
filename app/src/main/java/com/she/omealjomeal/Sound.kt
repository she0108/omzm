package com.she.omealjomeal

import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import kotlinx.android.parcel.Parcelize

class Sound() {

    var id: String = ""
    var title: String = ""
    var restaurantId: String = ""
    var userName: String = ""
    var review1: String = ""    // 1. 선택지형 (-와 함께)
    var review2: String = ""    // 2. 한줄표현 주관식. 최대 글자수 제한 30byte
    var review3: String = ""    // 3. 추가 텍스트 리뷰(선택). 최대 글자수 제한 (UI에 맞춰서?)
    var imagePath: String = ""      // 이미지 파일 storage에 저장한 경로
    var audioPath: String? = ""     // 오디오 파일 storage에 저장한 경로
    var duration: Long? = null      // 오디오 파일 길이. CountUpView에서 가져와야 할 듯?


}
