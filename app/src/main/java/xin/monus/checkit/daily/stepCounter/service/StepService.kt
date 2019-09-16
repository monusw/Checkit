package xin.monus.checkit.daily.stepCounter.service

import android.annotation.TargetApi
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import androidx.core.app.NotificationCompat
import android.util.Log
import xin.monus.checkit.R
import xin.monus.checkit.daily.DailyFragment
import xin.monus.checkit.daily.stepCounter.config.Constant
import xin.monus.checkit.daily.stepCounter.utils.StepData
import java.text.SimpleDateFormat
import java.util.*


@TargetApi(Build.VERSION_CODES.CUPCAKE)
class StepService : Service(), SensorEventListener {
    private val TAG = "TAG_StepService"

    //默认为30秒进行一次存储
    private var duration = 30000
    private var CURRENTDATE = ""   //当前的日期
    private var sensorManager: SensorManager? = null    //传感器管理者

    private var stepDetector: StepDetector? = null
    private var nm: NotificationManager? = null
    private var builder: NotificationCompat.Builder? = null
    private val messenger = Messenger(MessengerHandler())
    //广播
    private var mBatInfoReceiver: BroadcastReceiver? = null
//    private var mWakeLock: PowerManager.WakeLock? = null
    private var time: TimeCount? = null

    //计步传感器类型 0-counter 1-detector 2-加速度传感器
    private var stepSensor = -1
    private var mStepData : StepData ?= null

    //用于计步传感器
    private var previousStep: Int = 0    //用于记录之前的步数
    private var isNewDay = false    //用于判断是否是新的一天，如果是新的一天则将之前的步数赋值给previousStep

