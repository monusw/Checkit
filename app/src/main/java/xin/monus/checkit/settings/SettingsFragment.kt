package xin.monus.checkit.settings

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.User
import xin.monus.checkit.login.LoginActivity
import xin.monus.checkit.login.UserProfile

/**
 * @author wu
 * @date   2018/1/7
 */
class SettingsFragment: Fragment(), SettingsContract.View {

    override lateinit var presenter: SettingsContract.Presenter

    lateinit var userMessage : User
    lateinit var newUserMessage : User
    lateinit var nicknameEdit : EditText
    lateinit var passwordEdit : EditText
    lateinit var heightEdit : EditText
    lateinit var weightEdit : EditText
    lateinit var modifyBtn : Button
    lateinit var logoutBtn : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.activity_settings_frag, container, false)

        with(root) {
            userMessage = UserProfile.getUser(activity)
            nicknameEdit = findViewById(R.id.nickname_edit)
            passwordEdit = findViewById(R.id.password_edit)
            heightEdit = findViewById(R.id.height_edit)
            weightEdit = findViewById(R.id.weight_edit)
            modifyBtn = findViewById(R.id.user_modify)
            logoutBtn = findViewById(R.id.logout)
        }

        nicknameEdit.setText(userMessage.nickname)
        passwordEdit.setText(userMessage.password)
        heightEdit.setText(userMessage.height.toString())
        weightEdit.setText(userMessage.weight.toString())

        modifyBtn.setOnClickListener {
            newUserMessage.nickname = nicknameEdit.text.toString()
            newUserMessage.password = passwordEdit.text.toString()
            newUserMessage.height = heightEdit.text.toString().toDouble()
            newUserMessage.weight = weightEdit.text.toString().toDouble()

            UserProfile.update(userMessage,activity)
        }

        logoutBtn.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        println("settings fragment resume")
        presenter.start()
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}