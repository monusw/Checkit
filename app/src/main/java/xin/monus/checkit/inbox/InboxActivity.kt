package xin.monus.checkit.inbox

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import xin.monus.checkit.R
import xin.monus.checkit.daily.DailyActivity
import xin.monus.checkit.forecast.ForecastActivity
import xin.monus.checkit.projects.ProjectsActivity
import xin.monus.checkit.settings.SettingsActivity
import xin.monus.checkit.util.Injection
import xin.monus.checkit.util.replaceFragmentInActivity
import xin.monus.checkit.util.setupActionBar

class InboxActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val drawerLayout by lazy { findViewById(R.id.drawer_layout) as DrawerLayout }

    private val toolbar by lazy { findViewById(R.id.toolbar) as Toolbar }

    private lateinit var inboxPresenter: InboxPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbox)

        // setup the tool bar
        setupActionBar(R.id.toolbar) {
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
            // add
            setTitle(R.string.nav_inbox_title)
        }

        // Set up the navigation drawer.
        drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark)
        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        // TODO: Change the index in different activity
        navigationView.menu.getItem(0).isChecked = true
        navigationView.setNavigationItemSelectedListener(this)


        // initialize the fragment
        val inboxFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
                as InboxFragment? ?: InboxFragment.newInstance().also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }

        //fake data
//        FakeData.generateData(this)
//        if (LocalDbHelper.deleteDatabase(this)) {
//            println("delete database success")
//        } else {
//            println("no database to be deleted")
//        }

        // create the presenter
        inboxPresenter = InboxPresenter(Injection.getInboxItemRepository(this), inboxFragment)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        when (id) {
            R.id.nav_inbox -> {

            }
            R.id.nav_projects -> {
                val intent = Intent(this@InboxActivity, ProjectsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_daily -> {
                val intent = Intent(this@InboxActivity, DailyActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_forecast -> {
                val intent = Intent(this@InboxActivity, ForecastActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this@InboxActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_about -> {

            }
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}
