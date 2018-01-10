package xin.monus.checkit.daily.stepCounter.utils

import android.content.Context


class StepData(context : Context) {

    val TODAY_KEY = "today"
    val STEP_NUMBER_KEY = "stepKey"
    val PREVIOUS_STEP = "previousStep"
    var stepData = context.getSharedPreferences("step_data", Context.MODE_PRIVATE)
    var editor = stepData.edit()

    //获取存储的日期信息，默认为yyyy-MMM-dd
    fun getDate() = stepData.getString(TODAY_KEY,"yyyy-MM-dd")

    //获取保存的步数，默认为0
    fun getStepNumber() = stepData.getInt(STEP_NUMBER_KEY, 0)

    //获取计步传感器之前保存的总步数,默认为0
    fun getPreviousStep() = stepData.getInt(PREVIOUS_STEP, 0)

    //保存计步传感器之前的总步数
    fun setPreviousStep(previousStep : Int) {
        editor.putInt(PREVIOUS_STEP, previousStep)
        editor.apply()
    }

    //保存日期
    fun setDate(date : String) {
        editor.putString(TODAY_KEY, date)
        editor.apply()
    }

    //保存步数
    fun setStep(step : Int) {
        editor.putInt(STEP_NUMBER_KEY, step)
        editor.apply()
    }
}