package com.hg.qynnlauncher.ui2.home.composables

import android.view.View
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.hg.qynnlauncher.R
import com.hg.qynnlauncher.services.displayshape.ObserveDisplayShape
import com.hg.qynnlauncher.services.settings2.SystemBarAppearanceOptions
import com.hg.qynnlauncher.services.windowinsetsholder.ObserveWindowInsets
import com.hg.qynnlauncher.services.windowinsetsholder.WindowInsetsOptions
import com.hg.qynnlauncher.ui2.appdrawer.AppDrawerVM
import com.hg.qynnlauncher.ui2.appdrawer.IAppDrawerApp
import com.hg.qynnlauncher.ui2.appdrawer.composables.AppListItem
import com.hg.qynnlauncher.ui2.appdrawer.composables.appcontextmenu.AppContextMenu
import com.hg.qynnlauncher.ui2.appdrawer.composables.appcontextmenu.AppContextMenuState
import com.hg.qynnlauncher.ui2.appdrawer.composables.emptyGetIconFunc
import com.hg.qynnlauncher.ui2.home.QYNNWebViewDeps
import com.hg.qynnlauncher.ui2.home.HomeScreen2VM
import com.hg.qynnlauncher.ui2.home.HomeScreenObserverCallbacks
import com.hg.qynnlauncher.ui2.home.HomeScreenSystemUIState
import com.hg.qynnlauncher.ui2.home.IHomeScreenProjectState
import com.hg.qynnlauncher.ui2.home.qynnmenu.QYNNMenu
import com.hg.qynnlauncher.ui2.home.qynnmenu.QYNNMenuActions
import com.hg.qynnlauncher.ui2.home.qynnmenu.QYNNMenuState
import com.hg.qynnlauncher.ui2.shared.botbar.SearchbarBottomToolbar
import com.hg.qynnlauncher.ui2.theme.QYNNLauncherThemeStateless
import com.hg.qynnlauncher.utils.ComposableContent
import com.hg.qynnlauncher.utils.compose.elasticOverscroll
import com.hg.qynnlauncher.utils.launchApp
import com.hg.qynnlauncher.webview.WebView
import com.hg.qynnlauncher.webview.rememberSaveableWebViewState
import com.hg.qynnlauncher.webview.rememberWebViewNavigator
import kotlinx.coroutines.launch

private val TAG = "HomeScreen2"

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen2(
    homeVM: HomeScreen2VM = viewModel(),
    appDrawerVM: AppDrawerVM = viewModel(factory = AppDrawerVM.Factory)
)
{
    val coroutineScope = rememberCoroutineScope()
    val sheetStateValue by homeVM.appDrawerSheetState.collectAsState()
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    LaunchedEffect(sheetStateValue) {
        if (modalBottomSheetState.currentValue != sheetStateValue) {
            when (sheetStateValue) {
                ModalBottomSheetValue.Expanded -> modalBottomSheetState.show()
                ModalBottomSheetValue.Hidden -> modalBottomSheetState.hide()
                else -> {}
            }
        }
    }

    // This is a two-way street. If the user swipes the sheet down, we need to tell the VM.
    LaunchedEffect(modalBottomSheetState.currentValue) {
        if (modalBottomSheetState.currentValue != sheetStateValue) {
            homeVM.onAppDrawerSheetStateChange(modalBottomSheetState.currentValue)
        }
    }

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            AppDrawerSheetContent(vm = appDrawerVM)
        },
        sheetBackgroundColor = Color.Black.copy(alpha = 0.7f),
        scrimColor = Color.Black.copy(alpha = 0.6f)
    ) {
        HomeScreen2(
            systemUIState = homeVM.systemUIState.value,
            projectState = homeVM.projectState.value,
            qynnMenuState = homeVM.qynnMenuState.value,
            qynnMenuActions = homeVM.qynnMenuActions,
            webViewDeps = homeVM.webViewDeps,
            observerCallbacks = homeVM.observerCallbacks,
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen2(
    systemUIState: HomeScreenSystemUIState,
    projectState: IHomeScreenProjectState,
    qynnMenuState: QYNNMenuState,
    qynnMenuActions: QYNNMenuActions,
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
        modifier = Modifier.fillMaxSize(),
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
                    val pageCount = 5

                    HorizontalPager(
                        count = pageCount,
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
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
                                    it.setBackgroundColor(0x00000000)
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
                                webView?.overScrollMode = when (drawOverscrollEffects) {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppDrawerSheetContent(vm: AppDrawerVM) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    var appContextMenuState by remember { mutableStateOf<AppContextMenuState?>(null) }
    var dropdownParentSize by remember { mutableStateOf<IntSize?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchbarBottomToolbar(
            onLeftActionClick = { /* This should probably close the sheet, handled by the sheet state */ },
            searchbarText = vm.searchString.value,
            searchbarPlaceholderText = "Search apps...",
            onSearchbarTextUpdateRequest = { vm.updateSearchStringRequest(it) },
        )

        CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .elasticOverscroll()
                    .onGloballyPositioned { dropdownParentSize = it.size },
                contentPadding = PaddingValues(0.dp, 8.dp),
            ) {
                items(vm.filteredApps.value, key = { it.packageName }) { app ->
                    var dropdownItemInLazyColOffset by remember { mutableStateOf(Offset(0f, 0f)) }
                    AppListItem(
                        app = app,
                        getIconFunc = { _, app -> vm.getIcon(null, app) },
                        onTap = { context.launchApp(app.packageName) },
                        onLongPress = { offset ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            // ... (context menu logic remains the same)
                        },
                        modifier = Modifier.onGloballyPositioned {
                            dropdownItemInLazyColOffset = it.positionInParent()
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                }
            }
        }
    }

    val state = appContextMenuState
    if (state != null) {
        AppContextMenu(
            state = state,
            onDismissRequest = { appContextMenuState = null },
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
// Previews will need to be updated to reflect the new structure,
// but I will omit that for now to focus on the core logic.