/*
 * Copyright (c) 2022 Ascendit Sp. z o. o.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/
 *
 */

package pl.ascendit.compressmygallery

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.anggrayudi.storage.SimpleStorageHelper
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.permissionx.guolindev.PermissionX
import pl.ascendit.compressmygallery.able.Navigable
import pl.ascendit.compressmygallery.data.app.AppDb
import pl.ascendit.compressmygallery.data.comp.CompDb
import pl.ascendit.compressmygallery.data.log.LogDb
import pl.ascendit.compressmygallery.fragment.*
import pl.ascendit.compressmygallery.helper.IntentHelper
import pl.ascendit.compressmygallery.logic.CompLogic
import pl.ascendit.compressmygallery.logic.SettingsLogic
import pl.ascendit.compressmygallery.logic.TopBarLogic
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), Navigable {
    private lateinit var statsFragment: StatsFragment
    private lateinit var compFragment: CompFragment
    private var aboutFragment: AboutFragment? = null
    private var introFragment: IntroFragment? = null
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var topBar: MaterialToolbar
    private val storageHelper = SimpleStorageHelper(this)
    private lateinit var tr: Tr

    private class Tr(c: Context) {
        val aboutTitle = c.getString(R.string.topbar_about_title)
        val settingsTitle = c.getString(R.string.topbar_settings_title)
        val logBrowserTitle = c.getString(R.string.topbar_log_browser_title)
        val compTitle = c.getString(R.string.topbar_comp_title)
        val statsTitle = c.getString(R.string.topbar_stats_title)
        val dirsTitle = c.getString(R.string.topbar_dirs_title)
        val ok = c.getString(R.string.ok)
        val cancel = c.getString(R.string.cancel)
        val requestReason = c.getString(R.string.perm_request_reason)
        val toSettings = c.getString(R.string.perm_to_settings)
        val permNotGiven = c.getString(R.string.perm_not_given)
        val permRequiredToRunApp = c.getString(R.string.perm_required_to_run_app)
        val appWillShutDown = c.getString(R.string.perm_app_will_shutdown)
        val usageUrl = c.getString(R.string.usage_url)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tr = Tr(baseContext)

        LogDb.init(baseContext)
        AppDb.init(baseContext)
        CompDb.init(baseContext)

        setContentView(R.layout.activity_main)

        SettingsLogic.loadPrefsToObjects(baseContext)

        bottomNav = findViewById(R.id.navigation);
        bottomNav.setSelectedItemId(R.id.navStats);
        bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.navComp -> navigate(Navigable.Destination.Comp)
                R.id.navStats -> navigate(Navigable.Destination.Stats)
                R.id.navDirs -> navigate(Navigable.Destination.Dirs)
            }
            true
        }

        topBar = findViewById(R.id.topAppBar)
        topBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.settings -> {
                    navigate(Navigable.Destination.Settings)
                    true
                }
                R.id.log_browser -> {
                    navigate(Navigable.Destination.LogBrowser)
                    true
                }
                R.id.usage -> {
                    IntentHelper.startBrowser(this, tr.usageUrl)
                    true
                }
                R.id.about -> {
                    navigate(Navigable.Destination.About)
                    true
                }
                else -> false
            }
        }
        topBar.setNavigationOnClickListener {
            goBack()
        }
        TopBarLogic.topBar = topBar

        compFragment = CompFragment()
        compFragment.storageHelper = storageHelper
        CompLogic.updatable = compFragment
        statsFragment = StatsFragment()

        if ( SettingsLogic.isFirstRun() ) {
            navigate(Navigable.Destination.Intro)
        } else {
            navigate(Navigable.Destination.Stats)
        }
    }

    // Permissions
    fun askForPermissions() {
        val permissionList = mutableListOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_MEDIA_LOCATION)
        if (Build.VERSION.SDK_INT >= 30) {
            permissionList.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        } else {
            storageHelper.requestStorageAccess()
        }
        PermissionX.init(this)
            .permissions(permissionList)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, tr.requestReason, tr.ok, tr.cancel)
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, tr.toSettings, tr.ok, tr.cancel)
            }
            .request { allGranted, grantedList, deniedList ->
                if ( ! deniedList.isEmpty() ) {
                    Toast.makeText(this, "${tr.permNotGiven} $deniedList, ${tr.permRequiredToRunApp}", Toast.LENGTH_LONG).show()
                    thread {
                        Thread.sleep(1000*10)
                        runOnUiThread {
                            Toast.makeText(this, tr.appWillShutDown, Toast.LENGTH_LONG).show()
                        }
                        Thread.sleep(1000*10)
                        finish()
                    }
                }
            }
    }

    // Navigation
    var level = 0
    var isIntro: Boolean = false
    var back: Navigable.Destination = Navigable.Destination.Stats

    fun changeLevel(level: Int) {
        this.level = level
        if ( level == 0 ) {
            bottomNav.visibility = View.VISIBLE
            TopBarLogic.hasBackButton(false)
        } else if ( level == 1 ) {
            bottomNav.visibility = View.GONE
            TopBarLogic.hasBackButton(true)
        }
    }

    override fun navigate(to: Navigable.Destination) {
        when (to) {
            Navigable.Destination.Intro -> {
                isIntro = true
                topBar.visibility = View.GONE
                bottomNav.visibility = View.GONE
                introFragment = IntroFragment()
                introFragment!!.onIntroFinished = {
                    SettingsLogic.setFirstRun(false)
                    isIntro = false
                    askForPermissions()
                    navigate(Navigable.Destination.Stats)
                    topBar.visibility = View.VISIBLE
                    bottomNav.visibility = View.VISIBLE
                }
                goToFragment(introFragment!!, IntroFragment::class.java.name)
                level = 0
            }
            Navigable.Destination.About -> {
                if ( aboutFragment == null ) {
                    aboutFragment = AboutFragment()
                }
                changeLevel(1)
                goToFragment(aboutFragment!!, AboutFragment::class.java.name)
                topBar.title = tr.aboutTitle
            }
            Navigable.Destination.Settings -> {
                changeLevel(1)
                goToFragment(SettingsFragment(), SettingsFragment::class.java.name)
                topBar.title = tr.settingsTitle
            }
            Navigable.Destination.LogBrowser -> {
                changeLevel(1)
                goToFragment(LogBrowserFragment(), LogBrowserFragment::class.java.name)
                topBar.title = tr.logBrowserTitle
            }
            Navigable.Destination.Comp -> {
                changeLevel(0)
                goToFragment(compFragment, CompFragment::class.java.name)
                topBar.title = tr.compTitle
                back = Navigable.Destination.Comp
            }
            Navigable.Destination.Stats -> {
                changeLevel(0)
                goToFragment(statsFragment, StatsFragment::class.java.name)
                topBar.title = tr.statsTitle
                back = Navigable.Destination.Stats
            }
            Navigable.Destination.Dirs -> {
                changeLevel(0)
                goToFragment(DirsFragment(), DirsFragment::class.java.name)
                topBar.title = tr.dirsTitle
                back = Navigable.Destination.Dirs
            }
        }
    }

    fun goToFragment(fragment: Fragment, tag : String) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment, tag)
        }.commit()
    }

    fun goBack() {
        if ( level > 0 ) {
            navigate(back)
            bottomNav.visibility = View.VISIBLE
        } else if ( isIntro && introFragment != null ) {
            if ( ! introFragment!!.previous() ) {
                finish()
            }
        } else {
            moveTaskToBack(false)
        }
    }

    override fun onBackPressed() {
        goBack()
    }
}
