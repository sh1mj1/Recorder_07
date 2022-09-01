package com.example.recorder_07

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class SoundVisualizerView (
    context: Context,
    attrs: AttributeSet? = null): View(context, attrs) {

    // Mark -Properties/////////////////////////////////////////
    var onRequestCurrentAmplitude: ( () -> Int )? = null

    private val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.purple_200)
        strokeWidth = LINE_WIDTH
        strokeCap = Paint.Cap.ROUND
    } // 계단화 방지

    private var drawingWidth: Int = 0
    private var drawingHeight: Int = 0
    private var drawingAmplitudes: List<Int> = emptyList()
    // For test (10번 최대값까지 임의 수를 리스트에 넣기)
//    var drawingAmplitudes: List<Int> = (0..10).map{ Random.nextInt(Short.MAX_VALUE.toInt())}
    private var isReplaying = false
    private var replayingPosition: Int = 0

    private val visualizeRepeatAction: Runnable = object: Runnable {
        override fun run() {
            // TODO: Amplitude 가져오고 Draw 요청 \
            if(!isReplaying){
                val currentAmplitude = onRequestCurrentAmplitude?.invoke() ?:0
                drawingAmplitudes = listOf(currentAmplitude) + drawingAmplitudes
            }else{
                replayingPosition++
            }

            invalidate() // 데이터가 초기화 되었을 때 이걸 호출 해야 onDraw 가 다시 호출됨. 호출하지 않으면 데이터는 계속 추가되는데 뷰가 갱신이 안된다.
            handler?.postDelayed(this, 20L)

        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        drawingWidth = w
        drawingHeight = h

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        val centerY = drawingHeight / 2f
        // 어떤걸 그려야 할지.

        var offsetX = drawingWidth.toFloat()
        // 수많은 진폭값들을 리스트의 원소로 오른쪽부터 접근할 거임. (drawingAmplitudes)
        // (UI가 오른쪽부터 왼쪽으로 이동하는 느낌으로 보이니까)
        drawingAmplitudes
            .let{ amplitudes ->
                if(isReplaying){
                    amplitudes.takeLast(replayingPosition) // 가장 뒤에 있는 것부터 순서대로 가져오게
                }else{
                    amplitudes
                }
            }
            .forEach {amplitude ->
            val lineLength = amplitude / MAX_AMPLITUDE * drawingHeight * 0.8F

            offsetX -= LINE_SPACE
            // 녹음을 길게하면 분명히 뒤에서 다 못 그리는 amplitude 가 발생할 것임.
            // 화면에 보이지 않는다면 forEach 루프를 벗어나도록 하자.
            if (offsetX < 0) return@forEach

            canvas.drawLine(
                offsetX,
                centerY - lineLength / 2F,
                offsetX,
                centerY + lineLength / 2F,
                amplitudePaint
            )

        }



    }

    fun startVisualizing(isReplaying: Boolean) {
        this.isReplaying = isReplaying // 현재 우리가 가지고 있는 drawingAmplitudes 가 있는데 Replaying 할 때는 visualizeRepeatAction
        handler?.post(visualizeRepeatAction)
    }

    fun stopVisualizing() {
            // 리플레잉 할 때 리플레이가 마무리 되었을 때 그것을 초기화하는
        replayingPosition = 0
            // 부분
        handler?.removeCallbacks(visualizeRepeatAction)
    }

    fun clearVisualization(){
        drawingAmplitudes = emptyList()
        invalidate()
    }


    companion object {
        private const val LINE_WIDTH = 10F
        private const val LINE_SPACE = 15F
        // amplitude 라는 메서드를 사용할 것임. 그 때 리턴되는 최대값이 32,767 : Short
        // Int 값을 이 32,767 로 나누면 값이 0이 됨. 그래서 Float() 타입으로 변경..
        // 왜지?
        private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat()
        private const val ACTION_INTERVAL = 20L
    }

}