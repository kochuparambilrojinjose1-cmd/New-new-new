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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
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
import com.example.data.local.InventoryTransactionEntity
import com.example.data.local.WarehouseInventoryEntity
import com.example.model.AvlCategory
import com.example.model.InventoryTransactionType
import com.example.model.WorkDepartment
import com.example.ui.components.AvlCategoryBadge
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
import com.example.ui.theme.OrangeDamaged
import com.example.viewmodel.AvlViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminInventorySection(
    viewModel: AvlViewModel,
    modifier: Modifier = Modifier
) {
    val allInventory by viewModel.allInventory.collectAsStateWithLifecycle()
    val filteredInventory by viewModel.filteredInventory.collectAsStateWithLifecycle()
    val lowStockInventory by viewModel.lowStockInventory.collectAsStateWithLifecycle()
    val recentTransactions by viewModel.recentInventoryTransactions.collectAsStateWithLifecycle()

    val selectedCategory by viewModel.selectedInventoryCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.inventorySearchQuery.collectAsStateWithLifecycle()

    var showAddItemDialog by remember { mutableStateOf(false) }
    var itemToAdjust by remember { mutableStateOf<WarehouseInventoryEntity?>(null) }
    var itemToDelete by remember { mutableStateOf<WarehouseInventoryEntity?>(null) }
    var showTransactionsLog by remember { mutableStateOf(false) }

    val totalOwnedUnits = allInventory.sumOf { it.totalStockQty }
    val totalAvailableUnits = allInventory.sumOf { it.availableQty }
    val totalAllocatedUnits = allInventory.sumOf { it.allocatedQty }
    val totalRepairUnits = allInventory.sumOf { it.maintenanceQty }
    val lowStockCount = lowStockInventory.size

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
                        "MASTER WAREHOUSE INVENTORY & ASSET STOCKS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = AmberConcert
                    )
                    Text(
                        "Live asset catalog, stock balances, field allocations, and repair queue",
                        fontSize = 10.sp,
                        color = DenseTextSecondaryDark
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { showTransactionsLog = !showTransactionsLog },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanNeon),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.5f)),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(if (showTransactionsLog) "Catalog" else "Movements", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showAddItemDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(34.dp).testTag("btn_register_inventory_item")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Add Asset", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Warehouse Stock KPI Card
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
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    KpiStat(label = "Total Owned", value = "$totalOwnedUnits", color = Color.White)
                    Box(modifier = Modifier.height(24.dp).width(1.dp).background(DenseBorderSubtleDark))
                    KpiStat(label = "Ready on Shelf", value = "$totalAvailableUnits", color = LaserGreen)
                    Box(modifier = Modifier.height(24.dp).width(1.dp).background(DenseBorderSubtleDark))
                    KpiStat(label = "Deployed on Gigs", value = "$totalAllocatedUnits", color = CyanNeon)
                    Box(modifier = Modifier.height(24.dp).width(1.dp).background(DenseBorderSubtleDark))
                    KpiStat(label = "Repair Bench", value = "$totalRepairUnits", color = OrangeDamaged)
                    Box(modifier = Modifier.height(24.dp).width(1.dp).background(DenseBorderSubtleDark))
                    KpiStat(label = "Shortage Alerts", value = "$lowStockCount", color = if (lowStockCount > 0) CrimsonAlert else DenseTextSecondaryDark)
                }
            }
        }

        if (showTransactionsLog) {
            // Real-Time Inventory Movement History Log
            item {
                Text(
                    "RECENT ASSET MOVEMENTS & STOCK AUDIT LOG",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = CyanNeon
                )
            }

            if (recentTransactions.isEmpty()) {
                item {
                    Text("No transactions logged yet.", fontSize = 11.sp, color = DenseTextSecondaryDark)
                }
            } else {
                items(recentTransactions, key = { it.id }) { tx ->
                    InventoryTransactionRow(tx = tx)
                }
            }
        } else {
            // Search Input
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.inventorySearchQuery.value = it },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.inventorySearchQuery.value = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = DenseTextSecondaryDark, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    placeholder = { Text("Search by SKU, model, brand, or shelf bin...", fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = DenseBorderDark,
                        focusedContainerColor = DenseSurfaceDark,
                        unfocusedContainerColor = DenseSurfaceDark
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("search_inventory_input")
                )
            }

            // Category Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { viewModel.selectedInventoryCategory.value = null },
                            label = { Text("All (${allInventory.size})", fontSize = 11.sp) },
                            shape = RoundedCornerShape(4.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanNeon,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                    AvlCategory.entries.forEach { cat ->
                        item {
                            val count = allInventory.count { it.category == cat }
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { viewModel.selectedInventoryCategory.value = cat },
                                label = { Text("${cat.iconEmoji} ${cat.displayName} ($count)", fontSize = 11.sp) },
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

            if (filteredInventory.isEmpty()) {
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
                            Icon(Icons.Default.Inventory, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No Inventory Assets Match Filter", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            Text("Try searching with another query or reset the category filter.", fontSize = 11.sp, color = DenseTextSecondaryDark)
                        }
                    }
                }
            } else {
                items(filteredInventory, key = { it.id }) { item ->
                    InventoryItemCard(
                        item = item,
                        onAdjustStock = { itemToAdjust = item },
                        onDelete = { itemToDelete = item }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    if (showAddItemDialog) {
        AddInventoryItemDialog(
            onDismiss = { showAddItemDialog = false },
            onConfirm = { name, cat, dept, sku, model, mfr, totalQty, bin, cost, minThreshold, notes ->
                viewModel.addInventoryItem(name, cat, dept, sku, model, mfr, totalQty, bin, cost, minThreshold, notes)
                showAddItemDialog = false
            }
        )
    }

    itemToAdjust?.let { item ->
        AdjustStockDialog(
            item = item,
            onDismiss = { itemToAdjust = null },
            onConfirm = { type, delta, notes ->
                viewModel.adjustInventoryQuantity(item.id, type, delta, notes)
                itemToAdjust = null
            }
        )
    }

    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            containerColor = DenseSurfaceDark,
            title = { Text("Delete Inventory Asset?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("Remove '${item.itemName}' (${item.skuOrBarcode}) from the warehouse database?", color = DenseTextSecondaryDark) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteInventoryItem(item.id)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel", color = DenseTextSecondaryDark)
                }
            }
        )
    }
}

