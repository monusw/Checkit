package xin.monus.checkit.projects.actions

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import xin.monus.checkit.R
import xin.monus.checkit.util.Injection
import xin.monus.checkit.util.setupActionBar

class ActionsActivity : AppCompatActivity(), ActionsContract.View {

    override lateinit var presenter: ActionsContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actions)

        setupActionBar(R.id.toolbar) {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            // add
//            setTitle(R.string.inbox_edit_title)
        }

        val projectId = intent.getStringExtra("PROJECT_ID").toInt()

        presenter = ActionsPresenter(Injection.getActionRepository(this), this, projectId)
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
