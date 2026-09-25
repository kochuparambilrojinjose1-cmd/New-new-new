package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.EquipmentLoadoutEntity
import com.example.data.local.LoadoutItemEntity
import com.example.data.local.LoadoutWithItems
import com.example.data.local.ProductionEventEntity
import com.example.model.AvlCategory
import com.example.model.LoadoutStatus
import com.example.model.WorkDepartment
import com.example.ui.components.AvlCategoryBadge
import com.example.ui.components.DepartmentBadge
import com.example.ui.theme.AmberConcert
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DenseBackgroundDark
import com.example.ui.theme.DenseBorderDark
import com.example.ui.theme.DenseBorderSubtleDark
import com.example.ui.theme.DenseSurfaceDark
import com.example.ui.theme.DenseSurfaceElevatedDark
import com.example.ui.theme.DenseTextPrimaryDark
import com.example.ui.theme.DenseTextSecondaryDark
import com.example.ui.theme.LaserGreen
import com.example.viewmodel.AvlViewModel

@Composable
fun AdminLoadoutsSection(
    viewModel: AvlViewModel,
    selectedEvent: ProductionEventEntity?,
    modifier: Modifier = Modifier
) {
    val loadoutsWithItems by viewModel.currentEventLoadouts.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }
    var loadoutToAddItemTo by remember { mutableStateOf<EquipmentLoadoutEntity?>(null) }
    var loadoutToDelete by remember { mutableStateOf<EquipmentLoadoutEntity?>(null) }

    val totalLoadouts = loadoutsWithItems.size
    val loadedCount = loadoutsWithItems.count { it.loadout.status == LoadoutStatus.LOADED_ON_TRUCK }
    val totalWeightLbs = loadoutsWithItems.sumOf { it.loadout.targetWeightLbs }
    val totalAmps = loadoutsWithItems.sumOf { it.loadout.powerDrawAmps }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "EQUIPMENT LOADOUTS & VEHICLE MANIFESTS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = AmberConcert
                    )
                    Text(
                        if (selectedEvent != null) "Assigned Packages for: ${selectedEvent.name}" else "Global Master Loadout Templates",
                        fontSize = 10.sp,
                        color = DenseTextSecondaryDark
                    )
                }

                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.testTag("btn_create_loadout")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Loadout", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Summary KPI Strip
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    KpiStat(label = "Total Loadouts", value = "$totalLoadouts", color = Color.White)
                    Box(modifier = Modifier.height(24.dp).width(1.dp).background(DenseBorderSubtleDark))
                    KpiStat(label = "Truck Loaded", value = "$loadedCount", color = LaserGreen)
                    Box(modifier = Modifier.height(24.dp).width(1.dp).background(DenseBorderSubtleDark))
                    KpiStat(label = "Total Weight", value = "${totalWeightLbs.toInt()} lbs", color = CyanNeon)
                    Box(modifier = Modifier.height(24.dp).width(1.dp).background(DenseBorderSubtleDark))
                    KpiStat(label = "Power Draw", value = "$totalAmps A", color = AmberConcert)
                }
            }
        }

        if (loadoutsWithItems.isEmpty()) {
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
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Loadout Packages Created Yet", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text("Create a vehicle loadout manifest for audio, video, lighting or power rigs.", fontSize = 11.sp, color = DenseTextSecondaryDark)
                    }
                }
            }
        } else {
            items(loadoutsWithItems, key = { it.loadout.id }) { loadoutWithItems ->
                LoadoutCard(
                    loadoutWithItems = loadoutWithItems,
                    onUpdateStatus = { newStatus ->
                        viewModel.updateLoadoutStatus(loadoutWithItems.loadout.id, newStatus)
                    },
                    onAddItem = {
                        loadoutToAddItemTo = loadoutWithItems.loadout
                    },
                    onUpdateItemProgress = { itemId, packed, loaded, verified ->
                        viewModel.updateLoadoutItemProgress(itemId, packed, loaded, verified)
                    },
                    onDeleteItem = { itemId ->
                        viewModel.deleteLoadoutItem(itemId)
                    },
                    onDeleteLoadout = {
                        loadoutToDelete = loadoutWithItems.loadout
                    }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    if (showCreateDialog) {
        CreateLoadoutDialog(
            eventId = selectedEvent?.id,
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, eventId, dept, truck, notes, weight, amps ->
                viewModel.createLoadout(name, eventId, dept, truck, notes, weight, amps)
                showCreateDialog = false
            }
        )
    }

    loadoutToAddItemTo?.let { loadout ->
        AddLoadoutItemDialog(
            loadoutName = loadout.name,
            onDismiss = { loadoutToAddItemTo = null },
            onConfirm = { name, category, requiredQty, caseRack, barcode ->
                viewModel.addLoadoutItem(loadout.id, name, category, requiredQty, caseRack, barcode)
                loadoutToAddItemTo = null
            }
        )
    }

    loadoutToDelete?.let { loadout ->
        AlertDialog(
            onDismissRequest = { loadoutToDelete = null },
            containerColor = DenseSurfaceDark,
            title = { Text("Delete Loadout?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("Remove loadout '${loadout.name}' and all its line items?", color = DenseTextSecondaryDark) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteLoadout(loadout.id)
                        loadoutToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { loadoutToDelete = null }) {
                    Text("Cancel", color = DenseTextSecondaryDark)
                }
            }
        )
    }
}

@Composable
fun LoadoutCard(
    loadoutWithItems: LoadoutWithItems,
    onUpdateStatus: (LoadoutStatus) -> Unit,
    onAddItem: () -> Unit,
    onUpdateItemProgress: (itemId: String, packed: Int, loaded: Int, isVerified: Boolean) -> Unit,
    onDeleteItem: (String) -> Unit,
    onDeleteLoadout: () -> Unit
) {
    val loadout = loadoutWithItems.loadout
    val items = loadoutWithItems.items
    var expanded by remember { mutableStateOf(false) }
    var statusMenuExpanded by remember { mutableStateOf(false) }

    val totalRequired = items.sumOf { it.requiredQuantity }
    val totalPacked = items.sumOf { it.packedQuantity }
    val totalLoaded = items.sumOf { it.loadedQuantity }
    val progress = if (totalRequired > 0) (totalLoaded.toFloat() / totalRequired.toFloat()).coerceIn(0f, 1f) else 0f

    Card(
        colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(
                        loadout.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Status Badge with dropdown selector
                    Box {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = when (loadout.status) {
                                LoadoutStatus.DRAFT -> DenseSurfaceElevatedDark
                                LoadoutStatus.STAGED_IN_BAY -> AmberConcert.copy(alpha = 0.2f)
                                LoadoutStatus.LOADED_ON_TRUCK -> LaserGreen.copy(alpha = 0.2f)
                                LoadoutStatus.ON_SITE_DEPLOYED -> CyanNeon.copy(alpha = 0.2f)
                                LoadoutStatus.RETURNED_AUDITED -> LaserGreen.copy(alpha = 0.3f)
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                when (loadout.status) {
                                    LoadoutStatus.DRAFT -> DenseBorderDark
                                    LoadoutStatus.STAGED_IN_BAY -> AmberConcert
                                    LoadoutStatus.LOADED_ON_TRUCK -> LaserGreen
                                    LoadoutStatus.ON_SITE_DEPLOYED -> CyanNeon
                                    LoadoutStatus.RETURNED_AUDITED -> LaserGreen
                                }
                            ),
                            modifier = Modifier.clickable { statusMenuExpanded = true }
                        ) {
                            Text(
                                "${loadout.status.iconEmoji} ${loadout.status.displayName} ▼",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = statusMenuExpanded,
                            onDismissRequest = { statusMenuExpanded = false },
                            modifier = Modifier.background(DenseSurfaceDark)
                        ) {
                            LoadoutStatus.entries.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text("${status.iconEmoji} ${status.displayName}", color = DenseTextPrimaryDark, fontSize = 11.sp) },
                                    onClick = {
                                        onUpdateStatus(status)
                                        statusMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = onDeleteLoadout, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DenseTextSecondaryDark, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Metadata Tags Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DepartmentBadge(department = loadout.department)

                if (loadout.truckOrVehicle.isNotBlank()) {
                    Surface(shape = RoundedCornerShape(3.dp), color = DenseSurfaceElevatedDark) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(10.dp), tint = CyanNeon)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(loadout.truckOrVehicle, fontSize = 9.sp, color = DenseTextSecondaryDark)
                        }
                    }
                }

                if (loadout.targetWeightLbs > 0) {
                    Surface(shape = RoundedCornerShape(3.dp), color = DenseSurfaceElevatedDark) {
                        Text("${loadout.targetWeightLbs.toInt()} lbs", fontSize = 9.sp, color = AmberConcert, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                }

                if (loadout.powerDrawAmps > 0) {
                    Surface(shape = RoundedCornerShape(3.dp), color = DenseSurfaceElevatedDark) {
                        Text("${loadout.powerDrawAmps}A Draw", fontSize = 9.sp, color = CyanNeon, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                }
            }

            if (loadout.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(loadout.notes, fontSize = 10.sp, color = DenseTextSecondaryDark, maxLines = 2)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Pack & Truck Progress: $totalLoaded / $totalRequired Units Loaded ($totalPacked Staged)",
                    fontSize = 10.sp,
                    color = if (progress >= 1f) LaserGreen else DenseTextSecondaryDark
                )
                Text(
                    "${(progress * 100).toInt()}%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (progress >= 1f) LaserGreen else CyanNeon
                )
            }

            Spacer(modifier = Modifier.height(3.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                color = if (progress >= 1f) LaserGreen else CyanNeon,
                trackColor = DenseSurfaceElevatedDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Expand / Collapse Items Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Manifest Line Items (${items.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CyanNeon
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (expanded) "Hide" else "Show Details", fontSize = 10.sp, color = DenseTextSecondaryDark)
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = DenseTextSecondaryDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                    items.forEach { item ->
                        LoadoutItemRow(
                            item = item,
                            onUpdateProgress = { packed, loaded, verified ->
                                onUpdateItemProgress(item.id, packed, loaded, verified)
                            },
                            onDelete = { onDeleteItem(item.id) }
                        )
                    }

                    OutlinedButton(
                        onClick = onAddItem,
                        modifier = Modifier.fillMaxWidth().height(32.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanNeon),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Add Gear to Manifest", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LoadoutItemRow(
    item: LoadoutItemEntity,
    onUpdateProgress: (packed: Int, loaded: Int, isVerified: Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = DenseSurfaceElevatedDark,
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, DenseBorderSubtleDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        item.itemName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.White
                    )
                    if (item.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = LaserGreen, modifier = Modifier.size(12.dp))
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    AvlCategoryBadge(category = item.category)
                    if (item.caseOrFlightRack.isNotBlank()) {
                        Text("📦 ${item.caseOrFlightRack}", fontSize = 9.sp, color = DenseTextSecondaryDark)
                    }
                    if (item.barcodeOrRfid.isNotBlank()) {
                        Text("🏷️ ${item.barcodeOrRfid}", fontSize = 9.sp, color = DenseTextSecondaryDark, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            // Quick quantity status and steppers
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("Req: ${item.requiredQuantity}", fontSize = 9.sp, color = DenseTextSecondaryDark)
                    Text(
                        "Packed: ${item.packedQuantity} | Loaded: ${item.loadedQuantity}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.loadedQuantity >= item.requiredQuantity) LaserGreen else CyanNeon
                    )
                }

                // Quick load-all button
                IconButton(
                    onClick = {
                        val nextLoaded = if (item.loadedQuantity < item.requiredQuantity) item.requiredQuantity else 0
                        val nextPacked = if (nextLoaded > item.packedQuantity) nextLoaded else item.packedQuantity
                        onUpdateProgress(nextPacked, nextLoaded, nextLoaded >= item.requiredQuantity)
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Load All",
                        tint = if (item.loadedQuantity >= item.requiredQuantity) LaserGreen else DenseTextSecondaryDark,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(20.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DenseTextSecondaryDark, modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLoadoutDialog(
    eventId: String?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, eventId: String?, dept: WorkDepartment, truck: String, notes: String, weight: Double, amps: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var department by remember { mutableStateOf(WorkDepartment.AUDIO) }
    var truck by remember { mutableStateOf("Truck 01 (53ft Semi)") }
    var weightText by remember { mutableStateOf("4500") }
    var ampsText by remember { mutableStateOf("60") }
    var notes by remember { mutableStateOf("") }
    var deptExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DenseSurfaceDark,
        title = { Text("Create Equipment Loadout", color = CyanNeon, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Loadout Name *") },
                    placeholder = { Text("e.g. Main PA & Array Package") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(expanded = deptExpanded, onExpandedChange = { deptExpanded = it }) {
                    OutlinedTextField(
                        value = "${department.iconEmoji} ${department.displayName}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Department") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = deptExpanded, onDismissRequest = { deptExpanded = false }, modifier = Modifier.background(DenseSurfaceDark)) {
                        WorkDepartment.entries.forEach { dept ->
                            DropdownMenuItem(
                                text = { Text("${dept.iconEmoji} ${dept.displayName}", color = DenseTextPrimaryDark) },
                                onClick = {
                                    department = dept
                                    deptExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = truck,
                    onValueChange = { truck = it },
                    label = { Text("Assigned Vehicle / Transport") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        label = { Text("Est. Weight (lbs)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = ampsText,
                        onValueChange = { ampsText = it },
                        label = { Text("Power Draw (Amps)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Loadout Notes / Instructions") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val weight = weightText.toDoubleOrNull() ?: 0.0
                        val amps = ampsText.toIntOrNull() ?: 0
                        onConfirm(name, eventId, department, truck, notes, weight, amps)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black)
            ) {
                Text("Create Loadout")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DenseTextSecondaryDark) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLoadoutItemDialog(
    loadoutName: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: AvlCategory, requiredQty: Int, caseRack: String, barcode: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(AvlCategory.AUDIO) }
    var qtyText by remember { mutableStateOf("1") }
    var caseRack by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var catExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DenseSurfaceDark,
        title = { Text("Add Gear to '$loadoutName'", color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 14.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name *") },
                    placeholder = { Text("e.g. Shure Axient Receiver") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = it }) {
                    OutlinedTextField(
                        value = "${category.iconEmoji} ${category.displayName}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("AVL Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }, modifier = Modifier.background(DenseSurfaceDark)) {
                        AvlCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text("${cat.iconEmoji} ${cat.displayName}", color = DenseTextPrimaryDark) },
                                onClick = {
                                    category = cat
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = qtyText,
                        onValueChange = { qtyText = it },
                        label = { Text("Required Qty *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = caseRack,
                        onValueChange = { caseRack = it },
                        label = { Text("Flight Case / Rack") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it },
                    label = { Text("Barcode / Asset Tag") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = qtyText.toIntOrNull() ?: 1
                    if (name.isNotBlank() && qty > 0) {
                        onConfirm(name, category, qty, caseRack, barcode)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black)
            ) {
                Text("Add to Loadout")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DenseTextSecondaryDark) }
        }
    )
}
