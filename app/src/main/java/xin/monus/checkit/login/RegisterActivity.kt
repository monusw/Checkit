package xin.monus.checkit.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.User
import xin.monus.checkit.inbox.InboxActivity
import xin.monus.checkit.network.API
import xin.monus.checkit.network.api.NetWorkApi
import xin.monus.checkit.util.setupActionBar

class RegisterActivity : AppCompatActivity() {

    lateinit var usernameTxt: EditText
    lateinit var passwordTxt: EditText
    lateinit var rePasswordTxt: EditText
    lateinit var progressCircle: ProgressBar

    val TAG = "FIRST_LOGIN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupActionBar(R.id.toolbar) {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.register_activity_title)
        }

        setupUI()
    }

    private fun setupUI() {
        usernameTxt = findViewById(R.id.username) as EditText
        passwordTxt = findViewById(R.id.password) as EditText
        rePasswordTxt = findViewById(R.id.re_password) as EditText
        progressCircle = findViewById(R.id.register_progress) as ProgressBar
        btn_register.setOnClickListener {
            checkRegister()
        }
        btn_back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun checkRegister() {
        usernameTxt.error = null
        passwordTxt.error = null
        rePasswordTxt.error = null


        if (usernameTxt.text.isEmpty()) {
            usernameTxt.error = getString(R.string.no_null_username)
            return
        }
        if (passwordTxt.text.isEmpty()) {
            passwordTxt.error = getString(R.string.no_null_password)
            return
        }
        if (rePasswordTxt.text.isEmpty()) {
            rePasswordTxt.error = getString(R.string.no_null_re_password)
            return
        }

        val username = usernameTxt.text.toString()
        val password = passwordTxt.text.toString()
        val rePassword = rePasswordTxt.text.toString()

        if (password != rePassword) {
            rePasswordTxt.error = getString(R.string.not_fit_password)
            return
        }

        progressCircle.visibility = View.VISIBLE

        doAsync {
            API.checkRegister(username, password, object : NetWorkApi.UserCallback {
                override fun success(user: User) {
                    uiThread {
                        println("register success")
                        UserProfile.saveUser(user, this@RegisterActivity)
                        progressCircle.visibility = View.GONE
                        updateAutoLogin()
                        val intent = Intent(this@RegisterActivity, InboxActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun fail(msg: String) {
                    uiThread {
                        println("register failed")
                        progressCircle.visibility = View.GONE
                        Toast.makeText(this@RegisterActivity, R.string.register_error, Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }


    }

    private fun updateAutoLogin() {
        val preferences = getSharedPreferences("login", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean(TAG, false)
        editor.apply()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
