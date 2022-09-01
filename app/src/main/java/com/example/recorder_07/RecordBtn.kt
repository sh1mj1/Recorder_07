package com.example.recorder_07

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatImageButton

class RecordBtn(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs) {

    // RecordBtn 의 재사용성을 더 늘리고 싶으면 xml 에서 속성으로 지정하지 않고 이 클래스 자체에 넣어준다.
    init {
        setBackgroundResource(R.drawable.shape_oval_button)
    }


    fun updateIconWithState(state: State){
        when (state) {
            State.BEFORE_RECORDING -> {
                setImageResource(R.drawable.ic_record)
            }
            State.ON_RECORDING -> {
                setImageResource(R.drawable.ic_stop)
            }
            State.AFTER_RECORDING -> {
                setImageResource(R.drawable.ic_play)
            }
            State.ON_PLAYING -> {
                  setImageResource(R.drawable.ic_stop)
            }
        }
    }




}

