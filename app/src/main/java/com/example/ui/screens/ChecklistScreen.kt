package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ChecklistItemEntity
import com.example.data.local.EquipmentItemEntity
import com.example.model.AvlCategory
import com.example.model.ChecklistPhase
import com.example.model.ItemStatus
import com.example.model.WorkDepartment
import com.example.ui.components.AvlCategoryBadge
import com.example.ui.components.DepartmentBadge
import com.example.ui.components.QuickQuantityStepper
import com.example.ui.theme.AmberConcert
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.CyanNeonGlow
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

@Composable
fun ChecklistScreen(
    viewModel: AvlViewModel,
    modifier: Modifier = Modifier
) {
    val selectedEvent by viewModel.selectedEvent.collectAsStateWithLifecycle()
    val filteredItems by viewModel.filteredEquipment.collectAsStateWithLifecycle()
    val allEventItems by viewModel.currentEquipment.collectAsStateWithLifecycle()

    val selectedCategory by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val statusFilter by viewModel.statusFilter.collectAsStateWithLifecycle()

    var showAddItemDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<EquipmentItemEntity?>(null) }
    var itemToEditQty by remember { mutableStateOf<EquipmentItemEntity?>(null) }
    var activeMode by remember { mutableStateOf("MANIFEST") } // "MANIFEST" or "PHASE_CHECKLIST"

    val allChecklist by viewModel.currentEventChecklist.collectAsStateWithLifecycle()
    val completedChecklistCount = allChecklist.count { it.isCompleted }
    val pendingCriticalCount by viewModel.pendingCriticalChecklistCount.collectAsStateWithLifecycle()

    val totalRequired = allEventItems.sumOf { it.targetQuantity }
    val totalPacked = allEventItems.sumOf { it.packedQuantity }
    val packedPct = if (totalRequired > 0) ((totalPacked.toFloat() / totalRequired.toFloat()) * 100).toInt() else 0

    if (selectedEvent == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Inventory2,
                    contentDescription = null,
                    tint = CyanNeon,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No Production Event Selected",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    "Please create or select an event from the Events tab.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Mode Selector Tab Bar
        Surface(
            color = DenseSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = { activeMode = "MANIFEST" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeMode == "MANIFEST") CyanNeon else Color.Transparent,
                        contentColor = if (activeMode == "MANIFEST") Color.Black else DenseTextSecondaryDark
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f).height(32.dp).testTag("tab_gear_manifest")
                ) {
                    Text(
                        "📦 Gear Pack ($totalPacked/$totalRequired)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { activeMode = "PHASE_CHECKLIST" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeMode == "PHASE_CHECKLIST") CyanNeon else Color.Transparent,
                        contentColor = if (activeMode == "PHASE_CHECKLIST") Color.Black else DenseTextSecondaryDark
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f).height(32.dp).testTag("tab_phase_checklist")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "✅ Safety Checklist ($completedChecklistCount/${allChecklist.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (pendingCriticalCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                shape = CircleShape,
                                color = CrimsonAlert
                            ) {
                                Text(
                                    "$pendingCriticalCount",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (activeMode == "PHASE_CHECKLIST") {
            PhaseChecklistSection(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
            item {
                Spacer(modifier = Modifier.height(2.dp))
                // Packing Progress Banner - High Density
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
                                Text(
                                    text = "OUTBOUND PACKING MANIFEST",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = DenseTextPrimaryDark,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "${selectedEvent?.name} • ${selectedEvent?.truckOrVehicle}",
                                    fontSize = 11.sp,
                                    color = CyanNeon
                                )
                            }
                            Surface(
                                color = if (packedPct == 100) LaserGreenGlow else CyanNeonGlow,
                                shape = RoundedCornerShape(4.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (packedPct == 100) LaserGreen else CyanNeon
                                )
                            ) {
                                Text(
                                    text = "$packedPct% PACKED",
                                    color = if (packedPct == 100) LaserGreen else CyanNeon,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.5.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Count: $totalPacked / $totalRequired units staged",
                                fontSize = 11.sp,
                                color = DenseTextSecondaryDark,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "${allEventItems.size} Line Items",
                                fontSize = 11.sp,
                                color = DenseTextSecondaryDark
                            )
                        }
                    }
                }
            }

            // Search Bar & Filter Controls
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search Gear", tint = CyanNeon, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    placeholder = { Text("Search equipment, barcode, case...", fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = DenseBorderDark,
                        focusedContainerColor = DenseSurfaceDark,
                        unfocusedContainerColor = DenseSurfaceDark
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_gear_input")
                )
            }

            // AVL Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(vertical = 1.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { viewModel.selectedCategoryFilter.value = null },
                            label = { Text("All (${allEventItems.size})", fontSize = 11.sp) },
                            shape = RoundedCornerShape(4.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanNeon,
                                selectedLabelColor = DenseBackgroundDark,
                                containerColor = DenseSurfaceDark,
                                labelColor = DenseTextPrimaryDark
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedCategory == null,
                                borderColor = DenseBorderDark,
                                selectedBorderColor = CyanNeon
                            ),
                            modifier = Modifier.testTag("filter_category_all")
                        )
                    }
                    items(AvlCategory.entries) { category ->
                        val count = allEventItems.count { it.category == category }
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { viewModel.selectedCategoryFilter.value = category },
                            label = { Text("${category.iconEmoji} ${category.displayName} ($count)", fontSize = 11.sp) },
                            shape = RoundedCornerShape(4.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanNeon,
                                selectedLabelColor = DenseBackgroundDark,
                                containerColor = DenseSurfaceDark,
                                labelColor = DenseTextPrimaryDark
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedCategory == category,
                                borderColor = DenseBorderDark,
                                selectedBorderColor = CyanNeon
                            ),
                            modifier = Modifier.testTag("filter_category_${category.name}")
                        )
                    }
                }
            }

            // Status Sub-filters & Quick Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        listOf("ALL" to "All", "PENDING" to "Pending", "LOADED" to "Loaded").forEach { (key, label) ->
                            val isSelected = statusFilter == key
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isSelected) DenseSurfaceHighlightDark else DenseSurfaceDark,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) CyanNeon else DenseBorderDark
                                ),
                                modifier = Modifier
                                    .clickable { viewModel.statusFilter.value = key }
                            ) {
                                Text(
                                    text = label.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    letterSpacing = 0.3.sp,
                                    color = if (isSelected) CyanNeon else DenseTextSecondaryDark,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.5.dp)
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.packAllFiltered() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LaserGreen),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LaserGreen.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("pack_all_filtered_btn")
                    ) {
                        Icon(
                            Icons.Default.DoneAll,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("PACK ALL FILTERED", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.3.sp)
                    }
                }
            }

            // Equipment Item Cards
            if (filteredItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No equipment matches current search or filter.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(filteredItems, key = { it.id }) { item ->
                    ChecklistItemCard(
                        item = item,
                        onIncrement = { viewModel.incrementPackedQuantity(item) },
                        onDecrement = { viewModel.decrementPackedQuantity(item) },
                        onMarkPacked = { viewModel.markItemFullyPacked(item) },
                        onEditCustomQty = { itemToEditQty = item },
                        onDelete = { itemToDelete = item }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Add Item FAB
        FloatingActionButton(
            onClick = { showAddItemDialog = true },
            containerColor = AmberConcert,
            contentColor = DenseBackgroundDark,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(14.dp)
                .testTag("add_gear_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Equipment Item")
        }
    }
    }
    }

    if (showAddItemDialog) {
        AddEquipmentDialog(
            onDismiss = { showAddItemDialog = false },
            onAdd = { name, cat, qty, caseLoc, barcode, notes ->
                selectedEvent?.id?.let { evtId ->
                    viewModel.addEquipmentItem(evtId, name, cat, qty, caseLoc, barcode, notes)
                }
                showAddItemDialog = false
            }
        )
    }

    itemToEditQty?.let { item ->
        EditQuantityDialog(
            item = item,
            onDismiss = { itemToEditQty = null },
            onSave = { newQty ->
                viewModel.setPackedQuantity(item, newQty)
                itemToEditQty = null
            }
        )
    }

    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Remove Item from Manifest?") },
            text = { Text("Remove '${item.name}' from this event's packing list?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEquipmentItem(item.id)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ChecklistItemCard(
    item: EquipmentItemEntity,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onMarkPacked: () -> Unit,
    onEditCustomQty: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFullyPacked = item.packedQuantity >= item.targetQuantity && item.targetQuantity > 0

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isFullyPacked) DenseSurfaceElevatedDark else DenseSurfaceDark
        ),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isFullyPacked) LaserGreen.copy(alpha = 0.5f) else DenseBorderDark
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("checklist_item_${item.id}")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvlCategoryBadge(category = item.category)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (item.barcodeOrTag.isNotBlank()) {
                        Surface(
                            color = DenseSurfaceHighlightDark,
                            shape = RoundedCornerShape(3.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                            ) {
                                Icon(
                                    Icons.Default.QrCode,
                                    contentDescription = null,
                                    tint = CyanNeon,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = item.barcodeOrTag,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = DenseTextPrimaryDark
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete item",
                            tint = DenseTextSecondaryDark.copy(alpha = 0.6f),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = DenseTextPrimaryDark
            )

            if (item.storageLocation.isNotBlank() || item.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = buildString {
                        if (item.storageLocation.isNotBlank()) append("📍 ${item.storageLocation}")
                        if (item.storageLocation.isNotBlank() && item.notes.isNotBlank()) append(" • ")
                        if (item.notes.isNotBlank()) append(item.notes)
                    },
                    fontSize = 11.sp,
                    color = DenseTextSecondaryDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stepper and Quick Max Out Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickQuantityStepper(
                    currentValue = item.packedQuantity,
                    targetValue = item.targetQuantity,
                    onIncrement = onIncrement,
                    onDecrement = onDecrement,
                    onMaxOut = onEditCustomQty,
                    accentColor = if (isFullyPacked) LaserGreen else CyanNeon
                )

                Button(
                    onClick = onMarkPacked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFullyPacked) LaserGreen else DenseSurfaceHighlightDark,
                        contentColor = if (isFullyPacked) DenseBackgroundDark else DenseTextPrimaryDark
                    ),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isFullyPacked) LaserGreen else DenseBorderDark
                    ),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (isFullyPacked) "STAGED" else "PACK ALL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.4.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEquipmentDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, cat: AvlCategory, qty: Int, caseLoc: String, barcode: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(AvlCategory.AUDIO) }
    var quantityText by remember { mutableStateOf("1") }
    var caseLocation by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Equipment to Packing List", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Equipment Name") },
                    placeholder = { Text("e.g. Shure SM58, Chauvet Spot, 50ft XLR") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = DenseBorderDark,
                        focusedContainerColor = DenseSurfaceDark,
                        unfocusedContainerColor = DenseSurfaceDark
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_item_name_input")
                )

                // Category selector
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = "${selectedCategory.iconEmoji} ${selectedCategory.displayName}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("AVL Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = DenseBorderDark,
                            focusedContainerColor = DenseSurfaceDark,
                            unfocusedContainerColor = DenseSurfaceDark
                        ),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        AvlCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text("${cat.iconEmoji} ${cat.displayName}") },
                                onClick = {
                                    selectedCategory = cat
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it.filter { char -> char.isDigit() } },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = DenseBorderDark,
                            focusedContainerColor = DenseSurfaceDark,
                            unfocusedContainerColor = DenseSurfaceDark
                        ),
                        modifier = Modifier.weight(1f).testTag("add_item_qty_input")
                    )

                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it },
                        label = { Text("Asset / Barcode") },
                        placeholder = { Text("e.g. AUD-042") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = DenseBorderDark,
                            focusedContainerColor = DenseSurfaceDark,
                            unfocusedContainerColor = DenseSurfaceDark
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = caseLocation,
                    onValueChange = { caseLocation = it },
                    label = { Text("Storage Case / Trunk") },
                    placeholder = { Text("e.g. Trunk #3, Pelican 1510") },
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
                    label = { Text("Tech Notes (Optional)") },
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
                    val qty = quantityText.toIntOrNull() ?: 1
                    if (name.isNotBlank()) {
                        onAdd(name, selectedCategory, qty, caseLocation, barcode, notes)
                    }
                },
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AmberConcert, contentColor = DenseBackgroundDark),
                modifier = Modifier.testTag("confirm_add_gear_btn")
            ) {
                Text("Add Gear", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontSize = 12.sp)
            }
        }
    )
}

@Composable
fun EditQuantityDialog(
    item: EquipmentItemEntity,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var qtyText by remember { mutableStateOf(item.packedQuantity.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Packed Count", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
        text = {
            Column {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.SemiBold,
                    color = DenseTextPrimaryDark
                )
                Text(
                    text = "Target Needed: ${item.targetQuantity}",
                    fontSize = 11.sp,
                    color = DenseTextSecondaryDark
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = qtyText,
                    onValueChange = { qtyText = it.filter { char -> char.isDigit() } },
                    label = { Text("Units Packed & Staged") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    val count = qtyText.toIntOrNull() ?: item.packedQuantity
                    onSave(count)
                },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = DenseBackgroundDark)
            ) {
                Text("Update Count", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontSize = 12.sp)
            }
        }
    )
}
