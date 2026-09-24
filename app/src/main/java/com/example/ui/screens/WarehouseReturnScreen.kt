package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.EquipmentItemEntity
import com.example.model.AvlCategory
import com.example.model.EventStatus
import com.example.ui.components.AvlCategoryBadge
import com.example.ui.components.QuickQuantityStepper
import com.example.ui.theme.AmberConcert
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CrimsonGlow
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
import com.example.ui.theme.OrangeDamaged
import com.example.viewmodel.AvlViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WarehouseReturnScreen(
    viewModel: AvlViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedEvent by viewModel.selectedEvent.collectAsStateWithLifecycle()
    val allItems by viewModel.currentEquipment.collectAsStateWithLifecycle()
    val audit by viewModel.currentAudit.collectAsStateWithLifecycle()

    var filterMode by remember { mutableStateOf("ALL") } // ALL, MISSING, DAMAGED, RETURNED
    var itemToFlagDamage by remember { mutableStateOf<EquipmentItemEntity?>(null) }
    var showFinalizeAuditDialog by remember { mutableStateOf(false) }

    val totalPacked = allItems.sumOf { it.packedQuantity }
    val totalReturned = allItems.sumOf { it.returnedQuantity }
    val totalDamaged = allItems.sumOf { it.damagedQuantity }
    val totalMissing = allItems.sumOf { (it.packedQuantity - it.returnedQuantity - it.damagedQuantity).coerceAtLeast(0) }

    val returnPct = if (totalPacked > 0) ((totalReturned.toFloat() / totalPacked.toFloat()) * 100).toInt() else 0

    val filteredItems = allItems.filter { item ->
        val itemMissing = (item.packedQuantity - item.returnedQuantity - item.damagedQuantity).coerceAtLeast(0)
        when (filterMode) {
            "MISSING" -> itemMissing > 0
            "DAMAGED" -> item.damagedQuantity > 0
            "RETURNED" -> item.returnedQuantity == item.packedQuantity && item.damagedQuantity == 0
            else -> true
        }
    }

    if (selectedEvent == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Please select an event to perform warehouse return check-in.",
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
                // Reconciliation Dashboard Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (totalMissing > 0) CrimsonAlert.copy(alpha = 0.5f) else DenseBorderDark
                    ),
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
                                    text = "WAREHOUSE RECEIVING & AUDIT",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = DenseTextPrimaryDark,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Reconcile returned equipment against outbound manifest",
                                    fontSize = 11.sp,
                                    color = DenseTextSecondaryDark
                                )
                            }

                            Surface(
                                color = if (totalMissing == 0 && totalDamaged == 0 && totalReturned == totalPacked && totalPacked > 0) LaserGreen.copy(alpha = 0.15f) else if (totalMissing > 0) CrimsonAlert.copy(alpha = 0.15f) else CyanNeon.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (totalMissing == 0 && totalDamaged == 0 && totalReturned == totalPacked && totalPacked > 0) LaserGreen else if (totalMissing > 0) CrimsonAlert else CyanNeon
                                )
                            ) {
                                Text(
                                    text = "$returnPct% RECONCILED",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (totalMissing == 0 && totalDamaged == 0 && totalReturned == totalPacked && totalPacked > 0) LaserGreen else if (totalMissing > 0) CrimsonAlert else CyanNeon,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Scoreboard Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            ScoreMetric(
                                title = "Loaded",
                                value = "$totalPacked",
                                color = DenseTextPrimaryDark,
                                modifier = Modifier.weight(1f)
                            )
                            ScoreMetric(
                                title = "Returned",
                                value = "$totalReturned",
                                color = LaserGreen,
                                modifier = Modifier.weight(1f)
                            )
                            ScoreMetric(
                                title = "Missing",
                                value = "$totalMissing",
                                color = if (totalMissing > 0) CrimsonAlert else DenseTextSecondaryDark,
                                modifier = Modifier.weight(1f)
                            )
                            ScoreMetric(
                                title = "Damaged",
                                value = "$totalDamaged",
                                color = if (totalDamaged > 0) OrangeDamaged else DenseTextSecondaryDark,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.returnAllAccounted() },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = LaserGreen),
                                shape = RoundedCornerShape(4.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, LaserGreen.copy(alpha = 0.6f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(32.dp)
                                    .testTag("bulk_checkin_btn")
                            ) {
                                Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("CHECK-IN ALL CLEAN", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.3.sp)
                            }

                            Button(
                                onClick = { showFinalizeAuditDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (totalMissing == 0) LaserGreen else AmberConcert,
                                    contentColor = DenseBackgroundDark
                                ),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(32.dp)
                                    .testTag("finalize_audit_btn")
                            ) {
                                Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("SIGN-OFF AUDIT", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.3.sp)
                            }
                        }
                    }
                }
            }

            // Filter Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        "ALL" to "All (${allItems.size})",
                        "MISSING" to "Missing ($totalMissing)",
                        "DAMAGED" to "Damaged ($totalDamaged)",
                        "RETURNED" to "Returned ($totalReturned)"
                    ).forEach { (key, label) ->
                        val isSelected = filterMode == key
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isSelected) DenseSurfaceHighlightDark else DenseSurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CyanNeon else DenseBorderDark
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { filterMode = key }
                        ) {
                            Text(
                                text = label.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                letterSpacing = 0.3.sp,
                                color = if (isSelected) CyanNeon else DenseTextSecondaryDark,
                                modifier = Modifier.padding(vertical = 5.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Items List
            if (filteredItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (filterMode == "MISSING") "Awesome! No items currently missing." else if (filterMode == "DAMAGED") "Great! No damaged equipment reported." else "No equipment matches this filter.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(filteredItems, key = { it.id }) { item ->
                    ReturnItemCard(
                        item = item,
                        onIncrement = { viewModel.incrementReturnedQuantity(item) },
                        onDecrement = { viewModel.decrementReturnedQuantity(item) },
                        onMarkAllReturned = { viewModel.markItemFullyReturned(item) },
                        onFlagDamage = { itemToFlagDamage = item }
                    )
                }
            }

            // If an audit was completed, show share summary button
            if (audit != null) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LaserGreen.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = LaserGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Reconciliation Audit Signed Off", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                }
                                IconButton(
                                    onClick = {
                                        val reportText = buildString {
                                            append("=== AVL INVENTORY RECONCILIATION AUDIT ===\n")
                                            append("Event: ${selectedEvent?.name}\n")
                                            append("Client/Venue: ${selectedEvent?.clientOrVenue}\n")
                                            append("Reconciled By: ${audit?.reconciledBy}\n")
                                            append("Status: ${audit?.statusSummary}\n")
                                            append("Total Packed Out: ${audit?.totalPacked}\n")
                                            append("Total Returned In: ${audit?.totalReturned}\n")
                                            append("Missing Count: ${audit?.totalMissing}\n")
                                            append("Damaged / Repair Bench: ${audit?.totalDamaged}\n")
                                            if (audit?.reportNotes?.isNotBlank() == true) {
                                                append("Notes: ${audit?.reportNotes}\n")
                                            }
                                        }
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("AVL Audit Report", reportText))
                                        Toast.makeText(context, "Audit Report copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Copy report", tint = CyanNeon)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Signed by ${audit?.reconciledBy} • ${audit?.statusSummary}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    itemToFlagDamage?.let { item ->
        FlagDamageDialog(
            item = item,
            onDismiss = { itemToFlagDamage = null },
            onSave = { returned, damaged, report ->
                viewModel.flagItemDamagedOrMissing(item, returned, damaged, report)
                itemToFlagDamage = null
            }
        )
    }

    if (showFinalizeAuditDialog) {
        FinalizeAuditDialog(
            defaultManager = viewModel.currentTechName.value,
            totalPacked = totalPacked,
            totalReturned = totalReturned,
            totalMissing = totalMissing,
            totalDamaged = totalDamaged,
            onDismiss = { showFinalizeAuditDialog = false },
            onConfirm = { manager, notes ->
                selectedEvent?.id?.let { evtId ->
                    viewModel.finalizeWarehouseAudit(evtId, manager, notes)
                }
                showFinalizeAuditDialog = false
            }
        )
    }
}

@Composable
fun ScoreMetric(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DenseSurfaceHighlightDark,
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = title.uppercase(),
                fontSize = 9.sp,
                letterSpacing = 0.3.sp,
                color = DenseTextSecondaryDark
            )
        }
    }
}

