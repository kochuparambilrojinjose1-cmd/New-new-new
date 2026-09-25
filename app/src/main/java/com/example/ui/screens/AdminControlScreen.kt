package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.EquipmentItemEntity
import com.example.data.local.ProductionEventEntity
import com.example.data.local.SetupLogEntity
import com.example.data.local.TeamNotificationEntity
import com.example.data.local.UserAccountEntity
import com.example.model.AvlCategory
import com.example.model.EventStatus
import com.example.model.ItemStatus
import com.example.model.LogType
import com.example.model.MemberAvailability
import com.example.model.NotificationPriority
import com.example.model.UserRole
import com.example.model.WorkDepartment
import com.example.ui.components.AvlCategoryBadge
import com.example.ui.components.DepartmentBadge
import com.example.ui.components.EventStatusBadge
import com.example.ui.components.LogTypeBadge
import com.example.ui.components.PriorityBadge
import com.example.ui.components.UserRoleBadge
import com.example.ui.theme.AmberConcert
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DenseBackgroundDark
import com.example.ui.theme.DenseBorderDark
import com.example.ui.theme.DenseBorderSubtleDark
import com.example.ui.theme.DenseSurfaceDark
import com.example.ui.theme.DenseSurfaceElevatedDark
import com.example.ui.theme.DenseSurfaceHighlightDark
import com.example.ui.theme.DenseTextMutedDark
import com.example.ui.theme.DenseTextPrimaryDark
import com.example.ui.theme.DenseTextSecondaryDark
import com.example.ui.theme.LaserGreen
import com.example.model.ReleaseChannel
import com.example.model.UpdateUiState
import com.example.model.AppReleaseInfo
import com.example.viewmodel.AvlViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AdminSubsystem(val title: String, val testTag: String) {
    GIGS("🎪 Gigs & Events", "admin_sub_gigs"),
    EQUIPMENT("🎛️ Gear Manifest", "admin_sub_equipment"),
    ROSTER("👥 Team Roster", "admin_sub_roster"),
    ASSIGNMENTS("📋 Call Roster", "admin_sub_assignments"),
    LOGS("📝 Logs & Audits", "admin_sub_logs"),
    DISPATCH("📢 Priority Alerts", "admin_sub_dispatch"),
    UPDATES("🚀 OTA & Upgrades", "admin_sub_updates"),
    RECOVERY("⚙️ System Recovery", "admin_sub_recovery")
}

@Composable
fun AdminControlScreen(
    viewModel: AvlViewModel,
    onNavigateToPublic: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val allEvents by viewModel.allEvents.collectAsStateWithLifecycle()
    val selectedEvent by viewModel.selectedEvent.collectAsStateWithLifecycle()
    val equipmentItems by viewModel.currentEquipment.collectAsStateWithLifecycle()
    val currentLogs by viewModel.currentSetupLogs.collectAsStateWithLifecycle()
    val notifications by viewModel.allRecentNotifications.collectAsStateWithLifecycle()
    val availabilities by viewModel.currentEventAvailabilities.collectAsStateWithLifecycle()
    val errorMessage by viewModel.authErrorMessage.collectAsStateWithLifecycle()
    val successMessage by viewModel.authSuccessMessage.collectAsStateWithLifecycle()

    val isAdmin = currentUser?.role == UserRole.OWNER || currentUser?.role == UserRole.ADMIN

    if (!isAdmin) {
        // Render Dedicated Admin Login Page
        AdminLoginView(
            viewModel = viewModel,
            errorMessage = errorMessage,
            successMessage = successMessage,
            onContinueToApp = onNavigateToPublic,
            modifier = modifier
        )
    } else {
        // Render Master Admin Command & Control Console
        AdminDashboardView(
            viewModel = viewModel,
            currentUser = currentUser!!,
            allUsers = allUsers,
            allEvents = allEvents,
            selectedEvent = selectedEvent,
            equipmentItems = equipmentItems,
            currentLogs = currentLogs,
            notifications = notifications,
            availabilities = availabilities,
            errorMessage = errorMessage,
            successMessage = successMessage,
            modifier = modifier
        )
    }
}

