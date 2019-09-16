package xin.monus.checkit.daily.dailyEdit

import android.app.TimePickerDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
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

    private val dailyRepository by lazy { Injection.getDailyRepository(this)}
    lateinit var selectRemindTime: Button
    lateinit var contentEditor: EditText
    lateinit var finishEdit : FloatingActionButton
    lateinit var tempDailyItem : Daily

    lateinit var btnFlag: Button

    var isNew = true
    val id:Int by lazy { intent.getIntExtra("ID",0) }

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val calendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_item_edit)

        selectRemindTime = findViewById(R.id.remind_time_btn)
        contentEditor = findViewById(R.id.daily_content_edit)
        finishEdit = findViewById(R.id.daily_edit_finish)
        btnFlag = findViewById(R.id.btn_flag)

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
                    calendar.time = tempDailyItem.remindTime.toDate() as Date
                }

                override fun onDataNotAvailable() {
                    println("never here")
                }
            })
        } else {
            tempDailyItem  = Daily(0,
                    UserProfile.getUser(this).username,
                    "","",
                    complete = false,
                    flag = false)
        }

        setFlagBtnPic()

        btnFlag.setOnClickListener {
            tempDailyItem.flag = !tempDailyItem.flag
            setFlagBtnPic()
        }


        selectRemindTime.setOnClickListener {
            TimePickerDialog(
                    this@DailyEditActivity,
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE,minute)
                        updateTimeShow()
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
                Toast.makeText(this,getString(R.string.no_null_project_content),Toast.LENGTH_SHORT).show()
            }
            else if (isNew){
                tempDailyItem.content = addContent
                tempDailyItem.remindTime = deadlineDB

                dailyRepository.addDailyItem(tempDailyItem, object: DailyDataSource.OperationCallback{
                    override fun success() {
                        //Toast.makeText(applicationContext,"创建成功", Toast.LENGTH_SHORT).show()
                        //return to InboxFragment
                        finish()
                    }

                    override fun fail() {
                        Toast.makeText(applicationContext, getString(R.string.global_operation_failed), Toast.LENGTH_SHORT).show()
                    }
                })
            }
            else {
                tempDailyItem.content = addContent
                tempDailyItem.remindTime = deadlineDB

                dailyRepository.updateDailyItem(tempDailyItem, object: DailyDataSource.OperationCallback{
                    override fun success() {
                        //Toast.makeText(applicationContext,"修改成功", Toast.LENGTH_SHORT).show()
                        //return to InboxFragment
                        finish()
                    }

                    override fun fail() {
                        Toast.makeText(applicationContext, getString(R.string.global_operation_failed), Toast.LENGTH_SHORT).show()
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

    private fun setFlagBtnPic() {
        val drawable: Drawable? = if (tempDailyItem.flag) {
            ContextCompat.getDrawable(this, R.drawable.flag_press)
        } else {
            ContextCompat.getDrawable(this, R.drawable.flag)
        }
        drawable!!.setBounds(0,0, drawable.minimumWidth, drawable.minimumHeight)
        btnFlag.setCompoundDrawables(null, null, drawable, null)
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
            SimpleDateFormat(pattern, Locale.getDefault())
        } catch (e: IllegalArgumentException) {
            null
        }
        return sdFormat?.let {
            try {
                it.parse(this)
            } catch (e: ParseException) {
                Date()
            }
        }
    }
}