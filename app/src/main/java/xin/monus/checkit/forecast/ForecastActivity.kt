package xin.monus.checkit.forecast

import android.content.Intent
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import xin.monus.checkit.R
import xin.monus.checkit.daily.DailyActivity
import xin.monus.checkit.inbox.InboxActivity
import xin.monus.checkit.projects.ProjectsActivity
import xin.monus.checkit.settings.SettingsActivity
import xin.monus.checkit.util.Injection
import xin.monus.checkit.util.replaceFragmentInActivity
import xin.monus.checkit.util.setupActionBar

class ForecastActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val drawerLayout by lazy { findViewById<DrawerLayout>(R.id.drawer_layout) }

    private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    private lateinit var forecastPresenter : ForecastContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily)

        // setup the tool bar
        setupActionBar(R.id.toolbar) {
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
            // add
            setTitle(R.string.nav_forecast_title)
        }
        // Set up the navigation drawer.
        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.menu.getItem(3).isChecked = true
        navigationView.setNavigationItemSelectedListener(this)

        val forecastFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
                as ForecastFragment? ?: ForecastFragment.newInstance().also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }
        forecastPresenter = ForecastPresenter(Injection.getForecastRepository(this), forecastFragment)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_inbox -> {
                val intent = Intent(this@ForecastActivity, InboxActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_projects -> {
                val intent = Intent(this@ForecastActivity, ProjectsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_daily -> {
                val intent = Intent(this@ForecastActivity, DailyActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_forecast -> {

            }
            R.id.nav_settings -> {
                val intent = Intent(this@ForecastActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
//            R.id.nav_about -> {
//
//            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
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
