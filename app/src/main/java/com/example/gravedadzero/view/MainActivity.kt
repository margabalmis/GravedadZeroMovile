package com.example.gravedadzero.view



import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.gravedadzero.R
import com.example.gravedadzero.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var binding: ActivityMainBinding
    lateinit var drawer_layout: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var toolbar: MaterialToolbar
    lateinit var navView: NavigationView
    lateinit var navController: NavController
    var message: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        message = intent.getStringExtra(EXTRA_MESSAGE)

        if(message == "AddFragment"){
            navegar(AddBulderFragment())
        }

        drawer_layout=binding.drawerLayout
        toolbar=binding.topAppBar
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this, drawer_layout, binding.topAppBar,
            R.string.navigation_open, R.string.navigation_close)

        drawer_layout.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        navView = findViewById(R.id.navigation_view)
        navController = findNavController(R.id.nav_host_fragment)


        if(message == "anonimo"){
            val mMenu = navView.menu
            val itemAreaPersonal = mMenu.findItem(R.id.areaPersonalMenu)
            itemAreaPersonal.isVisible = false

            val itemMisProyectos = mMenu.findItem(R.id.misProyectosMenu)
            itemMisProyectos.isVisible = false
        }


        navView.setNavigationItemSelectedListener(this)

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.bloquesMenu-> navegar(BloquesFragment())
            R.id.misProyectosMenu-> navegar(MisProyectosFragment())
            R.id.noticiasMenu-> navegar(NoticiasFragment())
            R.id.areaPersonalMenu-> navegar(AreaPersonalFragment())
            R.id.cerrarSesion-> SingOut()

        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun SingOut() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun navegar(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
