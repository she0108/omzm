package com.she.omealjomeal

import android.net.Uri

object SaveReview {
    var title: String = ""
    var resID: String = ""
    var review1: String = ""
    var review2: String = ""
    var review3: String = ""
    lateinit var imageUri: Uri
    lateinit var state: State
    lateinit var check: Check
}
