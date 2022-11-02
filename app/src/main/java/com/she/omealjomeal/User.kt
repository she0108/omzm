package com.she.omealjomeal


// 사용자가 작성한 리뷰들 저장 (soundID 리스트로 저장) -> 나의 리뷰 화면에서 보여줌
data class User(var name: String = "") {

    var postedReviewsId: MutableList<String> = mutableListOf()

}