@Composable
fun InventoryItemCard(
    item: WarehouseInventoryEntity,
    onAdjustStock: () -> Unit,
    onDelete: () -> Unit
) {
    val isLowStock = item.availableQty <= item.minimumThresholdQty

    Card(
        colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isLowStock) CrimsonAlert.copy(alpha = 0.8f) else DenseBorderDark
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header: Name & Barcode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.itemName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(3.dp), color = DenseSurfaceElevatedDark) {
                            Text(
                                "SKU: ${item.skuOrBarcode}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanNeon,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        if (item.modelNumber.isNotBlank()) {
                            Text("Mod: ${item.modelNumber}", fontSize = 9.sp, color = DenseTextSecondaryDark)
                        }
                        if (item.manufacturer.isNotBlank()) {
                            Text("• ${item.manufacturer}", fontSize = 9.sp, color = DenseTextSecondaryDark)
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DenseTextSecondaryDark, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stock Breakdown Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DenseSurfaceElevatedDark, RoundedCornerShape(6.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StockBadge("Total Owned", "${item.totalStockQty}", Color.White)
                Box(modifier = Modifier.height(18.dp).width(1.dp).background(DenseBorderSubtleDark))
                StockBadge("Available Ready", "${item.availableQty}", if (isLowStock) CrimsonAlert else LaserGreen)
                Box(modifier = Modifier.height(18.dp).width(1.dp).background(DenseBorderSubtleDark))
                StockBadge("On Shows", "${item.allocatedQty}", CyanNeon)
                Box(modifier = Modifier.height(18.dp).width(1.dp).background(DenseBorderSubtleDark))
                StockBadge("Repair Bench", "${item.maintenanceQty}", if (item.maintenanceQty > 0) OrangeDamaged else DenseTextSecondaryDark)
            }

            if (isLowStock) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = CrimsonAlert.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonAlert.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = CrimsonAlert, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Shortage Alert: Stock (${item.availableQty}) is at or below safe minimum threshold (${item.minimumThresholdQty})!",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CrimsonAlert
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Metadata: Bin Location, Department, Unit Cost
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (item.warehouseAisleBin.isNotBlank()) {
                        Text("📍 ${item.warehouseAisleBin}", fontSize = 10.sp, color = DenseTextSecondaryDark)
                    }
                    if (item.unitReplacementCost > 0) {
                        Text("Val: $${item.unitReplacementCost.toInt()} ea", fontSize = 9.sp, color = AmberConcert)
                    }
                }

                Button(
                    onClick = onAdjustStock,
                    colors = ButtonDefaults.buttonColors(containerColor = DenseSurfaceElevatedDark, contentColor = CyanNeon),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Adjust Stock", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StockBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Black, color = color)
        Text(label, fontSize = 8.sp, color = DenseTextSecondaryDark)
    }
}

