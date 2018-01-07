package xin.monus.checkit.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import xin.monus.checkit.R
import xin.monus.checkit.inbox.InboxActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupUI()
    }


    private fun setupUI() {
        btn_login.setOnClickListener {
            checkLogin()
        }

        btn_register.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        btn_nologin.setOnClickListener {
            alert(R.string.confirm_direct_use) {
                yesButton {
                    val intent = Intent(this@LoginActivity, InboxActivity::class.java)
                    startActivity(intent)
                }
                noButton {
                    println("do nothing")
                }
            }.show()
        }
    }

    private fun checkLogin() {

    }

}