    private class MessengerHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Constant.MSG_FROM_CLIENT -> try {
                    val messenger = msg.replyTo
                    val replyMsg = Message.obtain(null, Constant.MSG_FROM_SERVER)
                    val bundle = Bundle()
                    //将现在的步数以消息的形式进行发送
                    bundle.putInt("step", StepDetector.CURRENT_STEP)
                    replyMsg.data = bundle
                    messenger.send(replyMsg)  //发送要返回的消息
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }

                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        //初始化广播
        initBroadcastReceiver()
        Thread(Runnable {
            //启动步数监测器
            startStepDetector()
        }).start()
        startTimeCount()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initTodayData()
        updateNotification("今日步数:" + StepDetector.CURRENT_STEP + " 步")
        return START_STICKY
    }

    /**
     * 获得今天的日期
     */
    private fun getTodayDate() : String {
        val date = Date(System.currentTimeMillis())
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(date)
    }

    /**
     * 初始化当天的日期
     */
    private fun initTodayData() {
        CURRENTDATE = getTodayDate()
        mStepData = StepData(this)

        //获取存储的日期
        val date = mStepData!!.getDate()
        if (date != CURRENTDATE) {
            StepDetector.CURRENT_STEP = 0
            isNewDay = true
        } else {
            isNewDay = false
            StepDetector.CURRENT_STEP = mStepData!!.getStepNumber()
        }
    }

    /**
     * 初始化广播
     */
    private fun initBroadcastReceiver() {
        //定义意图过滤器
        val filter = IntentFilter()
        //屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        //日期修改
        filter.addAction(Intent.ACTION_TIME_CHANGED)
        //关闭广播
        filter.addAction(Intent.ACTION_SHUTDOWN)
        //屏幕高亮广播
        filter.addAction(Intent.ACTION_SCREEN_ON)
        //屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT)
        //当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
        //example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
        //所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)

//        var broadcastReceiver = object : BroadcastReceiver(){
//            override fun onReceive(context : Context, intent : Intent) {
//                val action = intent.action
//
//                when {
//                    Intent.ACTION_SCREEN_ON == action -> Log.v(TAG, "screen on")
//                    Intent.ACTION_SCREEN_OFF == action -> {
//                        Log.v(TAG, "screen off")
//                        save()
//                        //改为60秒一存储
//                        duration = 60000
//                    }
//                    Intent.ACTION_USER_PRESENT == action -> {
//                        Log.v(TAG, "screen unlock")
//                        save()
//                        //改为30秒一存储
//                        duration = 30000
//                    }
//                    Intent.ACTION_CLOSE_SYSTEM_DIALOGS == intent.action -> {
//                        Log.v(TAG, "receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS  出现系统对话框")
//                        //保存一次
//                        save()
//                    }
//                    Intent.ACTION_SHUTDOWN == intent.action -> {
//                        Log.v(TAG, "receive ACTION_SHUTDOWN")
//                        save()
//                    }
//                    Intent.ACTION_TIME_CHANGED == intent.action -> {
//                        Log.v(TAG, "receive ACTION_TIME_CHANGED")
//                        initTodayData()
//                    }
//                }
//            }
//        }
    }

    private fun startTimeCount() {
        time = TimeCount(duration.toLong(), 1000L)
        time!!.start()
    }

    /**
     * 更新通知(显示通知栏信息)
     * @param content
     */
    private fun updateNotification(content : String) {
        builder = NotificationCompat.Builder(this, "notification")
        builder!!.priority = NotificationManager.IMPORTANCE_MIN
        val contentIntent = PendingIntent.getActivity(this, 0,
                Intent(this, DailyFragment::class.java), 0)
        builder!!.setContentIntent(contentIntent)
        builder!!.setSmallIcon(R.mipmap.ic_notification)
        builder!!.setTicker("BasePedo")
        builder!!.setContentTitle("BasePedo")
        //设置不可清除
        builder!!.setOngoing(true)
        builder!!.setContentText(content)
        val notification = builder!!.build() //上面均为构造Notification的构造器中设置的属性

        startForeground(0, notification)
        nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm!!.notify(R.string.app_name, notification)
    }

    override fun onBind(intent: Intent): IBinder? {
        return messenger.binder
    }

    private fun startStepDetector() {
        if (sensorManager != null && stepDetector != null) {
            sensorManager!!.unregisterListener(stepDetector)
            sensorManager = null
            stepDetector = null
        }
        //得到休眠锁，目的是为了当手机黑屏后仍然保持CPU运行，使得服务能持续运行
        sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //android4.4以后可以使用计步传感器
        val VERSION_CODES = Build.VERSION.SDK_INT
        if (VERSION_CODES >= 19) {
            addCountStepListener()
        } else {
            addBasePedoListener()
        }
    }

    private fun addCountStepListener() {
        val detectorSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        val countSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (countSensor != null) {
            stepSensor = 0
            Log.v(TAG, "countSensor 步数传感器")
            sensorManager!!.registerListener(this@StepService, countSensor, SensorManager.SENSOR_DELAY_UI)
        } else if (detectorSensor != null) {
            stepSensor = 1
            Log.v("base", "detector")
            sensorManager!!.registerListener(this@StepService, detectorSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            stepSensor = 2
            Log.e(TAG, "Count sensor not available! 没有可用的传感器，只能用加速传感器了")
            addBasePedoListener()
        }
    }

    /**
     * 使用加速度传感器
     */
    private fun addBasePedoListener() {
        //只有在使用加速传感器的时候才会调用StepDetector这个类
        stepDetector = StepDetector()
        //获得传感器类型，这里获得的类型是加速度传感器
        //此方法用来注册，只有注册过才会生效，参数：SensorEventListener的实例，Sensor的实例，更新速率
        val sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager!!.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_UI)
        stepDetector!!.setOnSensorChangeListener(object : StepDetector.OnSensorChangeListener {
            override fun onChange() {
                updateNotification("今日步数:" + StepDetector.CURRENT_STEP + " 步")
            }
        })
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onSensorChanged(event : SensorEvent) {
        if (stepSensor == 0) {   //使用计步传感器
            if (isNewDay) {
                //用于判断是否为新的一天，如果是那么记录下计步传感器统计步数中的数据
                // 今天走的步数=传感器当前统计的步数-之前统计的步数
                previousStep = event.values[0].toInt()    //得到传感器统计的步数
                isNewDay = false
                save()
                //为防止在previousStep赋值之前数据库就进行了保存，我们将数据库中的信息更新一下
                //val list = DbUtils.getQueryByWhere(StepData::class.java, "today", arrayOf(CURRENTDATE))
                //修改数据
                mStepData!!.setPreviousStep(previousStep)
            } else {
                //取出之前的数据
                this.previousStep = mStepData!!.getPreviousStep()
            }
            StepDetector.CURRENT_STEP = event.values[0].toInt() - previousStep

        } else if (stepSensor == 1) {
            StepDetector.CURRENT_STEP++
        }
        //更新状态栏信息
        updateNotification("今日步数：" + StepDetector.CURRENT_STEP + " 步")
    }

    /**
     * 保存数据
     */
    private fun save() {
        val tempStep = StepDetector.CURRENT_STEP

        if (mStepData!!.getDate() != getTodayDate()) {
            mStepData!!.setDate(getTodayDate())
            mStepData!!.setPreviousStep(previousStep)
        } else {
            mStepData!!.setStep(tempStep)
        }
    }

    override fun onDestroy() {
        //取消前台进程
        stopForeground(true)
        //DbUtils.closeDb()
        unregisterReceiver(mBatInfoReceiver)
        val intent = Intent(this, StepService::class.java)
        startService(intent)
        super.onDestroy()
    }

    internal inner class TimeCount(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            //如果计时器正常结束，则开始计步
            time!!.cancel()
            save()
            startTimeCount()
        }
    }

}
