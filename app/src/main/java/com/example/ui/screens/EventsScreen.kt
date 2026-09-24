package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ProductionEventEntity
import com.example.model.EventStatus
import com.example.ui.components.EventStatusBadge
import com.example.ui.components.ProgressStatsCard
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
import com.example.ui.theme.LaserGreen
import com.example.ui.theme.OrangeDamaged
import com.example.viewmodel.AvlViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EventsScreen(
    viewModel: AvlViewModel,
    onNavigateToChecklist: () -> Unit,
    modifier: Modifier = Modifier
) {
    val events by viewModel.allEvents.collectAsStateWithLifecycle()
    val selectedId by viewModel.selectedEventId.collectAsStateWithLifecycle()
    val currentItems by viewModel.currentEquipment.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<ProductionEventEntity?>(null) }

    val totalTarget = currentItems.sumOf { it.targetQuantity }
    val totalPacked = currentItems.sumOf { it.packedQuantity }
    val totalReturned = currentItems.sumOf { it.returnedQuantity }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "AVL PRODUCTION GIGS & EVENTS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DenseTextPrimaryDark,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Select active gig to manage loadout, setup logs, and return checks.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 11.sp,
                    color = DenseTextSecondaryDark
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Quick Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ProgressStatsCard(
                        title = "Outbound Packing",
                        currentCount = totalPacked,
                        targetCount = totalTarget,
                        accentColor = AmberConcert,
                        modifier = Modifier.weight(1f)
                    )
                    ProgressStatsCard(
                        title = "Warehouse Return",
                        currentCount = totalReturned,
                        targetCount = totalPacked,
                        accentColor = LaserGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
            }

            // Events List
            items(events, key = { it.id }) { event ->
                val isSelected = event.id == selectedId
                EventCard(
                    event = event,
                    isSelected = isSelected,
                    onSelect = {
                        viewModel.selectEvent(event.id)
                        onNavigateToChecklist()
                    },
                    onStatusChange = { newStatus ->
                        viewModel.updateEventStatus(event.id, newStatus)
                    },
                    onDelete = { eventToDelete = event }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating Action Button to Add Event
        FloatingActionButton(
            onClick = { showCreateDialog = true },
            containerColor = CyanNeon,
            contentColor = DenseBackgroundDark,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(14.dp)
                .testTag("add_event_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create New AVL Event")
        }
    }

    if (showCreateDialog) {
        CreateEventDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, venue, loc, lead, truck, date, notes, pack ->
                viewModel.createEvent(name, venue, loc, lead, truck, date, notes, pack)
                showCreateDialog = false
            }
        )
    }

    eventToDelete?.let { event ->
        AlertDialog(
            onDismissRequest = { eventToDelete = null },
            title = { Text("Delete Event?", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = { Text("Are you sure you want to delete '${event.name}' and all associated checklists and logs?", fontSize = 12.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEvent(event.id)
                        eventToDelete = null
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { eventToDelete = null }) {
                    Text("Cancel", fontSize = 12.sp)
                }
            }
        )
    }
}

@Composable
fun EventCard(
    event: ProductionEventEntity,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onStatusChange: (EventStatus) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault()) }
    val formattedDate = remember(event.eventDate) { dateFormat.format(Date(event.eventDate)) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DenseSurfaceHighlightDark else DenseSurfaceDark
        ),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) CyanNeon else DenseBorderDark
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .testTag("event_card_${event.id}")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DenseTextPrimaryDark
                    )
                    Text(
                        text = event.clientOrVenue,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 11.sp,
                        color = CyanNeon,
                        fontWeight = FontWeight.Medium
                    )
                }
                EventStatusBadge(status = event.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Metadata Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = DenseTextSecondaryDark,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = formattedDate,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = DenseTextSecondaryDark
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = DenseTextSecondaryDark,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = event.leadTech,
                        fontSize = 10.sp,
                        color = DenseTextSecondaryDark
                    )
                }
            }

            if (event.truckOrVehicle.isNotBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint = AmberConcert,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = event.truckOrVehicle,
                        fontSize = 10.sp,
                        color = AmberConcert
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Status Advance & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (event.status != EventStatus.COMPLETED_RECONCILED) {
                        val nextStatus = EventStatus.entries.getOrNull(event.status.stepIndex + 1)
                        if (nextStatus != null) {
                            OutlinedButton(
                                onClick = { onStatusChange(nextStatus) },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = CyanNeon
                                ),
                                shape = RoundedCornerShape(4.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.6f)),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text(
                                    text = "➔ ${nextStatus.displayName}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.3.sp
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Event",
                        tint = DenseTextSecondaryDark.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CreateEventDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, venue: String, location: String, lead: String, truck: String, date: Long, notes: String, withPreset: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Main Arena / Dock 1") }
    var leadTech by remember { mutableStateOf("Dave Miller (Audio Lead)") }
    var truck by remember { mutableStateOf("Truck 01 (53ft Semi)") }
    var notes by remember { mutableStateOf("") }
    var includePresets by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Create New AVL Production Gig",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = DenseTextPrimaryDark
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Event / Show Name") },
                    placeholder = { Text("e.g. Summer Rock Fest 2026") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = DenseBorderDark,
                        focusedContainerColor = DenseSurfaceDark,
                        unfocusedContainerColor = DenseSurfaceDark
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_event_name")
                )

                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Venue / Client") },
                    placeholder = { Text("e.g. Paramount Theater") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = DenseBorderDark,
                        focusedContainerColor = DenseSurfaceDark,
                        unfocusedContainerColor = DenseSurfaceDark
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_venue_name")
                )

                OutlinedTextField(
                    value = leadTech,
                    onValueChange = { leadTech = it },
                    label = { Text("Lead Engineer / Tech") },
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
                    value = truck,
                    onValueChange = { truck = it },
                    label = { Text("Assigned Transport / Truck") },
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
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Production Notes / Power Specs") },
                    placeholder = { Text("e.g. FOH distance, 3-phase Camlock, rigging limits") },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = DenseBorderDark,
                        focusedContainerColor = DenseSurfaceDark,
                        unfocusedContainerColor = DenseSurfaceDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { includePresets = !includePresets }
                ) {
                    Checkbox(
                        checked = includePresets,
                        onCheckedChange = { includePresets = it },
                        colors = CheckboxDefaults.colors(checkedColor = CyanNeon)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Auto-populate standard AVL gear checklist (Audio, Video, Lighting, Rigging, Power)",
                        fontSize = 11.sp,
                        color = DenseTextSecondaryDark
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(
                            name,
                            venue.ifBlank { "TBD Venue" },
                            location,
                            leadTech,
                            truck,
                            System.currentTimeMillis() + 86400000L,
                            notes,
                            includePresets
                        )
                    }
                },
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = DenseBackgroundDark),
                modifier = Modifier.testTag("btn_confirm_create_event")
            ) {
                Text("Create Gig", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontSize = 12.sp)
            }
        }
    )
}
