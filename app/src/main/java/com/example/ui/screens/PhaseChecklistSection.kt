package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ChecklistItemEntity
import com.example.model.ChecklistPhase
import com.example.model.WorkDepartment
import com.example.ui.components.DepartmentBadge
import com.example.ui.theme.AmberConcert
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DenseBackgroundDark
import com.example.ui.theme.DenseBorderDark
import com.example.ui.theme.DenseBorderSubtleDark
import com.example.ui.theme.DenseSurfaceDark
import com.example.ui.theme.DenseSurfaceElevatedDark
import com.example.ui.theme.DenseTextMutedDark
import com.example.ui.theme.DenseTextPrimaryDark
import com.example.ui.theme.DenseTextSecondaryDark
import com.example.ui.theme.LaserGreen
import com.example.viewmodel.AvlViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PhaseChecklistSection(
    viewModel: AvlViewModel,
    modifier: Modifier = Modifier
) {
    val allChecklist by viewModel.currentEventChecklist.collectAsStateWithLifecycle()
    val filteredChecklist by viewModel.filteredEventChecklist.collectAsStateWithLifecycle()
    val selectedPhase by viewModel.selectedChecklistPhase.collectAsStateWithLifecycle()
    val pendingCriticalCount by viewModel.pendingCriticalChecklistCount.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var itemToEditNote by remember { mutableStateOf<ChecklistItemEntity?>(null) }
    var itemToDelete by remember { mutableStateOf<ChecklistItemEntity?>(null) }

    val totalItems = allChecklist.size
    val completedItems = allChecklist.count { it.isCompleted }
    val progress = if (totalItems > 0) completedItems.toFloat() / totalItems.toFloat() else 0f

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Progress Card
            item {
                Spacer(modifier = Modifier.height(2.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "PRODUCTION & SAFETY CHECKLIST",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = AmberConcert
                                )
                                Text(
                                    "$completedItems of $totalItems Safety & Stage Tasks Completed",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                "${(progress * 100).toInt()}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = if (progress >= 1f) LaserGreen else CyanNeon
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                            color = if (progress >= 1f) LaserGreen else CyanNeon,
                            trackColor = DenseSurfaceElevatedDark
                        )
                    }
                }
            }

            // Critical Safety Warning Banner
            if (pendingCriticalCount > 0) {
                item {
                    Surface(
                        color = CrimsonAlert.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonAlert.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = CrimsonAlert, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "CRITICAL SHOW-STOPPERS: $pendingCriticalCount INCOMPLETE",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = CrimsonAlert
                                )
                                Text(
                                    "Rigging safety cables, Camlock 3-phase voltages, and Dante network sync must be checked.",
                                    fontSize = 9.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Phase Filter Chips Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedPhase == null,
                            onClick = { viewModel.selectedChecklistPhase.value = null },
                            label = { Text("All (${allChecklist.size})", fontSize = 11.sp) },
                            shape = RoundedCornerShape(4.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanNeon,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                    ChecklistPhase.entries.sortedBy { it.sortOrder }.forEach { phase ->
                        val count = allChecklist.count { it.phase == phase }
                        item {
                            FilterChip(
                                selected = selectedPhase == phase,
                                onClick = { viewModel.selectedChecklistPhase.value = phase },
                                label = { Text("${phase.iconEmoji} ${phase.displayName} ($count)", fontSize = 11.sp) },
                                shape = RoundedCornerShape(4.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanNeon,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }
                }
            }

            if (filteredChecklist.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.FactCheck, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No Checklist Tasks for Selected Phase", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            Text("Tap the '+' button to add custom tasks to this phase.", fontSize = 11.sp, color = DenseTextSecondaryDark)
                        }
                    }
                }
            } else {
                items(filteredChecklist, key = { it.id }) { item ->
                    PhaseChecklistItemCard(
                        item = item,
                        onToggle = { isChecked ->
                            viewModel.toggleChecklistItem(item.id, isChecked, item.verificationNote)
                        },
                        onEditNote = { itemToEditNote = item },
                        onDelete = { itemToDelete = item }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(60.dp)) }
        }

        // Floating Action Button to Add Task
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = CyanNeon,
            contentColor = Color.Black,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("btn_add_checklist_task")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Task")
        }
    }

    if (showAddDialog) {
        AddChecklistTaskDialog(
            initialPhase = selectedPhase ?: ChecklistPhase.PREP_PULL,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, phase, dept, isCritical, desc ->
                viewModel.addChecklistItem(title, phase, dept, isCritical, desc)
                showAddDialog = false
            }
        )
    }

    itemToEditNote?.let { item ->
        EditVerificationNoteDialog(
            item = item,
            onDismiss = { itemToEditNote = null },
            onSave = { note ->
                viewModel.toggleChecklistItem(item.id, item.isCompleted, note)
                itemToEditNote = null
            }
        )
    }

    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            containerColor = DenseSurfaceDark,
            title = { Text("Delete Checklist Task?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("Remove task '${item.title}'?", color = DenseTextSecondaryDark) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteChecklistItem(item.id)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("Cancel", color = DenseTextSecondaryDark) }
            }
        )
    }
}

