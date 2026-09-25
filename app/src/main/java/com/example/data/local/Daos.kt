package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.EventStatus
import com.example.model.MemberAvailability
import com.example.model.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_accounts ORDER BY role DESC, fullName ASC")
    fun getAllUsers(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE id = :id")
    fun getUserById(id: String): Flow<UserAccountEntity?>

    @Query("SELECT * FROM user_accounts WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUsers(users: List<UserAccountEntity>)

    @Update
    suspend fun updateUser(user: UserAccountEntity)

    @Query("UPDATE user_accounts SET role = :newRole WHERE id = :userId")
    suspend fun updateUserRole(userId: String, newRole: UserRole)

    @Query("DELETE FROM user_accounts WHERE id = :id")
    suspend fun deleteUserById(id: String)
}

@Dao
interface EventAvailabilityDao {
    @Query("SELECT * FROM event_availabilities WHERE eventId = :eventId ORDER BY isAssigned DESC, status ASC, userName ASC")
    fun getAvailabilityForEvent(eventId: String): Flow<List<EventAvailabilityEntity>>

    @Query("SELECT * FROM event_availabilities WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getAvailabilityForUser(userId: String): Flow<List<EventAvailabilityEntity>>

    @Query("SELECT * FROM event_availabilities WHERE eventId = :eventId AND userId = :userId LIMIT 1")
    fun getUserAvailabilityForEvent(eventId: String, userId: String): Flow<EventAvailabilityEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAvailability(availability: EventAvailabilityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(availabilities: List<EventAvailabilityEntity>)

    @Query("UPDATE event_availabilities SET status = :status, note = :note, updatedAt = :now WHERE eventId = :eventId AND userId = :userId")
    suspend fun updateMemberStatus(eventId: String, userId: String, status: MemberAvailability, note: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE event_availabilities SET isAssigned = :isAssigned, assignedPosition = :position, callTimeNote = :callTime, updatedAt = :now WHERE eventId = :eventId AND userId = :userId")
    suspend fun updateAssignment(eventId: String, userId: String, isAssigned: Boolean, position: String, callTime: String, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM event_availabilities WHERE eventId = :eventId AND userId = :userId")
    suspend fun deleteAvailability(eventId: String, userId: String)
}

@Dao
interface ProductionEventDao {
    @Query("SELECT * FROM production_events ORDER BY eventDate DESC")
    fun getAllEvents(): Flow<List<ProductionEventEntity>>

    @Query("SELECT * FROM production_events WHERE id = :id")
    fun getEventById(id: String): Flow<ProductionEventEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: ProductionEventEntity)

    @Update
    suspend fun updateEvent(event: ProductionEventEntity)

    @Query("UPDATE production_events SET status = :newStatus WHERE id = :eventId")
    suspend fun updateEventStatus(eventId: String, newStatus: EventStatus)

    @Query("DELETE FROM production_events WHERE id = :id")
    suspend fun deleteEventById(id: String)
}

@Dao
interface EquipmentDao {
    @Query("SELECT * FROM equipment_items WHERE eventId = :eventId ORDER BY category ASC, name ASC")
    fun getEquipmentByEvent(eventId: String): Flow<List<EquipmentItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: EquipmentItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<EquipmentItemEntity>)

    @Update
    suspend fun updateItem(item: EquipmentItemEntity)

    @Query("DELETE FROM equipment_items WHERE id = :id")
    suspend fun deleteItemById(id: String)

    @Query("DELETE FROM equipment_items WHERE eventId = :eventId")
    suspend fun deleteAllEquipmentByEvent(eventId: String)

    @Query("UPDATE equipment_items SET packedQuantity = :packedQty, status = CASE WHEN :packedQty >= targetQuantity THEN 'LOADED' WHEN :packedQty > 0 THEN 'PACKED' ELSE 'PENDING_PACK' END, lastUpdated = :now WHERE id = :id")
    suspend fun updatePackedQuantity(id: String, packedQty: Int, now: Long = System.currentTimeMillis())

    @Query("UPDATE equipment_items SET returnedQuantity = :returnedQty, damagedQuantity = :damagedQty, damageReport = :damageReport, status = CASE WHEN :damagedQty > 0 THEN 'BENCH_REPAIR' WHEN :returnedQty >= packedQuantity THEN 'RETURNED_OK' ELSE 'FLAGGED_DISCREPANCY' END, lastUpdated = :now WHERE id = :id")
    suspend fun updateReturnStatus(id: String, returnedQty: Int, damagedQty: Int, damageReport: String, now: Long = System.currentTimeMillis())
}

@Dao
interface SetupLogDao {
    @Query("SELECT * FROM setup_logs WHERE eventId = :eventId ORDER BY timestamp DESC")
    fun getLogsByEvent(eventId: String): Flow<List<SetupLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SetupLogEntity)

    @Query("DELETE FROM setup_logs WHERE id = :id")
    suspend fun deleteLogById(id: String)

    @Query("DELETE FROM setup_logs WHERE eventId = :eventId")
    suspend fun deleteAllLogsByEvent(eventId: String)
}

@Dao
interface TeamNotificationDao {
    @Query("SELECT * FROM team_notifications WHERE eventId = :eventId ORDER BY timestamp DESC")
    fun getNotificationsByEvent(eventId: String): Flow<List<TeamNotificationEntity>>

    @Query("SELECT * FROM team_notifications ORDER BY timestamp DESC LIMIT 50")
    fun getAllRecentNotifications(): Flow<List<TeamNotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: TeamNotificationEntity)

    @Query("UPDATE team_notifications SET isAcknowledged = 1, acknowledgedBy = :techName, acknowledgedAt = :now WHERE id = :id")
    suspend fun acknowledgeNotification(id: String, techName: String, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM team_notifications WHERE id = :id")
    suspend fun deleteNotificationById(id: String)

    @Query("DELETE FROM team_notifications WHERE eventId = :eventId")
    suspend fun deleteAllNotificationsByEvent(eventId: String)

    @Query("DELETE FROM team_notifications")
    suspend fun clearAllNotifications()
}

@Dao
interface WarehouseAuditDao {
    @Query("SELECT * FROM warehouse_audits WHERE eventId = :eventId ORDER BY reconciledAt DESC LIMIT 1")
    fun getLatestAuditForEvent(eventId: String): Flow<WarehouseAuditEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudit(audit: WarehouseAuditEntity)
}
