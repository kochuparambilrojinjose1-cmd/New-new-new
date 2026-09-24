package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AvlDatabase
import com.example.data.local.EquipmentItemEntity
import com.example.data.local.EventAvailabilityEntity
import com.example.data.local.ProductionEventEntity
import com.example.data.local.SetupLogEntity
import com.example.data.local.TeamNotificationEntity
import com.example.data.local.UserAccountEntity
import com.example.data.local.WarehouseAuditEntity
import com.example.data.repository.AvlRepository
import com.example.model.AvlCategory
import com.example.model.EventStatus
import com.example.model.ItemStatus
import com.example.model.LogType
import com.example.model.MemberAvailability
import com.example.model.NotificationPriority
import com.example.model.UserRole
import com.example.model.WorkDepartment
import com.example.util.NotificationHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class AvlViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AvlRepository

    init {
        val db = AvlDatabase.getDatabase(application, viewModelScope)
        repository = AvlRepository(
            db.eventDao(),
            db.equipmentDao(),
            db.setupLogDao(),
            db.teamNotificationDao(),
            db.warehouseAuditDao(),
            db.userAccountDao(),
            db.eventAvailabilityDao()
        )
        NotificationHelper.initChannels(application)
    }

    // -------------------------------------------------------------
    // USERS, AUTHENTICATION & 4-TIER ROLES
    // -------------------------------------------------------------
    val allUsers: StateFlow<List<UserAccountEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentUser = MutableStateFlow<UserAccountEntity?>(null)
    val currentUser: StateFlow<UserAccountEntity?> = _currentUser.asStateFlow()

    val authErrorMessage = MutableStateFlow<String?>(null)
    val authSuccessMessage = MutableStateFlow<String?>(null)

    val currentTechName = MutableStateFlow("Dave Vance (Owner)")

    // Auto-login default user if not logged in
    init {
        viewModelScope.launch {
            allUsers.collect { users ->
                if (_currentUser.value == null && users.isNotEmpty()) {
                    val defaultUser = users.find { it.role == UserRole.OWNER } ?: users.first()
                    _currentUser.value = defaultUser
                    currentTechName.value = "${defaultUser.fullName} (${defaultUser.role.displayName})"
                }
            }
        }
    }

    fun login(usernameOrEmail: String, password: String): Boolean {
        authErrorMessage.value = null
        val query = usernameOrEmail.trim()
        val user = allUsers.value.find {
            (it.username.equals(query, ignoreCase = true) || it.email.equals(query, ignoreCase = true)) &&
                    it.password == password.trim()
        }

        return if (user != null) {
            _currentUser.value = user
            currentTechName.value = "${user.fullName} (${user.role.displayName})"
            authSuccessMessage.value = "Welcome back, ${user.fullName} (${user.role.displayName})!"
            NotificationHelper.postWorkUpdateNotification(
                getApplication(),
                "Logged In Successfully",
                "Active session as ${user.fullName} [${user.role.displayName}]",
                NotificationPriority.GENERAL
            )
            true
        } else {
            authErrorMessage.value = "Invalid username/email or password. Check credentials or select demo user."
            false
        }
    }

    fun register(
        username: String,
        fullName: String,
        email: String,
        password: String,
        role: UserRole,
        department: WorkDepartment,
        phone: String
    ): Boolean {
        authErrorMessage.value = null
        val cleanUsername = username.trim().lowercase()
        val cleanEmail = email.trim().lowercase()

        if (cleanUsername.length < 3) {
            authErrorMessage.value = "Username must be at least 3 characters."
            return false
        }
        if (password.length < 4) {
            authErrorMessage.value = "Password must be at least 4 characters."
            return false
        }
        if (fullName.isBlank()) {
            authErrorMessage.value = "Full Name is required."
            return false
        }

        val existingUser = allUsers.value.find {
            it.username.equals(cleanUsername, ignoreCase = true) || it.email.equals(cleanEmail, ignoreCase = true)
        }
        if (existingUser != null) {
            authErrorMessage.value = "Username or email is already registered."
            return false
        }

        val initials = fullName.split(" ").filter { it.isNotBlank() }.take(2).map { it.first().uppercase() }.joinToString("")
        val newUser = UserAccountEntity(
            id = "usr-" + UUID.randomUUID().toString().take(8),
            username = cleanUsername,
            fullName = fullName.trim(),
            email = cleanEmail,
            password = password,
            role = role,
            department = department,
            phone = phone.trim(),
            initials = if (initials.isNotBlank()) initials else "AV"
        )

        viewModelScope.launch {
            repository.insertUser(newUser)
            _currentUser.value = newUser
            currentTechName.value = "${newUser.fullName} (${newUser.role.displayName})"
            authSuccessMessage.value = "Account created! You are logged in as ${newUser.fullName} [${newUser.role.displayName}]."

            sendTeamNotification(
                eventId = _selectedEventId.value ?: "general",
                sender = "System Dispatch",
                priority = NotificationPriority.GENERAL,
                title = "New Member Registered",
                message = "${newUser.fullName} joined the crew as ${newUser.role.displayName} (${newUser.department.displayName})."
            )

            NotificationHelper.postWorkUpdateNotification(
                getApplication(),
                "Account Registered",
                "Registered ${newUser.fullName} with ${newUser.role.displayName} privileges.",
                NotificationPriority.GENERAL
            )
        }
        return true
    }

    fun switchUser(user: UserAccountEntity) {
        _currentUser.value = user
        currentTechName.value = "${user.fullName} (${user.role.displayName})"
        authSuccessMessage.value = "Switched to ${user.fullName} (${user.role.displayName})"
    }

    fun logout() {
        _currentUser.value = null
        currentTechName.value = "Guest Tech"
    }

    fun updateUserRole(userId: String, newRole: UserRole) {
        val caller = _currentUser.value
        if (caller == null || (caller.role != UserRole.OWNER && caller.role != UserRole.ADMIN)) {
            authErrorMessage.value = "Only Owner or Admin can modify team roles."
            return
        }
        viewModelScope.launch {
            repository.updateUserRole(userId, newRole)
            if (_currentUser.value?.id == userId) {
                _currentUser.value = _currentUser.value?.copy(role = newRole)
            }
        }
    }

    // -------------------------------------------------------------
    // EVENTS & PRODUCTION SCHEDULES
    // -------------------------------------------------------------
    val allEvents: StateFlow<List<ProductionEventEntity>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedEventId = MutableStateFlow<String?>(null)
    val selectedEventId: StateFlow<String?> = _selectedEventId.asStateFlow()

    init {
        viewModelScope.launch {
            allEvents.collect { events ->
                if (_selectedEventId.value == null && events.isNotEmpty()) {
                    _selectedEventId.value = events.first().id
                }
            }
        }
    }

    val selectedEvent: StateFlow<ProductionEventEntity?> = _selectedEventId
        .flatMapLatest { id ->
            if (id != null) repository.getEventById(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // -------------------------------------------------------------
    // EVENT AVAILABILITY & MEMBER RSVP
    // -------------------------------------------------------------
    val currentEventAvailabilities: StateFlow<List<EventAvailabilityEntity>> = _selectedEventId
        .flatMapLatest { id ->
            if (id != null) repository.getAvailabilityForEvent(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentUserAvailability: StateFlow<EventAvailabilityEntity?> = combine(
        currentEventAvailabilities,
        _currentUser
    ) { list, user ->
        if (user != null) list.find { it.userId == user.id } else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setAvailabilityForEvent(
        eventId: String,
        status: MemberAvailability,
        note: String
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val existing = currentEventAvailabilities.value.find { it.userId == user.id }
            val record = EventAvailabilityEntity(
                id = existing?.id ?: UUID.randomUUID().toString(),
                eventId = eventId,
                userId = user.id,
                userName = user.fullName,
                userRole = user.role,
                department = user.department,
                status = status,
                note = note,
                assignedPosition = existing?.assignedPosition ?: "",
                isAssigned = existing?.isAssigned ?: false,
                callTimeNote = existing?.callTimeNote ?: "",
                updatedAt = System.currentTimeMillis()
            )
            repository.upsertAvailability(record)

            val event = allEvents.value.find { it.id == eventId }
            val eventTitle = event?.name ?: "Upcoming Program"

            repository.insertNotification(
                TeamNotificationEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = eventId,
                    sender = user.fullName,
                    priority = NotificationPriority.GENERAL,
                    title = "Member Availability: ${status.displayName}",
                    message = "${user.fullName} marked ${status.displayName} for '$eventTitle'${if (note.isNotBlank()) " ($note)" else ""}.",
                    isWorkUpdate = true
                )
            )

            NotificationHelper.postWorkUpdateNotification(
                getApplication(),
                "Availability Updated",
                "${user.fullName} is ${status.displayName} for $eventTitle",
                NotificationPriority.GENERAL
            )
        }
    }

    fun assignCrewMember(
        eventId: String,
        userId: String,
        position: String,
        callTime: String
    ) {
        val caller = _currentUser.value
        if (caller != null && caller.role == UserRole.CREW) {
            authErrorMessage.value = "Crew members cannot assign roster positions. Contact Admin/Manager."
            return
        }

        viewModelScope.launch {
            repository.assignCrewMember(eventId, userId, isAssigned = true, position = position, callTime = callTime)

            val targetUser = allUsers.value.find { it.id == userId }
            val event = allEvents.value.find { it.id == eventId }
            val eventName = event?.name ?: "Production Gig"

            val notifyMsg = "${targetUser?.fullName ?: "Crew Tech"} has been assigned to $eventName as '$position'. $callTime"
            repository.insertNotification(
                TeamNotificationEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = eventId,
                    sender = caller?.fullName ?: "Stage Manager",
                    priority = NotificationPriority.ALERT,
                    title = "Crew Call: $position",
                    message = notifyMsg,
                    isWorkUpdate = true
                )
            )

            NotificationHelper.postWorkUpdateNotification(
                getApplication(),
                "Crew Call Assignment",
                notifyMsg,
                NotificationPriority.ALERT,
                NotificationHelper.CHANNEL_CALL_TIMES
            )
        }
    }

    fun unassignCrewMember(eventId: String, userId: String) {
        viewModelScope.launch {
            repository.assignCrewMember(eventId, userId, isAssigned = false, position = "", callTime = "")
        }
    }

    // -------------------------------------------------------------
    // EQUIPMENT CHECKLISTS & INVENTORY
    // -------------------------------------------------------------
    val currentEquipment: StateFlow<List<EquipmentItemEntity>> = _selectedEventId
        .flatMapLatest { id ->
            if (id != null) repository.getEquipmentByEvent(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentSetupLogs: StateFlow<List<SetupLogEntity>> = _selectedEventId
        .flatMapLatest { id ->
            if (id != null) repository.getLogsByEvent(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Setup Logs filter
    val selectedLogTypeFilter = MutableStateFlow<LogType?>(null)

    val filteredLogs: StateFlow<List<SetupLogEntity>> = combine(
        currentSetupLogs,
        selectedLogTypeFilter
    ) { logs, typeFilter ->
        if (typeFilter == null) logs else logs.filter { it.logType == typeFilter }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentNotifications: StateFlow<List<TeamNotificationEntity>> = _selectedEventId
        .flatMapLatest { id ->
            if (id != null) repository.getNotificationsByEvent(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRecentNotifications: StateFlow<List<TeamNotificationEntity>> =
        repository.allRecentNotifications
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentAudit: StateFlow<WarehouseAuditEntity?> = _selectedEventId
        .flatMapLatest { id ->
            if (id != null) repository.getLatestAuditForEvent(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Filters for Checklist
    val selectedCategoryFilter = MutableStateFlow<AvlCategory?>(null)
    val searchQuery = MutableStateFlow("")
    val statusFilter = MutableStateFlow("ALL")

    val filteredEquipment: StateFlow<List<EquipmentItemEntity>> = combine(
        currentEquipment,
        selectedCategoryFilter,
        searchQuery,
        statusFilter
    ) { items, cat, query, status ->
        items.filter { item ->
            val matchesCategory = cat == null || item.category == cat
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.barcodeOrTag.contains(query, ignoreCase = true) ||
                    item.storageLocation.contains(query, ignoreCase = true)
            val matchesStatus = when (status) {
                "PENDING" -> item.packedQuantity < item.targetQuantity
                "PACKED" -> item.packedQuantity in 1 until item.targetQuantity
                "LOADED" -> item.packedQuantity >= item.targetQuantity
                "RETURN_OK" -> item.status == ItemStatus.RETURNED_OK
                "MISSING_DAMAGED" -> item.status == ItemStatus.FLAGGED_DISCREPANCY || item.status == ItemStatus.BENCH_REPAIR
                else -> true
            }
            matchesCategory && matchesQuery && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Progress Stats
    val packingProgress: StateFlow<Float> = currentEquipment.combine(currentEquipment) { items, _ ->
        val totalTarget = items.sumOf { it.targetQuantity }
        val totalPacked = items.sumOf { it.packedQuantity }
        if (totalTarget == 0) 0f else (totalPacked.toFloat() / totalTarget.toFloat()).coerceIn(0f, 1f)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val returnProgress: StateFlow<Float> = currentEquipment.combine(currentEquipment) { items, _ ->
        val totalPacked = items.sumOf { it.packedQuantity }
        val totalReturned = items.sumOf { it.returnedQuantity }
        if (totalPacked == 0) 0f else (totalReturned.toFloat() / totalPacked.toFloat()).coerceIn(0f, 1f)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val missingItemsCount: StateFlow<Int> = currentEquipment.combine(currentEquipment) { items, _ ->
        items.count { it.status == ItemStatus.FLAGGED_DISCREPANCY || (it.packedQuantity > 0 && it.returnedQuantity < it.packedQuantity && it.damagedQuantity == 0) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val damagedItemsCount: StateFlow<Int> = currentEquipment.combine(currentEquipment) { items, _ ->
        items.count { it.status == ItemStatus.BENCH_REPAIR || it.damagedQuantity > 0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun selectEvent(id: String) {
        _selectedEventId.value = id
    }

    // -------------------------------------------------------------
    // EVENT CREATION WITH AUTOMATED NOTIFICATIONS & ROSTER
    // -------------------------------------------------------------
    fun createEvent(
        name: String,
        venue: String,
        location: String,
        leadTech: String,
        truck: String,
        dateMs: Long,
        notes: String,
        withDefaultPackage: Boolean
    ) {
        viewModelScope.launch {
            val eventId = "evt-" + UUID.randomUUID().toString().take(8)
            val newEvent = ProductionEventEntity(
                id = eventId,
                name = name,
                clientOrVenue = venue,
                location = location,
                eventDate = dateMs,
                leadTech = leadTech.ifBlank { currentTechName.value },
                truckOrVehicle = truck,
                status = EventStatus.PLANNING,
                notes = notes
            )
            repository.insertEvent(newEvent)
            if (withDefaultPackage) {
                repository.addDefaultEquipmentPackage(eventId)
            }

            // Seed roster response records for all members
            val users = allUsers.value
            val initialAvails = users.map { u ->
                EventAvailabilityEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = eventId,
                    userId = u.id,
                    userName = u.fullName,
                    userRole = u.role,
                    department = u.department,
                    status = if (u.id == _currentUser.value?.id) MemberAvailability.AVAILABLE else MemberAvailability.TENTATIVE,
                    note = if (u.id == _currentUser.value?.id) "Creator / Lead" else "",
                    assignedPosition = if (u.id == _currentUser.value?.id) "Lead Coordinator" else "",
                    isAssigned = u.id == _currentUser.value?.id,
                    callTimeNote = ""
                )
            }
            initialAvails.forEach { repository.upsertAvailability(it) }

            // Auto add initial creation log
            repository.insertLog(
                SetupLogEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = eventId,
                    author = leadTech.ifBlank { currentTechName.value },
                    zone = "Production HQ",
                    logType = LogType.MILESTONE,
                    content = "Event checklist initialized. Outbound staging ready."
                )
            )

            // Auto add in-app work update notification
            repository.insertNotification(
                TeamNotificationEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = eventId,
                    sender = _currentUser.value?.fullName ?: "Production Lead",
                    priority = NotificationPriority.ALERT,
                    title = "📅 New Upcoming Program: $name",
                    message = "New AVL gig scheduled at $venue. All team members please interact and submit your availability!",
                    isWorkUpdate = true
                )
            )

            // Trigger real Android system notification on-time
            NotificationHelper.postWorkUpdateNotification(
                getApplication(),
                "New Gig Created: $name",
                "Scheduled at $venue. Crew members please mark your availability.",
                NotificationPriority.ALERT
            )

            _selectedEventId.value = eventId
        }
    }

    fun updateEventStatus(eventId: String, newStatus: EventStatus) {
        viewModelScope.launch {
            repository.updateEventStatus(eventId, newStatus)
            val eventName = allEvents.value.find { it.id == eventId }?.name ?: "Event"

            repository.insertLog(
                SetupLogEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = eventId,
                    author = currentTechName.value,
                    zone = "Production Stage",
                    logType = LogType.MILESTONE,
                    content = "Event status updated to: ${newStatus.displayName}"
                )
            )
            repository.insertNotification(
                TeamNotificationEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = eventId,
                    sender = currentTechName.value,
                    priority = NotificationPriority.LOGISTICS,
                    title = "Status: ${newStatus.displayName}",
                    message = "Production phase transitioned to ${newStatus.displayName} for $eventName.",
                    isWorkUpdate = true
                )
            )

            NotificationHelper.postWorkUpdateNotification(
                getApplication(),
                "Work Status Update: $eventName",
                "Phase transitioned to ${newStatus.displayName}",
                NotificationPriority.LOGISTICS
            )
        }
    }

    fun deleteEvent(eventId: String) {
        val caller = _currentUser.value
        if (caller != null && caller.role != UserRole.OWNER && caller.role != UserRole.ADMIN) {
            authErrorMessage.value = "Only Owner or Admin can delete events."
            return
        }
        viewModelScope.launch {
            repository.deleteEvent(eventId)
            val remaining = allEvents.value.filter { it.id != eventId }
            _selectedEventId.value = remaining.firstOrNull()?.id
        }
    }

    // Equipment Checklist Actions (Packing Outbound)
    fun addEquipmentItem(
        eventId: String,
        name: String,
        category: AvlCategory,
        targetQty: Int,
        storageCase: String,
        barcode: String,
        notes: String
    ) {
        viewModelScope.launch {
            val item = EquipmentItemEntity(
                id = UUID.randomUUID().toString(),
                eventId = eventId,
                name = name,
                category = category,
                targetQuantity = targetQty.coerceAtLeast(1),
                packedQuantity = 0,
                storageLocation = storageCase,
                barcodeOrTag = barcode,
                notes = notes,
                status = ItemStatus.PENDING_PACK
            )
            repository.insertEquipmentItem(item)
        }
    }

    fun updatePackedQuantity(id: String, packedQty: Int) {
        viewModelScope.launch {
            repository.updatePackedQuantity(id, packedQty)
        }
    }

    fun setPackedQuantity(item: EquipmentItemEntity, qty: Int) {
        updatePackedQuantity(item.id, qty)
    }

    fun incrementPackedQuantity(item: EquipmentItemEntity) {
        if (item.packedQuantity < item.targetQuantity) {
            updatePackedQuantity(item.id, item.packedQuantity + 1)
        }
    }

    fun decrementPackedQuantity(item: EquipmentItemEntity) {
        if (item.packedQuantity > 0) {
            updatePackedQuantity(item.id, item.packedQuantity - 1)
        }
    }

    fun markItemFullyPacked(item: EquipmentItemEntity) {
        updatePackedQuantity(item.id, item.targetQuantity)
    }

    fun packAllCurrentItems() {
        viewModelScope.launch {
            repository.packAllEquipment(currentEquipment.value)
            val eventId = _selectedEventId.value ?: return@launch
            repository.insertLog(
                SetupLogEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = eventId,
                    author = currentTechName.value,
                    zone = "Warehouse Loading Dock",
                    logType = LogType.SIGN_OFF,
                    content = "All equipment marked as packed and verified against master AVL manifest."
                )
            )
            repository.insertNotification(
                TeamNotificationEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = eventId,
                    sender = currentTechName.value,
                    priority = NotificationPriority.LOGISTICS,
                    title = "Outbound Packing Completed",
                    message = "All ${currentEquipment.value.size} manifest line items packed and staged for truck loading."
                )
            )
        }
    }

    fun packAllFiltered() {
        viewModelScope.launch {
            val itemsToPack = filteredEquipment.value
            itemsToPack.forEach { item ->
                repository.updatePackedQuantity(item.id, item.targetQuantity)
            }
        }
    }

    fun deleteEquipmentItem(id: String) {
        viewModelScope.launch {
            repository.deleteEquipmentItem(id)
        }
    }

    // Warehouse Return Actions
    fun recordItemReturn(
        id: String,
        returnedQty: Int,
        damagedQty: Int,
        damageReport: String
    ) {
        viewModelScope.launch {
            repository.updateReturnStatus(id, returnedQty, damagedQty, damageReport)
        }
    }

    fun incrementReturnedQuantity(item: EquipmentItemEntity) {
        val newReturned = (item.returnedQuantity + 1).coerceAtMost(item.packedQuantity)
        recordItemReturn(item.id, newReturned, item.damagedQuantity, item.damageReport)
    }

    fun decrementReturnedQuantity(item: EquipmentItemEntity) {
        val newReturned = (item.returnedQuantity - 1).coerceAtLeast(0)
        recordItemReturn(item.id, newReturned, item.damagedQuantity, item.damageReport)
    }

    fun markItemFullyReturned(item: EquipmentItemEntity) {
        quickReturnItemOk(item)
    }

    fun flagItemDamagedOrMissing(
        item: EquipmentItemEntity,
        returnedQty: Int,
        damagedQty: Int,
        report: String
    ) {
        viewModelScope.launch {
            repository.updateReturnStatus(item.id, returnedQty, damagedQty, report)
            val eventId = _selectedEventId.value ?: return@launch
            if (damagedQty > 0) {
                repository.insertLog(
                    SetupLogEntity(
                        id = UUID.randomUUID().toString(),
                        eventId = eventId,
                        author = currentTechName.value,
                        zone = "Repair Bench",
                        logType = LogType.ISSUE_ALERT,
                        content = "Damaged Gear Alert: '${item.name}' flagged for repair bench. Issue: $report"
                    )
                )
                repository.insertNotification(
                    TeamNotificationEntity(
                        id = UUID.randomUUID().toString(),
                        eventId = eventId,
                        sender = currentTechName.value,
                        priority = NotificationPriority.ALERT,
                        title = "Repair Bench Flag: ${item.name}",
                        message = report,
                        isWorkUpdate = true
                    )
                )
            }
        }
    }

    fun quickReturnItemOk(item: EquipmentItemEntity) {
        viewModelScope.launch {
            repository.updateReturnStatus(
                id = item.id,
                returnedQty = item.packedQuantity,
                damagedQty = 0,
                damageReport = ""
            )
        }
    }

    fun flagItemDamaged(item: EquipmentItemEntity, report: String) {
        viewModelScope.launch {
            val returnedIntact = (item.packedQuantity - 1).coerceAtLeast(0)
            repository.updateReturnStatus(
                id = item.id,
                returnedQty = returnedIntact,
                damagedQty = 1,
                damageReport = report
            )
            val eventId = _selectedEventId.value ?: return@launch
            repository.insertLog(
                SetupLogEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = eventId,
                    author = currentTechName.value,
                    zone = "Repair Bench",
                    logType = LogType.ISSUE_ALERT,
                    content = "Damaged Gear Alert: '${item.name}' flagged for repair bench. Issue: $report"
                )
            )
            repository.insertNotification(
                TeamNotificationEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = eventId,
                    sender = currentTechName.value,
                    priority = NotificationPriority.ALERT,
                    title = "Repair Bench Flag: ${item.name}",
                    message = report
                )
            )
        }
    }

    fun returnAllAccounted() {
        bulkCheckInAllIntact()
    }

    fun bulkCheckInAllIntact() {
        viewModelScope.launch {
            repository.checkInAllEquipment(currentEquipment.value)
            val eventId = _selectedEventId.value ?: return@launch
            repository.insertLog(
                SetupLogEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = eventId,
                    author = currentTechName.value,
                    zone = "Warehouse Bay",
                    logType = LogType.SIGN_OFF,
                    content = "Bulk check-in completed for all intact loaded equipment."
                )
            )
        }
    }

    fun finalizeWarehouseAudit(
        eventId: String,
        managerName: String,
        auditNotes: String
    ) {
        viewModelScope.launch {
            val items = currentEquipment.value
            val totalPacked = items.sumOf { it.packedQuantity }
            val totalReturned = items.sumOf { it.returnedQuantity }
            val totalDamaged = items.sumOf { it.damagedQuantity }
            val totalMissing = items.sumOf { (it.packedQuantity - it.returnedQuantity - it.damagedQuantity).coerceAtLeast(0) }

            val statusSummary = if (totalMissing == 0 && totalDamaged == 0) {
                "100% Accounted For - Clean Warehouse Return"
            } else if (totalMissing > 0) {
                "Discrepancy: $totalMissing Missing, $totalDamaged Damaged"
            } else {
                "Complete Return with $totalDamaged Item(s) in Repair Bench"
            }

            val audit = WarehouseAuditEntity(
                id = UUID.randomUUID().toString(),
                eventId = eventId,
                reconciledAt = System.currentTimeMillis(),
                reconciledBy = managerName.ifBlank { currentTechName.value },
                totalPacked = totalPacked,
                totalReturned = totalReturned,
                totalDamaged = totalDamaged,
                totalMissing = totalMissing,
                statusSummary = statusSummary,
                managerSignoff = managerName.ifBlank { currentTechName.value },
                reportNotes = auditNotes
            )
            repository.insertAudit(audit)
            repository.updateEventStatus(eventId, EventStatus.COMPLETED_RECONCILED)

            repository.insertNotification(
                TeamNotificationEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = eventId,
                    sender = managerName.ifBlank { currentTechName.value },
                    priority = NotificationPriority.GENERAL,
                    title = "Warehouse Audit Reconciled",
                    message = "Final audit signed off by $managerName. $statusSummary.",
                    isWorkUpdate = true
                )
            )

            repository.insertLog(
                SetupLogEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = eventId,
                    author = managerName.ifBlank { currentTechName.value },
                    zone = "Warehouse Office",
                    logType = LogType.SIGN_OFF,
                    content = "Warehouse reconciliation completed. $statusSummary. $auditNotes"
                )
            )
        }
    }

    // Setup Log Actions
    fun addSetupLog(
        eventId: String,
        author: String,
        zone: String,
        type: LogType,
        content: String
    ) {
        viewModelScope.launch {
            val log = SetupLogEntity(
                id = UUID.randomUUID().toString(),
                eventId = eventId,
                author = author.ifBlank { currentTechName.value },
                zone = zone.ifBlank { "Stage" },
                logType = type,
                content = content
            )
            repository.insertLog(log)
            if (type == LogType.ISSUE_ALERT) {
                repository.insertNotification(
                    TeamNotificationEntity(
                        id = UUID.randomUUID().toString(),
                        eventId = eventId,
                        sender = author.ifBlank { currentTechName.value },
                        priority = NotificationPriority.CRITICAL,
                        title = "Issue Reported @ $zone",
                        message = content
                    )
                )
                NotificationHelper.postWorkUpdateNotification(
                    getApplication(),
                    "Issue Reported @ $zone",
                    content,
                    NotificationPriority.CRITICAL
                )
            }
        }
    }

    fun deleteSetupLog(id: String) {
        viewModelScope.launch {
            repository.deleteLog(id)
        }
    }

    // Team Notification Actions
    fun sendTeamNotification(
        eventId: String,
        sender: String,
        priority: NotificationPriority,
        title: String,
        message: String,
        isWorkUpdate: Boolean = false
    ) {
        viewModelScope.launch {
            val notification = TeamNotificationEntity(
                id = UUID.randomUUID().toString(),
                eventId = eventId,
                sender = sender.ifBlank { currentTechName.value },
                priority = priority,
                title = title,
                message = message,
                isWorkUpdate = isWorkUpdate
            )
            repository.insertNotification(notification)
            NotificationHelper.postWorkUpdateNotification(
                getApplication(),
                title,
                message,
                priority
            )
        }
    }

    fun acknowledgeNotification(id: String) {
        viewModelScope.launch {
            repository.acknowledgeNotification(id, currentTechName.value)
        }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    // Call Time Notification Trigger
    fun triggerCallTimeReminder(eventId: String, note: String) {
        val event = allEvents.value.find { it.id == eventId }
        val eventName = event?.name ?: "Upcoming Program"
        val title = "⏰ Call Time Alert: $eventName"
        val message = "Upcoming gig call: $note. Please verify equipment prep & staging dock."
        sendTeamNotification(eventId, "Stage Dispatch", NotificationPriority.ALERT, title, message, isWorkUpdate = true)
        NotificationHelper.postWorkUpdateNotification(
            getApplication(),
            title,
            message,
            NotificationPriority.ALERT,
            NotificationHelper.CHANNEL_CALL_TIMES
        )
    }

    // Test simulation for live team notifications
    fun simulateIncomingCrewNotification() {
        val eventId = _selectedEventId.value ?: return
        viewModelScope.launch {
            val simulatedBroadcasts = listOf(
                Triple("Stage Manager (Kelly)", NotificationPriority.ALERT, "5-Minute Warning: Doors Opening"),
                Triple("FOH Audio (Dave)", NotificationPriority.GENERAL, "Line check with Lead Vocalist complete - RF signal 100%"),
                Triple("Lighting Tech (Sarah)", NotificationPriority.LOGISTICS, "Truss 2 trim height locked at 24ft"),
                Triple("Video Lead (Marcus)", NotificationPriority.CRITICAL, "Intermittent sync flicker on SDI input 4 - re-clocking DA"),
                Triple("Warehouse Dispatch", NotificationPriority.LOGISTICS, "Truck 2 staging at gate for overnight loadout")
            )
            val randomChoice = simulatedBroadcasts.random()
            sendTeamNotification(
                eventId = eventId,
                sender = randomChoice.first,
                priority = randomChoice.second,
                title = randomChoice.third,
                message = "Live update broadcasted to all active crew members on site."
            )
        }
    }
}
