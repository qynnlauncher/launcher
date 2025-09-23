package com.hg.qynnlauncher.ui2.home.composables

import android.view.View
import android.webkit.WebView
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
    val webViewState = rememberSaveableWebViewState()
    val webViewNavigator = rememberWebViewNavigator()

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
                if (webViewDeps == null)
                {
                    WebViewPlaceholder()
                }
                else
                {
                    var webView by remember { mutableStateOf<WebView?>(null) }

                    WebView(
                        state = webViewState,
                        navigator = webViewNavigator,
                        client = webViewDeps.webViewClient,
                        chromeClient = webViewDeps.chromeClient,
                        onCreated = {
                            webView = it
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
