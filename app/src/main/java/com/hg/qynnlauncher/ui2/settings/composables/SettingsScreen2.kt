package com.hg.qynnlauncher.ui2.settings.composables

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hg.qynnlauncher.R
import com.hg.qynnlauncher.services.mockexport.MockExportProgressState
import com.hg.qynnlauncher.services.settings2.QYNNThemeOptions
import com.hg.qynnlauncher.services.settings2.SystemBarAppearanceOptions
import com.hg.qynnlauncher.ui2.dirpicker.DirectoryPickerActions
import com.hg.qynnlauncher.ui2.dirpicker.DirectoryPickerDialog
import com.hg.qynnlauncher.ui2.dirpicker.DirectoryPickerState
import com.hg.qynnlauncher.ui2.progressdialog.MockExportProgressDialog
import com.hg.qynnlauncher.ui2.progressdialog.MockExportProgressDialogActions
import com.hg.qynnlauncher.ui2.settings.SettingsScreen2MiscActions
import com.hg.qynnlauncher.ui2.settings.SettingsScreenVM
import com.hg.qynnlauncher.ui2.settings.sections.about.SettingsScreen2AboutSectionContent
import com.hg.qynnlauncher.ui2.settings.sections.gestures.SettingsScreen2GesturesSectionContent
import com.hg.qynnlauncher.ui2.settings.sections.qynn.SettingsScreen2QYNNSectionActions
import com.hg.qynnlauncher.ui2.settings.sections.qynn.SettingsScreen2QYNNSectionContent
import com.hg.qynnlauncher.ui2.settings.sections.qynn.SettingsScreen2QYNNSectionState
import com.hg.qynnlauncher.ui2.settings.sections.development.SettingsScreen2DevelopmentSectionActions
import com.hg.qynnlauncher.ui2.settings.sections.development.SettingsScreen2DevelopmentSectionContent
import com.hg.qynnlauncher.ui2.settings.sections.development.SettingsScreen2DevelopmentSectionState
import com.hg.qynnlauncher.ui2.settings.sections.overlays.SettingsScreen2OverlaysSectionActions
import com.hg.qynnlauncher.ui2.settings.sections.overlays.SettingsScreen2OverlaysSectionContent
import com.hg.qynnlauncher.ui2.settings.sections.overlays.SettingsScreen2OverlaysSectionState
import com.hg.qynnlauncher.ui2.settings.sections.project.ScreenLockingMethodOptions
import com.hg.qynnlauncher.ui2.settings.sections.project.SettingsScreen2ProjectSectionActions
import com.hg.qynnlauncher.ui2.settings.sections.project.SettingsScreen2ProjectSectionContent
import com.hg.qynnlauncher.ui2.settings.sections.project.SettingsScreen2ProjectSectionState
import com.hg.qynnlauncher.ui2.settings.sections.project.SettingsScreen2ProjectSectionStateProjectInfo
import com.hg.qynnlauncher.ui2.settings.sections.reset.SettingsScreen2ResetSectionActions
import com.hg.qynnlauncher.ui2.settings.sections.reset.SettingsScreen2ResetSectionContent
import com.hg.qynnlauncher.ui2.settings.sections.reset.SettingsScreen2ResetSectionState
import com.hg.qynnlauncher.ui2.settings.sections.wallpaper.SettingsScreen2WallpaperSectionActions
import com.hg.qynnlauncher.ui2.settings.sections.wallpaper.SettingsScreen2WallpaperSectionContent
import com.hg.qynnlauncher.ui2.settings.sections.wallpaper.SettingsScreen2WallpaperSectionState
import com.hg.qynnlauncher.ui2.shared.BotBarScreen
import com.hg.qynnlauncher.ui2.theme.QYNNLauncherThemeStateless
import com.hg.qynnlauncher.utils.CurrentAndroidVersion
import com.hg.qynnlauncher.utils.UseEdgeToEdgeWithTransparentBars
import com.hg.qynnlauncher.utils.tryStartExtStorageManagerPermissionActivity