// -----------------------------------------------------------------------------
// 1. DEDICATED ADMIN LOGIN VIEW
// -----------------------------------------------------------------------------
@Composable
fun AdminLoginView(
    viewModel: AvlViewModel,
    errorMessage: String?,
    successMessage: String?,
    onContinueToApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var adminUsername by remember { mutableStateOf("admin") }
    var adminPassword by remember { mutableStateOf("admin") }
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DenseBackgroundDark)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // High-Security Admin Header Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, AmberConcert),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(AmberConcert.copy(alpha = 0.15f))
                        .border(1.5.dp, AmberConcert, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Gatekeeper",
                        tint = AmberConcert,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "AVL OPS • ADMIN CONTROL PORTAL",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = Color.White,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Restricted Administrator Gatekeeper: Full system governance, equipment manifests override, user roster controls, and database operations.",
                    fontSize = 11.sp,
                    color = DenseTextSecondaryDark,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Error / Success Banners
                errorMessage?.let { err ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF381212))
                            .border(1.dp, CrimsonAlert, RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        Text("⚠️ $err", color = CrimsonAlert, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                successMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF0E2E1D))
                            .border(1.dp, LaserGreen, RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        Text("✅ $msg", color = LaserGreen, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedTextField(
                    value = adminUsername,
                    onValueChange = { adminUsername = it },
                    label = { Text("Admin Username or Email") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = AmberConcert, modifier = Modifier.size(18.dp))
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberConcert,
                        unfocusedBorderColor = DenseBorderDark,
                        focusedContainerColor = DenseSurfaceElevatedDark,
                        unfocusedContainerColor = DenseSurfaceElevatedDark
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_login_username")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = adminPassword,
                    onValueChange = { adminPassword = it },
                    label = { Text("Admin Password") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = AmberConcert, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = DenseTextSecondaryDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberConcert,
                        unfocusedBorderColor = DenseBorderDark,
                        focusedContainerColor = DenseSurfaceElevatedDark,
                        unfocusedContainerColor = DenseSurfaceElevatedDark
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_login_password")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.adminLogin(adminUsername, adminPassword) },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberConcert, contentColor = Color.Black),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_admin_login_submit")
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Unlock Master Admin Console", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fast 1-Click Superadmin Shortcuts
                Text(
                    text = "⚡ QUICK 1-TAP ADMIN ACCESS (INSTANT UNLOCK):",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberConcert,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.quickAdminLogin(UserRole.OWNER) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF372608), contentColor = AmberConcert),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberConcert),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("btn_quick_admin_owner")
                    ) {
                        Text("👑 Dave (Owner)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.quickAdminLogin(UserRole.ADMIN) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003040), contentColor = CyanNeon),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("btn_quick_admin_admin")
                    ) {
                        Text("⚡ Elena (Admin)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onContinueToApp) {
                    Text("← Return to Production Checklist (Crew View)", fontSize = 11.sp, color = DenseTextSecondaryDark)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 2. MASTER ADMIN COMMAND CENTER (AUTHENTICATED)
// -----------------------------------------------------------------------------
@Composable
fun AdminDashboardView(
    viewModel: AvlViewModel,
    currentUser: UserAccountEntity,
    allUsers: List<UserAccountEntity>,
    allEvents: List<ProductionEventEntity>,
    selectedEvent: ProductionEventEntity?,
    equipmentItems: List<EquipmentItemEntity>,
    currentLogs: List<SetupLogEntity>,
    notifications: List<TeamNotificationEntity>,
    availabilities: List<com.example.data.local.EventAvailabilityEntity>,
    errorMessage: String?,
    successMessage: String?,
    modifier: Modifier = Modifier
) {
    var activeSubsystem by rememberSaveable { mutableStateOf(AdminSubsystem.GIGS) }

    // Dialog state controllers
    var showAddGigDialog by remember { mutableStateOf(false) }
    var eventToEdit by remember { mutableStateOf<ProductionEventEntity?>(null) }
    var eventToDelete by remember { mutableStateOf<ProductionEventEntity?>(null) }

    var showAddEquipmentDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<EquipmentItemEntity?>(null) }
    var itemToDelete by remember { mutableStateOf<EquipmentItemEntity?>(null) }

    var showAddUserDialog by remember { mutableStateOf(false) }
    var userToEdit by remember { mutableStateOf<UserAccountEntity?>(null) }
    var userToDelete by remember { mutableStateOf<UserAccountEntity?>(null) }

    var showAddLogDialog by remember { mutableStateOf(false) }
    var logToDelete by remember { mutableStateOf<SetupLogEntity?>(null) }

    var showDispatchDialog by remember { mutableStateOf(false) }
    var notificationToDelete by remember { mutableStateOf<TeamNotificationEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DenseBackgroundDark)
    ) {
        // Sticky Top Admin Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E170A))
                .border(1.dp, AmberConcert.copy(alpha = 0.6f))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(AmberConcert),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MASTER ADMIN CONSOLE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberConcert,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "[SUPERUSER]",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = LaserGreen
                            )
                        }
                        Text(
                            text = "Logged in as ${currentUser.fullName} (${currentUser.role.displayName})",
                            fontSize = 10.sp,
                            color = DenseTextSecondaryDark
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    UserRoleBadge(role = currentUser.role)
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedButton(
                        onClick = { viewModel.logout() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonAlert),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Log Out", fontSize = 10.sp)
                    }
                }
            }
        }

        // System Diagnostic KPI Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DenseSurfaceDark)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            KpiStat("🎪 Gigs", "${allEvents.size}", CyanNeon)
            KpiStat("🎛️ Gear", "${equipmentItems.size}", AmberConcert)
            KpiStat("👥 Roster", "${allUsers.size}", LaserGreen)
            KpiStat("📝 Logs", "${currentLogs.size}", DenseTextPrimaryDark)
            KpiStat("📢 Alerts", "${notifications.size}", CrimsonAlert)
        }

        // Subsystem Tab Navigation Bar (Scrollable chips)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DenseSurfaceElevatedDark)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AdminSubsystem.entries.forEach { subsystem ->
                val isSelected = activeSubsystem == subsystem
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) AmberConcert else DenseSurfaceDark)
                        .border(
                            1.dp,
                            if (isSelected) AmberConcert else DenseBorderDark,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { activeSubsystem = subsystem }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag(subsystem.testTag)
                ) {
                    Text(
                        text = subsystem.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.Black else DenseTextPrimaryDark
                    )
                }
            }
        }

        // Feedback Banners
        errorMessage?.let { err ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF381212))
                    .border(1.dp, CrimsonAlert)
                    .padding(8.dp)
            ) {
                Text("⚠️ $err", color = CrimsonAlert, fontSize = 11.sp)
            }
        }
        successMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0E2E1D))
                    .border(1.dp, LaserGreen)
                    .padding(8.dp)
            ) {
                Text("✅ $msg", color = LaserGreen, fontSize = 11.sp)
            }
        }

        // Active Subsystem Content Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            when (activeSubsystem) {
                AdminSubsystem.GIGS -> AdminGigsSection(
                    viewModel = viewModel,
                    allEvents = allEvents,
                    selectedEvent = selectedEvent,
                    onAddGig = { showAddGigDialog = true },
                    onEditGig = { eventToEdit = it },
                    onDeleteGig = { eventToDelete = it }
                )
                AdminSubsystem.EQUIPMENT -> AdminEquipmentSection(
                    viewModel = viewModel,
                    selectedEvent = selectedEvent,
                    allEvents = allEvents,
                    equipmentItems = equipmentItems,
                    onAddItem = { showAddEquipmentDialog = true },
                    onEditItem = { itemToEdit = it },
                    onDeleteItem = { itemToDelete = it }
                )
                AdminSubsystem.ROSTER -> AdminRosterSection(
                    viewModel = viewModel,
                    allUsers = allUsers,
                    currentUser = currentUser,
                    onAddUser = { showAddUserDialog = true },
                    onEditUser = { userToEdit = it },
                    onDeleteUser = { userToDelete = it }
                )
                AdminSubsystem.ASSIGNMENTS -> AdminAssignmentsSection(
                    viewModel = viewModel,
                    selectedEvent = selectedEvent,
                    allEvents = allEvents,
                    allUsers = allUsers,
                    availabilities = availabilities
                )
                AdminSubsystem.LOGS -> AdminLogsSection(
                    viewModel = viewModel,
                    selectedEvent = selectedEvent,
                    allEvents = allEvents,
                    currentLogs = currentLogs,
                    onAddLog = { showAddLogDialog = true },
                    onDeleteLog = { logToDelete = it }
                )
                AdminSubsystem.DISPATCH -> AdminDispatchSection(
                    viewModel = viewModel,
                    selectedEvent = selectedEvent,
                    allEvents = allEvents,
                    notifications = notifications,
                    onSendBroadcast = { showDispatchDialog = true },
                    onDeleteNotification = { notificationToDelete = it }
                )
                AdminSubsystem.UPDATES -> AdminUpdatesSection(
                    viewModel = viewModel
                )
                AdminSubsystem.RECOVERY -> AdminRecoverySection(
                    viewModel = viewModel,
                    selectedEvent = selectedEvent
                )
            }
        }
    }

    // -------------------------------------------------------------
    // MODAL DIALOGS FOR ADD / EDIT / DELETE OVERRIDES
    // -------------------------------------------------------------
    if (showAddGigDialog) {
        AddGigDialog(
            onDismiss = { showAddGigDialog = false },
            onConfirm = { name, venue, loc, lead, truck, dateMs, notes, withPack ->
                viewModel.createEvent(name, venue, loc, lead, truck, dateMs, notes, withPack)
                showAddGigDialog = false
            }
        )
    }

    eventToEdit?.let { evt ->
        EditGigDialog(
            event = evt,
            onDismiss = { eventToEdit = null },
            onConfirm = { updated ->
                viewModel.editEvent(updated)
                eventToEdit = null
            }
        )
    }

    eventToDelete?.let { evt ->
        AlertDialog(
            onDismissRequest = { eventToDelete = null },
            containerColor = DenseSurfaceDark,
            title = { Text("Delete Production Gig?", color = CrimsonAlert, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to delete '${evt.name}'? This will permanently wipe all associated equipment line items, setup logs, and assignments.",
                    color = DenseTextPrimaryDark
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEvent(evt.id)
                        eventToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert)
                ) {
                    Text("Delete Gig Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { eventToDelete = null }) {
                    Text("Cancel", color = DenseTextSecondaryDark)
                }
            }
        )
    }

    if (showAddEquipmentDialog) {
        selectedEvent?.let { evt ->
            AddEquipmentDialog(
                eventId = evt.id,
                onDismiss = { showAddEquipmentDialog = false },
                onConfirm = { name, category, targetQty, storage, barcode, notes ->
                    viewModel.addEquipmentItem(evt.id, name, category, targetQty, storage, barcode, notes)
                    showAddEquipmentDialog = false
                }
            )
        }
    }

    itemToEdit?.let { item ->
        EditEquipmentDialog(
            item = item,
            onDismiss = { itemToEdit = null },
            onConfirm = { updated ->
                viewModel.editEquipmentItem(updated)
                itemToEdit = null
            }
        )
    }

    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            containerColor = DenseSurfaceDark,
            title = { Text("Delete Gear Line Item?", color = CrimsonAlert, fontWeight = FontWeight.Bold) },
            text = { Text("Delete '${item.name}' from current manifest?", color = DenseTextPrimaryDark) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEquipmentItem(item.id)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert)
                ) {
                    Text("Delete Item")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel", color = DenseTextSecondaryDark)
                }
            }
        )
    }

    if (showAddUserDialog) {
        AddUserDialog(
            onDismiss = { showAddUserDialog = false },
            onConfirm = { user, pass ->
                viewModel.register(
                    username = user.username,
                    fullName = user.fullName,
                    email = user.email,
                    password = pass,
                    role = user.role,
                    department = user.department,
                    phone = user.phone
                )
                showAddUserDialog = false
            }
        )
    }

    userToEdit?.let { user ->
        EditUserDialog(
            user = user,
            onDismiss = { userToEdit = null },
            onConfirm = { updated ->
                viewModel.updateUserDetails(updated)
                userToEdit = null
            }
        )
    }

    userToDelete?.let { user ->
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            containerColor = DenseSurfaceDark,
            title = { Text("Remove User from Roster?", color = CrimsonAlert, fontWeight = FontWeight.Bold) },
            text = { Text("Delete user account '${user.fullName}' (@${user.username})?", color = DenseTextPrimaryDark) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUser(user.id)
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert)
                ) {
                    Text("Delete User")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("Cancel", color = DenseTextSecondaryDark)
                }
            }
        )
    }

    if (showAddLogDialog) {
        selectedEvent?.let { evt ->
            AddLogDialog(
                eventId = evt.id,
                author = currentUser.fullName,
                onDismiss = { showAddLogDialog = false },
                onConfirm = { zone, type, content ->
                    viewModel.addSetupLog(evt.id, currentUser.fullName, zone, type, content)
                    showAddLogDialog = false
                }
            )
        }
    }

    logToDelete?.let { log ->
        AlertDialog(
            onDismissRequest = { logToDelete = null },
            containerColor = DenseSurfaceDark,
            title = { Text("Delete Log Entry?", color = CrimsonAlert, fontWeight = FontWeight.Bold) },
            text = { Text("Delete log '${log.content}'?", color = DenseTextPrimaryDark) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSetupLog(log.id)
                        logToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert)
                ) {
                    Text("Delete Log")
                }
            },
            dismissButton = {
                TextButton(onClick = { logToDelete = null }) {
                    Text("Cancel", color = DenseTextSecondaryDark)
                }
            }
        )
    }

    if (showDispatchDialog) {
        selectedEvent?.let { evt ->
            SendDispatchDialog(
                eventId = evt.id,
                sender = currentUser.fullName,
                onDismiss = { showDispatchDialog = false },
                onConfirm = { priority, title, msg ->
                    viewModel.sendTeamNotification(evt.id, currentUser.fullName, priority, title, msg, isWorkUpdate = true)
                    showDispatchDialog = false
                }
            )
        }
    }

    notificationToDelete?.let { notif ->
        AlertDialog(
            onDismissRequest = { notificationToDelete = null },
            containerColor = DenseSurfaceDark,
            title = { Text("Delete Broadcast Alert?", color = CrimsonAlert, fontWeight = FontWeight.Bold) },
            text = { Text("Remove alert '${notif.title}'?", color = DenseTextPrimaryDark) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteNotification(notif.id)
                        notificationToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert)
                ) {
                    Text("Delete Alert")
                }
            },
            dismissButton = {
                TextButton(onClick = { notificationToDelete = null }) {
                    Text("Cancel", color = DenseTextSecondaryDark)
                }
            }
        )
    }
}

