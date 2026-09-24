package com.example.data.repository

import com.example.data.local.EquipmentDao
import com.example.data.local.EquipmentItemEntity
import com.example.data.local.EventAvailabilityDao
import com.example.data.local.EventAvailabilityEntity
import com.example.data.local.ProductionEventDao
import com.example.data.local.ProductionEventEntity
import com.example.data.local.SetupLogDao
import com.example.data.local.SetupLogEntity
import com.example.data.local.TeamNotificationDao
import com.example.data.local.TeamNotificationEntity
import com.example.data.local.UserAccountDao
import com.example.data.local.UserAccountEntity
import com.example.data.local.WarehouseAuditDao
import com.example.data.local.WarehouseAuditEntity
import com.example.model.AvlPresets
import com.example.model.EventStatus
import com.example.model.ItemStatus
import com.example.model.MemberAvailability
import com.example.model.UserRole
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class AvlRepository(
    private val eventDao: ProductionEventDao,
    private val equipmentDao: EquipmentDao,
    private val setupLogDao: SetupLogDao,
    private val teamNotificationDao: TeamNotificationDao,
    private val warehouseAuditDao: WarehouseAuditDao,
    private val userAccountDao: UserAccountDao,
    private val eventAvailabilityDao: EventAvailabilityDao
) {
    // Users & Roles
    val allUsers: Flow<List<UserAccountEntity>> = userAccountDao.getAllUsers()

    fun getUserById(id: String): Flow<UserAccountEntity?> = userAccountDao.getUserById(id)

    suspend fun getUserByUsername(username: String): UserAccountEntity? =
        userAccountDao.getUserByUsername(username)

    suspend fun getUserByEmail(email: String): UserAccountEntity? =
        userAccountDao.getUserByEmail(email)

    suspend fun insertUser(user: UserAccountEntity) = userAccountDao.insertUser(user)

    suspend fun updateUser(user: UserAccountEntity) = userAccountDao.updateUser(user)

    suspend fun updateUserRole(userId: String, newRole: UserRole) =
        userAccountDao.updateUserRole(userId, newRole)

    suspend fun deleteUser(userId: String) = userAccountDao.deleteUserById(userId)

    // Event Availability & Member Interactions
    fun getAvailabilityForEvent(eventId: String): Flow<List<EventAvailabilityEntity>> =
        eventAvailabilityDao.getAvailabilityForEvent(eventId)

    fun getUserAvailabilityForEvent(eventId: String, userId: String): Flow<EventAvailabilityEntity?> =
        eventAvailabilityDao.getUserAvailabilityForEvent(eventId, userId)

    suspend fun upsertAvailability(availability: EventAvailabilityEntity) =
        eventAvailabilityDao.upsertAvailability(availability)

    suspend fun updateMemberAvailability(
        eventId: String,
        userId: String,
        status: MemberAvailability,
        note: String
    ) = eventAvailabilityDao.updateMemberStatus(eventId, userId, status, note)

    suspend fun assignCrewMember(
        eventId: String,
        userId: String,
        isAssigned: Boolean,
        position: String,
        callTime: String
    ) = eventAvailabilityDao.updateAssignment(eventId, userId, isAssigned, position, callTime)

    // Events
    val allEvents: Flow<List<ProductionEventEntity>> = eventDao.getAllEvents()

    fun getEventById(id: String): Flow<ProductionEventEntity?> = eventDao.getEventById(id)

    suspend fun insertEvent(event: ProductionEventEntity) = eventDao.insertEvent(event)

    suspend fun updateEvent(event: ProductionEventEntity) = eventDao.updateEvent(event)

    suspend fun updateEventStatus(eventId: String, status: EventStatus) =
        eventDao.updateEventStatus(eventId, status)

    suspend fun deleteEvent(id: String) = eventDao.deleteEventById(id)

    suspend fun addDefaultEquipmentPackage(eventId: String) {
        val items = AvlPresets.defaultInventory.map { preset ->
            EquipmentItemEntity(
                id = UUID.randomUUID().toString(),
                eventId = eventId,
                name = preset.name,
                category = preset.category,
                targetQuantity = preset.defaultQty,
                packedQuantity = 0,
                returnedQuantity = 0,
                damagedQuantity = 0,
                barcodeOrTag = preset.suggestedBarcode,
                storageLocation = preset.storageCase,
                status = ItemStatus.PENDING_PACK,
                notes = preset.notes
            )
        }
        equipmentDao.insertAll(items)
    }

    // Equipment
    fun getEquipmentByEvent(eventId: String): Flow<List<EquipmentItemEntity>> =
        equipmentDao.getEquipmentByEvent(eventId)

    suspend fun insertEquipmentItem(item: EquipmentItemEntity) = equipmentDao.insertItem(item)

    suspend fun updateEquipmentItem(item: EquipmentItemEntity) = equipmentDao.updateItem(item)

    suspend fun updatePackedQuantity(id: String, packedQty: Int) =
        equipmentDao.updatePackedQuantity(id, packedQty)

    suspend fun updateReturnStatus(id: String, returnedQty: Int, damagedQty: Int, damageReport: String) =
        equipmentDao.updateReturnStatus(id, returnedQty, damagedQty, damageReport)

    suspend fun deleteEquipmentItem(id: String) = equipmentDao.deleteItemById(id)

    suspend fun packAllEquipment(items: List<EquipmentItemEntity>) {
        items.forEach { item ->
            equipmentDao.updatePackedQuantity(item.id, item.targetQuantity)
        }
    }

    suspend fun checkInAllEquipment(items: List<EquipmentItemEntity>) {
        items.forEach { item ->
            if (item.damagedQuantity == 0) {
                equipmentDao.updateReturnStatus(item.id, item.packedQuantity, 0, "")
            }
        }
    }

    // Setup Logs
    fun getLogsByEvent(eventId: String): Flow<List<SetupLogEntity>> =
        setupLogDao.getLogsByEvent(eventId)

    suspend fun insertLog(log: SetupLogEntity) = setupLogDao.insertLog(log)

    suspend fun deleteLog(id: String) = setupLogDao.deleteLogById(id)

    // Team Notifications
    fun getNotificationsByEvent(eventId: String): Flow<List<TeamNotificationEntity>> =
        teamNotificationDao.getNotificationsByEvent(eventId)

    val allRecentNotifications: Flow<List<TeamNotificationEntity>> =
        teamNotificationDao.getAllRecentNotifications()

    suspend fun insertNotification(notification: TeamNotificationEntity) =
        teamNotificationDao.insertNotification(notification)

    suspend fun acknowledgeNotification(id: String, techName: String) =
        teamNotificationDao.acknowledgeNotification(id, techName)

    suspend fun deleteNotification(id: String) = teamNotificationDao.deleteNotificationById(id)

    // Warehouse Audits
    fun getLatestAuditForEvent(eventId: String): Flow<WarehouseAuditEntity?> =
        warehouseAuditDao.getLatestAuditForEvent(eventId)

    suspend fun insertAudit(audit: WarehouseAuditEntity) = warehouseAuditDao.insertAudit(audit)
}

