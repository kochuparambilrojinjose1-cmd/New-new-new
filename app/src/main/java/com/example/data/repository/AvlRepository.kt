package com.example.data.repository

import com.example.data.local.ChecklistItemDao
import com.example.data.local.ChecklistItemEntity
import com.example.data.local.ChecklistTemplateEntity
import com.example.data.local.EquipmentDao
import com.example.data.local.EquipmentItemEntity
import com.example.data.local.EquipmentLoadoutDao
import com.example.data.local.EquipmentLoadoutEntity
import com.example.data.local.EventAvailabilityDao
import com.example.data.local.EventAvailabilityEntity
import com.example.data.local.InventoryItemWithTransactions
import com.example.data.local.InventoryTransactionEntity
import com.example.data.local.LoadoutItemEntity
import com.example.data.local.LoadoutWithItems
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
import com.example.data.local.WarehouseInventoryDao
import com.example.data.local.WarehouseInventoryEntity
import com.example.model.AvlCategory
import com.example.model.AvlPresets
import com.example.model.ChecklistPhase
import com.example.model.EventStatus
import com.example.model.InventoryTransactionType
import com.example.model.ItemStatus
import com.example.model.LoadoutStatus
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
    private val eventAvailabilityDao: EventAvailabilityDao,
    private val equipmentLoadoutDao: EquipmentLoadoutDao,
    private val checklistItemDao: ChecklistItemDao,
    private val warehouseInventoryDao: WarehouseInventoryDao
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

    suspend fun deleteEvent(id: String) {
        equipmentDao.deleteAllEquipmentByEvent(id)
        setupLogDao.deleteAllLogsByEvent(id)
        teamNotificationDao.deleteAllNotificationsByEvent(id)
        eventDao.deleteEventById(id)
    }

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

    suspend fun deleteAllEquipmentByEvent(eventId: String) =
        equipmentDao.deleteAllEquipmentByEvent(eventId)

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

    suspend fun deleteAllLogsByEvent(eventId: String) =
        setupLogDao.deleteAllLogsByEvent(eventId)

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

    suspend fun deleteAllNotificationsByEvent(eventId: String) =
        teamNotificationDao.deleteAllNotificationsByEvent(eventId)

    suspend fun clearAllNotifications() =
        teamNotificationDao.clearAllNotifications()

    // Warehouse Audits
    fun getLatestAuditForEvent(eventId: String): Flow<WarehouseAuditEntity?> =
        warehouseAuditDao.getLatestAuditForEvent(eventId)

    suspend fun insertAudit(audit: WarehouseAuditEntity) = warehouseAuditDao.insertAudit(audit)

    // -------------------------------------------------------------
    // 1. EQUIPMENT LOADOUTS
    // -------------------------------------------------------------
    val allLoadouts: Flow<List<EquipmentLoadoutEntity>> = equipmentLoadoutDao.getAllLoadouts()

    val allLoadoutsWithItems: Flow<List<LoadoutWithItems>> = equipmentLoadoutDao.getAllLoadoutsWithItems()

    fun getLoadoutsByEvent(eventId: String): Flow<List<EquipmentLoadoutEntity>> =
        equipmentLoadoutDao.getLoadoutsByEvent(eventId)

    fun getLoadoutById(id: String): Flow<EquipmentLoadoutEntity?> =
        equipmentLoadoutDao.getLoadoutById(id)

    fun getLoadoutWithItems(loadoutId: String): Flow<LoadoutWithItems?> =
        equipmentLoadoutDao.getLoadoutWithItems(loadoutId)

    fun getEventLoadoutsWithItems(eventId: String): Flow<List<LoadoutWithItems>> =
        equipmentLoadoutDao.getEventLoadoutsWithItems(eventId)

    suspend fun insertLoadout(loadout: EquipmentLoadoutEntity) =
        equipmentLoadoutDao.insertLoadout(loadout)

    suspend fun updateLoadout(loadout: EquipmentLoadoutEntity) =
        equipmentLoadoutDao.updateLoadout(loadout)

    suspend fun updateLoadoutStatus(loadoutId: String, status: LoadoutStatus) =
        equipmentLoadoutDao.updateLoadoutStatus(loadoutId, status)

    suspend fun deleteLoadout(id: String) =
        equipmentLoadoutDao.deleteLoadoutById(id)

    fun getItemsForLoadout(loadoutId: String): Flow<List<LoadoutItemEntity>> =
        equipmentLoadoutDao.getItemsForLoadout(loadoutId)

    suspend fun insertLoadoutItem(item: LoadoutItemEntity) =
        equipmentLoadoutDao.insertLoadoutItem(item)

    suspend fun updateLoadoutItemPackProgress(
        itemId: String,
        packedQty: Int,
        loadedQty: Int,
        isVerified: Boolean
    ) = equipmentLoadoutDao.updateItemPackProgress(itemId, packedQty, loadedQty, isVerified)

    suspend fun deleteLoadoutItem(id: String) =
        equipmentLoadoutDao.deleteLoadoutItemById(id)

    // -------------------------------------------------------------
    // 2. CHECKLIST ITEMS & TEMPLATES
    // -------------------------------------------------------------
    fun getChecklistForEvent(eventId: String): Flow<List<ChecklistItemEntity>> =
        checklistItemDao.getChecklistForEvent(eventId)

    fun getChecklistByEventAndPhase(eventId: String, phase: ChecklistPhase): Flow<List<ChecklistItemEntity>> =
        checklistItemDao.getChecklistByEventAndPhase(eventId, phase)

    fun getPendingCriticalCount(eventId: String): Flow<Int> =
        checklistItemDao.getPendingCriticalCount(eventId)

    suspend fun toggleChecklistItem(
        id: String,
        completed: Boolean,
        tech: String,
        note: String
    ) = checklistItemDao.toggleChecklistItem(
        id = id,
        completed = completed,
        tech = tech,
        completedAt = if (completed) System.currentTimeMillis() else null,
        note = note
    )

    suspend fun insertChecklistItem(item: ChecklistItemEntity) =
        checklistItemDao.insertChecklistItem(item)

    suspend fun deleteChecklistItem(id: String) =
        checklistItemDao.deleteChecklistItemById(id)

    val allChecklistTemplates: Flow<List<ChecklistTemplateEntity>> =
        checklistItemDao.getAllChecklistTemplates()

    // -------------------------------------------------------------
    // 3. WAREHOUSE INVENTORY & QUANTITY ADJUSTMENTS
    // -------------------------------------------------------------
    val allInventory: Flow<List<WarehouseInventoryEntity>> =
        warehouseInventoryDao.getAllInventory()

    val lowStockInventory: Flow<List<WarehouseInventoryEntity>> =
        warehouseInventoryDao.getLowStockInventory()

    fun getInventoryByCategory(category: AvlCategory): Flow<List<WarehouseInventoryEntity>> =
        warehouseInventoryDao.getInventoryByCategory(category)

    fun getInventoryItemById(id: String): Flow<WarehouseInventoryEntity?> =
        warehouseInventoryDao.getInventoryItemById(id)

    fun getInventoryItemWithTransactions(id: String): Flow<InventoryItemWithTransactions?> =
        warehouseInventoryDao.getInventoryItemWithTransactions(id)

    suspend fun insertInventoryItem(item: WarehouseInventoryEntity) =
        warehouseInventoryDao.insertInventoryItem(item)

    suspend fun updateInventoryItem(item: WarehouseInventoryEntity) =
        warehouseInventoryDao.updateInventoryItem(item)

    suspend fun updateInventoryQuantities(
        id: String,
        available: Int,
        allocated: Int,
        maintenance: Int
    ) = warehouseInventoryDao.updateQuantities(id, available, allocated, maintenance)

    suspend fun deleteInventoryItem(id: String) =
        warehouseInventoryDao.deleteInventoryItemById(id)

    val recentInventoryTransactions: Flow<List<InventoryTransactionEntity>> =
        warehouseInventoryDao.getRecentTransactions()

    fun getTransactionsForItem(itemId: String): Flow<List<InventoryTransactionEntity>> =
        warehouseInventoryDao.getTransactionsForItem(itemId)

    suspend fun recordInventoryTransaction(transaction: InventoryTransactionEntity) =
        warehouseInventoryDao.insertTransaction(transaction)
}

