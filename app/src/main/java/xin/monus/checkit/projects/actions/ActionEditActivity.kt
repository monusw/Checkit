package xin.monus.checkit.projects.actions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.Action
import xin.monus.checkit.data.source.ActionDataSource
import xin.monus.checkit.util.Injection
import xin.monus.checkit.util.setupActionBar
import java.text.SimpleDateFormat
import java.util.*

class ActionEditActivity : AppCompatActivity() {

    val actionRepository by lazy { Injection.getActionRepository(this) }
    var projectId: Int = 0
    var actionId: Int = 0
    lateinit var contenTxt: EditText
    lateinit var btnFlag: Button
    lateinit var btnDeadline: Button
    lateinit var btnCheck: FloatingActionButton
    lateinit var newAction: Action

    val calendar = Calendar.getInstance()
    val sf = SimpleDateFormat("yyyy-MM-dd HH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_edit)

        projectId = intent.getIntExtra("PROJECT_ID", 1)
        actionId = intent.getIntExtra("ACTION_ID", 0)

        setupActionBar(R.id.toolbar) {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            if (actionId == 0) {
                setTitle(R.string.action_new_title)
            } else {
                setTitle(R.string.action_edit_title)
            }
        }

        if (actionId != 0) {
            actionRepository.getActionById(actionId, object : ActionDataSource.GetActionCallback {
                override fun onActionLoaded(action: Action) {
                    newAction = action
                    setupUI()
                }

                override fun onDataNotAvailable() {
                    setupUI()
                }

            })
        } else {
            newAction = Action(
                    projectId = projectId,
                    content = "example",
                    deadline = "2018-1-1 00:00",
                    complete = false,
                    flag = false,
                    subActionList = ArrayList()
            )
            setupUI()
        }

    }

    private fun setupUI() {
        contenTxt = findViewById(R.id.content) as EditText
        btnFlag = findViewById(R.id.btn_flag) as Button
        btnDeadline = findViewById(R.id.btn_deadline) as Button
        btnCheck = findViewById(R.id.edit_finish) as FloatingActionButton

        if (actionId != 0) {
            contenTxt.setText(newAction.content)
        }

        btnDeadline.text = newAction.deadline

        setFlagBtnPic()

        btnFlag.setOnClickListener {
            newAction.flag = !newAction.flag
            setFlagBtnPic()
        }

        btnDeadline.setOnClickListener {
            println("deadline")
            DatePickerDialog(
                    this@ActionEditActivity,
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
                    this@ActionEditActivity,
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE,minute)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            ).show()
        }

        btnCheck.setOnClickListener {
            checkEditFinish()
        }

    }

    private fun checkEditFinish() {
        if (contenTxt.text.isEmpty()) {
            Toast.makeText(this, R.string.no_null_project_content, Toast.LENGTH_SHORT).show()
            return
        }
        newAction.content = contenTxt.text.toString()
        newAction.deadline = btnDeadline.text.toString()

        if (actionId == 0) {
            actionRepository.addAction(projectId, newAction, object : ActionDataSource.OperationCallback {
                override fun success() {
                    val intent = Intent(this@ActionEditActivity, ActionsActivity::class.java)
                    intent.putExtra("PROJECT_ID", projectId.toString())
                    startActivity(intent)
                    finish()
                }
                override fun fail() {
                    Toast.makeText(this@ActionEditActivity, R.string.global_operation_failed, Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            actionRepository.updateAction(newAction, object : ActionDataSource.OperationCallback {
                override fun success() {
                    val intent = Intent(this@ActionEditActivity, ActionsActivity::class.java)
                    intent.putExtra("PROJECT_ID", projectId.toString())
                    startActivity(intent)
                    finish()
                }
                override fun fail() {
                    Toast.makeText(this@ActionEditActivity, R.string.global_operation_failed, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun setFlagBtnPic() {
        val drawable: Drawable? = if (newAction.flag) {
            ContextCompat.getDrawable(this, R.drawable.flag_press)
        } else {
            ContextCompat.getDrawable(this, R.drawable.flag)
        }
        drawable!!.setBounds(0,0, drawable.minimumWidth, drawable.minimumHeight)
        btnFlag.setCompoundDrawables(null, null, drawable, null)
    }

    private fun updateTimeShow() {
        val date = calendar.time
        btnDeadline.text = sf.format(date)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
