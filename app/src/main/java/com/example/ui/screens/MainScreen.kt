package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Warehouse
import com.example.model.UpdateUiState
import com.example.ui.components.AppUpdateDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.UserRole
import com.example.ui.components.UserRoleBadge
import com.example.ui.theme.AmberConcert
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DenseBackgroundDark
import com.example.ui.theme.DenseBorderDark
import com.example.ui.theme.DenseSurfaceDark
import com.example.ui.theme.DenseTextPrimaryDark
import com.example.ui.theme.DenseTextSecondaryDark
import com.example.viewmodel.AvlViewModel

enum class MainTab(val title: String, val testTag: String) {
    CHECKLIST("Pack", "nav_checklist"),
    WAREHOUSE_RETURN("Return", "nav_return"),
    ROSTER_RSVP("Roster", "nav_roster"),
    SETUP_LOGS("Logs", "nav_logs"),
    CREW_COMMS("Comms", "nav_comms"),
    EVENTS("Gigs", "nav_events"),
    ADMIN("Admin", "nav_admin")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: AvlViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by rememberSaveable { mutableStateOf(MainTab.CHECKLIST) }
    val allEvents by viewModel.allEvents.collectAsStateWithLifecycle()
    val selectedEvent by viewModel.selectedEvent.collectAsStateWithLifecycle()
    val notifications by viewModel.currentNotifications.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val showUpdateDialog by viewModel.showUpdateDialog.collectAsStateWithLifecycle()
    val updateState by viewModel.updateUiState.collectAsStateWithLifecycle()

