package com.she.omealjomeal

import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import kotlinx.android.parcel.Parcelize

@Parcelize
class Sound(): Parcelable {

    var title: String = ""
    var restaurantName: String = ""
    var userName: String = ""
    var review: String = ""
    var imagePath: String = ""
    var id: String = ""
    var audioPath: String? = ""
    var duration: Long? = null

}
