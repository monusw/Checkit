package xin.monus.checkit.daily.stepCounter.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import xin.monus.checkit.daily.stepCounter.utils.CountDownTimer
import java.util.*


class StepDetector(context : Context) : SensorEventListener {
    private var TAG = "TAG_StepDetector"    //"StepDetector"
    //存放三轴数据（x,y,z）的个数
    private var valueNum = 5
    //用于存放计算阈值的波峰波谷差值
    private val tempValue = FloatArray(valueNum)
    private var tempCount = 0
    //是否上升的标志位
    private var isDirectionUp = false
    //持续上升的次数
    private var continueUpCount = 0
    //上一点的持续上升的次数，为了记录波峰的上升次数
    private var continueUpFormerCount = 0
    //上升的状态，上升还是下降
    private var lastStatus = false
    //波峰值
    private var peakOfWave = 0f
    //波谷值
    private var valleyOfWave = 0f
    //此次波峰的时间
    private var timeOfThisPeak = 0L
    //上次波峰的时间
    private var timeOfLastPeak = 0L
    //当前的时间
    private var timeOfNow = 0L
    //上次传感器的值
    private var gravityOld = 0f
    //动态阈值需要动态的数据，这个值用于这些动态数据的阈值
    private var initialValue = 1.7f
    //初始阈值
    private var threadValue = 2.0f

    //初始范围
    private var minValue = 11f
    private var maxValue = 1906f

    /**
     * 0-准备计时，1-计时中，2-正常计步中
     */
    private var CountTimeState = 0
    //记录上一次临时的步数
    private var lastStep = -1
    lateinit var timer :Timer
    //倒计时3.5秒，3.5秒内不会显示计步，用于屏蔽席位波动
    private var duration = 3500L
    lateinit var time : TimeCount
    internal var onSensorChangeListener: OnSensorChangeListener ?= null

    companion object {
        //记录临时的步数
        var TEMP_STEP = 0
        //用x,y,z轴三个维度算出平均值
        var average = 0f
        //记录当前的步数
        var CURRENT_STEP = 0
    }

    //定义回调函数
    interface OnSensorChangeListener {
        fun onChange()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    //监听器set方法
    fun setOnSensorChangeListener(onSensorChangeListener : OnSensorChangeListener) {
        this.onSensorChangeListener = onSensorChangeListener
    }

    //当传感器发生变化后调用的回调函数
    override fun onSensorChanged(event : SensorEvent) {
        var sensor = event.sensor
        //同步块
        synchronized(this) {
            //获取加速度传感器
            if (sensor.type == Sensor.TYPE_ACCELEROMETER) {
                calc_step(event)
            }
        }
    }

    @Synchronized private fun calc_step(event: SensorEvent) {
        //算出加速度传感器的x、y、z三轴的平均数值（为了平衡在某一个方向数值过大造成的数据误差）
        average = Math.sqrt(Math.pow(event.values[0].toDouble(), 2.0)
                + Math.pow(event.values[1].toDouble(), 2.0) + Math.pow(event.values[2].toDouble(), 2.0)).toFloat()
        detectorNewStep(average)
    }

    /**
     * 监测新的步数
     *
     * 1.传入sersor中的数据
     * 2.如果检测到了波峰，并且符合时间差以及阈值的条件，则判定位1步
     * 3.符合时间差条件，波峰波谷差值大于initialValue，则将该差值纳入阈值的计算中
     * @param values  加速传感器三轴的平均值
     */
    private fun detectorNewStep(values : Float) {
        if (gravityOld == 0f) {
            gravityOld=values
        } else {
            if (DetectorPeak(values, gravityOld)) {
                timeOfLastPeak = timeOfThisPeak
                timeOfNow = System.currentTimeMillis()

                if (timeOfNow - timeOfLastPeak >= 200 && (peakOfWave - valleyOfWave >= threadValue)) {
                    timeOfThisPeak = timeOfNow
                    //更新界面的处理，不涉及算法
                    preStrp()
                }
                if (timeOfNow - timeOfLastPeak >= 200 && (peakOfWave - valleyOfWave >= initialValue)) {
                    timeOfThisPeak = timeOfNow
                    threadValue = peakValleyThread(peakOfWave - valleyOfWave)
                }
            }
        }
        gravityOld = values
    }

    /**
     * 判断状态并计步
     */
    private fun preStrp() {
        if (CountTimeState == 0) {
            //开启计时器(倒计时3.5秒,倒计时时间间隔为0.7秒)  是在3.5秒内每0.7面去监测一次。
            time = TimeCount(duration, 700)
            time.start()
            CountTimeState = 1  //计时中
            Log.v(TAG, "开启计时器")
        } else if (CountTimeState == 1) {
            TEMP_STEP++          //如果传感器测得的数据满足走一步的条件则步数加1
            Log.v(TAG, "计步中 TEMP_STEP:" + TEMP_STEP)
        } else if (CountTimeState == 2) {
            CURRENT_STEP++
            if (onSensorChangeListener != null) {
                //在这里调用onChange()  因此在StepService中会不断更新状态栏的步数
                onSensorChangeListener!!.onChange()
            }
        }
    }

