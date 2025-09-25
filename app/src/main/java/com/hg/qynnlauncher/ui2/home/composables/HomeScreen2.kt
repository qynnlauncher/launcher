package com.hg.qynnlauncher.ui2.home.composables

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.hg.qynnlauncher.services.displayshape.ObserveDisplayShape
import com.hg.qynnlauncher.services.settings2.SystemBarAppearanceOptions
import com.hg.qynnlauncher.services.windowinsetsholder.ObserveWindowInsets
import com.hg.qynnlauncher.services.windowinsetsholder.WindowInsetsOptions
import com.hg.qynnlauncher.ui2.home.QYNNWebViewDeps
import com.hg.qynnlauncher.ui2.home.HomeScreen2VM
import com.hg.qynnlauncher.ui2.home.HomeScreenObserverCallbacks
import com.hg.qynnlauncher.ui2.home.HomeScreenSystemUIState
import com.hg.qynnlauncher.ui2.home.IHomeScreenProjectState
import com.hg.qynnlauncher.ui2.home.qynnmenu.QYNNMenu
import com.hg.qynnlauncher.ui2.home.qynnmenu.QYNNMenuActions
import com.hg.qynnlauncher.ui2.home.qynnmenu.QYNNMenuState
import com.hg.qynnlauncher.ui2.theme.QYNNLauncherThemeStateless
import com.hg.qynnlauncher.utils.ComposableContent
import com.hg.qynnlauncher.webview.WebView
import com.hg.qynnlauncher.webview.rememberSaveableWebViewState
import com.hg.qynnlauncher.webview.rememberWebViewNavigator

