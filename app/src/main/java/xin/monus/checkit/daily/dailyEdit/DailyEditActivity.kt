package xin.monus.checkit.daily.dailyEdit

import android.app.TimePickerDialog
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.Daily
import xin.monus.checkit.data.source.DailyDataSource
import xin.monus.checkit.login.UserProfile
import xin.monus.checkit.util.Injection
import xin.monus.checkit.util.setupActionBar
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class DailyEditActivity : AppCompatActivity() {

    val dailyRepository by lazy { Injection.getDailyRepository(this)}
    lateinit var selectRemindTime: Button
    lateinit var contentEditor: EditText
    lateinit var finishEdit : FloatingActionButton
    lateinit var tempDailyItem : Daily

    var isNew = true
    val id:Int by lazy { intent.getIntExtra("ID",0) }

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_item_edit)

        selectRemindTime = findViewById(R.id.remind_time_btn) as Button
        contentEditor = findViewById(R.id.daily_content_edit) as EditText
        finishEdit = findViewById(R.id.daily_edit_finish) as FloatingActionButton

        updateTimeShow()

        if (id != 0) {
            isNew = false

            dailyRepository.getDailyItemById(id, object: DailyDataSource.GetDailyItemCallback{
                override fun onDailyItemLoaded(item: Daily) {
                    tempDailyItem = item

                    selectRemindTime.text = tempDailyItem.remindTime
                    contentEditor.setText(tempDailyItem.content)
                    //set the position of the selection
                    contentEditor.setSelection(tempDailyItem.content.length)
                    calendar.time = tempDailyItem.remindTime.toDate()
                }

                override fun onDataNotAvailable() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }

        selectRemindTime.setOnClickListener {
            TimePickerDialog(
                    this@DailyEditActivity,
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE,minute)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            ).show()
        }

        finishEdit.setOnClickListener {
            val addContent = contentEditor.text.toString()
            val deadline = calendar.time
            val deadlineDB = timeFormat.format(deadline)


            if (addContent.isEmpty()) {
                Toast.makeText(this,"内容不能为空",Toast.LENGTH_SHORT).show()
            }
            else if (isNew){
                tempDailyItem = Daily(
                        0,
                        UserProfile.getUser(this).username,
                        addContent,deadlineDB,
                        false,
                        false)

                dailyRepository.addDailyItem(tempDailyItem, object: DailyDataSource.OperationCallback{
                    override fun success() {
                        Toast.makeText(applicationContext,"创建成功", Toast.LENGTH_SHORT).show()


                        //return to InboxFragment
                        finish()
                    }

                    override fun fail() {
                        Toast.makeText(applicationContext, "创建失败", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            else {
                tempDailyItem.content = addContent
                tempDailyItem.remindTime = deadlineDB

                dailyRepository.updateDailyItem(tempDailyItem, object: DailyDataSource.OperationCallback{
                    override fun success() {
                        Toast.makeText(applicationContext,"修改成功", Toast.LENGTH_SHORT).show()



                        //return to InboxFragment
                        finish()
                    }

                    override fun fail() {
                        Toast.makeText(applicationContext, "修改失败", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        setupActionBar(R.id.toolbar) {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            // add
            setTitle(R.string.inbox_edit_title)
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun updateTimeShow() {
        val date = calendar.time
        selectRemindTime.text = timeFormat.format(date)
    }

    fun String.toDate(pattern: String = "HH:mm"): Date? {
        val sdFormat = try {
            SimpleDateFormat(pattern)
        } catch (e: IllegalArgumentException) {
            null
        }
        val date = sdFormat?.let {
            try {
                it.parse(this)
            } catch (e: ParseException) {
                null
            }
        }
        return date
    }
}