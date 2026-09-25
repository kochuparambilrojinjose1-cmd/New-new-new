package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppReleaseInfo
import com.example.model.ReleaseChannel
import com.example.model.UpdateUiState
import com.example.ui.theme.AmberConcert
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DenseBackgroundDark
import com.example.ui.theme.DenseBorderDark
import com.example.ui.theme.DenseSurfaceDark
import com.example.ui.theme.DenseSurfaceElevatedDark
import com.example.ui.theme.DenseSurfaceHighlightDark
import com.example.ui.theme.DenseTextMutedDark
import com.example.ui.theme.DenseTextPrimaryDark
import com.example.ui.theme.DenseTextSecondaryDark
import com.example.ui.theme.LaserGreen
import com.example.util.AppUpdateManager
import com.example.viewmodel.AvlViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AppUpdateDialog(
    viewModel: AvlViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val updateState by viewModel.updateUiState.collectAsStateWithLifecycle()
    val currentChannel by viewModel.selectedReleaseChannel.collectAsStateWithLifecycle()

    AlertDialog(
        onDismissRequest = {
            if (updateState !is UpdateUiState.Downloading) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = updateState !is UpdateUiState.Downloading,
            dismissOnClickOutside = updateState !is UpdateUiState.Downloading,
            usePlatformDefaultWidth = false
        ),
        modifier = modifier
            .fillMaxWidth(0.95f)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, CyanNeon.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
            .testTag("dialog_app_update"),
        containerColor = DenseBackgroundDark,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF00384D))
                            .border(1.dp, CyanNeon, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.RocketLaunch,
                            contentDescription = "App Updates",
                            tint = CyanNeon,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AVL OPS UPGRADE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "In-App OTA Software Engine",
                            fontSize = 10.sp,
                            color = CyanNeon
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = DenseTextSecondaryDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Channel Selection Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ReleaseChannel.entries.forEach { ch ->
                        FilterChip(
                            selected = currentChannel == ch,
                            onClick = {
                                viewModel.setReleaseChannel(ch)
                                viewModel.checkForAppUpdate()
                            },
                            label = { Text(ch.displayName.split(" ").first(), fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(ch.badgeColorHex).copy(alpha = 0.2f),
                                selectedLabelColor = Color(ch.badgeColorHex),
                                containerColor = DenseSurfaceDark,
                                labelColor = DenseTextSecondaryDark
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = currentChannel == ch,
                                borderColor = if (currentChannel == ch) Color(ch.badgeColorHex) else DenseBorderDark
                            ),
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                when (val state = updateState) {
                    is UpdateUiState.Idle -> {
                        IdleUpdateCard(
                            versionName = viewModel.currentAppVersionName,
                            versionCode = viewModel.currentAppVersionCode,
                            onCheckNow = { viewModel.checkForAppUpdate() }
                        )
                    }

                    is UpdateUiState.Checking -> {
                        CheckingUpdateCard(channel = state.channel)
                    }

                    is UpdateUiState.UpToDate -> {
                        UpToDateCard(
                            versionName = state.currentVersionName,
                            versionCode = state.currentVersionCode,
                            timestamp = state.lastCheckedTimestamp,
                            onCheckAgain = { viewModel.checkForAppUpdate() },
                            onSimulateNewVersion = {
                                viewModel.checkForAppUpdate(forceSimulate = true)
                            }
                        )
                    }

                    is UpdateUiState.UpdateAvailable -> {
                        UpdateAvailableCard(
                            release = state.release,
                            currentVersion = state.currentVersionName,
                            onApplyInApp = {
                                viewModel.applyInAppUpdate(state.release)
                            },
                            onStartDownload = {
                                viewModel.downloadUpdate(context, state.release)
                            },
                            onOpenBrowser = {
                                viewModel.openDownloadUrlInBrowser(context, state.release.downloadUrl)
                            }
                        )
                    }

                    is UpdateUiState.InAppApplying -> {
                        InAppApplyingCard(
                            release = state.release,
                            stepMessage = state.stepMessage,
                            progress = state.progressPercent
                        )
                    }

                    is UpdateUiState.InAppUpdateSuccess -> {
                        InAppUpdateSuccessCard(
                            release = state.release,
                            versionName = state.updatedVersionName,
                            versionCode = state.updatedVersionCode,
                            onDone = {
                                viewModel.dismissUpdateDialog()
                            }
                        )
                    }

                    is UpdateUiState.Downloading -> {
                        DownloadingCard(
                            release = state.release,
                            progress = state.progressPercent,
                            downloadedBytes = state.downloadedBytes,
                            totalBytes = state.totalBytes
                        )
                    }

                    is UpdateUiState.ReadyToInstall -> {
                        ReadyToInstallCard(
                            release = state.release,
                            onInstall = {
                                val result = viewModel.installApk(context, state.apkFile)
                                when (result) {
                                    AppUpdateManager.InstallResult.Success -> {
                                        Toast.makeText(context, "Launching Android Package Installer...", Toast.LENGTH_SHORT).show()
                                    }
                                    AppUpdateManager.InstallResult.PermissionRequested -> {
                                        Toast.makeText(context, "Please allow 'Install Unknown Apps' for AVL Ops, then tap Install again.", Toast.LENGTH_LONG).show()
                                    }
                                    is AppUpdateManager.InstallResult.Error -> {
                                        Toast.makeText(context, "Install failed: ${result.message}", Toast.LENGTH_SHORT).show()
                                    }
                                    AppUpdateManager.InstallResult.FileNotFound -> {
                                        Toast.makeText(context, "APK file not found.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onOpenBrowser = {
                                viewModel.openDownloadUrlInBrowser(context, state.release.downloadUrl)
                            }
                        )
                    }

                    is UpdateUiState.Error -> {
                        ErrorCard(
                            message = state.message,
                            onRetry = { viewModel.checkForAppUpdate() }
                        )
                    }
                }
            }
        },
        confirmButton = {
            // Contextual primary button handled in state cards
        },
        dismissButton = null
    )
}

@Composable
private fun IdleUpdateCard(
    versionName: String,
    versionCode: Int,
    onCheckNow: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Installed: v$versionName (Build $versionCode)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("Check online feeds for new features, bug fixes, and arena optimizations.", color = DenseTextSecondaryDark, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 6.dp))
            Button(
                onClick = onCheckNow,
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = DenseBackgroundDark),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth().testTag("btn_check_for_updates_now")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Check for Updates Now", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun CheckingUpdateCard(channel: ReleaseChannel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = CyanNeon,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Scanning ${channel.displayName}...",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = "Connecting to release server and verifying package integrity...",
                color = DenseTextSecondaryDark,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun UpToDateCard(
    versionName: String,
    versionCode: Int,
    timestamp: Long,
    onCheckAgain: () -> Unit,
    onSimulateNewVersion: () -> Unit
) {
    val dateStr = remember(timestamp) {
        SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.US).format(Date(timestamp))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, LaserGreen.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0D331A))
                    .border(1.5.dp, LaserGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = LaserGreen, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "You're All Up to Date!",
                color = LaserGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "AVL Ops v$versionName (Build $versionCode) is the latest release.",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = "Last checked: $dateStr",
                color = DenseTextMutedDark,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )

            // Current active feature indicators
            Surface(
                color = DenseSurfaceElevatedDark,
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, DenseBorderDark),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("ACTIVE ENGINE CAPABILITIES:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = LaserGreen)
                    Text("• 📦 Equipment Loadout Packages & Truck Manifests", fontSize = 9.sp, color = DenseTextPrimaryDark)
                    Text("• ✅ 8-Phase Safety & Stage Production Checklists", fontSize = 9.sp, color = DenseTextPrimaryDark)
                    Text("• 🏭 Master Warehouse Stock Balances & Asset Catalog", fontSize = 9.sp, color = DenseTextPrimaryDark)
                    Text("• 🔄 Immutable Movement & Adjustment Audit Ledger", fontSize = 9.sp, color = DenseTextPrimaryDark)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCheckAgain,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanNeon),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
                    modifier = Modifier.weight(1f).height(36.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Check Feed", fontSize = 11.sp)
                }

                Button(
                    onClick = onSimulateNewVersion,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberConcert, contentColor = DenseBackgroundDark),
                    modifier = Modifier.weight(1f).height(36.dp).testTag("btn_test_upgrade_flow")
                ) {
                    Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sync / Re-Apply", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun UpdateAvailableCard(
    release: AppReleaseInfo,
    currentVersion: String,
    onApplyInApp: () -> Unit,
    onStartDownload: () -> Unit,
    onOpenBrowser: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DenseSurfaceHighlightDark),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AmberConcert),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF4A3405))
                        .border(1.dp, AmberConcert, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "NEW VERSION AVAILABLE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberConcert
                    )
                }

                Text(
                    text = "${release.fileSizeFormatted} • ${release.releaseDate}",
                    fontSize = 10.sp,
                    color = DenseTextSecondaryDark
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = release.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White
            )
            Text(
                text = "Current: v$currentVersion ➔ New: v${release.versionName} (Build ${release.versionCode})",
                fontSize = 10.sp,
                color = CyanNeon,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "What's New in this Upgrade:",
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = DenseTextPrimaryDark
            )

            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = DenseSurfaceDark,
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, DenseBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    release.releaseNotes.forEach { note ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("•", color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(note, fontSize = 10.sp, color = DenseTextPrimaryDark, lineHeight = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: In-App Update (Primary) & APK Download (Secondary)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // PRIMARY ACTION: Update directly in app
                Button(
                    onClick = onApplyInApp,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = DenseBackgroundDark),
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("btn_apply_update_in_app")
                ) {
                    Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("⚡ APPLY UPDATE IN-APP NOW", fontSize = 12.sp, fontWeight = FontWeight.Black)
                }

                Text(
                    text = "Applies v${release.versionName} Room database schemas & features directly in the app without APK file reinstall.",
                    fontSize = 9.sp,
                    color = DenseTextSecondaryDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp)
                )

                // SECONDARY ACTION: Optional native APK download
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onStartDownload,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberConcert),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberConcert.copy(alpha = 0.6f)),
                        modifier = Modifier.weight(1f).height(34.dp).testTag("btn_download_and_install_upgrade")
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Install APK File (Optional)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onOpenBrowser,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DenseTextPrimaryDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
                        modifier = Modifier.height(34.dp).testTag("btn_browser_download_fallback")
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = "Browser APK", modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InAppApplyingCard(
    release: AppReleaseInfo,
    stepMessage: String,
    progress: Int
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = CyanNeon,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "UPGRADING IN-APP: v${release.versionName}",
                color = CyanNeon,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            )
            Text(
                text = stepMessage,
                color = Color.White,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
            )
            LinearProgressIndicator(
                progress = { progress / 100f },
                color = CyanNeon,
                trackColor = DenseBorderDark,
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
            )
            Text(
                text = "$progress% complete • Applying without APK re-install",
                color = DenseTextMutedDark,
                fontSize = 9.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
private fun InAppUpdateSuccessCard(
    release: AppReleaseInfo,
    versionName: String,
    versionCode: Int,
    onDone: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, LaserGreen),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0D331A))
                    .border(2.dp, LaserGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = LaserGreen, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "🎉 In-App Update Complete!",
                color = LaserGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = "AVL Ops is now running v$versionName (Build $versionCode)",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = DenseSurfaceElevatedDark,
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, DenseBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("ACTIVE FEATURES NOW RUNNING:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AmberConcert)
                    Text("• 📦 Room Database Equipment Loadouts & Manifests", fontSize = 9.sp, color = DenseTextPrimaryDark)
                    Text("• ✅ 8-Phase Safety & Stage Production Checklists", fontSize = 9.sp, color = DenseTextPrimaryDark)
                    Text("• 🏭 Master Warehouse Stock & Real-time Quantities", fontSize = 9.sp, color = DenseTextPrimaryDark)
                    Text("• 🔄 Immutable Movement & Adjustment Audit Ledger", fontSize = 9.sp, color = DenseTextPrimaryDark)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onDone,
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LaserGreen, contentColor = DenseBackgroundDark),
                modifier = Modifier.fillMaxWidth().height(38.dp).testTag("btn_done_in_app_update")
            ) {
                Text("Done & Continue", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DownloadingCard(
    release: AppReleaseInfo,
    progress: Int,
    downloadedBytes: Long,
    totalBytes: Long
) {
    val downloadedMb = (downloadedBytes.toDouble() / (1024 * 1024))
    val totalMb = (totalBytes.toDouble() / (1024 * 1024))

    Card(
        colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Downloading AVL Ops v${release.versionName}...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
                Text(
                    text = "$progress%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = CyanNeon
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress / 100f },
                color = CyanNeon,
                trackColor = Color(0xFF1B2E3D),
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = String.format(Locale.US, "%.1f MB / %.1f MB", downloadedMb, totalMb),
                    fontSize = 10.sp,
                    color = DenseTextSecondaryDark,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Verifying SHA-256...",
                    fontSize = 10.sp,
                    color = AmberConcert
                )
            }
        }
    }
}

