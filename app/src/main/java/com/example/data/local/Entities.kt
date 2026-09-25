package com.example.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.model.AvlCategory
import com.example.model.ChecklistPhase
import com.example.model.EventStatus
import com.example.model.InventoryTransactionType
import com.example.model.ItemStatus
import com.example.model.LoadoutStatus
import com.example.model.LogType
import com.example.model.MemberAvailability
import com.example.model.NotificationPriority
import com.example.model.UserRole
import com.example.model.WorkDepartment

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey val id: String,
    val username: String,
    val fullName: String,
    val email: String,
    val password: String,
    val role: UserRole,
    val department: WorkDepartment,
    val phone: String = "",
    val initials: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "production_events")
data class ProductionEventEntity(
    @PrimaryKey val id: String,
    val name: String,
    val clientOrVenue: String,
    val location: String,
    val eventDate: Long,
    val leadTech: String,
    val truckOrVehicle: String,
    val status: EventStatus,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "event_availabilities",
    foreignKeys = [
        ForeignKey(
            entity = ProductionEventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["eventId"]), Index(value = ["userId"]), Index(value = ["eventId", "userId"], unique = true)]
)
data class EventAvailabilityEntity(
    @PrimaryKey val id: String,
    val eventId: String,
    val userId: String,
    val userName: String,
    val userRole: UserRole,
    val department: WorkDepartment,
    val status: MemberAvailability = MemberAvailability.TENTATIVE,
    val note: String = "",
    val assignedPosition: String = "",
    val isAssigned: Boolean = false,
    val callTimeNote: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "equipment_items",
    foreignKeys = [
        ForeignKey(
            entity = ProductionEventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["eventId"]), Index(value = ["category"])]
)
data class EquipmentItemEntity(
    @PrimaryKey val id: String,
    val eventId: String,
    val name: String,
    val category: AvlCategory,
    val targetQuantity: Int,
    val packedQuantity: Int = 0,
    val returnedQuantity: Int = 0,
    val damagedQuantity: Int = 0,
    val barcodeOrTag: String = "",
    val storageLocation: String = "",
    val status: ItemStatus = ItemStatus.PENDING_PACK,
    val notes: String = "",
    val damageReport: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "setup_logs",
    foreignKeys = [
        ForeignKey(
            entity = ProductionEventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["eventId"])]
)
data class SetupLogEntity(
    @PrimaryKey val id: String,
    val eventId: String,
    val author: String,
    val zone: String,
    val logType: LogType,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "team_notifications",
    foreignKeys = [
        ForeignKey(
            entity = ProductionEventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["eventId"])]
)
data class TeamNotificationEntity(
    @PrimaryKey val id: String,
    val eventId: String,
    val sender: String,
    val priority: NotificationPriority,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isAcknowledged: Boolean = false,
    val acknowledgedBy: String = "",
    val acknowledgedAt: Long? = null,
    val isWorkUpdate: Boolean = false,
    val scheduledCallTime: Long? = null
)

@Entity(
    tableName = "warehouse_audits",
    foreignKeys = [
        ForeignKey(
            entity = ProductionEventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["eventId"])]
)
data class WarehouseAuditEntity(
    @PrimaryKey val id: String,
    val eventId: String,
    val reconciledAt: Long = System.currentTimeMillis(),
    val reconciledBy: String,
    val totalPacked: Int,
    val totalReturned: Int,
    val totalDamaged: Int,
    val totalMissing: Int,
    val statusSummary: String,
    val managerSignoff: String,
    val reportNotes: String = ""
)

// -------------------------------------------------------------
// 1. EQUIPMENT LOADOUTS SCHEMA
// -------------------------------------------------------------