@Composable
fun ReturnItemCard(
    item: EquipmentItemEntity,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onMarkAllReturned: () -> Unit,
    onFlagDamage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAllReturned = item.returnedQuantity == item.packedQuantity && item.packedQuantity > 0 && item.damagedQuantity == 0
    val isMissing = (item.packedQuantity - item.returnedQuantity - item.damagedQuantity) > 0
    val isDamaged = item.damagedQuantity > 0

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isDamaged) DenseSurfaceDark else if (isAllReturned) DenseSurfaceElevatedDark else DenseSurfaceDark
        ),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDamaged) OrangeDamaged.copy(alpha = 0.7f) else if (isMissing) CrimsonAlert.copy(alpha = 0.6f) else if (isAllReturned) LaserGreen.copy(alpha = 0.5f) else DenseBorderDark
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvlCategoryBadge(category = item.category)

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (isDamaged) {
                        Surface(
                            color = Color(0x33FF9100),
                            shape = RoundedCornerShape(3.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, OrangeDamaged)
                        ) {
                            Text(
                                text = "⚠️ DAMAGED (${item.damagedQuantity})",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangeDamaged,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                            )
                        }
                    }

                    if (isMissing) {
                        Surface(
                            color = CrimsonGlow,
                            shape = RoundedCornerShape(3.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonAlert)
                        ) {
                            Text(
                                text = "MISSING (${item.packedQuantity - item.returnedQuantity - item.damagedQuantity})",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CrimsonAlert,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                            )
                        }
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

            if (item.damageReport.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = Color(0x22FF9100),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OrangeDamaged.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🛠️ Bench Report: ${item.damageReport}",
                        fontSize = 11.sp,
                        color = OrangeDamaged,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Return Stepper & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickQuantityStepper(
                    currentValue = item.returnedQuantity,
                    targetValue = item.packedQuantity,
                    onIncrement = onIncrement,
                    onDecrement = onDecrement,
                    onMaxOut = onMarkAllReturned,
                    accentColor = if (isAllReturned) LaserGreen else CyanNeon
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedButton(
                        onClick = onFlagDamage,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangeDamaged),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OrangeDamaged.copy(alpha = 0.5f)),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("DEFECT", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.3.sp)
                    }

                    Button(
                        onClick = onMarkAllReturned,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAllReturned) LaserGreen else DenseSurfaceHighlightDark,
                            contentColor = if (isAllReturned) DenseBackgroundDark else DenseTextPrimaryDark
                        ),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isAllReturned) LaserGreen else DenseBorderDark
                        ),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(if (isAllReturned) "CHECKED IN" else "ALL IN", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.3.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun FlagDamageDialog(
    item: EquipmentItemEntity,
    onDismiss: () -> Unit,
    onSave: (returnedQty: Int, damagedQty: Int, damageReport: String) -> Unit
) {
    var returnedCount by remember { mutableStateOf(item.returnedQuantity.toString()) }
    var damagedCount by remember { mutableStateOf(item.damagedQuantity.toString()) }
    var damageNotes by remember { mutableStateOf(item.damageReport) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Flag Damaged / Defective Equipment", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.SemiBold,
                    color = DenseTextPrimaryDark
                )
                Text(
                    text = "Loaded on Truck: ${item.packedQuantity} units",
                    fontSize = 11.sp,
                    color = DenseTextSecondaryDark
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = returnedCount,
                        onValueChange = { returnedCount = it.filter { c -> c.isDigit() } },
                        label = { Text("Returned OK") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LaserGreen,
                            unfocusedBorderColor = DenseBorderDark,
                            focusedContainerColor = DenseSurfaceDark,
                            unfocusedContainerColor = DenseSurfaceDark
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = damagedCount,
                        onValueChange = { damagedCount = it.filter { c -> c.isDigit() } },
                        label = { Text("Damaged / Repair") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangeDamaged,
                            unfocusedBorderColor = DenseBorderDark,
                            focusedContainerColor = DenseSurfaceDark,
                            unfocusedContainerColor = DenseSurfaceDark
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = damageNotes,
                    onValueChange = { damageNotes = it },
                    label = { Text("Defect Description / Repair Bench Notes") },
                    placeholder = { Text("e.g. Broken XLR pin, cracked screen, burnt driver, water damage...") },
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangeDamaged,
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
                    val ret = returnedCount.toIntOrNull() ?: item.returnedQuantity
                    val dam = damagedCount.toIntOrNull() ?: item.damagedQuantity
                    onSave(ret, dam, damageNotes)
                },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeDamaged, contentColor = DenseBackgroundDark)
            ) {
                Text("Save to Bench", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
fun FinalizeAuditDialog(
    defaultManager: String,
    totalPacked: Int,
    totalReturned: Int,
    totalMissing: Int,
    totalDamaged: Int,
    onDismiss: () -> Unit,
    onConfirm: (manager: String, notes: String) -> Unit
) {
    var managerName by remember { mutableStateOf(defaultManager) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Finalize Warehouse Reconciliation Audit", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = DenseSurfaceHighlightDark,
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Audit Summary:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CyanNeon)
                        Text("• Packed Out: $totalPacked units", fontSize = 11.sp, color = DenseTextPrimaryDark)
                        Text("• Returned Clean: $totalReturned units", fontSize = 11.sp, color = LaserGreen)
                        Text("• Missing: $totalMissing units", fontSize = 11.sp, color = if (totalMissing > 0) CrimsonAlert else DenseTextPrimaryDark)
                        Text("• Repair Bench: $totalDamaged units", fontSize = 11.sp, color = if (totalDamaged > 0) OrangeDamaged else DenseTextPrimaryDark)
                    }
                }

                OutlinedTextField(
                    value = managerName,
                    onValueChange = { managerName = it },
                    label = { Text("Warehouse Manager / Sign-off Tech") },
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
                    label = { Text("Final Reconciliation Notes") },
                    placeholder = { Text("e.g. All cables wiped and restocked. Missing DI box billed to client...") },
                    minLines = 2,
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
                    onConfirm(managerName, notes)
                },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LaserGreen, contentColor = DenseBackgroundDark)
            ) {
                Text("Sign-Off & Close Gig", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontSize = 12.sp)
            }
        }
    )
}