@Composable
private fun ReadyToInstallCard(
    release: AppReleaseInfo,
    onInstall: () -> Unit,
    onOpenBrowser: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DenseSurfaceHighlightDark),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, LaserGreen),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0D331A))
                    .border(1.5.dp, LaserGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = LaserGreen, modifier = Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Upgrade Package Ready!",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = LaserGreen
            )
            Text(
                text = "AVL Ops v${release.versionName} APK has been downloaded and verified.",
                fontSize = 11.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            Button(
                onClick = onInstall,
                colors = ButtonDefaults.buttonColors(containerColor = LaserGreen, contentColor = DenseBackgroundDark),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth().height(40.dp).testTag("btn_trigger_apk_install")
            ) {
                Icon(Icons.Default.SystemUpdate, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Install Upgrade Now", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))
            TextButton(
                onClick = onOpenBrowser,
                modifier = Modifier.height(30.dp)
            ) {
                Text("Or open direct download URL in browser", fontSize = 10.sp, color = DenseTextSecondaryDark)
            }
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonAlert),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = CrimsonAlert, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Update Check Error", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CrimsonAlert)
            Text(message, color = DenseTextSecondaryDark, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 6.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert, contentColor = Color.White),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth().height(36.dp)
            ) {
                Text("Retry", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    }
}
