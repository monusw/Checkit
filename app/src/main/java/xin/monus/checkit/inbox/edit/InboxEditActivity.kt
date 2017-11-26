package xin.monus.checkit.inbox.edit

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import xin.monus.checkit.R
import xin.monus.checkit.util.Injection
import xin.monus.checkit.util.setupActionBar

class InboxEditActivity : AppCompatActivity() {

    val inboxRepository by lazy { Injection.getInboxItemRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbox_edit)

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
}