    val unacknowledgedNotifs = notifications.count { !it.isAcknowledged }
    var eventMenuExpanded by remember { mutableStateOf(false) }
    var showAuthBottomSheet by remember { mutableStateOf(false) }
    val authSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { eventMenuExpanded = true }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        // Glowing AVL indicator dot
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(CyanNeon)
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        Column {
                            Text(
                                text = selectedEvent?.name ?: "AVL Production Ops",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                fontSize = 13.sp,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (selectedEvent != null) "${selectedEvent?.clientOrVenue} ▼" else "Select Gig ▼",
                                style = MaterialTheme.typography.bodySmall,
                                color = CyanNeon,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }

                        DropdownMenu(
                            expanded = eventMenuExpanded,
                            onDismissRequest = { eventMenuExpanded = false },
                            modifier = Modifier.background(DenseSurfaceDark)
                        ) {
                            allEvents.forEach { evt ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(evt.name, fontWeight = FontWeight.SemiBold, color = DenseTextPrimaryDark, fontSize = 12.sp)
                                            Text("${evt.clientOrVenue} • ${evt.status.displayName}", fontSize = 10.sp, color = DenseTextSecondaryDark)
                                        }
                                    },
                                    onClick = {
                                        viewModel.selectEvent(evt.id)
                                        eventMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Quick Admin Portal entry button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (currentUser?.role == UserRole.ADMIN || currentUser?.role == UserRole.OWNER)
                                    Color(0xFF352507) else Color(0xFF1E2833)
                            )
                            .border(
                                1.dp,
                                if (currentUser?.role == UserRole.ADMIN || currentUser?.role == UserRole.OWNER)
                                    AmberConcert else DenseBorderDark,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { currentTab = MainTab.ADMIN }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                            .testTag("btn_top_admin_console")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Console",
                                tint = AmberConcert,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "ADMIN",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberConcert
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Active user / Role badge button (1-tap to switch or register)
                    currentUser?.let { user ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF1E2833))
                                .border(0.5.dp, CyanNeon.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                .clickable { showAuthBottomSheet = true }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                .testTag("btn_auth_profile")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.fullName.split(" ").firstOrNull() ?: user.fullName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                UserRoleBadge(role = user.role)
                            }
                        }
                    } ?: run {
                        IconButton(
                            onClick = { showAuthBottomSheet = true },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("btn_open_login")
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "Sign In", tint = CyanNeon, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // In-App Update / Upgrade button
                    val isUpdateAvailable = updateState is UpdateUiState.UpdateAvailable
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isUpdateAvailable) Color(0xFF3B2807) else Color(0xFF1E2833)
                            )
                            .border(
                                1.dp,
                                if (isUpdateAvailable) AmberConcert else CyanNeon.copy(alpha = 0.5f),
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { viewModel.openUpdateDialog() }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                            .testTag("btn_top_ota_update")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.RocketLaunch,
                                contentDescription = "Check for Updates",
                                tint = if (isUpdateAvailable) AmberConcert else CyanNeon,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isUpdateAvailable) "UPGRADE" else "v${viewModel.currentAppVersionName}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUpdateAvailable) AmberConcert else Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Real-time notification bell with alert counter
                    IconButton(
                        onClick = { currentTab = MainTab.CREW_COMMS },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("btn_notifications_bell")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unacknowledgedNotifs > 0) {
                                    Badge(
                                        containerColor = CrimsonAlert,
                                        contentColor = Color.White
                                    ) {
                                        Text("$unacknowledgedNotifs", fontSize = 8.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Team Notifications",
                                tint = if (unacknowledgedNotifs > 0) CrimsonAlert else DenseTextPrimaryDark,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DenseBackgroundDark,
                    titleContentColor = DenseTextPrimaryDark
                )
            )
        },
        bottomBar = {
            Surface(
                color = DenseBackgroundDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark)
            ) {
                NavigationBar(
                    containerColor = DenseBackgroundDark,
                    contentColor = DenseTextPrimaryDark,
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(56.dp)
                ) {
                    MainTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab
                        val (icon, badgeCount) = when (tab) {
                            MainTab.CHECKLIST -> Pair(Icons.Default.Checklist, 0)
                            MainTab.WAREHOUSE_RETURN -> Pair(Icons.Default.Warehouse, 0)
                            MainTab.ROSTER_RSVP -> Pair(Icons.Default.AssignmentInd, 0)
                            MainTab.SETUP_LOGS -> Pair(Icons.Default.HistoryEdu, 0)
                            MainTab.CREW_COMMS -> Pair(Icons.Default.Campaign, unacknowledgedNotifs)
                            MainTab.EVENTS -> Pair(Icons.Default.EventNote, 0)
                            MainTab.ADMIN -> Pair(Icons.Default.AdminPanelSettings, 0)
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                if (badgeCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge(containerColor = CrimsonAlert) {
                                                Text("$badgeCount", fontSize = 8.sp)
                                            }
                                        }
                                    ) {
                                        Icon(icon, contentDescription = tab.title, modifier = Modifier.size(16.dp))
                                    }
                                } else {
                                    Icon(icon, contentDescription = tab.title, modifier = Modifier.size(16.dp))
                                }
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DenseBackgroundDark,
                                selectedTextColor = CyanNeon,
                                indicatorColor = CyanNeon,
                                unselectedIconColor = DenseTextSecondaryDark,
                                unselectedTextColor = DenseTextSecondaryDark
                            ),
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DenseBackgroundDark)
        ) {
            when (currentTab) {
                MainTab.CHECKLIST -> ChecklistScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
                MainTab.WAREHOUSE_RETURN -> WarehouseReturnScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
                MainTab.ROSTER_RSVP -> UpcomingProgramsScreen(
                    viewModel = viewModel,
                    onNavigateToChecklist = { currentTab = MainTab.CHECKLIST },
                    modifier = Modifier.fillMaxSize()
                )
                MainTab.SETUP_LOGS -> SetupLogsScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
                MainTab.CREW_COMMS -> CrewCommsScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
                MainTab.EVENTS -> EventsScreen(
                    viewModel = viewModel,
                    onNavigateToChecklist = { currentTab = MainTab.CHECKLIST },
                    modifier = Modifier.fillMaxSize()
                )
                MainTab.ADMIN -> AdminControlScreen(
                    viewModel = viewModel,
                    onNavigateToPublic = { currentTab = MainTab.CHECKLIST },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Modal Bottom Sheet for Authentication, Role Selection & User Registration
    if (showAuthBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAuthBottomSheet = false },
            sheetState = authSheetState,
            containerColor = DenseBackgroundDark,
            dragHandle = null
        ) {
            AuthScreen(
                viewModel = viewModel,
                onAuthSuccess = { showAuthBottomSheet = false },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // In-App OTA Update & Upgrade Modal Dialog
    if (showUpdateDialog) {
        AppUpdateDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.dismissUpdateDialog() }
        )
    }
}
