package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.model.AvlCategory
import com.example.model.EventStatus
import com.example.model.ItemStatus
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
