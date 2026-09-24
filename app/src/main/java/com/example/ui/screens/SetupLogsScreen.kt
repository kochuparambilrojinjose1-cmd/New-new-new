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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
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
import com.example.data.local.SetupLogEntity
import com.example.model.LogType
import com.example.ui.components.LogTypeBadge
import com.example.ui.theme.AmberConcert
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DenseBackgroundDark
import com.example.ui.theme.DenseBorderDark
import com.example.ui.theme.DenseBorderSubtleDark
import com.example.ui.theme.DenseSurfaceDark
import com.example.ui.theme.DenseSurfaceElevatedDark
import com.example.ui.theme.DenseSurfaceHighlightDark
import com.example.ui.theme.DenseTextPrimaryDark
import com.example.ui.theme.DenseTextSecondaryDark
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.LaserGreen
import com.example.viewmodel.AvlViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SetupLogsScreen(
    viewModel: AvlViewModel,
    modifier: Modifier = Modifier
) {
    val selectedEvent by viewModel.selectedEvent.collectAsStateWithLifecycle()
    val logs by viewModel.filteredLogs.collectAsStateWithLifecycle()
    val allLogs by viewModel.currentSetupLogs.collectAsStateWithLifecycle()
    val selectedTypeFilter by viewModel.selectedLogTypeFilter.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var logToDelete by remember { mutableStateOf<SetupLogEntity?>(null) }

    if (selectedEvent == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Please select an event to view setup logs.",
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ON-SITE SETUP & TECH LOGS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DenseTextPrimaryDark,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Log soundchecks, voltage tests, rigging safety, and issues.",
                            fontSize = 11.sp,
                            color = DenseTextSecondaryDark
                        )
                    }
                    Surface(
                        color = DenseSurfaceDark,
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark)
                    ) {
                        Text(
                            text = "${allLogs.size} ENTRIES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = CyanNeon,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                        )
                    }
                }
            }

            // Quick Preset Bar
            item {
                Text(
                    text = "QUICK PRESETS:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp,
                    color = DenseTextSecondaryDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(vertical = 1.dp)
                ) {
                    item {
                        PresetLogChip(
                            emoji = "🔊",
                            label = "Soundcheck OK",
                            onClick = {
                                selectedEvent?.id?.let { evtId ->
                                    viewModel.addSetupLog(
                                        eventId = evtId,
                                        author = viewModel.currentTechName.value,
                                        zone = "FOH",
                                        type = LogType.SOUNDCHECK,
                                        content = "FOH soundcheck and monitor mix lines calibrated. All RF mic channels clear of intermodulation."
                                    )
                                }
                            }
                        )
                    }
                    item {
                        PresetLogChip(
                            emoji = "⚡",
                            label = "Voltage Verified",
                            onClick = {
                                selectedEvent?.id?.let { evtId ->
                                    viewModel.addSetupLog(
                                        eventId = evtId,
                                        author = viewModel.currentTechName.value,
                                        zone = "Power Distro",
                                        type = LogType.VOLTAGE_CHECK,
                                        content = "Camlock 3-phase power distro verified: 120V L-N balanced, 208V L-L, ground impedance certified."
                                    )
                                }
                            }
                        )
                    }
                    item {
                        PresetLogChip(
                            emoji = "📺",
                            label = "Video Wall Sync",
                            onClick = {
                                selectedEvent?.id?.let { evtId ->
                                    viewModel.addSetupLog(
                                        eventId = evtId,
                                        author = viewModel.currentTechName.value,
                                        zone = "Video Control",
                                        type = LogType.MILESTONE,
                                        content = "LED processor mapping confirmed, test pattern raster locked at 4K60, Genlock signal locked."
                                    )
                                }
                            }
                        )
                    }
                    item {
                        PresetLogChip(
                            emoji = "💡",
                            label = "Lighting DMX Homed",
                            onClick = {
                                selectedEvent?.id?.let { evtId ->
                                    viewModel.addSetupLog(
                                        eventId = evtId,
                                        author = viewModel.currentTechName.value,
                                        zone = "Lighting Desk",
                                        type = LogType.MILESTONE,
                                        content = "All moving lights homed, DMX universe patch verified, hazers and strobes tested."
                                    )
                                }
                            }
                        )
                    }
                    item {
                        PresetLogChip(
                            emoji = "🏗️",
                            label = "Rigging Inspection",
                            onClick = {
                                selectedEvent?.id?.let { evtId ->
                                    viewModel.addSetupLog(
                                        eventId = evtId,
                                        author = viewModel.currentTechName.value,
                                        zone = "Rigging Grid",
                                        type = LogType.SIGN_OFF,
                                        content = "All motor shackles mouse-wired, secondary safety cables secured, load limits checked."
                                    )
                                }
                            }
                        )
                    }
                }
            }

            // Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(vertical = 1.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedTypeFilter == null,
                            onClick = { viewModel.selectedLogTypeFilter.value = null },
                            label = { Text("All Logs", fontSize = 11.sp) },
                            shape = RoundedCornerShape(4.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanNeon,
                                selectedLabelColor = DenseBackgroundDark,
                                containerColor = DenseSurfaceDark,
                                labelColor = DenseTextPrimaryDark
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedTypeFilter == null,
                                borderColor = DenseBorderDark,
                                selectedBorderColor = CyanNeon
                            )
                        )
                    }
                    items(LogType.entries) { type ->
                        FilterChip(
                            selected = selectedTypeFilter == type,
                            onClick = { viewModel.selectedLogTypeFilter.value = type },
                            label = { Text("${type.iconEmoji} ${type.displayName}", fontSize = 11.sp) },
                            shape = RoundedCornerShape(4.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanNeon,
                                selectedLabelColor = DenseBackgroundDark,
                                containerColor = DenseSurfaceDark,
                                labelColor = DenseTextPrimaryDark
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedTypeFilter == type,
                                borderColor = DenseBorderDark,
                                selectedBorderColor = CyanNeon
                            )
                        )
                    }
                }
            }

            // Logs List
            if (logs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No setup logs recorded for this category yet.",
                            color = DenseTextSecondaryDark,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                items(logs, key = { it.id }) { log ->
                    SetupLogCard(
                        log = log,
                        onDelete = { logToDelete = log }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = ElectricViolet,
            contentColor = Color.White,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(14.dp)
                .testTag("add_setup_log_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Setup Log Entry")
        }
    }

    if (showAddDialog) {
        AddSetupLogDialog(
            defaultAuthor = viewModel.currentTechName.value,
            onDismiss = { showAddDialog = false },
            onAdd = { author, zone, type, content ->
                selectedEvent?.id?.let { evtId ->
                    viewModel.addSetupLog(evtId, author, zone, type, content)
                }
                showAddDialog = false
            }
        )
    }

    logToDelete?.let { log ->
        AlertDialog(
            onDismissRequest = { logToDelete = null },
            title = { Text("Delete Log Entry?", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = { Text("Delete this setup log record from the event history?", fontSize = 12.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSetupLog(log.id)
                        logToDelete = null
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { logToDelete = null }) {
                    Text("Cancel", fontSize = 12.sp)
                }
            }
        )
    }
}

@Composable
fun PresetLogChip(
    emoji: String,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = DenseSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
        modifier = Modifier
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
        ) {
            Text(emoji, fontSize = 11.sp)
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = DenseTextPrimaryDark
            )
        }
    }
}