private val TAG = "HomeScreen2"

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen2(vm: HomeScreen2VM = viewModel())
{
    HomeScreen2(
        vm.systemUIState.value,
        vm.projectState.value,
        vm.qynnMenuState.value,
        vm.qynnMenuActions,
        vm.webViewDeps,
        vm.observerCallbacks,
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen2(
    systemUIState: HomeScreenSystemUIState,
    projectState: IHomeScreenProjectState,
    qynnMenuState: QYNNMenuState,
    qynnMenuActions: QYNNMenuActions,
    // when null, show a placeholder instead of the WebView
    webViewDeps: QYNNWebViewDeps? = null,
    observerCallbacks: HomeScreenObserverCallbacks,
)
{
    SetHomeScreenSystemUIState(systemUIState)

    ObserveWindowInsets(
        options = WindowInsetsOptions.entries,
        onWindowInsetsChanged = observerCallbacks.onWindowInsetsChanged,
    )

    ObserveDisplayShape(
        observerCallbacks.onDisplayShapePathChanged,
        observerCallbacks.onCutoutPathChanged,
    )

    Box(
        modifier = Modifier
            .fillMaxSize(),
    )
    {
        when (projectState)
        {
            is IHomeScreenProjectState.FirstTimeLaunch -> PromptContainer { HomeScreenWelcomePrompt() }
            is IHomeScreenProjectState.NoStoragePerm -> PromptContainer { HomeScreenNoStoragePermsPrompt() }
            is IHomeScreenProjectState.NoProjectLoaded -> PromptContainer { HomeScreenNoProjectPrompt() }
            is IHomeScreenProjectState.Initializing -> PromptContainer { HomeScreenLoadingMessage() }
            is IHomeScreenProjectState.ProjectLoaded ->
            {
                if (webViewDeps == null) {
                    WebViewPlaceholder()
                } else {
                    val pagerState = rememberPagerState()
                    // For now, we'll use a fixed number of pages for demonstration
                    val pageCount = 5

                    HorizontalPager(
                        count = pageCount,
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        // Each page is a WebView that now correctly uses the webViewDeps
                        val webViewState = rememberSaveableWebViewState()
                        val navigator = rememberWebViewNavigator()
                        var webView by remember { mutableStateOf<android.webkit.WebView?>(null) }

                        WebView(
                            state = webViewState,
                            navigator = navigator,
                            client = webViewDeps.webViewClient,
                            chromeClient = webViewDeps.chromeClient,
                            onCreated = {
                                webView = it
                                it.setBackgroundColor(0x00000000) // Transparent
                                it.clearCache(true)
                                webViewDeps.onCreated(it)
                            },
                            onDispose = {
                                webView = null
                                webViewDeps.onDispose
                            },
                            modifier = Modifier.fillMaxSize(),
                        )

                        val drawOverscrollEffects = webViewDeps.drawOverscrollEffects.value
                        LaunchedEffect(drawOverscrollEffects) {
                            webView?.overScrollMode = when (drawOverscrollEffects)
                            {
                                true -> View.OVER_SCROLL_IF_CONTENT_SCROLLS
                                false -> View.OVER_SCROLL_NEVER
                            }
                        }
                    }
                }
            }
        }

        QYNNMenu(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .systemBarsPadding()
                .padding(16.dp),
            state = qynnMenuState,
            actions = qynnMenuActions,
        )
    }
}

@Composable
fun PromptContainer(modifier: Modifier = Modifier, content: ComposableContent)
{
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    )
    {
        content()
    }
}

@Composable
fun WebViewPlaceholder(modifier: Modifier = Modifier)
{
    PromptContainer(modifier = modifier.background(Color.LightGray))
    {
        Text(text = "WebView")
    }
}


// PREVIEWS

@OptIn(ExperimentalPagerApi::class)
@Composable
@PreviewLightDark
fun HomeScreen2InitializingPreview()
{
    QYNNLauncherThemeStateless(useDarkTheme = true)
    {
        HomeScreen2(
            HomeScreenSystemUIState(
                statusBarAppearance = SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.Hide,
                drawSystemWallpaperBehindWebView = false,
            ),
            IHomeScreenProjectState.Initializing,
            QYNNMenuState(
                isShown = false,
                isExpanded = true,
                showAppDrawerButtonWhenCollapsed = false
            ),
            QYNNMenuActions.empty(),
            observerCallbacks = HomeScreenObserverCallbacks.empty(),
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
@PreviewLightDark
fun HomeScreen2NoStoragePermsPreview()
{
    QYNNLauncherThemeStateless()
    {
        HomeScreen2(
            HomeScreenSystemUIState(
                statusBarAppearance = SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.Hide,
                drawSystemWallpaperBehindWebView = false,
            ),
            IHomeScreenProjectState.NoStoragePerm,
            QYNNMenuState(
                isShown = false,
                isExpanded = true,
                showAppDrawerButtonWhenCollapsed = false
            ),
            QYNNMenuActions.empty(),
            observerCallbacks = HomeScreenObserverCallbacks.empty(),
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
@PreviewLightDark
fun HomeScreen2NoProjectPreviewNoMenu()
{
    QYNNLauncherThemeStateless()
    {
        HomeScreen2(
            HomeScreenSystemUIState(
                statusBarAppearance = SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.Hide,
                drawSystemWallpaperBehindWebView = false,
            ),
            IHomeScreenProjectState.NoProjectLoaded,
            QYNNMenuState(
                isShown = false,
                isExpanded = false,
                showAppDrawerButtonWhenCollapsed = false,
            ),
            QYNNMenuActions.empty(),
            observerCallbacks = HomeScreenObserverCallbacks.empty(),
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
@PreviewLightDark
fun HomeScreen2NoProjectPreviewMenuCollapsed()
{
    QYNNLauncherThemeStateless()
    {
        HomeScreen2(
            HomeScreenSystemUIState(
                statusBarAppearance = SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.Hide,
                drawSystemWallpaperBehindWebView = false,
            ),
            IHomeScreenProjectState.NoProjectLoaded,
            QYNNMenuState(
                isShown = true,
                isExpanded = false,
                showAppDrawerButtonWhenCollapsed = false,
            ),
            QYNNMenuActions.empty(),
            observerCallbacks = HomeScreenObserverCallbacks.empty(),
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
@PreviewLightDark
fun HomeScreen2NoProjectPreviewMenuCollapsedWithAppDrawerButton()
{
    QYNNLauncherThemeStateless()
    {
        HomeScreen2(
            HomeScreenSystemUIState(
                statusBarAppearance = SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.Hide,
                drawSystemWallpaperBehindWebView = false,
            ),
            IHomeScreenProjectState.NoProjectLoaded,
            QYNNMenuState(
                isShown = true,
                isExpanded = false,
                showAppDrawerButtonWhenCollapsed = true,
            ),
            QYNNMenuActions.empty(),
            observerCallbacks = HomeScreenObserverCallbacks.empty(),
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
@PreviewLightDark
fun HomeScreen2NoProjectPreviewMenuOpen()
{
    QYNNLauncherThemeStateless()
    {
        HomeScreen2(
            HomeScreenSystemUIState(
                statusBarAppearance = SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.Hide,
                drawSystemWallpaperBehindWebView = false,
            ),
            IHomeScreenProjectState.NoProjectLoaded,
            QYNNMenuState(
                isShown = true,
                isExpanded = true,
                showAppDrawerButtonWhenCollapsed = false,
            ),
            QYNNMenuActions.empty(),
            observerCallbacks = HomeScreenObserverCallbacks.empty(),
        )
    }
}