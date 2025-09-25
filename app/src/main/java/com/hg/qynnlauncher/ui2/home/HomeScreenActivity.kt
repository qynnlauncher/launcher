package com.hg.qynnlauncher.ui2.home

import android.app.UiModeManager
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.hg.qynnlauncher.services.gestures.GestureOverlayView
import com.hg.qynnlauncher.ui2.home.composables.HomeScreen2
import com.hg.qynnlauncher.ui2.theme.QYNNLauncherTheme

private val TAG = HomeScreenActivity::class.simpleName

class HomeScreenActivity : ComponentActivity()
{
    private lateinit var _modeman: UiModeManager

    private val _homeScreenVM: HomeScreen2VM by viewModels { HomeScreen2VM.Factory }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        // This must be called before super.onCreate() for edge-to-edge to work correctly.
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        // More robust edge-to-edge and gesture navigation setup
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            // Return CONSUMED to prevent the system from consuming the insets
            WindowInsetsCompat.CONSUMED
        }


        _modeman = getSystemService(UI_MODE_SERVICE) as UiModeManager
        _homeScreenVM.afterCreate(this)

        setContent {
            QYNNLauncherTheme {
                HomeScreen2(_homeScreenVM)
            }
        }

        // Add the gesture overlay to the window
        val decorView = window.decorView as ViewGroup
        val gestureOverlay = GestureOverlayView(this)
        decorView.addView(gestureOverlay)
    }

    override fun onConfigurationChanged(newConfig: Configuration)
    {
        super.onConfigurationChanged(newConfig)
        _homeScreenVM.onConfigurationChanged()
    }

    override fun onPause()
    {
        _homeScreenVM.beforePause()
        super.onPause()
    }

    override fun onNewIntent(intent: Intent)
    {
        super.onNewIntent(intent)
        _homeScreenVM.onNewIntent()
    }

    override fun onResume()
    {
        super.onResume()
        _homeScreenVM.afterResume()
    }

    override fun onDestroy()
    {
        _homeScreenVM.beforeDestroy()
        super.onDestroy()
    }
}