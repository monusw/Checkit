package xin.monus.checkit.projects

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import xin.monus.checkit.R
import xin.monus.checkit.daily.DailyActivity
import xin.monus.checkit.forecast.ForecastActivity
import xin.monus.checkit.inbox.InboxActivity
import xin.monus.checkit.settings.SettingsActivity
import xin.monus.checkit.util.Injection
import xin.monus.checkit.util.replaceFragmentInActivity
import xin.monus.checkit.util.setupActionBar

class ProjectsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val drawerLayout by lazy { findViewById<DrawerLayout>(R.id.drawer_layout) }

    private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    private lateinit var projectsPresenter: ProjectsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects)

        // setup the tool bar
        setupActionBar(R.id.toolbar) {
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
            // add
            setTitle(R.string.nav_projects_title)
        }
        // Set up the navigation drawer.
        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.menu.getItem(1).isChecked = true
        navigationView.setNavigationItemSelectedListener(this)

        val projectsFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
                as ProjectsFragment? ?: ProjectsFragment.newInstance().also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }
        projectsPresenter = ProjectsPresenter(Injection.getProjectsRepository(this), projectsFragment)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        when (item.itemId) {
            R.id.nav_inbox -> {
                val intent = Intent(this@ProjectsActivity, InboxActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_projects -> {

            }
            R.id.nav_daily -> {
                val intent = Intent(this@ProjectsActivity, DailyActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_forecast -> {
                val intent = Intent(this@ProjectsActivity, ForecastActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this@ProjectsActivity, SettingsActivity::class.java)
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
