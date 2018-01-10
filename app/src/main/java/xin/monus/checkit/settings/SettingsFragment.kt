package xin.monus.checkit.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.uiThread
import org.jetbrains.anko.yesButton
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.User
import xin.monus.checkit.db.LocalDbHelper
import xin.monus.checkit.login.LoginActivity
import xin.monus.checkit.login.UserProfile
import xin.monus.checkit.network.API
import xin.monus.checkit.network.api.NetWorkApi

/**
 * @author wu
 * @date   2018/1/7
 */
class SettingsFragment: Fragment(), SettingsContract.View {

    override lateinit var presenter: SettingsContract.Presenter

    lateinit var userMessage : User
    lateinit var nicknameEdit : EditText
    lateinit var heightEdit : EditText
    lateinit var weightEdit : EditText
    lateinit var calEdit: EditText
    lateinit var modifyBtn : Button
    lateinit var logoutBtn : Button

    lateinit var nicknameLabel: TextView
    lateinit var usernameLabel: TextView
    lateinit var syncBtn: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.activity_settings_frag, container, false)

        with(root) {
            userMessage = UserProfile.getUser(activity)
            nicknameLabel = findViewById(R.id.nickname_label)
            usernameLabel = findViewById(R.id.username_label)
            nicknameEdit = findViewById(R.id.nickname_edit)
            heightEdit = findViewById(R.id.height_edit)
            weightEdit = findViewById(R.id.weight_edit)
            calEdit  = findViewById(R.id.cal_edit)
            modifyBtn = findViewById(R.id.btn_confirm_modify)
            logoutBtn = findViewById(R.id.btn_logout)
            syncBtn = findViewById(R.id.btn_sync)
        }

        nicknameLabel.text = userMessage.nickname
        usernameLabel.text = userMessage.username
        nicknameEdit.setText(userMessage.nickname)
        heightEdit.setText(userMessage.height.toString())
        weightEdit.setText(userMessage.weight.toString())
        calEdit.setText(userMessage.daily_calorie.toString())

        modifyBtn.setOnClickListener {
            checkModify()
        }

        logoutBtn.setOnClickListener {
            checkLogout()
        }

        syncBtn.setOnClickListener {
            syncFromServer()
        }

        return root
    }

    //TODO: add
    private fun syncFromServer() {
        println("start sync")
        Toast.makeText(activity, getString(R.string.settings_start_sync), Toast.LENGTH_SHORT).show()
        doAsync {
            API.syncUserInfo(userMessage.username, activity, object : NetWorkApi.SyncResult {
                override fun success() {
                    println("sync user success")
                }
                override fun fail() {
                    println("sync user fail")
                }
            })
            //TODO
            API.syncInboxItems(userMessage.username, activity, object : NetWorkApi.SyncResult {
                override fun success() {
                    println("sync inbox item success")
                }
                override fun fail() {
                    println("sync inbox item failed")
                }
            })
            uiThread {
                Toast.makeText(activity, getString(R.string.settings_end_sync), Toast.LENGTH_SHORT).show()
                println("sync success")
            }
        }

    }

    private fun checkLogout() {
        alert(R.string.settings_msg_logout) {
            yesButton {
                clearData()
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                activity.finish()
            }
            noButton {
                println("cancel logout")
            }
        }.show()
    }

    private fun clearData() {
        val preferences = activity.getSharedPreferences("login", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("FIRST_LOGIN", true)
        editor.apply()
        UserProfile.deleteUser(activity)
        LocalDbHelper.deleteDatabase(activity)
    }

    private fun checkModify() {
        if (nicknameEdit.text.isEmpty() || heightEdit.text.isEmpty() ||
                weightEdit.text.isEmpty() || calEdit.text.isEmpty()) {
            Toast.makeText(activity, getString(R.string.settings_incorrect_input), Toast.LENGTH_SHORT).show()
            return
        }

        userMessage.nickname = nicknameEdit.text.toString()
        userMessage.height = heightEdit.text.toString().toDouble()
        userMessage.weight = weightEdit.text.toString().toDouble()
        userMessage.daily_calorie = calEdit.text.toString().toDouble()

        if (UserProfile.update(userMessage, activity)) {
            nicknameLabel.text = userMessage.nickname
            Toast.makeText(activity, getString(R.string.settings_edit_success), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, getString(R.string.settings_edit_failed), Toast.LENGTH_SHORT).show()
        }
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