@Composable
fun PhaseChecklistItemCard(
    item: ChecklistItemEntity,
    onToggle: (Boolean) -> Unit,
    onEditNote: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val completedDate = if (item.completedAt != null) dateFormat.format(Date(item.completedAt)) else ""

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCompleted) DenseSurfaceElevatedDark.copy(alpha = 0.5f) else DenseSurfaceDark
        ),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.isCompleted) LaserGreen.copy(alpha = 0.4f)
            else if (item.isCritical) CrimsonAlert.copy(alpha = 0.6f)
            else DenseBorderDark
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = item.isCompleted,
                onCheckedChange = onToggle,
                colors = CheckboxDefaults.colors(
                    checkedColor = LaserGreen,
                    uncheckedColor = if (item.isCritical) CrimsonAlert else DenseTextSecondaryDark,
                    checkmarkColor = Color.Black
                ),
                modifier = Modifier.size(24.dp).padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        if (item.isCritical) {
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = CrimsonAlert.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, CrimsonAlert),
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = CrimsonAlert, modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("CRITICAL", fontSize = 8.sp, fontWeight = FontWeight.Black, color = CrimsonAlert)
                                }
                            }
                        }

                        Text(
                            item.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (item.isCompleted) LaserGreen else Color.White,
                            textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )
                    }

                    Row {
                        IconButton(onClick = onEditNote, modifier = Modifier.size(22.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Add Note", tint = DenseTextSecondaryDark, modifier = Modifier.size(14.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(22.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DenseTextSecondaryDark, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                if (item.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(item.description, fontSize = 10.sp, color = DenseTextSecondaryDark)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = RoundedCornerShape(3.dp), color = DenseSurfaceElevatedDark) {
                        Text(
                            "${item.phase.iconEmoji} ${item.phase.displayName}",
                            fontSize = 9.sp,
                            color = AmberConcert,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }

                    DepartmentBadge(department = item.department)

                    if (item.isCompleted) {
                        Surface(shape = RoundedCornerShape(3.dp), color = LaserGreen.copy(alpha = 0.15f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = LaserGreen, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    "Signed: ${item.completedBy} ($completedDate)",
                                    fontSize = 9.sp,
                                    color = LaserGreen
                                )
                            }
                        }
                    }
                }

                if (item.verificationNote.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = DenseSurfaceElevatedDark,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, CyanNeon.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "📝 Note: ${item.verificationNote}",
                            fontSize = 9.sp,
                            color = CyanNeon,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChecklistTaskDialog(
    initialPhase: ChecklistPhase,
    onDismiss: () -> Unit,
    onConfirm: (title: String, phase: ChecklistPhase, dept: WorkDepartment, isCritical: Boolean, desc: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var phase by remember { mutableStateOf(initialPhase) }
    var department by remember { mutableStateOf(WorkDepartment.AUDIO) }
    var isCritical by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }

    var phaseExpanded by remember { mutableStateOf(false) }
    var deptExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DenseSurfaceDark,
        title = { Text("Add Checklist Task", color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 14.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Description / Check *") },
                    placeholder = { Text("e.g. Test voltage on stage left 3-phase distro") },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(expanded = phaseExpanded, onExpandedChange = { phaseExpanded = it }) {
                    OutlinedTextField(
                        value = "${phase.iconEmoji} ${phase.displayName}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Production Phase") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = phaseExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = phaseExpanded, onDismissRequest = { phaseExpanded = false }, modifier = Modifier.background(DenseSurfaceDark)) {
                        ChecklistPhase.entries.forEach { p ->
                            DropdownMenuItem(
                                text = { Text("${p.iconEmoji} ${p.displayName}", color = DenseTextPrimaryDark) },
                                onClick = {
                                    phase = p
                                    phaseExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(expanded = deptExpanded, onExpandedChange = { deptExpanded = it }) {
                    OutlinedTextField(
                        value = department.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Responsible Department") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = deptExpanded, onDismissRequest = { deptExpanded = false }, modifier = Modifier.background(DenseSurfaceDark)) {
                        WorkDepartment.entries.forEach { d ->
                            DropdownMenuItem(
                                text = { Text(d.displayName, color = DenseTextPrimaryDark) },
                                onClick = {
                                    department = d
                                    deptExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Critical Show-Stopping Task", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Must be cleared before show go", fontSize = 9.sp, color = DenseTextSecondaryDark)
                    }
                    Switch(
                        checked = isCritical,
                        onCheckedChange = { isCritical = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = CrimsonAlert, checkedTrackColor = CrimsonAlert.copy(alpha = 0.3f))
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Specification / Safety Guidance") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, phase, department, isCritical, description)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black)
            ) {
                Text("Add Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DenseTextSecondaryDark) }
        }
    )
}

@Composable
fun EditVerificationNoteDialog(
    item: ChecklistItemEntity,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var note by remember { mutableStateOf(item.verificationNote) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DenseSurfaceDark,
        title = {
            Column {
                Text("Verification Note / Test Log", color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(item.title, fontSize = 10.sp, color = DenseTextSecondaryDark)
            }
        },
        text = {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Technician Verification Note") },
                placeholder = { Text("e.g. Measured 120.4V on L1-N with Fluke 87V") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onSave(note) },
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black)
            ) {
                Text("Save Note")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DenseTextSecondaryDark) }
        }
    )
}
