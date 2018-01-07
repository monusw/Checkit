package xin.monus.checkit.daily

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import xin.monus.checkit.R
import xin.monus.checkit.forecast.ForecastActivity
import xin.monus.checkit.inbox.InboxActivity
import xin.monus.checkit.projects.ProjectsActivity
import xin.monus.checkit.settings.SettingsActivity
import xin.monus.checkit.util.replaceFragmentInActivity
import xin.monus.checkit.util.setupActionBar

class DailyActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val drawerLayout by lazy { findViewById(R.id.drawer_layout) as DrawerLayout }

    private val toolbar by lazy { findViewById(R.id.toolbar) as Toolbar }

    private lateinit var dailyPresenter: DailyContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily)

        // setup the tool bar
        setupActionBar(R.id.toolbar) {
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
            // add
            setTitle(R.string.nav_daily_title)
        }
        // Set up the navigation drawer.
        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        // TODO: Change the index in different activity
        navigationView.menu.getItem(2).isChecked = true
        navigationView.setNavigationItemSelectedListener(this)

        val dailyFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
                as DailyFragment? ?: DailyFragment.newInstance().also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }
        dailyPresenter = DailyPresenter(dailyFragment)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        when (id) {
            R.id.nav_inbox -> {
                val intent = Intent(this@DailyActivity, InboxActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_projects -> {
                val intent = Intent(this@DailyActivity, ProjectsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_daily -> {
            }
            R.id.nav_forecast -> {
                val intent = Intent(this@DailyActivity, ForecastActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this@DailyActivity, SettingsActivity::class.java)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // TODO: change
//        menuInflater.inflate(R.menu.inbox, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

}