@Composable
fun SettingsScreen2(vm: SettingsScreenVM = viewModel(), requestFinish: () -> Unit)
{
    SettingsScreen2(
        projectSectionState = vm.projectSectionState.value,
        projectSectionActions = vm.projectSectionActions,

        wallpaperSectionState = vm.wallpaperSectionState.value,
        wallpaperSectionActions = vm.wallpaperSectionActions,

        overlaysSectionState = vm.overlaysSectionState.value,
        overlaysSectionActions = vm.overlaysSectionActions,

        qynnSectionState = vm.qynnSectionState.value,
        qynnSectionActions = vm.qynnSectionActions,

        developmentSectionState = vm.developmentSectionState.value,
        developmentSectionActions = vm.developmentSectionActions,

        directoryPickerState = vm.directoryPickerState.value,
        directoryPickerActions = vm.directoryPickerActions,

        mockExportProgressState = vm.mockExportProgressState.value,
        mockExportProgressDialogActions = vm.mockExportProgressDialogActions,

        resetSectionState = vm.resetSectionState.value,
        resetSectionActions = vm.resetSectionActions,

        miscActions = vm.miscActions,
        requestFinish = requestFinish,
    )
}

@Composable
fun SettingsScreen2(
    projectSectionState: SettingsScreen2ProjectSectionState,
    projectSectionActions: SettingsScreen2ProjectSectionActions,

    wallpaperSectionState: SettingsScreen2WallpaperSectionState,
    wallpaperSectionActions: SettingsScreen2WallpaperSectionActions,

    overlaysSectionState: SettingsScreen2OverlaysSectionState,
    overlaysSectionActions: SettingsScreen2OverlaysSectionActions,

    qynnSectionState: SettingsScreen2QYNNSectionState,
    qynnSectionActions: SettingsScreen2QYNNSectionActions,

    developmentSectionState: SettingsScreen2DevelopmentSectionState,
    developmentSectionActions: SettingsScreen2DevelopmentSectionActions,

    directoryPickerState: DirectoryPickerState?,
    directoryPickerActions: DirectoryPickerActions,

    mockExportProgressState: MockExportProgressState?,
    mockExportProgressDialogActions: MockExportProgressDialogActions,

    resetSectionState: SettingsScreen2ResetSectionState,
    resetSectionActions: SettingsScreen2ResetSectionActions,

    miscActions: SettingsScreen2MiscActions,
    requestFinish: () -> Unit,
)
{
    val permsLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { areGranted ->
        miscActions.permissionsChanged(areGranted)
    }

    val context = LocalContext.current

    fun requestStoragePermission()
    {
        if (CurrentAndroidVersion.supportsScopedStorage())
        {
            context.tryStartExtStorageManagerPermissionActivity()
        }
        else
        {
            permsLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            )
        }
    }

    UseEdgeToEdgeWithTransparentBars()

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize(),
    )
    {
        BotBarScreen(
            onLeftActionClick = { requestFinish() },
            titleAreaContent = {
                Text(text = "Settings")
            }
        )
        {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .safeContentPadding()
                    .padding(0.dp, 8.dp)
            )
            {
                SettingsScreen2Section(label = "Project", iconResId = R.drawable.ic_folder_open) {
                    SettingsScreen2ProjectSectionContent(
                        state = projectSectionState,
                        actions = projectSectionActions,
                        requestStoragePermission = ::requestStoragePermission,
                    )
                }

                Divider()

                SettingsScreen2Section(label = "System wallpaper", iconResId = R.drawable.ic_image) {
                    SettingsScreen2WallpaperSectionContent(
                        state = wallpaperSectionState,
                        actions = wallpaperSectionActions,
                    )
                }

                Divider()

                SettingsScreen2Section(label = "Overlays", iconResId = R.drawable.ic_overlays) {
                    SettingsScreen2OverlaysSectionContent(
                        state = overlaysSectionState,
                        actions = overlaysSectionActions,
                    )
                }

                Divider()

                SettingsScreen2Section(label = "QYNN", iconResId = R.drawable.ic_qynn) {
                    SettingsScreen2QYNNSectionContent(
                        state = qynnSectionState,
                        actions = qynnSectionActions,
                    )
                }

                Divider()

                SettingsScreen2Section(label = "Gestures", iconResId = R.drawable.ic_qynn) {
                    SettingsScreen2GesturesSectionContent()
                }

                Divider()

                SettingsScreen2Section(label = "Development", iconResId = R.drawable.ic_tools) {
                    SettingsScreen2DevelopmentSectionContent(
                        state = developmentSectionState,
                        actions = developmentSectionActions,
                    )
                }

                Divider()

                SettingsScreen2Section(label = "About QYNN Launcher", iconResId = R.drawable.ic_about) {
                    SettingsScreen2AboutSectionContent()
                }

                Divider()

                SettingsScreen2Section(label = "Reset settings", iconResId = R.drawable.ic_clear_all) {
                    SettingsScreen2ResetSectionContent(
                        state = resetSectionState,
                        actions = resetSectionActions,
                    )
                }
            }
        }
    }

    if (directoryPickerState != null)
    {
        DirectoryPickerDialog(
            state = directoryPickerState,
            actions = directoryPickerActions,
            requestStoragePermission = ::requestStoragePermission,
        )
    }

    if (mockExportProgressState != null)
    {
        MockExportProgressDialog(
            state = mockExportProgressState,
            actions = mockExportProgressDialogActions,
        )
    }
}


