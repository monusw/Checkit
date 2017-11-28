package xin.monus.checkit.inbox.edit

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
    lateinit var finishEdit : FloatingActionButton
    lateinit var tempInboxItem : InboxItem

    //default new one InboxItem
    var isNew = true
    val id:Int by lazy { intent.getIntExtra("ID",0) }



    //date format design
    @SuppressLint("SimpleDateFormat")
    val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

    val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbox_edit)

        selectShowTime = findViewById(R.id.show_select_btn) as Button
        contentEditor = findViewById(R.id.content_edit) as EditText
        finishEdit = findViewById(R.id.edit_finish) as FloatingActionButton

        updateTimeShow()

        if (id != 0) {
            isNew = false

            inboxRepository.getInboxItemById(id, object: InboxItemDataSource.GetInboxItemCallback{
                override fun onInboxItemLoaded(item: InboxItem) {
                    tempInboxItem = item

                    selectShowTime.text = tempInboxItem.deadline
                    contentEditor.setText(tempInboxItem.content)
                    //set the position of the selection
                    contentEditor.setSelection(tempInboxItem.content.length)
                    calendar.time = tempInboxItem.deadline.toDate()
                }

                override fun onDataNotAvailable() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }

        selectShowTime.setOnClickListener {
            DatePickerDialog(
                    this@InboxEditActivity,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_YEAR, dayOfMonth)
                        updateTimeShow()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_YEAR)
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
            val addContent = contentEditor.text.toString()
            val deadline = calendar.time
            val deadlineDB = timeFormat.format(deadline)


            if (addContent.isEmpty()) {
                Toast.makeText(this,"内容不能为空",Toast.LENGTH_SHORT).show()
            }
            else if (isNew){
                tempInboxItem = InboxItem(
                        0,
                        UserProfile.getUser(this).username,
                        addContent,deadlineDB,
                        false,
                        false)

                inboxRepository.addInboxItem(tempInboxItem, object: InboxItemDataSource.OperationCallback{
                    override fun success() {
                        Toast.makeText(applicationContext,"创建成功", Toast.LENGTH_SHORT).show()

                        println(inboxRepository.cachedInboxItems.count())

                        //return to InboxFragment
                        finish()
                    }

                    override fun fail() {
                        Toast.makeText(applicationContext, "创建失败", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            else {
                tempInboxItem.content = addContent
                tempInboxItem.deadline = deadlineDB

                inboxRepository.updateInboxItem(tempInboxItem, object: InboxItemDataSource.OperationCallback{
                    override fun success() {
                        Toast.makeText(applicationContext,"修改成功", Toast.LENGTH_SHORT).show()

                        println(inboxRepository.cachedInboxItems.count())

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
        selectShowTime.text = timeFormat.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    fun String.toDate(pattern: String = "yyyy-MM-dd HH:mm"): Date? {
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
