package xin.monus.checkit.login

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*
import xin.monus.checkit.R
import xin.monus.checkit.util.setupActionBar

class RegisterActivity : AppCompatActivity() {

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
        btn_register.setOnClickListener {
            checkRegister()
        }
        btn_back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun checkRegister() {

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