// PREVIEWS

@Composable
@PreviewLightDark
fun SettingsScreen2Preview01()
{
    QYNNLauncherThemeStateless {
        SettingsScreen2(
            projectSectionState = SettingsScreen2ProjectSectionState(
                projectInfo = SettingsScreen2ProjectSectionStateProjectInfo("LOL"),
                hasStoragePerms = true,
                allowProjectsToTurnScreenOff = true,
                screenLockingMethod = ScreenLockingMethodOptions.DeviceAdmin,
                canQYNNTurnScreenOff = true,
            ),
            projectSectionActions = SettingsScreen2ProjectSectionActions.empty(),

            wallpaperSectionState = SettingsScreen2WallpaperSectionState(
                drawSystemWallpaperBehindWebView = true,
            ),
            wallpaperSectionActions = SettingsScreen2WallpaperSectionActions.empty(),

            overlaysSectionState = SettingsScreen2OverlaysSectionState(
                statusBarAppearance = SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.LightIcons,
                drawWebViewOverscrollEffects = true,
            ),
            overlaysSectionActions = SettingsScreen2OverlaysSectionActions.empty(),

            qynnSectionState = SettingsScreen2QYNNSectionState(
                theme = QYNNThemeOptions.System,
                isQSTilePromptSupported = true,
                isQSTileAdded = false,
                showQYNNButton = true,
                showLaunchAppsWhenQYNNButtonCollapsed = false,
            ),
            qynnSectionActions = SettingsScreen2QYNNSectionActions.empty(),

            developmentSectionState = SettingsScreen2DevelopmentSectionState(
                isExportDisabled = false,
            ),
            developmentSectionActions = SettingsScreen2DevelopmentSectionActions.empty(),

            directoryPickerState = null,
            directoryPickerActions = DirectoryPickerActions.empty(),

            mockExportProgressState = null,
            mockExportProgressDialogActions = MockExportProgressDialogActions.empty(),

            resetSectionState = SettingsScreen2ResetSectionState(false),
            resetSectionActions = SettingsScreen2ResetSectionActions.empty(),

            miscActions = SettingsScreen2MiscActions.empty(),
            requestFinish = {},
        )
    }
}