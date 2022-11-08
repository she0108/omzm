package com.she.omealjomeal

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

// 녹음버튼 custom view
class RecordButton(context: Context, attrs: AttributeSet): AppCompatImageButton(context, attrs) {
    init {
        setImageResource(R.drawable.ic_record2)     //아이콘 여기에 입력
    }

    fun updateIconWithState(state: State) {
        when (state) {
            State.BEFORE_RECORDING -> {
                setImageResource(R.drawable.ic_record2)
            }
            State.ON_RECORDING -> {
                setImageResource(R.drawable.stop)
            }
            State.AFTER_RECORDING -> {
                setImageResource(R.drawable.ic_play)
            }
            State.ON_PLAYING -> {
                setImageResource(R.drawable.ic_review_pause)
            }
        }
    }
}