@Entity(
    tableName = "equipment_loadouts",
    indices = [Index(value = ["eventId"]), Index(value = ["department"]), Index(value = ["status"])]
)
data class EquipmentLoadoutEntity(
    @PrimaryKey val id: String,
    val eventId: String? = null,
    val name: String,
    val department: WorkDepartment,
    val truckOrVehicle: String = "",
    val targetWeightLbs: Double = 0.0,
    val powerDrawAmps: Int = 0,
    val status: LoadoutStatus = LoadoutStatus.DRAFT,
    val assignedLeadTech: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "loadout_items",
    foreignKeys = [
        ForeignKey(
            entity = EquipmentLoadoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["loadoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["loadoutId"]), Index(value = ["category"])]
)
data class LoadoutItemEntity(
    @PrimaryKey val id: String,
    val loadoutId: String,
    val inventoryItemId: String? = null,
    val itemName: String,
    val category: AvlCategory,
    val requiredQuantity: Int,
    val packedQuantity: Int = 0,
    val loadedQuantity: Int = 0,
    val caseOrFlightRack: String = "",
    val barcodeOrRfid: String = "",
    val notes: String = "",
    val isVerified: Boolean = false
)

data class LoadoutWithItems(
    @Embedded val loadout: EquipmentLoadoutEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "loadoutId"
    )
    val items: List<LoadoutItemEntity>
)

// -------------------------------------------------------------
// 2. CHECKLIST ITEMS SCHEMA
// -------------------------------------------------------------

@Entity(
    tableName = "checklist_items",
    foreignKeys = [
        ForeignKey(
            entity = ProductionEventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["eventId"]), Index(value = ["phase"]), Index(value = ["isCompleted"])]
)
data class ChecklistItemEntity(
    @PrimaryKey val id: String,
    val eventId: String,
    val loadoutId: String? = null,
    val phase: ChecklistPhase,
    val department: WorkDepartment,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val completedBy: String = "",
    val completedAt: Long? = null,
    val isCritical: Boolean = false,
    val sortOrder: Int = 0,
    val verificationNote: String = ""
)

@Entity(
    tableName = "checklist_templates",
    indices = [Index(value = ["phase"])]
)
data class ChecklistTemplateEntity(
    @PrimaryKey val id: String,
    val phase: ChecklistPhase,
    val department: WorkDepartment,
    val title: String,
    val description: String = "",
    val isCritical: Boolean = false,
    val sortOrder: Int = 0
)

// -------------------------------------------------------------
// 3. WAREHOUSE INVENTORY & QUANTITY TRANSACTIONS SCHEMA
// -------------------------------------------------------------

@Entity(
    tableName = "warehouse_inventory",
    indices = [
        Index(value = ["skuOrBarcode"], unique = true),
        Index(value = ["category"]),
        Index(value = ["department"])
    ]
)
data class WarehouseInventoryEntity(
    @PrimaryKey val id: String,
    val skuOrBarcode: String,
    val itemName: String,
    val category: AvlCategory,
    val department: WorkDepartment,
    val modelNumber: String = "",
    val manufacturer: String = "",
    val totalStockQty: Int,
    val availableQty: Int,
    val allocatedQty: Int = 0,
    val maintenanceQty: Int = 0,
    val minimumThresholdQty: Int = 2,
    val warehouseAisleBin: String = "",
    val unitWeightLbs: Double = 0.0,
    val unitReplacementCost: Double = 0.0,
    val notes: String = "",
    val lastAuditedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "inventory_transactions",
    foreignKeys = [
        ForeignKey(
            entity = WarehouseInventoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["inventoryItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["inventoryItemId"]), Index(value = ["timestamp"])]
)
data class InventoryTransactionEntity(
    @PrimaryKey val id: String,
    val inventoryItemId: String,
    val eventId: String? = null,
    val loadoutId: String? = null,
    val transactionType: InventoryTransactionType,
    val quantityDelta: Int,
    val previousAvailableQty: Int,
    val newAvailableQty: Int,
    val technicianName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val referenceNotes: String = ""
)

data class InventoryItemWithTransactions(
    @Embedded val item: WarehouseInventoryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "inventoryItemId"
    )
    val transactions: List<InventoryTransactionEntity>
)