    /**
     * 监测波峰
     * 以下四个条件判断为波峰
     * 1.目前点为下降的趋势：isDirectionUp为false
     * 2.之前的点为上升的趋势：lastStatus为true
     * 3.到波峰为止，持续上升大于等于2次
     * 4.波峰值大于1.2g,小于2g
     * 记录波谷值
     * 1.观察波形图，可以发现在出现步子的地方，波谷的下一个就是波峰，有比较明显的特征以及差值
     * 2.所以要记录每次的波谷值，为了和下次的波峰作对比
     * @param newValue
     * @param oldValue
     * @return
     */
    fun DetectorPeak(newValue: Float, oldValue: Float): Boolean {
        lastStatus = isDirectionUp
        if (newValue >= oldValue) {
            isDirectionUp = true
            continueUpCount++
        } else {
            continueUpFormerCount = continueUpCount
            continueUpCount = 0
            isDirectionUp = false
        }
        if (!isDirectionUp && lastStatus && continueUpFormerCount >= 2 && oldValue >= minValue && oldValue < maxValue) {
            //满足上面波峰的四个条件，此时为波峰状态
            peakOfWave = oldValue
            return true
        } else if (!lastStatus && isDirectionUp) {
            //满足波谷条件，此时为波谷状态
            valleyOfWave = oldValue
            return false
        } else {
            return false
        }
    }

    /**
     * 阈值的计算
     * 1.通过波峰波谷的差值计算阈值
     * 2.记录4个值，存入tempValue[]数组中
     * 3.在将数组传入函数averageValue中计算阈值
     * @param value
     * @return
     */
    fun peakValleyThread(value: Float): Float {
        var tempThread = threadValue
        if (tempCount < valueNum) {
            tempValue[tempCount] = value
            tempCount++
        } else {
            //此时tempCount=valueNum=5
            tempThread = averageValue(tempValue, valueNum)
            for (i in 1 until valueNum) {
                tempValue[i - 1] = tempValue[i]
            }
            tempValue[valueNum - 1] = value
        }
        return tempThread
    }

    /**
     * 梯度化阈值
     * 1.计算数组的均值
     * 2.通过均值将阈值梯度化在一个范围里
     *
     * 这些数据是通过大量的统计得到的
     * @param value
     * @param n
     * @return
     */
    fun averageValue(value: FloatArray, n: Int): Float {
        var ave = 0f
        for (i in 0 until n) {
            ave += value[i]
        }
        ave /= valueNum  //计算数组均值
        if (ave >= 8) {
            Log.v(TAG, "超过8")
            ave = 4.3.toFloat()
        } else if (ave >= 7 && ave < 8) {
            Log.v(TAG, "7-8")
            ave = 3.3.toFloat()
        } else if (ave >= 4 && ave < 7) {
            Log.v(TAG, "4-7")
            ave = 2.3.toFloat()
        } else if (ave >= 3 && ave < 4) {
            Log.v(TAG, "3-4")
            ave = 2.0.toFloat()
        } else {
            Log.v(TAG, "else (ave<3)")
            ave = 1.7.toFloat()
        }
        return ave
    }

    inner class TimeCount(millisInFuture : Long, countDownIntercal : Long) :
            CountDownTimer(millisInFuture, countDownIntercal) {
        override fun onFinish() {
            //如果计时器正常结束，则开始计步
            time.cancel()
            CURRENT_STEP += TEMP_STEP
            lastStep = -1
            Log.v(TAG, "计时器正常结束")

            timer = Timer(true)
            val task = object : TimerTask() {
                override fun run() {
                    //当步数不在增长的时候停止计步
                    if (lastStep == CURRENT_STEP) {
                        timer.cancel()
                        CountTimeState = 0
                        lastStep = -1
                        TEMP_STEP = 0
                        Log.v(TAG, "停止计步：" + CURRENT_STEP)
                    } else {
                        lastStep = CURRENT_STEP
                    }
                }
            }
            timer.schedule(task, 0, 2000)   //每隔两秒执行一次，不断监测是否已经停止运动了。
            CountTimeState = 2
        }

        override fun onTick(millisUntilFinished: Long) {
            if (lastStep == TEMP_STEP) {
                //一段时间内，TEMP_STEP没有步数增长，则计时停止，同时计步也停止
                Log.v(TAG, "onTick 计时停止")
                time.cancel()
                CountTimeState = 0
                lastStep = -1
                TEMP_STEP = 0
            } else {
                lastStep = TEMP_STEP
            }
        }

    }
}