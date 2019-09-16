package xin.monus.checkit.inbox.edit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.InboxItem
import xin.monus.checkit.data.source.InboxItemDataSource
import xin.monus.checkit.login.UserProfile
import xin.monus.checkit.util.Injection
import xin.monus.checkit.util.setupActionBar
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class InboxEditActivity : AppCompatActivity() {
    val inboxRepository by lazy { Injection.getInboxItemRepository(this) }
    lateinit var selectShowTime: Button
    lateinit var contentEditor: EditText
    private lateinit var finishEdit : FloatingActionButton
    lateinit var tempInboxItem : InboxItem
    private lateinit var btnFlag: Button

    //default new one InboxItem
    private var isNew = true
    val id:Int by lazy { intent.getIntExtra("ID",0) }

    //date format design
    val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
    val timeFormatFull = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbox_edit)

        selectShowTime = findViewById(R.id.show_select_btn)
        contentEditor = findViewById(R.id.content_edit)
        finishEdit = findViewById(R.id.edit_finish)
        btnFlag = findViewById(R.id.btn_flag)

        if (id != 0) {
            isNew = false

            inboxRepository.getInboxItemById(id, object: InboxItemDataSource.GetInboxItemCallback{
                override fun onInboxItemLoaded(item: InboxItem) {
                    tempInboxItem = item
                    selectShowTime.text = tempInboxItem.deadline
                    contentEditor.setText(tempInboxItem.content)
                    //set the position of the selection
                    contentEditor.setSelection(tempInboxItem.content.length)
                    val time = try {
                        timeFormat.parse(tempInboxItem.deadline)
                    } catch (e : Exception) {
                        timeFormatFull.parse(tempInboxItem.deadline)
                    }
                    println(timeFormat.format(time))
                    calendar.time = time
                }

                override fun onDataNotAvailable() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        } else {
            tempInboxItem = InboxItem(
                    0,
                    UserProfile.getUser(this).username,
                    "", "",
                    complete = false,
                    flag = false)
        }

        updateTimeShow()

        setFlagBtnPic()

        btnFlag.setOnClickListener {
            tempInboxItem.flag = !tempInboxItem.flag
            setFlagBtnPic()
        }
        selectShowTime.setOnClickListener {
            DatePickerDialog(
                    this@InboxEditActivity,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        updateTimeShow()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
            TimePickerDialog(
                    this@InboxEditActivity,
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
            checkEditFinish()
        }

        setupActionBar(R.id.toolbar) {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            // add
            setTitle(R.string.inbox_edit_title)
        }


    }


    private fun checkEditFinish() {
        val addContent = contentEditor.text.toString()
        val deadline = calendar.time
        val deadlineDB = timeFormat.format(deadline)


        when {
            addContent.isEmpty() -> {
                Toast.makeText(this,getString(R.string.no_null_project_content),Toast.LENGTH_SHORT).show()
                return
            }
            isNew -> {
                tempInboxItem.content = addContent
                tempInboxItem.deadline = deadlineDB
                inboxRepository.addInboxItem(tempInboxItem, object: InboxItemDataSource.OperationCallback{
                    override fun success() {
//                        Toast.makeText(applicationContext,"创建成功", Toast.LENGTH_SHORT).show()
                        println(inboxRepository.cachedInboxItems.count())
                        finish()
                    }

                    override fun fail() {
                        Toast.makeText(applicationContext, getString(R.string.global_operation_failed), Toast.LENGTH_SHORT).show()
                    }
                })
            }
            else -> {
                tempInboxItem.content = addContent
                tempInboxItem.deadline = deadlineDB
                inboxRepository.updateInboxItem(tempInboxItem, object: InboxItemDataSource.OperationCallback{
                    override fun success() {
//                        Toast.makeText(applicationContext,"修改成功", Toast.LENGTH_SHORT).show()
                        println(inboxRepository.cachedInboxItems.count())
                        finish()
                    }

                    override fun fail() {
                        Toast.makeText(applicationContext, getString(R.string.global_operation_failed), Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun setFlagBtnPic() {
        val drawable: Drawable? = if (tempInboxItem.flag) {
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
        selectShowTime.text = timeFormat.format(date)
    }

    fun String.toDate(pattern: String = "yyyy-MM-dd HH:mm"): Date? {

        val sdFormat = try {
            SimpleDateFormat(pattern, Locale.CHINA)
        } catch (e: IllegalArgumentException) {
            null
        }
        return sdFormat?.let {
            try {
                it.parse(this)
            } catch (e: ParseException) {
                null
            }
        }
    }
}
