package xin.monus.checkit.daily.stepCounter.utils

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.os.SystemClock


abstract class CountDownTimer(millisInFuture : Long, countdownInterval : Long) {
    //多长时间计时器应该停止
    private val mMillisInFutrue = millisInFuture
    //倒计时的时间间隔
    private val mCountdownInterval = countdownInterval
    private var mStopTimeInFuture: Long = 0
    private var mCancelled = false

    /**
     * 取消倒计时
     * 不要从倒计时计时器线程中调用它
     */
    fun cancel() {
        mHandler.removeMessages(MSG)
        mCancelled = true
    }

    /**
     * 开始倒计时
     * @return
     */
    @Synchronized
    fun start() : CountDownTimer {
        if (mMillisInFutrue <= 0) {
            onFinish()
            return this
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFutrue
        mHandler.sendMessage(mHandler.obtainMessage(MSG))
        mCancelled = false
        return this
    }

    /**
     * 根据固定时间间隔进行回调
     * 在给定总的时间millisInFutrue内，每隔一个countdownInterval时间就会回调一次
     * @param millisUntilFinished  结束之前的总时间
     */
    abstract fun onTick(millisUntilFinished: Long)

    /**
     * 当计时器结束时调用的函数
     */
    abstract fun onFinish()

    companion object {
        private val MSG = 1
    }

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            synchronized(this@CountDownTimer) {
                val millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime()

                if (millisLeft <= 0) {
                    onFinish()
                } else if (millisLeft < mCountdownInterval) {
                    //当要完成的时间减去当前时间小于规定时间间隔时，不调用onTick(),只是等待结束
                    sendMessageDelayed(obtainMessage(MSG), millisLeft)
                } else {
                    val lastTickStart = SystemClock.elapsedRealtime()
                    onTick(millisLeft)
                    //考虑到用户执行onTick 占用的时间
                    var delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime()

                    //特殊情况：用户执行onTick的时间多于一个时间间隔，则跳到下一个时间间隔
                    while (delay < 0) {
                        delay += mCountdownInterval
                    }

                    if (!mCancelled) {
                        sendMessageDelayed(obtainMessage(MSG), delay)
                    } else {

                    }
                }
            }
        }

    }
}