@Composable
fun SetupLogCard(
    log: SetupLogEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormat = remember { SimpleDateFormat("hh:mm a • MMM dd", Locale.getDefault()) }
    val formattedTime = remember(log.timestamp) { timeFormat.format(Date(log.timestamp)) }

    Card(
        colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
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
                    LogTypeBadge(logType = log.logType)
                    Surface(
                        color = DenseSurfaceHighlightDark,
                        shape = RoundedCornerShape(3.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark)
                    ) {
                        Text(
                            text = "📍 ${log.zone}",
                            fontSize = 10.sp,
                            color = CyanNeon,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(22.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete log",
                        tint = DenseTextSecondaryDark.copy(alpha = 0.5f),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = log.content,
                fontSize = 12.sp,
                color = DenseTextPrimaryDark,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👤 ${log.author}",
                    fontSize = 10.sp,
                    color = DenseTextSecondaryDark,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formattedTime,
                    fontSize = 10.sp,
                    color = DenseTextSecondaryDark,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSetupLogDialog(
    defaultAuthor: String,
    onDismiss: () -> Unit,
    onAdd: (author: String, zone: String, type: LogType, content: String) -> Unit
) {
    var author by remember { mutableStateOf(defaultAuthor) }
    var zone by remember { mutableStateOf("FOH Mix Position") }
    var selectedType by remember { mutableStateOf(LogType.INFO) }
    var content by remember { mutableStateOf("") }
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Setup / Tech Log Entry", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Logged By (Tech Name/Role)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = DenseBorderDark,
                        focusedContainerColor = DenseSurfaceDark,
                        unfocusedContainerColor = DenseSurfaceDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = zone,
                    onValueChange = { zone = it },
                    label = { Text("Stage Zone / Location") },
                    placeholder = { Text("e.g. FOH, Stage Left, Grid, Video Control, Truck") },
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
                    expanded = typeDropdownExpanded,
                    onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = "${selectedType.iconEmoji} ${selectedType.displayName}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Log Classification") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = DenseBorderDark,
                            focusedContainerColor = DenseSurfaceDark,
                            unfocusedContainerColor = DenseSurfaceDark
                        ),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeDropdownExpanded,
                        onDismissRequest = { typeDropdownExpanded = false }
                    ) {
                        LogType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text("${type.iconEmoji} ${type.displayName}") },
                                onClick = {
                                    selectedType = type
                                    typeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Log Details / Test Readings") },
                    placeholder = { Text("e.g. Dante clock synchronized, DMX universe 1 addressed, zero buzz in mains...") },
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = DenseBorderDark,
                        focusedContainerColor = DenseSurfaceDark,
                        unfocusedContainerColor = DenseSurfaceDark
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_log_content_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (content.isNotBlank()) {
                        onAdd(author, zone, selectedType, content)
                    }
                },
                enabled = content.isNotBlank(),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet, contentColor = Color.White),
                modifier = Modifier.testTag("confirm_add_log_btn")
            ) {
                Text("Save Log", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontSize = 12.sp)
            }
        }
    )
}