// -----------------------------------------------------------------------------
// SUBSYSTEM 1: GIGS & EVENTS CONTROL
// -----------------------------------------------------------------------------
@Composable
fun AdminGigsSection(
    viewModel: AvlViewModel,
    allEvents: List<ProductionEventEntity>,
    selectedEvent: ProductionEventEntity?,
    onAddGig: () -> Unit,
    onEditGig: (ProductionEventEntity) -> Unit,
    onDeleteGig: (ProductionEventEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("PRODUCTION GIGS MASTER LIST", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AmberConcert)
                Text("${allEvents.size} production events configured in local database", fontSize = 10.sp, color = DenseTextSecondaryDark)
            }
            Button(
                onClick = onAddGig,
                colors = ButtonDefaults.buttonColors(containerColor = AmberConcert, contentColor = Color.Black),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("btn_admin_add_gig")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add New Gig", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allEvents, key = { it.id }) { evt ->
                val isSelected = evt.id == selectedEvent?.id
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) DenseSurfaceHighlightDark else DenseSurfaceDark
                    ),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) CyanNeon else DenseBorderDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(evt.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("● ACTIVE CONSOLE FOCUS", fontSize = 8.sp, color = LaserGreen, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text("${evt.clientOrVenue} • ${evt.location}", fontSize = 10.sp, color = CyanNeon)
                                Text(
                                    "Date: ${SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(evt.eventDate))} • Lead: ${evt.leadTech} • Truck: ${evt.truckOrVehicle}",
                                    fontSize = 9.sp,
                                    color = DenseTextSecondaryDark
                                )
                            }
                            EventStatusBadge(status = evt.status)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!isSelected) {
                                    TextButton(
                                        onClick = { viewModel.selectEvent(evt.id) },
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Select Gig Focus", fontSize = 10.sp, color = CyanNeon)
                                    }
                                }

                                // Quick Status Dropdown Menu
                                var statusMenuOpen by remember { mutableStateOf(false) }
                                Box {
                                    OutlinedButton(
                                        onClick = { statusMenuOpen = true },
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Phase ▼", fontSize = 10.sp, color = DenseTextPrimaryDark)
                                    }
                                    DropdownMenu(
                                        expanded = statusMenuOpen,
                                        onDismissRequest = { statusMenuOpen = false },
                                        modifier = Modifier.background(DenseSurfaceDark)
                                    ) {
                                        EventStatus.entries.forEach { status ->
                                            DropdownMenuItem(
                                                text = { Text(status.displayName, fontSize = 11.sp, color = DenseTextPrimaryDark) },
                                                onClick = {
                                                    viewModel.updateEventStatus(evt.id, status)
                                                    statusMenuOpen = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Row {
                                IconButton(
                                    onClick = { onEditGig(evt) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Gig", tint = CyanNeon, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { onDeleteGig(evt) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Gig", tint = CrimsonAlert, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// SUBSYSTEM 2: GEAR MANIFEST & EQUIPMENT CONTROL
// -----------------------------------------------------------------------------
@Composable
fun AdminEquipmentSection(
    viewModel: AvlViewModel,
    selectedEvent: ProductionEventEntity?,
    allEvents: List<ProductionEventEntity>,
    equipmentItems: List<EquipmentItemEntity>,
    onAddItem: () -> Unit,
    onEditItem: (EquipmentItemEntity) -> Unit,
    onDeleteItem: (EquipmentItemEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<AvlCategory?>(null) }

    val filteredItems = equipmentItems.filter { item ->
        (selectedCategory == null || item.category == selectedCategory) &&
                (searchQuery.isBlank() || item.name.contains(searchQuery, ignoreCase = true) || item.barcodeOrTag.contains(searchQuery, ignoreCase = true) || item.storageLocation.contains(searchQuery, ignoreCase = true))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Gig Selector & Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "GEAR MANIFEST: ${selectedEvent?.name ?: "No Gig Selected"}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = AmberConcert
                )
                Text("${equipmentItems.size} line items in active manifest", fontSize = 10.sp, color = DenseTextSecondaryDark)
            }

            Row {
                Button(
                    onClick = onAddItem,
                    colors = ButtonDefaults.buttonColors(containerColor = LaserGreen, contentColor = Color.Black),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("btn_admin_add_gear")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Gear Item", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Batch Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.packAllCurrentItems() },
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Text("⚡ Pack All Items", fontSize = 10.sp, color = CyanNeon)
            }
            OutlinedButton(
                onClick = { viewModel.bulkCheckInAllIntact() },
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Text("⚡ Check-in All Intact", fontSize = 10.sp, color = LaserGreen)
            }
            OutlinedButton(
                onClick = { selectedEvent?.let { viewModel.resetAllEquipmentQuantities(it.id) } },
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Text("↺ Reset All Qty to 0", fontSize = 10.sp, color = AmberConcert)
            }
            OutlinedButton(
                onClick = { selectedEvent?.let { viewModel.deleteAllEquipmentForEvent(it.id) } },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonAlert),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Text("🗑️ Delete All Gear", fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search gear name, barcode, trunk...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AmberConcert, modifier = Modifier.size(16.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = null, tint = DenseTextSecondaryDark, modifier = Modifier.size(14.dp))
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AmberConcert,
                unfocusedBorderColor = DenseBorderDark,
                focusedContainerColor = DenseSurfaceDark,
                unfocusedContainerColor = DenseSurfaceDark
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Equipment List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(filteredItems, key = { it.id }) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, DenseBorderDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                AvlCategoryBadge(category = item.category)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DenseTextPrimaryDark)
                                    Text(
                                        "${item.storageLocation} • Tag: ${item.barcodeOrTag}",
                                        fontSize = 9.sp,
                                        color = DenseTextSecondaryDark,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { onEditItem(item) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = CyanNeon, modifier = Modifier.size(14.dp))
                                }
                                IconButton(onClick = { onDeleteItem(item) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonAlert, modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Quantity overrides row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Target: ${item.targetQuantity} | Packed: ${item.packedQuantity} | Returned: ${item.returnedQuantity}",
                                fontSize = 10.sp,
                                color = if (item.packedQuantity >= item.targetQuantity) LaserGreen else AmberConcert,
                                fontWeight = FontWeight.Bold
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                OutlinedButton(
                                    onClick = { viewModel.decrementPackedQuantity(item) },
                                    modifier = Modifier.size(26.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                ) {
                                    Text("-", fontSize = 11.sp, color = CrimsonAlert)
                                }
                                OutlinedButton(
                                    onClick = { viewModel.incrementPackedQuantity(item) },
                                    modifier = Modifier.size(26.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                ) {
                                    Text("+", fontSize = 11.sp, color = LaserGreen)
                                }
                                Button(
                                    onClick = { viewModel.markItemFullyPacked(item) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
                                    shape = RoundedCornerShape(2.dp),
                                    modifier = Modifier.height(26.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp)
                                ) {
                                    Text("Full Pack", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// SUBSYSTEM 3: TEAM ROSTER & USERS CONTROL
// -----------------------------------------------------------------------------
@Composable
fun AdminRosterSection(
    viewModel: AvlViewModel,
    allUsers: List<UserAccountEntity>,
    currentUser: UserAccountEntity,
    onAddUser: () -> Unit,
    onEditUser: (UserAccountEntity) -> Unit,
    onDeleteUser: (UserAccountEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("TEAM ROSTER & ACCESS GOVERNANCE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AmberConcert)
                Text("${allUsers.size} crew accounts registered across 4 role classes", fontSize = 10.sp, color = DenseTextSecondaryDark)
            }
            Button(
                onClick = onAddUser,
                colors = ButtonDefaults.buttonColors(containerColor = LaserGreen, contentColor = Color.Black),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .height(30.dp)
                    .testTag("btn_admin_add_user")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Register Member", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allUsers, key = { it.id }) { u ->
                val isSelf = u.id == currentUser.id
                Card(
                    colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        if (isSelf) AmberConcert else DenseBorderDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(AmberConcert.copy(alpha = 0.2f))
                                        .border(1.dp, AmberConcert, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(u.initials, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = AmberConcert)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(u.fullName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DenseTextPrimaryDark)
                                        if (isSelf) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("(You)", fontSize = 9.sp, color = LaserGreen, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Text("@${u.username} • ${u.email}", fontSize = 9.sp, color = DenseTextSecondaryDark)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                UserRoleBadge(role = u.role)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                DepartmentBadge(department = u.department)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(u.phone, fontSize = 9.sp, color = DenseTextMutedDark)
                            }

                            Row {
                                // Fast role change dropdown
                                var roleMenuOpen by remember { mutableStateOf(false) }
                                Box {
                                    OutlinedButton(
                                        onClick = { roleMenuOpen = true },
                                        modifier = Modifier.height(26.dp)
                                    ) {
                                        Text("Role: ${u.role.name} ▼", fontSize = 9.sp, color = DenseTextPrimaryDark)
                                    }
                                    DropdownMenu(
                                        expanded = roleMenuOpen,
                                        onDismissRequest = { roleMenuOpen = false },
                                        modifier = Modifier.background(DenseSurfaceDark)
                                    ) {
                                        UserRole.entries.forEach { r ->
                                            DropdownMenuItem(
                                                text = { Text(r.displayName, fontSize = 11.sp, color = DenseTextPrimaryDark) },
                                                onClick = {
                                                    viewModel.updateUserRole(u.id, r)
                                                    roleMenuOpen = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(onClick = { onEditUser(u) }, modifier = Modifier.size(26.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = CyanNeon, modifier = Modifier.size(14.dp))
                                }

                                if (!isSelf) {
                                    IconButton(onClick = { onDeleteUser(u) }, modifier = Modifier.size(26.dp)) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonAlert, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// SUBSYSTEM 4: CALL ROSTER & ASSIGNMENTS CONTROL
// -----------------------------------------------------------------------------
@Composable
fun AdminAssignmentsSection(
    viewModel: AvlViewModel,
    selectedEvent: ProductionEventEntity?,
    allEvents: List<ProductionEventEntity>,
    allUsers: List<UserAccountEntity>,
    availabilities: List<com.example.data.local.EventAvailabilityEntity>
) {
    var assignUserDialog by remember { mutableStateOf<UserAccountEntity?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "CREW ROSTER ASSIGNMENT & CALL-TIMES",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = AmberConcert
        )
        Text(
            "Assign team members to '${selectedEvent?.name ?: "Active Gig"}' with specific positions & dispatch call alerts.",
            fontSize = 10.sp,
            color = DenseTextSecondaryDark
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(allUsers, key = { it.id }) { u ->
                val record = availabilities.find { it.userId == u.id }
                val isAssigned = record?.isAssigned == true

                Card(
                    colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        if (isAssigned) LaserGreen.copy(alpha = 0.6f) else DenseBorderDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(u.fullName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DenseTextPrimaryDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                UserRoleBadge(role = u.role)
                            }
                            Text(
                                if (isAssigned) "Assigned: '${record?.assignedPosition}' • ${record?.callTimeNote}" else "Status: ${record?.status?.displayName ?: "No RSVP"}",
                                fontSize = 10.sp,
                                color = if (isAssigned) LaserGreen else DenseTextSecondaryDark
                            )
                        }

                        Row {
                            if (isAssigned) {
                                OutlinedButton(
                                    onClick = { selectedEvent?.let { viewModel.unassignCrewMember(it.id, u.id) } },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonAlert),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Text("Unassign", fontSize = 9.sp)
                                }
                            } else {
                                Button(
                                    onClick = { assignUserDialog = u },
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberConcert, contentColor = Color.Black),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Text("Assign Gig", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    assignUserDialog?.let { u ->
        selectedEvent?.let { evt ->
            AssignUserDialog(
                user = u,
                eventName = evt.name,
                onDismiss = { assignUserDialog = null },
                onConfirm = { position, callTime ->
                    viewModel.assignCrewMember(evt.id, u.id, position, callTime)
                    assignUserDialog = null
                }
            )
        }
    }
}

// -----------------------------------------------------------------------------
// SUBSYSTEM 5: PRODUCTION LOGS & AUDITS CONTROL
// -----------------------------------------------------------------------------
@Composable
fun AdminLogsSection(
    viewModel: AvlViewModel,
    selectedEvent: ProductionEventEntity?,
    allEvents: List<ProductionEventEntity>,
    currentLogs: List<SetupLogEntity>,
    onAddLog: () -> Unit,
    onDeleteLog: (SetupLogEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("SETUP LOGS & PRODUCTION MILESTONES", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AmberConcert)
                Text("${currentLogs.size} logs for ${selectedEvent?.name ?: "Selected Event"}", fontSize = 10.sp, color = DenseTextSecondaryDark)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(
                    onClick = { selectedEvent?.let { viewModel.clearAllLogsForEvent(it.id) } },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonAlert),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Purge Logs", fontSize = 10.sp)
                }
                Button(
                    onClick = onAddLog,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Post Log", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(currentLogs, key = { it.id }) { log ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, DenseBorderDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LogTypeBadge(logType = log.logType)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(log.zone, fontSize = 10.sp, color = CyanNeon, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    SimpleDateFormat("HH:mm", Locale.US).format(Date(log.timestamp)),
                                    fontSize = 9.sp,
                                    color = DenseTextSecondaryDark
                                )
                            }
                            Text(log.content, fontSize = 11.sp, color = DenseTextPrimaryDark, modifier = Modifier.padding(top = 2.dp))
                            Text("Author: ${log.author}", fontSize = 9.sp, color = DenseTextMutedDark)
                        }

                        IconButton(onClick = { onDeleteLog(log) }, modifier = Modifier.size(26.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonAlert, modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// SUBSYSTEM 6: PRIORITY DISPATCH ALERTS CONTROL
// -----------------------------------------------------------------------------
@Composable
fun AdminDispatchSection(
    viewModel: AvlViewModel,
    selectedEvent: ProductionEventEntity?,
    allEvents: List<ProductionEventEntity>,
    notifications: List<TeamNotificationEntity>,
    onSendBroadcast: () -> Unit,
    onDeleteNotification: (TeamNotificationEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("DISPATCH CENTER & EMERGENCY ALERTS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CrimsonAlert)
                Text("${notifications.size} team notifications broadcasted", fontSize = 10.sp, color = DenseTextSecondaryDark)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(
                    onClick = { viewModel.clearAllNotifications() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonAlert),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Clear All", fontSize = 10.sp)
                }
                Button(
                    onClick = onSendBroadcast,
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert, contentColor = Color.White),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Broadcast Alert", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(notifications, key = { it.id }) { notif ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        if (notif.priority == NotificationPriority.CRITICAL) CrimsonAlert else DenseBorderDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PriorityBadge(priority = notif.priority)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                            }
                            Text(notif.message, fontSize = 10.sp, color = DenseTextPrimaryDark, modifier = Modifier.padding(top = 2.dp))
                            Text("Sender: ${notif.sender} • ${SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(notif.timestamp))}", fontSize = 8.sp, color = DenseTextSecondaryDark)
                        }

                        IconButton(onClick = { onDeleteNotification(notif) }, modifier = Modifier.size(26.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonAlert, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// SUBSYSTEM 7: SYSTEM RECOVERY & RESET CONTROL
// -----------------------------------------------------------------------------
@Composable
fun AdminRecoverySection(
    viewModel: AvlViewModel,
    selectedEvent: ProductionEventEntity?
) {
    var showReseedConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text("SYSTEM RECOVERY & DATABASE MASTER OVERRIDES", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AmberConcert)
        Text("Restore fresh factory AVL production data, simulate live dispatch radio, or trigger maintenance routines.", fontSize = 10.sp, color = DenseTextSecondaryDark)

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AmberConcert.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("⚠️ RESTORE FACTORY DEMO DATA", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AmberConcert)
                Text(
                    "Re-seeds the local SQLite Room database with 3 complete production tour gigs, 60+ AVL equipment line items (Audio, Video, Lighting, Rigging, Cables), 6 demo team accounts, and setup logs.",
                    fontSize = 10.sp,
                    color = DenseTextSecondaryDark,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { showReseedConfirmation = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberConcert, contentColor = Color.Black),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Re-seed Pristine AVL Data", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("📡 SIMULATE LIVE CREW FIELD BROADCAST", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CyanNeon)
                Text(
                    "Triggers an incoming live technician notification broadcast with real Android system vibration & heads-up push alert.",
                    fontSize = 10.sp,
                    color = DenseTextSecondaryDark,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.simulateIncomingCrewNotification() },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Trigger Live Push Notification Test", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }

    if (showReseedConfirmation) {
        AlertDialog(
            onDismissRequest = { showReseedConfirmation = false },
            containerColor = DenseSurfaceDark,
            title = { Text("Re-seed Database?", color = AmberConcert, fontWeight = FontWeight.Bold) },
            text = { Text("This will add back all standard concert manifests, soundcheck checklists, and demo crew profiles.", color = DenseTextPrimaryDark) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.reseedDatabase()
                        showReseedConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberConcert, contentColor = Color.Black)
                ) {
                    Text("Confirm Re-seed")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReseedConfirmation = false }) {
                    Text("Cancel", color = DenseTextSecondaryDark)
                }
            }
        )
    }
}

@Composable
fun AdminUpdatesSection(
    viewModel: AvlViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val updateState by viewModel.updateUiState.collectAsStateWithLifecycle()
    val currentChannel by viewModel.selectedReleaseChannel.collectAsStateWithLifecycle()
    var simulationMode by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    ) {
        Text("SYSTEM OTA & IN-APP UPGRADE ENGINE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AmberConcert)
        Text("Manage software distribution tracks, trigger in-app updates, and broadcast upgrade directives to crew.", fontSize = 10.sp, color = DenseTextSecondaryDark)

        Spacer(modifier = Modifier.height(14.dp))

        // Build Metadata & Version Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("INSTALLED APP BUILD", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF00384D))
                            .border(1.dp, CyanNeon, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("v${viewModel.currentAppVersionName} (Build ${viewModel.currentAppVersionCode})", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyanNeon)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Package ID", fontSize = 9.sp, color = DenseTextMutedDark)
                        Text("com.aistudio.avlops.zkvt", fontSize = 10.sp, color = Color.White, fontFamily = FontFamily.Monospace)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Target Android API", fontSize = 9.sp, color = DenseTextMutedDark)
                        Text("API 36 (Android 15/16)", fontSize = 10.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Architecture", fontSize = 9.sp, color = DenseTextMutedDark)
                        Text("ARM64 / x86_64 Universal APK", fontSize = 10.sp, color = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Min SDK", fontSize = 9.sp, color = DenseTextMutedDark)
                        Text("Android 7.0 (API 24+)", fontSize = 10.sp, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // In-App Upgrade Action Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AmberConcert.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("🚀 IN-APP OTA UPGRADE CONTROLS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AmberConcert)
                Text("Execute on-device package upgrade without leaving the application or needing a computer.", fontSize = 10.sp, color = DenseTextSecondaryDark, modifier = Modifier.padding(top = 2.dp, bottom = 8.dp))

                // Track Selector
                Text("Release Channel / Track:", fontSize = 10.sp, color = DenseTextPrimaryDark, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                                containerColor = DenseSurfaceElevatedDark,
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

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.openUpdateDialog() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f).height(36.dp).testTag("btn_admin_open_update_dialog")
                    ) {
                        Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Open Upgrade Center", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.checkForAppUpdate() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberConcert),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberConcert),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f).height(36.dp).testTag("btn_admin_check_updates")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Check for Updates", fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Live Update Status & Crew Broadcast Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DenseSurfaceHighlightDark),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("STATUS & CREW UPGRADE DIRECTIVES", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)

                when (val state = updateState) {
                    is UpdateUiState.Idle -> {
                        Text("System update engine is idle. Tap Check for Updates above to scan feeds.", fontSize = 10.sp, color = DenseTextSecondaryDark, modifier = Modifier.padding(vertical = 6.dp))
                    }
                    is UpdateUiState.Checking -> {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                            CircularProgressIndicator(color = CyanNeon, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Querying release manifest for channel ${state.channel.displayName}...", fontSize = 10.sp, color = CyanNeon)
                        }
                    }
                    is UpdateUiState.UpToDate -> {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = LaserGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("All systems current: v${state.currentVersionName} (Build ${state.currentVersionCode})", fontSize = 11.sp, color = LaserGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                    is UpdateUiState.UpdateAvailable -> {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF4A3405))
                                        .border(1.dp, AmberConcert, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("NEW UPGRADE: v${state.release.versionName}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AmberConcert)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${state.release.fileSizeFormatted} • ${state.release.releaseDate}", fontSize = 10.sp, color = DenseTextSecondaryDark)
                            }
                            Text(state.release.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(top = 4.dp))

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { viewModel.broadcastUpdateToCrew(state.release) },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert, contentColor = Color.White),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.fillMaxWidth().height(36.dp).testTag("btn_broadcast_update_crew")
                            ) {
                                Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Broadcast Upgrade Notice to Entire Crew Roster", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    is UpdateUiState.Downloading -> {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text("Downloading update package: ${state.progressPercent}%", fontSize = 10.sp, color = CyanNeon, fontWeight = FontWeight.Bold)
                            LinearProgressIndicator(progress = { state.progressPercent / 100f }, color = CyanNeon, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
                        }
                    }
                    is UpdateUiState.ReadyToInstall -> {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = LaserGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("v${state.release.versionName} APK downloaded and verified. Ready for installation.", fontSize = 10.sp, color = LaserGreen)
                        }
                    }
                    is UpdateUiState.Error -> {
                        Text("Update check failed: ${state.message}", fontSize = 10.sp, color = CrimsonAlert)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Simulation & Field Testing Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("🧪 OTA FIELD TEST OVERRIDES", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                Text("Test and demonstrate the in-app update UI, download animation, and installer launch without requiring external server deployment.", fontSize = 10.sp, color = DenseTextSecondaryDark, modifier = Modifier.padding(top = 2.dp, bottom = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Simulate New Version Available", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.White)
                        Text("Feeds v1.2.0 upgrade manifest into updater", fontSize = 9.sp, color = DenseTextSecondaryDark)
                    }
                    Switch(
                        checked = simulationMode,
                        onCheckedChange = {
                            simulationMode = it
                            viewModel.toggleUpdateSimulation(it)
                            viewModel.checkForAppUpdate(forceSimulate = it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyanNeon,
                            checkedTrackColor = Color(0xFF00384D),
                            uncheckedThumbColor = DenseTextSecondaryDark,
                            uncheckedTrackColor = DenseBackgroundDark
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.checkForAppUpdate(forceSimulate = true)
                        viewModel.openUpdateDialog()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberConcert, contentColor = Color.Black),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp).testTag("btn_trigger_simulated_upgrade")
                ) {
                    Icon(Icons.Default.SystemUpdate, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Trigger Simulated In-App Upgrade Flow", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Direct Download & Export
        Card(
            colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("📦 DIRECT APK DOWNLOAD & EXPORT", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                Text("In addition to in-app updating, you can download the APK file directly or open it in your browser for sideloading to crew devices.", fontSize = 10.sp, color = DenseTextSecondaryDark, modifier = Modifier.padding(top = 2.dp, bottom = 8.dp))

                Button(
                    onClick = {
                        viewModel.openDownloadUrlInBrowser(
                            context,
                            "https://github.com/aistudio/avl-ops/releases/download/v1.2.0/AVL-Production-App.apk"
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2833), contentColor = CyanNeon),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp).testTag("btn_open_apk_browser")
                ) {
                    Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Open Direct APK Download Link in Browser", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// HELPER DIALOGS (ADD / EDIT)
// -----------------------------------------------------------------------------
@Composable
fun AddGigDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, venue: String, loc: String, lead: String, truck: String, dateMs: Long, notes: String, withPack: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("") }
    var loc by remember { mutableStateOf("") }
    var lead by remember { mutableStateOf("") }
    var truck by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var withPack by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DenseSurfaceDark,
        title = { Text("Add New Production Gig", color = AmberConcert, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Event / Tour Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Client or Venue *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = loc,
                    onValueChange = { loc = it },
                    label = { Text("Loading Bay / Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = lead,
                    onValueChange = { lead = it },
                    label = { Text("Lead Audio/Lighting Tech") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = truck,
                    onValueChange = { truck = it },
                    label = { Text("Truck / Transport Vehicle") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Staging & Schedule Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = withPack,
                        onCheckedChange = { withPack = it },
                        colors = CheckboxDefaults.colors(checkedColor = AmberConcert)
                    )
                    Text("Auto-seed standard AVL gear package", fontSize = 11.sp, color = DenseTextPrimaryDark)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && venue.isNotBlank()) {
                        onConfirm(name, venue, loc, lead, truck, System.currentTimeMillis() + (86400000L * 3), notes, withPack)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberConcert, contentColor = Color.Black)
            ) {
                Text("Create Gig")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DenseTextSecondaryDark) }
        }
    )
}

@Composable
fun EditGigDialog(
    event: ProductionEventEntity,
    onDismiss: () -> Unit,
    onConfirm: (ProductionEventEntity) -> Unit
) {
    var name by remember { mutableStateOf(event.name) }
    var venue by remember { mutableStateOf(event.clientOrVenue) }
    var loc by remember { mutableStateOf(event.location) }
    var lead by remember { mutableStateOf(event.leadTech) }
    var truck by remember { mutableStateOf(event.truckOrVehicle) }
    var notes by remember { mutableStateOf(event.notes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DenseSurfaceDark,
        title = { Text("Edit Production Gig", color = CyanNeon, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Gig Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = venue, onValueChange = { venue = it }, label = { Text("Venue") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = loc, onValueChange = { loc = it }, label = { Text("Location") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = lead, onValueChange = { lead = it }, label = { Text("Lead Tech") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = truck, onValueChange = { truck = it }, label = { Text("Vehicle") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        event.copy(
                            name = name,
                            clientOrVenue = venue,
                            location = loc,
                            leadTech = lead,
                            truckOrVehicle = truck,
                            notes = notes
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DenseTextSecondaryDark) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEquipmentDialog(
    eventId: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: AvlCategory, targetQty: Int, storage: String, barcode: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(AvlCategory.AUDIO) }
    var targetQty by remember { mutableIntStateOf(1) }
    var storage by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var catExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DenseSurfaceDark,
        title = { Text("Add Equipment to Manifest", color = LaserGreen, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Equipment Name *") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = !catExpanded }) {
                    OutlinedTextField(
                        value = category.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }, modifier = Modifier.background(DenseSurfaceDark)) {
                        AvlCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.displayName, color = DenseTextPrimaryDark) },
                                onClick = {
                                    category = cat
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = "$targetQty",
                    onValueChange = { targetQty = it.toIntOrNull() ?: 1 },
                    label = { Text("Target Quantity *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(value = storage, onValueChange = { storage = it }, label = { Text("Trunk / Road Case") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = barcode, onValueChange = { barcode = it }, label = { Text("Barcode Tag") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Prep Notes") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, category, targetQty, storage, barcode, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LaserGreen, contentColor = Color.Black)
            ) {
                Text("Add Item")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DenseTextSecondaryDark) }
        }
    )
}

@Composable
fun EditEquipmentDialog(
    item: EquipmentItemEntity,
    onDismiss: () -> Unit,
    onConfirm: (EquipmentItemEntity) -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    var targetQty by remember { mutableIntStateOf(item.targetQuantity) }
    var packedQty by remember { mutableIntStateOf(item.packedQuantity) }
    var returnedQty by remember { mutableIntStateOf(item.returnedQuantity) }
    var storage by remember { mutableStateOf(item.storageLocation) }
    var barcode by remember { mutableStateOf(item.barcodeOrTag) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DenseSurfaceDark,
        title = { Text("Edit Equipment Line", color = CyanNeon, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = "$targetQty", onValueChange = { targetQty = it.toIntOrNull() ?: targetQty }, label = { Text("Target Quantity") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = "$packedQty", onValueChange = { packedQty = it.toIntOrNull() ?: packedQty }, label = { Text("Packed Quantity") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = "$returnedQty", onValueChange = { returnedQty = it.toIntOrNull() ?: returnedQty }, label = { Text("Returned Quantity") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = storage, onValueChange = { storage = it }, label = { Text("Road Case / Rack") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = barcode, onValueChange = { barcode = it }, label = { Text("Barcode") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        item.copy(
                            name = name,
                            targetQuantity = targetQty,
                            packedQuantity = packedQty,
                            returnedQuantity = returnedQty,
                            storageLocation = storage,
                            barcodeOrTag = barcode
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black)
            ) {
                Text("Save Line Item")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DenseTextSecondaryDark) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserDialog(
    onDismiss: () -> Unit,
    onConfirm: (user: UserAccountEntity, pass: String) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("password123") }
    var phone by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(UserRole.CREW) }
    var dept by remember { mutableStateOf(WorkDepartment.AUDIO) }
    var roleExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DenseSurfaceDark,
        title = { Text("Add Crew Member", color = LaserGreen, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Mobile Contact") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                ExposedDropdownMenuBox(expanded = roleExpanded, onExpandedChange = { roleExpanded = !roleExpanded }) {
                    OutlinedTextField(
                        value = role.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Role Tier") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = roleExpanded, onDismissRequest = { roleExpanded = false }, modifier = Modifier.background(DenseSurfaceDark)) {
                        UserRole.entries.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r.displayName, color = DenseTextPrimaryDark) },
                                onClick = {
                                    role = r
                                    roleExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank() && username.isNotBlank()) {
                        val dummyUser = UserAccountEntity(
                            id = "",
                            username = username.lowercase().trim(),
                            fullName = fullName.trim(),
                            email = email.trim(),
                            password = password,
                            role = role,
                            department = dept,
                            phone = phone.trim(),
                            initials = fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString("").take(2)
                        )
                        onConfirm(dummyUser, password)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LaserGreen, contentColor = Color.Black)
            ) {
                Text("Register Member")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DenseTextSecondaryDark) }
        }
    )
}

@Composable
fun EditUserDialog(
    user: UserAccountEntity,
    onDismiss: () -> Unit,
    onConfirm: (UserAccountEntity) -> Unit
) {
    var fullName by remember { mutableStateOf(user.fullName) }
    var email by remember { mutableStateOf(user.email) }
    var phone by remember { mutableStateOf(user.phone) }
    var password by remember { mutableStateOf(user.password) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DenseSurfaceDark,
        title = { Text("Edit User Profile: ${user.username}", color = CyanNeon, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Reset Password") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(user.copy(fullName = fullName, email = email, phone = phone, password = password))
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black)
            ) {
                Text("Save Profile")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DenseTextSecondaryDark) }
        }
    )
}

@Composable
fun AssignUserDialog(
    user: UserAccountEntity,
    eventName: String,
    onDismiss: () -> Unit,
    onConfirm: (position: String, callTime: String) -> Unit
) {
    var position by remember { mutableStateOf(when (user.department) {
        WorkDepartment.AUDIO -> "FOH Audio Engineer"
        WorkDepartment.LIGHTING -> "Lighting Board Operator"
        WorkDepartment.VIDEO -> "Master Video Switcher"
        WorkDepartment.RIGGING_POWER -> "Head Rigger"
        WorkDepartment.STAGE_HAND -> "Stage Hand / Cable Runner"
        WorkDepartment.PRODUCTION_MGMT -> "Stage Manager"
    }) }
    var callTime by remember { mutableStateOf("Call Time: 07:00 at Stage Loading Bay") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DenseSurfaceDark,
        title = { Text("Assign to '$eventName'", color = AmberConcert, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Assigning ${user.fullName} [${user.role.displayName}]", color = DenseTextPrimaryDark, fontSize = 11.sp)
                OutlinedTextField(value = position, onValueChange = { position = it }, label = { Text("Assigned Role / Position") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = callTime, onValueChange = { callTime = it }, label = { Text("Call-Time Dispatch Note") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(position, callTime) },
                colors = ButtonDefaults.buttonColors(containerColor = AmberConcert, contentColor = Color.Black)
            ) {
                Text("Dispatch Assignment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DenseTextSecondaryDark) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLogDialog(
    eventId: String,
    author: String,
    onDismiss: () -> Unit,
    onConfirm: (zone: String, type: LogType, content: String) -> Unit
) {
    var zone by remember { mutableStateOf("Stage Center") }
    var logType by remember { mutableStateOf(LogType.MILESTONE) }
    var content by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DenseSurfaceDark,
        title = { Text("Post Production Log Entry", color = CyanNeon, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = zone, onValueChange = { zone = it }, label = { Text("Zone / Area") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = !typeExpanded }) {
                    OutlinedTextField(
                        value = logType.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Log Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }, modifier = Modifier.background(DenseSurfaceDark)) {
                        LogType.entries.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t.displayName, color = DenseTextPrimaryDark) },
                                onClick = {
                                    logType = t
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Log Details & Observables *") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = { if (content.isNotBlank()) onConfirm(zone, logType, content) },
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black)
            ) {
                Text("Post Log")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DenseTextSecondaryDark) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendDispatchDialog(
    eventId: String,
    sender: String,
    onDismiss: () -> Unit,
    onConfirm: (priority: NotificationPriority, title: String, message: String) -> Unit
) {
    var priority by remember { mutableStateOf(NotificationPriority.CRITICAL) }
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var prioExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DenseSurfaceDark,
        title = { Text("Broadcast Team Alert", color = CrimsonAlert, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(expanded = prioExpanded, onExpandedChange = { prioExpanded = !prioExpanded }) {
                    OutlinedTextField(
                        value = priority.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Alert Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = prioExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = prioExpanded, onDismissRequest = { prioExpanded = false }, modifier = Modifier.background(DenseSurfaceDark)) {
                        NotificationPriority.entries.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.displayName, color = DenseTextPrimaryDark) },
                                onClick = {
                                    priority = p
                                    prioExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Alert Headline *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Urgent Message to Crew *") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank() && message.isNotBlank()) onConfirm(priority, title, message) },
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert, contentColor = Color.White)
            ) {
                Text("Broadcast Live Alert")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DenseTextSecondaryDark) }
        }
    )
}

@Composable
fun KpiStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Black, color = color)
        Text(label, fontSize = 8.sp, color = DenseTextSecondaryDark)
    }
}
