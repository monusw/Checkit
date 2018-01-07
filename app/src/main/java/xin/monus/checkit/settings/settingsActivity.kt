package xin.monus.checkit.settings

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.User
import xin.monus.checkit.login.LoginActivity
import xin.monus.checkit.login.UserProfile


class settingsActivity : AppCompatActivity() {
    lateinit var userMessage : User
    lateinit var newUserMessage : User
    lateinit var nicknameEdit : EditText
    lateinit var passwordEdit : EditText
    lateinit var heightEdit : EditText
    lateinit var weightEdit : EditText
    lateinit var modifyBtn : Button
    lateinit var logoutBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        userMessage = UserProfile.getUser(this)
        nicknameEdit = findViewById(R.id.nickname_edit) as EditText
        passwordEdit = findViewById(R.id.password_edit) as EditText
        heightEdit = findViewById(R.id.height_edit) as EditText
        weightEdit = findViewById(R.id.weight_edit) as EditText
        modifyBtn = findViewById(R.id.user_modify) as Button
        logoutBtn = findViewById(R.id.logout) as Button

        nicknameEdit.setText(userMessage.nickname)
        passwordEdit.setText(userMessage.password)
        heightEdit.setText(userMessage.height.toString())
        weightEdit.setText(userMessage.weight.toString())

        modifyBtn.setOnClickListener {
            newUserMessage.nickname = nicknameEdit.text.toString()
            newUserMessage.password = passwordEdit.text.toString()
            newUserMessage.height = heightEdit.text.toString().toDouble()
            newUserMessage.weight = weightEdit.text.toString().toDouble()

            UserProfile.update(userMessage,this@settingsActivity)
        }

        logoutBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}