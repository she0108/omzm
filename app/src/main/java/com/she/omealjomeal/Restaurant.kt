package com.she.omealjomeal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Restaurant(): Parcelable {

    var id: String = ""
    var name: String = ""
    var imagePath: String = ""
    var location: String = ""
}