@Composable
fun InventoryTransactionRow(tx: InventoryTransactionEntity) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(tx.timestamp))

    Surface(
        color = DenseSurfaceDark,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, DenseBorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${tx.transactionType.iconEmoji} ${tx.transactionType.displayName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (tx.quantityDelta >= 0) "+${tx.quantityDelta}" else "${tx.quantityDelta}",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = if (tx.quantityDelta >= 0) LaserGreen else CrimsonAlert
                    )
                }

                Text(
                    "Tech: ${tx.technicianName} • $formattedDate",
                    fontSize = 9.sp,
                    color = DenseTextSecondaryDark
                )

                if (tx.referenceNotes.isNotBlank()) {
                    Text(
                        tx.referenceNotes,
                        fontSize = 9.sp,
                        color = DenseTextMutedDark
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("Stock Balance", fontSize = 8.sp, color = DenseTextSecondaryDark)
                Text(
                    "${tx.previousAvailableQty} ➔ ${tx.newAvailableQty}",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = CyanNeon
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInventoryItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        category: AvlCategory,
        department: WorkDepartment,
        sku: String,
        model: String,
        manufacturer: String,
        totalQty: Int,
        bin: String,
        cost: Double,
        minThreshold: Int,
        notes: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(AvlCategory.AUDIO) }
    var department by remember { mutableStateOf(WorkDepartment.AUDIO) }
    var sku by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var manufacturer by remember { mutableStateOf("") }
    var totalQtyText by remember { mutableStateOf("10") }
    var bin by remember { mutableStateOf("") }
    var costText by remember { mutableStateOf("1200") }
    var minThresholdText by remember { mutableStateOf("2") }
    var notes by remember { mutableStateOf("") }

    var catExpanded by remember { mutableStateOf(false) }
    var deptExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DenseSurfaceDark,
        title = { Text("Register Warehouse Asset", color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 14.sp) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.height(340.dp)) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Equipment Name *") },
                        placeholder = { Text("e.g. Shure Axient Digital Dual") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = sku,
                            onValueChange = { sku = it },
                            label = { Text("SKU / Barcode *") },
                            placeholder = { Text("AUD-RF-03") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = totalQtyText,
                            onValueChange = { totalQtyText = it },
                            label = { Text("Total Stock *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = it }, modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = "${category.iconEmoji} ${category.displayName}",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Category") },
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

                        ExposedDropdownMenuBox(expanded = deptExpanded, onExpandedChange = { deptExpanded = it }, modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = department.displayName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Department") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(expanded = deptExpanded, onDismissRequest = { deptExpanded = false }, modifier = Modifier.background(DenseSurfaceDark)) {
                                WorkDepartment.entries.forEach { dept ->
                                    DropdownMenuItem(
                                        text = { Text(dept.displayName, color = DenseTextPrimaryDark) },
                                        onClick = {
                                            department = dept
                                            deptExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = model,
                            onValueChange = { model = it },
                            label = { Text("Model Number") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = manufacturer,
                            onValueChange = { manufacturer = it },
                            label = { Text("Manufacturer") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = bin,
                        onValueChange = { bin = it },
                        label = { Text("Warehouse Aisle / Shelf / Bay") },
                        placeholder = { Text("e.g. Aisle 1 - Bay B - Shelf 3") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = costText,
                            onValueChange = { costText = it },
                            label = { Text("Unit Value ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minThresholdText,
                            onValueChange = { minThresholdText = it },
                            label = { Text("Min Threshold") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Inventory Notes / Specs") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val total = totalQtyText.toIntOrNull() ?: 1
                    val cost = costText.toDoubleOrNull() ?: 0.0
                    val minThresh = minThresholdText.toIntOrNull() ?: 2
                    if (name.isNotBlank() && sku.isNotBlank()) {
                        onConfirm(name, category, department, sku, model, manufacturer, total, bin, cost, minThresh, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black)
            ) {
                Text("Register Asset")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DenseTextSecondaryDark) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdjustStockDialog(
    item: WarehouseInventoryEntity,
    onDismiss: () -> Unit,
    onConfirm: (type: InventoryTransactionType, delta: Int, notes: String) -> Unit
) {
    var type by remember { mutableStateOf(InventoryTransactionType.RECEIVE_NEW) }
    var deltaText by remember { mutableStateOf("1") }
    var notes by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DenseSurfaceDark,
        title = {
            Column {
                Text("Adjust Stock / Movement", color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(item.itemName, fontSize = 11.sp, color = DenseTextSecondaryDark)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
                    OutlinedTextField(
                        value = "${type.iconEmoji} ${type.displayName}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Movement Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }, modifier = Modifier.background(DenseSurfaceDark)) {
                        InventoryTransactionType.entries.forEach { t ->
                            DropdownMenuItem(
                                text = { Text("${t.iconEmoji} ${t.displayName}", color = DenseTextPrimaryDark) },
                                onClick = {
                                    type = t
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = deltaText,
                    onValueChange = { deltaText = it },
                    label = { Text("Quantity Units *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Reason / Event Reference Notes") },
                    placeholder = { Text("e.g. Returned from Neon Horizon Tour") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val delta = deltaText.toIntOrNull() ?: 1
                    if (delta > 0) {
                        onConfirm(type, delta, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black)
            ) {
                Text("Confirm Adjustment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DenseTextSecondaryDark) }
        }
    )
}
