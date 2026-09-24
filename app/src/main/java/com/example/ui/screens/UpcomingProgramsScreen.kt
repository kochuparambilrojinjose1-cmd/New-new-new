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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.example.data.local.EventAvailabilityEntity
import com.example.data.local.ProductionEventEntity
import com.example.data.local.UserAccountEntity
import com.example.model.EventStatus
import com.example.model.MemberAvailability
import com.example.model.UserRole
import com.example.ui.components.DepartmentBadge
import com.example.ui.components.EventStatusBadge
import com.example.ui.components.MemberAvailabilityBadge
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
import com.example.ui.theme.DenseTextPrimaryDark
import com.example.ui.theme.DenseTextSecondaryDark
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.LaserGreen
import com.example.viewmodel.AvlViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingProgramsScreen(
    viewModel: AvlViewModel,
    onNavigateToChecklist: () -> Unit,
    modifier: Modifier = Modifier
) {
    val events by viewModel.allEvents.collectAsStateWithLifecycle()
    val selectedEvent by viewModel.selectedEvent.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val availabilities by viewModel.currentEventAvailabilities.collectAsStateWithLifecycle()
    val myAvailability by viewModel.currentUserAvailability.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()

    var showCreateGigDialog by remember { mutableStateOf(false) }
    var showAssignDialogForUser by remember { mutableStateOf<EventAvailabilityEntity?>(null) }
    var showCallTimeBroadcastDialog by remember { mutableStateOf(false) }
    var showRsvpNoteDialog by remember { mutableStateOf(false) }
    var rsvpNoteInput by remember { mutableStateOf("") }

    val dateFormat = remember { SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault()) }

    val canManageRoster = currentUser?.role in listOf(UserRole.OWNER, UserRole.ADMIN, UserRole.MANAGER)

    val availableCount = availabilities.count { it.status == MemberAvailability.AVAILABLE }
    val tentativeCount = availabilities.count { it.status == MemberAvailability.TENTATIVE }
    val unavailableCount = availabilities.count { it.status == MemberAvailability.UNAVAILABLE }
    val assignedCount = availabilities.count { it.isAssigned }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            text = "UPCOMING PROGRAMS & ROSTER",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DenseTextPrimaryDark,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Member availability, call-time dispatches & crew assignment.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = DenseTextSecondaryDark
                        )
                    }

                    currentUser?.let { user ->
                        UserRoleBadge(role = user.role)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Upcoming Gigs Horizontal / List selector
            item {
                Text(
                    text = "SELECT UPCOMING PROGRAM",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanNeon,
                    letterSpacing = 0.4.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    events.take(4).forEach { evt ->
                        val isSelected = evt.id == selectedEvent?.id
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) DenseSurfaceHighlightDark else DenseSurfaceDark)
                                .border(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) CyanNeon else DenseBorderDark,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { viewModel.selectEvent(evt.id) }
                                .padding(6.dp)
                                .testTag("program_chip_${evt.id}")
                        ) {
                            Column {
                                Text(
                                    text = evt.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (isSelected) CyanNeon else DenseTextPrimaryDark,
                                    maxLines = 1
                                )
                                Text(
                                    text = evt.clientOrVenue,
                                    fontSize = 8.sp,
                                    color = DenseTextSecondaryDark,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                EventStatusBadge(status = evt.status)
                            }
                        }
                    }
                }
            }

            // Active Event Banner
            selectedEvent?.let { evt ->
                item {
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = evt.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "📍 ${evt.clientOrVenue} (${evt.location})",
                                        fontSize = 11.sp,
                                        color = CyanNeon
                                    )
                                    Text(
                                        text = "📅 Date: ${dateFormat.format(Date(evt.eventDate))} • Truck: ${evt.truckOrVehicle}",
                                        fontSize = 10.sp,
                                        color = DenseTextSecondaryDark,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                EventStatusBadge(status = evt.status)
                            }

                            if (evt.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(DenseSurfaceElevatedDark)
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        text = "Gig Notes: ${evt.notes}",
                                        fontSize = 10.sp,
                                        color = DenseTextSecondaryDark
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Action buttons for Event
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onNavigateToChecklist,
                                    shape = RoundedCornerShape(4.dp),
                                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                        contentColor = CyanNeon
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(32.dp)
                                ) {
                                    Text("Open Pack Manifest →", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                if (canManageRoster) {
                                    Button(
                                        onClick = { showCallTimeBroadcastDialog = true },
                                        shape = RoundedCornerShape(4.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AmberConcert, contentColor = DenseBackgroundDark),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(32.dp)
                                            .testTag("btn_broadcast_call_time")
                                    ) {
                                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Send Call Alert", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // -------------------------------------------------------------
                // MEMBER INTERACTIVE RSVP SECTION
                // -------------------------------------------------------------
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DenseSurfaceHighlightDark),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "YOUR AVAILABILITY RSVP",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = DenseTextPrimaryDark,
                                        letterSpacing = 0.4.sp
                                    )
                                    Text(
                                        text = "Are you available for this upcoming program?",
                                        fontSize = 9.sp,
                                        color = DenseTextSecondaryDark
                                    )
                                }

                                myAvailability?.let {
                                    MemberAvailabilityBadge(status = it.status)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // 3 Interactive Availability Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val currentStatus = myAvailability?.status ?: MemberAvailability.TENTATIVE

                                // Available Button
                                Button(
                                    onClick = {
                                        viewModel.setAvailabilityForEvent(
                                            evt.id,
                                            MemberAvailability.AVAILABLE,
                                            myAvailability?.note ?: ""
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentStatus == MemberAvailability.AVAILABLE) LaserGreen else DenseSurfaceElevatedDark,
                                        contentColor = if (currentStatus == MemberAvailability.AVAILABLE) DenseBackgroundDark else LaserGreen
                                    ),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .testTag("rsvp_available")
                                ) {
                                    Text("🟢 Available", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                // Tentative Button
                                Button(
                                    onClick = {
                                        viewModel.setAvailabilityForEvent(
                                            evt.id,
                                            MemberAvailability.TENTATIVE,
                                            myAvailability?.note ?: ""
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentStatus == MemberAvailability.TENTATIVE) AmberConcert else DenseSurfaceElevatedDark,
                                        contentColor = if (currentStatus == MemberAvailability.TENTATIVE) DenseBackgroundDark else AmberConcert
                                    ),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .testTag("rsvp_tentative")
                                ) {
                                    Text("🟡 Tentative", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                // Unavailable Button
                                Button(
                                    onClick = {
                                        viewModel.setAvailabilityForEvent(
                                            evt.id,
                                            MemberAvailability.UNAVAILABLE,
                                            myAvailability?.note ?: ""
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentStatus == MemberAvailability.UNAVAILABLE) CrimsonAlert else DenseSurfaceElevatedDark,
                                        contentColor = if (currentStatus == MemberAvailability.UNAVAILABLE) Color.White else CrimsonAlert
                                    ),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .testTag("rsvp_unavailable")
                                ) {
                                    Text("🔴 Unavailable", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // RSVP Note or assigned position preview
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (myAvailability?.note?.isNotBlank() == true) "Note: ${myAvailability?.note}" else "No notes attached",
                                    fontSize = 10.sp,
                                    color = DenseTextSecondaryDark
                                )

                                TextButton(
                                    onClick = {
                                        rsvpNoteInput = myAvailability?.note ?: ""
                                        showRsvpNoteDialog = true
                                    },
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Text("Add Note ✍️", fontSize = 10.sp, color = CyanNeon)
                                }
                            }

                            if (myAvailability?.isAssigned == true) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF072738))
                                        .border(1.dp, CyanNeon, RoundedCornerShape(4.dp))
                                        .padding(6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AssignmentInd, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = "ASSIGNED POSITION: ${myAvailability?.assignedPosition}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = CyanNeon
                                            )
                                            if (myAvailability?.callTimeNote?.isNotBlank() == true) {
                                                Text(
                                                    text = "Call Time: ${myAvailability?.callTimeNote}",
                                                    fontSize = 10.sp,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // -------------------------------------------------------------
                // TEAM ROSTER & AVAILABILITY TABLE
                // -------------------------------------------------------------
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TEAM ROSTER & MEMBER AVAILABILITY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DenseTextPrimaryDark,
                            letterSpacing = 0.4.sp
                        )

                        // Quick Summary Badges
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("🟢 $availableCount", fontSize = 10.sp, color = LaserGreen, fontWeight = FontWeight.Bold)
                            Text("🟡 $tentativeCount", fontSize = 10.sp, color = AmberConcert, fontWeight = FontWeight.Bold)
                            Text("🔴 $unavailableCount", fontSize = 10.sp, color = CrimsonAlert, fontWeight = FontWeight.Bold)
                            Text("🔵 $assignedCount Assigned", fontSize = 10.sp, color = CyanNeon, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                items(availabilities, key = { it.id }) { member ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (member.userId == currentUser?.id) DenseSurfaceHighlightDark else DenseSurfaceDark
                        ),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            if (member.isAssigned) 1.dp else 0.5.dp,
                            if (member.isAssigned) CyanNeon else DenseBorderSubtleDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(if (member.isAssigned) CyanNeon else Color(0xFF2C3540)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = member.userName.take(1),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (member.isAssigned) DenseBackgroundDark else Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = member.userName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = DenseTextPrimaryDark
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            UserRoleBadge(role = member.userRole)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            DepartmentBadge(department = member.department)
                                            if (member.note.isNotBlank()) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("• ${member.note}", fontSize = 9.sp, color = DenseTextSecondaryDark, maxLines = 1)
                                            }
                                        }
                                    }
                                }

                                MemberAvailabilityBadge(status = member.status)
                            }

                            // Roster Assignment details & actions
                            if (member.isAssigned) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color(0xFF0F1E29))
                                        .padding(horizontal = 6.dp, vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Call Role: ", fontSize = 9.sp, color = DenseTextSecondaryDark)
                                        Text(member.assignedPosition, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyanNeon)
                                        if (member.callTimeNote.isNotBlank()) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("(${member.callTimeNote})", fontSize = 9.sp, color = Color(0xFFFFCC80))
                                        }
                                    }

                                    if (canManageRoster) {
                                        TextButton(
                                            onClick = { viewModel.unassignCrewMember(evt.id, member.userId) },
                                            modifier = Modifier.height(20.dp)
                                        ) {
                                            Text("Unassign", fontSize = 8.sp, color = CrimsonAlert)
                                        }
                                    }
                                }
                            } else if (canManageRoster && member.status == MemberAvailability.AVAILABLE) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = { showAssignDialogForUser = member },
                                        modifier = Modifier.height(22.dp)
                                    ) {
                                        Text("+ Assign Role & Call Time", fontSize = 9.sp, color = CyanNeon, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            } ?: run {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No event selected. Please choose or create an upcoming program.", color = DenseTextSecondaryDark, fontSize = 12.sp)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating Action Button to Create New Program (Available to Manager / Admin / Owner)
        if (canManageRoster) {
            FloatingActionButton(
                onClick = { showCreateGigDialog = true },
                containerColor = CyanNeon,
                contentColor = DenseBackgroundDark,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(14.dp)
                    .testTag("fab_create_upcoming_gig")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create New Upcoming Program")
            }
        }
    }

    // -------------------------------------------------------------
    // DIALOG: CREATE NEW GIG / UPCOMING PROGRAM
    // -------------------------------------------------------------
    if (showCreateGigDialog) {
        CreateEventDialog(
            onDismiss = { showCreateGigDialog = false },
            onCreate = { name, venue, loc, lead, truck, date, notes, pack ->
                viewModel.createEvent(name, venue, loc, lead, truck, date, notes, pack)
                showCreateGigDialog = false
            }
        )
    }

    // -------------------------------------------------------------
    // DIALOG: ASSIGN CREW POSITION & CALL TIME
    // -------------------------------------------------------------
    showAssignDialogForUser?.let { targetMember ->
        var positionInput by remember { mutableStateOf("Lead A1 Audio") }
        var callTimeInput by remember { mutableStateOf("Call: 08:00 AM Dock") }

        AlertDialog(
            onDismissRequest = { showAssignDialogForUser = null },
            title = { Text("Assign Crew Member", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Assign ${targetMember.userName} (${targetMember.userRole.displayName} • ${targetMember.department.displayName}) to ${selectedEvent?.name}",
                        fontSize = 11.sp,
                        color = DenseTextSecondaryDark
                    )

                    OutlinedTextField(
                        value = positionInput,
                        onValueChange = { positionInput = it },
                        label = { Text("Position / Gig Role") },
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
                        value = callTimeInput,
                        onValueChange = { callTimeInput = it },
                        label = { Text("Call Time & Staging Location") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = DenseBorderDark,
                            focusedContainerColor = DenseSurfaceDark,
                            unfocusedContainerColor = DenseSurfaceDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val eventId = selectedEvent?.id
                        if (eventId != null) {
                            viewModel.assignCrewMember(eventId, targetMember.userId, positionInput, callTimeInput)
                        }
                        showAssignDialogForUser = null
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = DenseBackgroundDark)
                ) {
                    Text("Confirm Assignment", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAssignDialogForUser = null }) {
                    Text("Cancel", fontSize = 11.sp)
                }
            }
        )
    }

    // -------------------------------------------------------------
    // DIALOG: BROADCAST ON-TIME CALL REMINDER
    // -------------------------------------------------------------
    if (showCallTimeBroadcastDialog) {
        var callReminderNote by remember { mutableStateOf("08:00 AM Call Time @ Venue Loading Dock. All assigned crew report for truss build.") }

        AlertDialog(
            onDismissRequest = { showCallTimeBroadcastDialog = false },
            title = { Text("⏰ Broadcast Call Time Alert", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Trigger an on-time push alert and system notification to all crew members assigned to '${selectedEvent?.name}'.",
                        fontSize = 11.sp,
                        color = DenseTextSecondaryDark
                    )

                    OutlinedTextField(
                        value = callReminderNote,
                        onValueChange = { callReminderNote = it },
                        label = { Text("Call Time Instructions") },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberConcert,
                            unfocusedBorderColor = DenseBorderDark,
                            focusedContainerColor = DenseSurfaceDark,
                            unfocusedContainerColor = DenseSurfaceDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedEvent?.id?.let { evtId ->
                            viewModel.triggerCallTimeReminder(evtId, callReminderNote)
                        }
                        showCallTimeBroadcastDialog = false
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberConcert, contentColor = DenseBackgroundDark)
                ) {
                    Text("Broadcast Alert Now", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCallTimeBroadcastDialog = false }) {
                    Text("Cancel", fontSize = 11.sp)
                }
            }
        )
    }

    // -------------------------------------------------------------
    // DIALOG: RSVP NOTE INPUT
    // -------------------------------------------------------------
    if (showRsvpNoteDialog) {
        AlertDialog(
            onDismissRequest = { showRsvpNoteDialog = false },
            title = { Text("Add RSVP Note", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
            text = {
                OutlinedTextField(
                    value = rsvpNoteInput,
                    onValueChange = { rsvpNoteInput = it },
                    label = { Text("Availability details (e.g. available after 1pm, can do A1/L1)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = DenseBorderDark,
                        focusedContainerColor = DenseSurfaceDark,
                        unfocusedContainerColor = DenseSurfaceDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedEvent?.id?.let { evtId ->
                            val currentStatus = myAvailability?.status ?: MemberAvailability.AVAILABLE
                            viewModel.setAvailabilityForEvent(evtId, currentStatus, rsvpNoteInput)
                        }
                        showRsvpNoteDialog = false
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = DenseBackgroundDark)
                ) {
                    Text("Save Note", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRsvpNoteDialog = false }) {
                    Text("Cancel", fontSize = 11.sp)
                }
            }
        )
    }
}
