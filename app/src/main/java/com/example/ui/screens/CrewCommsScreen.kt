package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.TeamNotificationEntity
import com.example.model.NotificationPriority
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.AmberConcert
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DenseBackgroundDark
import com.example.ui.theme.DenseBorderDark
import com.example.ui.theme.DenseBorderSubtleDark
import com.example.ui.theme.DenseSurfaceDark
import com.example.ui.theme.DenseSurfaceElevatedDark
import com.example.ui.theme.DenseSurfaceHighlightDark
import com.example.ui.theme.DenseTextPrimaryDark
import com.example.ui.theme.DenseTextSecondaryDark
import com.example.ui.theme.LaserGreen
import com.example.ui.theme.LaserGreenGlow
import com.example.viewmodel.AvlViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CrewCommsScreen(
    viewModel: AvlViewModel,
    modifier: Modifier = Modifier
) {
    val selectedEvent by viewModel.selectedEvent.collectAsStateWithLifecycle()
    val notifications by viewModel.currentNotifications.collectAsStateWithLifecycle()

    var selectedPriorityFilter by remember { mutableStateOf<NotificationPriority?>(null) }
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var notificationToDelete by remember { mutableStateOf<TeamNotificationEntity?>(null) }

    val filteredNotifs = notifications.filter {
        selectedPriorityFilter == null || it.priority == selectedPriorityFilter
    }

    val unacknowledgedCount = notifications.count { !it.isAcknowledged }

    if (selectedEvent == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Please select an event to view crew notifications.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(2.dp))
                // Comms Header Banner
                Card(
                    colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Campaign,
                                        contentDescription = null,
                                        tint = CyanNeon,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "LIVE CREW DISPATCH & ALERTS",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = DenseTextPrimaryDark,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                Text(
                                    text = "Real-time updates for audio, video, lighting, and stage hands",
                                    fontSize = 11.sp,
                                    color = DenseTextSecondaryDark
                                )
                            }

                            if (unacknowledgedCount > 0) {
                                Surface(
                                    color = CrimsonAlert.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonAlert)
                                ) {
                                    Text(
                                        text = "$unacknowledgedCount NEW",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = CrimsonAlert,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Trigger Simulator Button
                        OutlinedButton(
                            onClick = { viewModel.simulateIncomingCrewNotification() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanNeon),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                                .testTag("btn_simulate_notif")
                        ) {
                            Icon(Icons.Default.Sensors, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("SIMULATE LIVE INCOMING CREW ALERT", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.3.sp)
                        }
                    }
                }
            }

            // Priority Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(vertical = 1.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedPriorityFilter == null,
                            onClick = { selectedPriorityFilter = null },
                            label = { Text("All (${notifications.size})", fontSize = 11.sp) },
                            shape = RoundedCornerShape(4.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanNeon,
                                selectedLabelColor = DenseBackgroundDark,
                                containerColor = DenseSurfaceDark,
                                labelColor = DenseTextPrimaryDark
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedPriorityFilter == null,
                                borderColor = DenseBorderDark,
                                selectedBorderColor = CyanNeon
                            )
                        )
                    }
                    items(NotificationPriority.entries) { priority ->
                        val count = notifications.count { it.priority == priority }
                        FilterChip(
                            selected = selectedPriorityFilter == priority,
                            onClick = { selectedPriorityFilter = priority },
                            label = { Text("${priority.iconEmoji} ${priority.displayName} ($count)", fontSize = 11.sp) },
                            shape = RoundedCornerShape(4.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanNeon,
                                selectedLabelColor = DenseBackgroundDark,
                                containerColor = DenseSurfaceDark,
                                labelColor = DenseTextPrimaryDark
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedPriorityFilter == priority,
                                borderColor = DenseBorderDark,
                                selectedBorderColor = CyanNeon
                            )
                        )
                    }
                }
            }

            // Notifications List
            if (filteredNotifs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No broadcast notifications in this channel.",
                            color = DenseTextSecondaryDark,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                items(filteredNotifs, key = { it.id }) { notif ->
                    CrewNotificationCard(
                        notification = notif,
                        onAcknowledge = { viewModel.acknowledgeNotification(notif.id) },
                        onDelete = { notificationToDelete = notif }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Broadcast FAB
        FloatingActionButton(
            onClick = { showBroadcastDialog = true },
            containerColor = CyanNeon,
            contentColor = DenseBackgroundDark,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(14.dp)
                .testTag("broadcast_msg_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Broadcast New Team Notification")
        }
    }

    if (showBroadcastDialog) {
        BroadcastNotificationDialog(
            defaultSender = viewModel.currentTechName.value,
            onDismiss = { showBroadcastDialog = false },
            onSend = { sender, priority, title, message ->
                selectedEvent?.id?.let { evtId ->
                    viewModel.sendTeamNotification(evtId, sender, priority, title, message)
                }
                showBroadcastDialog = false
            }
        )
    }

    notificationToDelete?.let { notif ->
        AlertDialog(
            onDismissRequest = { notificationToDelete = null },
            title = { Text("Delete Notification?", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = { Text("Delete notification '${notif.title}'?", fontSize = 12.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteNotification(notif.id)
                        notificationToDelete = null
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { notificationToDelete = null }) {
                    Text("Cancel", fontSize = 12.sp)
                }
            }
        )
    }
}

@Composable
fun CrewNotificationCard(
    notification: TeamNotificationEntity,
    onAcknowledge: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormat = remember { SimpleDateFormat("hh:mm a • MMM dd", Locale.getDefault()) }
    val formattedTime = remember(notification.timestamp) { timeFormat.format(Date(notification.timestamp)) }

    val isCritical = notification.priority == NotificationPriority.CRITICAL
    val borderStroke = when {
        isCritical && !notification.isAcknowledged -> androidx.compose.foundation.BorderStroke(1.dp, CrimsonAlert)
        notification.isAcknowledged -> androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark)
        else -> androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isCritical && !notification.isAcknowledged) Color(0xFF281017) else DenseSurfaceDark
        ),
        shape = RoundedCornerShape(6.dp),
        border = borderStroke,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PriorityBadge(priority = notification.priority)
                    Text(
                        text = "From: ${notification.sender}",
                        fontSize = 11.sp,
                        color = CyanNeon,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formattedTime,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = DenseTextSecondaryDark
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete notification",
                            tint = DenseTextSecondaryDark.copy(alpha = 0.4f),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = notification.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = DenseTextPrimaryDark
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = notification.message,
                fontSize = 12.sp,
                color = DenseTextSecondaryDark,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Acknowledgment Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (notification.isAcknowledged) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = LaserGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Acknowledged by ${notification.acknowledgedBy}",
                            fontSize = 10.sp,
                            color = LaserGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Button(
                        onClick = onAcknowledge,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCritical) CrimsonAlert else CyanNeon,
                            contentColor = DenseBackgroundDark
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("ack_notif_btn_${notification.id}")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("ACKNOWLEDGE", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.3.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BroadcastNotificationDialog(
    defaultSender: String,
    onDismiss: () -> Unit,
    onSend: (sender: String, priority: NotificationPriority, title: String, message: String) -> Unit
) {
    var sender by remember { mutableStateOf(defaultSender) }
    var selectedPriority by remember { mutableStateOf(NotificationPriority.ALERT) }
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var priorityDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Broadcast Team Notification", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(
                    value = sender,
                    onValueChange = { sender = it },
                    label = { Text("Sender Name & Role") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = DenseBorderDark,
                        focusedContainerColor = DenseSurfaceDark,
                        unfocusedContainerColor = DenseSurfaceDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = priorityDropdownExpanded,
                    onExpandedChange = { priorityDropdownExpanded = !priorityDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = "${selectedPriority.iconEmoji} ${selectedPriority.displayName}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Notification Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = DenseBorderDark,
                            focusedContainerColor = DenseSurfaceDark,
                            unfocusedContainerColor = DenseSurfaceDark
                        ),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = priorityDropdownExpanded,
                        onDismissRequest = { priorityDropdownExpanded = false }
                    ) {
                        NotificationPriority.entries.forEach { priority ->
                            DropdownMenuItem(
                                text = { Text("${priority.iconEmoji} ${priority.displayName}") },
                                onClick = {
                                    selectedPriority = priority
                                    priorityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Subject / Headline") },
                    placeholder = { Text("e.g. Load-In Gate Changed, Stage Power Down") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = DenseBorderDark,
                        focusedContainerColor = DenseSurfaceDark,
                        unfocusedContainerColor = DenseSurfaceDark
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("broadcast_title_input")
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Broadcast Message to Crew") },
                    placeholder = { Text("e.g. Forklift arriving at Bay 2. Clear all audio trunk cases...") },
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = DenseBorderDark,
                        focusedContainerColor = DenseSurfaceDark,
                        unfocusedContainerColor = DenseSurfaceDark
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("broadcast_msg_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && message.isNotBlank()) {
                        onSend(sender, selectedPriority, title, message)
                    }
                },
                enabled = title.isNotBlank() && message.isNotBlank(),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = DenseBackgroundDark),
                modifier = Modifier.testTag("confirm_send_broadcast_btn")
            ) {
                Text("Send Broadcast", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontSize = 12.sp)
            }
        }
    )
}
