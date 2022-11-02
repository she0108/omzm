package com.she.omealjomeal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Restaurant(): Parcelable {

    var id: String = ""
    var name: String = ""
    var imagePath: String = ""
    var latitude: Float = 0.0F
    var longitude: Float = 0.0F
    var address: String = ""

}