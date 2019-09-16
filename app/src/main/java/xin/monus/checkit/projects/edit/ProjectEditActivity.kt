package xin.monus.checkit.projects.edit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_project_edit.*
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.Project
import xin.monus.checkit.data.entity.ProjectType
import xin.monus.checkit.data.source.ProjectsDataSource
import xin.monus.checkit.login.UserProfile
import xin.monus.checkit.projects.ProjectsActivity
import xin.monus.checkit.util.Injection
import xin.monus.checkit.util.setupActionBar
import java.text.SimpleDateFormat
import java.util.*

class ProjectEditActivity : AppCompatActivity() {

    private val projectRepository by lazy { Injection.getProjectsRepository(this) }
    var projectId: Int = 0
    private lateinit var contentTxt: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var btnFlag: Button
    private lateinit var btnDeadline: Button
    private lateinit var btnCheck: FloatingActionButton
    lateinit var newProject: Project

    private val calendar: Calendar = Calendar.getInstance()
    private val sf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_edit)

        projectId = intent.getIntExtra("ID", 0)

        setupActionBar(R.id.toolbar) {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            if (projectId == 0) {
                setTitle(R.string.project_new_title)
            } else {
                setTitle(R.string.project_edit_title)
            }
        }

        if (projectId != 0) {
            projectRepository.getProjectById(projectId, object : ProjectsDataSource.GetProjectCallback {
                override fun onProjectLoaded(project: Project) {
                    newProject = project
                    setupUI()
                }

                override fun onDataNotAvailable() {
                    setupUI()
                }

            })
        } else {
            val user = UserProfile.getUser(this)
            newProject = Project(
                    username = user.username,
                    content = "example",
                    type = ProjectType.PARALLEL,
                    deadline = "2018-1-1 00:00",
                    complete = false,
                    flag = false,
                    actionList = ArrayList()
            )
            setupUI()
        }

    }

    fun setupUI() {
        contentTxt = findViewById(R.id.content)
        radioGroup = findViewById(R.id.radio_group)
        btnFlag = findViewById(R.id.btn_flag)
        btnDeadline = findViewById(R.id.btn_deadline)
        btnCheck = findViewById(R.id.edit_finish)

        // 初始化UI
        if (projectId != 0) {
            when(newProject.type) {
                ProjectType.PARALLEL ->
                    radioGroup.check(parallel_btn.id)
                ProjectType.SEQUENCE ->
                    radioGroup.check(sequence_btn.id)
                ProjectType.SINGLE ->
                    radioGroup.check(single_btn.id)
            }
            contentTxt.setText(newProject.content)
            println(newProject.deadline)
            btnDeadline.text = newProject.deadline

        } else {
            btnDeadline.text = sf.format(Date())
        }


        setFlagBtnPic()

        btnFlag.setOnClickListener {
            newProject.flag = !newProject.flag
            setFlagBtnPic()
        }

        radioGroup.setOnCheckedChangeListener { _, i ->
            when(i) {
                parallel_btn.id ->
                    newProject.type = ProjectType.PARALLEL
                sequence_btn.id ->
                    newProject.type = ProjectType.SEQUENCE
                single_btn.id ->
                    newProject.type = ProjectType.SINGLE
            }
        }


        btnDeadline.setOnClickListener {
            println("deadline")

            DatePickerDialog(
                    this@ProjectEditActivity,
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
                    this@ProjectEditActivity,
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
        if (contentTxt.text.isEmpty()) {
            Toast.makeText(this, R.string.no_null_project_content, Toast.LENGTH_SHORT).show()
            return
        }
        newProject.content = contentTxt.text.toString()
        newProject.deadline = btnDeadline.text.toString()
        if (projectId == 0) {
            projectRepository.addProject(newProject, object : ProjectsDataSource.OperationCallback {
                override fun success() {
                    val intent = Intent(this@ProjectEditActivity, ProjectsActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                override fun fail() {
                    Toast.makeText(this@ProjectEditActivity, R.string.global_operation_failed, Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            projectRepository.updateProject(newProject, object : ProjectsDataSource.OperationCallback {
                override fun success() {
                    val intent = Intent(this@ProjectEditActivity, ProjectsActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                override fun fail() {
                    Toast.makeText(this@ProjectEditActivity, R.string.global_operation_failed, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun updateTimeShow() {
        val date = calendar.time
        btnDeadline.text = sf.format(date)
    }

    private fun setFlagBtnPic() {
        val drawable: Drawable? = if (newProject.flag) {
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
}
