package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.AvlCategory
import com.example.model.AvlPresets
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [
        UserAccountEntity::class,
        ProductionEventEntity::class,
        EventAvailabilityEntity::class,
        EquipmentItemEntity::class,
        SetupLogEntity::class,
        TeamNotificationEntity::class,
        WarehouseAuditEntity::class,
        EquipmentLoadoutEntity::class,
        LoadoutItemEntity::class,
        ChecklistItemEntity::class,
        ChecklistTemplateEntity::class,
        WarehouseInventoryEntity::class,
        InventoryTransactionEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AvlDatabase : RoomDatabase() {
    abstract fun userAccountDao(): UserAccountDao
    abstract fun eventAvailabilityDao(): EventAvailabilityDao
    abstract fun eventDao(): ProductionEventDao
    abstract fun equipmentDao(): EquipmentDao
    abstract fun setupLogDao(): SetupLogDao
    abstract fun teamNotificationDao(): TeamNotificationDao
    abstract fun warehouseAuditDao(): WarehouseAuditDao
    abstract fun equipmentLoadoutDao(): EquipmentLoadoutDao
    abstract fun checklistItemDao(): ChecklistItemDao
    abstract fun warehouseInventoryDao(): WarehouseInventoryDao

    companion object {
        @Volatile
        private var INSTANCE: AvlDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AvlDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AvlDatabase::class.java,
                    "avl_production_database"
                )
                    .addCallback(AvlDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun reseedInitialData(db: AvlDatabase) {
            populateInitialData(db)
        }

        suspend fun populateInitialData(db: AvlDatabase) {
            val userDao = db.userAccountDao()
            val availDao = db.eventAvailabilityDao()
            val eventDao = db.eventDao()
            val equipmentDao = db.equipmentDao()
            val logDao = db.setupLogDao()
            val notifDao = db.teamNotificationDao()
            val loadoutDao = db.equipmentLoadoutDao()
            val checklistDao = db.checklistItemDao()
            val inventoryDao = db.warehouseInventoryDao()

            val now = System.currentTimeMillis()
            val oneDayMs = 24 * 60 * 60 * 1000L
            val oneHourMs = 60 * 60 * 1000L

            // Seed Super Admin Account
            val superAdmin = UserAccountEntity(
                id = "usr-admin-root",
                username = "admin",
                fullName = "Master Administrator",
                email = "admin@avlops.live",
                password = "admin",
                role = UserRole.OWNER,
                department = WorkDepartment.PRODUCTION_MGMT,
                phone = "+1 (555) 999-0000",
                initials = "AD",
                createdAt = now - (60 * oneDayMs)
            )

            // Seed 4 Role Demo Accounts
            val userOwner = UserAccountEntity(
                id = "usr-owner-001",
                username = "dave_owner",
                fullName = "Dave Vance",
                email = "dave@avlpro.live",
                password = "password123",
                role = UserRole.OWNER,
                department = WorkDepartment.PRODUCTION_MGMT,
                phone = "+1 (555) 019-2831",
                initials = "DV",
                createdAt = now - (30 * oneDayMs)
            )
            val userAdmin = UserAccountEntity(
                id = "usr-admin-002",
                username = "elena_admin",
                fullName = "Elena Rostova",
                email = "elena@avlpro.live",
                password = "password123",
                role = UserRole.ADMIN,
                department = WorkDepartment.VIDEO,
                phone = "+1 (555) 014-9920",
                initials = "ER",
                createdAt = now - (20 * oneDayMs)
            )
            val userManager = UserAccountEntity(
                id = "usr-mgr-003",
                username = "marcus_mgr",
                fullName = "Marcus Brody",
                email = "marcus@avlpro.live",
                password = "password123",
                role = UserRole.MANAGER,
                department = WorkDepartment.RIGGING_POWER,
                phone = "+1 (555) 018-7744",
                initials = "MB",
                createdAt = now - (15 * oneDayMs)
            )
            val userCrewAudio = UserAccountEntity(
                id = "usr-crew-004",
                username = "sam_audio",
                fullName = "Sam Rivera",
                email = "sam@avlpro.live",
                password = "password123",
                role = UserRole.CREW,
                department = WorkDepartment.AUDIO,
                phone = "+1 (555) 012-3311",
                initials = "SR",
                createdAt = now - (10 * oneDayMs)
            )
            val userCrewLight = UserAccountEntity(
                id = "usr-crew-005",
                username = "maya_light",
                fullName = "Maya Lin",
                email = "maya@avlpro.live",
                password = "password123",
                role = UserRole.CREW,
                department = WorkDepartment.LIGHTING,
                phone = "+1 (555) 017-4488",
                initials = "ML",
                createdAt = now - (8 * oneDayMs)
            )
            val userCrewStage = UserAccountEntity(
                id = "usr-crew-006",
                username = "jordan_stage",
                fullName = "Jordan Blake",
                email = "jordan@avlpro.live",
                password = "password123",
                role = UserRole.CREW,
                department = WorkDepartment.STAGE_HAND,
                phone = "+1 (555) 013-6622",
                initials = "JB",
                createdAt = now - (5 * oneDayMs)
            )

            userDao.insertAllUsers(listOf(superAdmin, userOwner, userAdmin, userManager, userCrewAudio, userCrewLight, userCrewStage))

            // Event 1: Neon Horizon Tour - Live Setup
            val event1Id = "evt-neon-horizon-001"
            val event1 = ProductionEventEntity(
                id = event1Id,
                name = "Neon Horizon Tour - Main Stage",
                clientOrVenue = "Metro Civic Arena",
                location = "Loading Dock B / Arena Floor",
                eventDate = now + (oneDayMs * 2),
                leadTech = "Marcus Vance (Production Mgr)",
                truckOrVehicle = "Truck 01 (53ft Semi)",
                status = EventStatus.ON_SITE_SETUP,
                notes = "FOH located 85ft from stage center. Rigging load-in starts 07:00. Soundcheck scheduled 15:30.",
                createdAt = now - (oneDayMs * 3)
            )
            eventDao.insertEvent(event1)

            // Equipment for Event 1
            val event1Equipment = AvlPresets.defaultInventory.map { preset ->
                val packed = preset.defaultQty
                EquipmentItemEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event1Id,
                    name = preset.name,
                    category = preset.category,
                    targetQuantity = preset.defaultQty,
                    packedQuantity = packed,
                    returnedQuantity = 0,
                    damagedQuantity = 0,
                    barcodeOrTag = preset.suggestedBarcode,
                    storageLocation = preset.storageCase,
                    status = ItemStatus.LOADED,
                    notes = preset.notes
                )
            }
            equipmentDao.insertAll(event1Equipment)

            // Setup logs for Event 1
            val logsEvent1 = listOf(
                SetupLogEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event1Id,
                    author = "Elena Ramos (Rigging Lead)",
                    zone = "Grid / Fly-Loft",
                    logType = LogType.MILESTONE,
                    content = "All 4 motor hoists hung, secondary safety steels attached, load cell readings balanced.",
                    timestamp = now - (oneHourMs * 4)
                ),
                SetupLogEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event1Id,
                    author = "Dave Miller (Chief Audio)",
                    zone = "FOH Mix Position",
                    logType = LogType.SOUNDCHECK,
                    content = "Dante primary & secondary networks locked. Rio 3224 stage boxes communicating at 96kHz. Delay curves calibrated to arena acoustics.",
                    timestamp = now - (oneHourMs * 2)
                ),
                SetupLogEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event1Id,
                    author = "Tyler Brooks (Electrician)",
                    zone = "Power Distro Room",
                    logType = LogType.VOLTAGE_CHECK,
                    content = "Main 3-phase camlock distribution test: L1-N: 120.4V, L2-N: 120.8V, L3-N: 119.9V. Zero ground current.",
                    timestamp = now - (oneHourMs * 3)
                ),
                SetupLogEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event1Id,
                    author = "Sarah Chen (Lighting Director)",
                    zone = "Stage Trusses",
                    logType = LogType.INFO,
                    content = "All 8 Robe MegaPointes homed and addressed across DMX Universe 1 & 2. Hazer warmup cycle completed.",
                    timestamp = now - (oneHourMs * 1)
                )
            )
            logsEvent1.forEach { logDao.insertLog(it) }

            // Team notifications for Event 1
            val notifsEvent1 = listOf(
                TeamNotificationEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event1Id,
                    sender = "Stage Manager",
                    priority = NotificationPriority.ALERT,
                    title = "Band Arrival Update",
                    message = "Artist bus arrived early. Soundcheck moved forward 30 minutes to 15:00 sharp. Please have stage lines hot.",
                    timestamp = now - (oneHourMs * 1),
                    isAcknowledged = true,
                    acknowledgedBy = "Dave Miller",
                    acknowledgedAt = now - (oneHourMs / 2)
                ),
                TeamNotificationEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event1Id,
                    sender = "Video Tech (Marcus)",
                    priority = NotificationPriority.CRITICAL,
                    title = "SDI Line 3 Signal Loss",
                    message = "Stage right PTZ lost 12G-SDI handshake. Switching to redundant Cat6 NDI feed while swapping BNC barrel.",
                    timestamp = now - (15 * 60 * 1000L),
                    isAcknowledged = false
                ),
                TeamNotificationEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event1Id,
                    sender = "Warehouse Dispatch",
                    priority = NotificationPriority.LOGISTICS,
                    title = "Strike Transport Assigned",
                    message = "Truck 01 will back into Bay 3 immediately following show curtain call at 23:15.",
                    timestamp = now - (30 * 60 * 1000L),
                    isAcknowledged = false
                )
            )
            notifsEvent1.forEach { notifDao.insertNotification(it) }

            // Event 2: Symphony Under The Stars - Ready for Return & Check-in
            val event2Id = "evt-symphony-002"
            val event2 = ProductionEventEntity(
                id = event2Id,
                name = "Symphony Under The Stars Gala",
                clientOrVenue = "Riverfront Amphitheater",
                location = "Warehouse Receiving Bay",
                eventDate = now - (oneDayMs * 1),
                leadTech = "Rachel Adams (Lead Tech)",
                truckOrVehicle = "Truck 03 (26ft Box)",
                status = EventStatus.STRIKE_RETURN,
                notes = "Outdoor gig wrapped at 22:30. Equipment in staging area for return count verification and cable testing.",
                createdAt = now - (oneDayMs * 4)
            )
            eventDao.insertEvent(event2)

            // Equipment for Event 2 (some checked in, one damaged, one missing for realistic check-in reconciliation)
            val event2Equipment = listOf(
                EquipmentItemEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event2Id,
                    name = "Yamaha QL5 32ch Digital Audio Mixer",
                    category = AvlCategory.AUDIO,
                    targetQuantity = 1,
                    packedQuantity = 1,
                    returnedQuantity = 1,
                    damagedQuantity = 0,
                    barcodeOrTag = "AUD-MIX-01",
                    storageLocation = "Console Road Case #1",
                    status = ItemStatus.RETURNED_OK,
                    notes = "Cleaned and faders tested"
                ),
                EquipmentItemEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event2Id,
                    name = "Shure Axient Digital Dual Handheld Mics",
                    category = AvlCategory.AUDIO,
                    targetQuantity = 4,
                    packedQuantity = 4,
                    returnedQuantity = 4,
                    damagedQuantity = 0,
                    barcodeOrTag = "AUD-RF-01",
                    storageLocation = "Mic Trunk A",
                    status = ItemStatus.RETURNED_OK
                ),
                EquipmentItemEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event2Id,
                    name = "Radial J48 Active Direct Box (DI)",
                    category = AvlCategory.AUDIO,
                    targetQuantity = 6,
                    packedQuantity = 6,
                    returnedQuantity = 5,
                    damagedQuantity = 0,
                    barcodeOrTag = "AUD-DI-01",
                    storageLocation = "DI Pelican Case",
                    status = ItemStatus.FLAGGED_DISCREPANCY,
                    notes = "1 Unit missing from Stage Left acoustic guitar position"
                ),
                EquipmentItemEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event2Id,
                    name = "Chauvet COLORado 2 Quad Zoom LED",
                    category = AvlCategory.LIGHTING,
                    targetQuantity = 8,
                    packedQuantity = 8,
                    returnedQuantity = 7,
                    damagedQuantity = 1,
                    damageReport = "Cracked front fresnel lens & bent yoke bracket during outdoor wind gust. Send to repair bench.",
                    barcodeOrTag = "LGT-PAR-01",
                    storageLocation = "Road Case A",
                    status = ItemStatus.BENCH_REPAIR
                ),
                EquipmentItemEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event2Id,
                    name = "50ft ProCo Heavy Duty XLR Cable",
                    category = AvlCategory.CABLES_ACCESSORIES,
                    targetQuantity = 15,
                    packedQuantity = 15,
                    returnedQuantity = 15,
                    damagedQuantity = 0,
                    barcodeOrTag = "CBL-XLR-50",
                    storageLocation = "Cable Trunk #1",
                    status = ItemStatus.RETURNED_OK
                ),
                EquipmentItemEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event2Id,
                    name = "K&M Heavy Duty Boom Mic Stands",
                    category = AvlCategory.AUDIO,
                    targetQuantity = 8,
                    packedQuantity = 8,
                    returnedQuantity = 8,
                    damagedQuantity = 0,
                    barcodeOrTag = "AUD-STD-01",
                    storageLocation = "Mic Stand Bag 1",
                    status = ItemStatus.RETURNED_OK
                ),
                EquipmentItemEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event2Id,
                    name = "Motion Labs 100A 3-Phase Power Distro",
                    category = AvlCategory.RIGGING_POWER,
                    targetQuantity = 1,
                    packedQuantity = 1,
                    returnedQuantity = 1,
                    damagedQuantity = 0,
                    barcodeOrTag = "PWR-DST-01",
                    storageLocation = "Main Distro Rack",
                    status = ItemStatus.RETURNED_OK
                )
            )
            equipmentDao.insertAll(event2Equipment)

            // Setup logs for Event 2
            val logsEvent2 = listOf(
                SetupLogEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event2Id,
                    author = "Rachel Adams (Lead Tech)",
                    zone = "Warehouse Receiving",
                    logType = LogType.SIGN_OFF,
                    content = "Truck 03 unloaded. Inventory check-in in progress. 1 DI box unaccounted for, 1 Chauvet par flagged with lens fracture.",
                    timestamp = now - (2 * oneHourMs)
                )
            )
            logsEvent2.forEach { logDao.insertLog(it) }

            // Event 3: Corporate Global Tech Summit
            val event3Id = "evt-tech-summit-003"
            val event3 = ProductionEventEntity(
                id = event3Id,
                name = "Global Tech Summit 2026",
                clientOrVenue = "Grand Hyatt Ballroom A-D",
                location = "Bay 1 Warehouse Prep",
                eventDate = now + (oneDayMs * 5),
                leadTech = "Carlos Ruiz (Video Director)",
                truckOrVehicle = "Truck 02 (26ft Box)",
                status = EventStatus.PACKING,
                notes = "Requires 16x9 LED screen, dual redundancy ATEM switchers, 6 wireless lavs.",
                createdAt = now - (oneDayMs * 1)
            )
            eventDao.insertEvent(event3)

            val event3Equipment = listOf(
                EquipmentItemEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event3Id,
                    name = "Absen 2.9mm Indoor LED Wall Panel",
                    category = AvlCategory.VIDEO,
                    targetQuantity = 24,
                    packedQuantity = 16,
                    returnedQuantity = 0,
                    damagedQuantity = 0,
                    barcodeOrTag = "VID-LED-01",
                    storageLocation = "LED Cases 1-3",
                    status = ItemStatus.PACKED
                ),
                EquipmentItemEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event3Id,
                    name = "Novastar VX1000 All-in-One Video Processor",
                    category = AvlCategory.VIDEO,
                    targetQuantity = 2,
                    packedQuantity = 2,
                    returnedQuantity = 0,
                    damagedQuantity = 0,
                    barcodeOrTag = "VID-PROC-01",
                    storageLocation = "Video Rack 1",
                    status = ItemStatus.LOADED
                ),
                EquipmentItemEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event3Id,
                    name = "Panasonic 4K PTZ Camera AW-UE150",
                    category = AvlCategory.VIDEO,
                    targetQuantity = 3,
                    packedQuantity = 0,
                    returnedQuantity = 0,
                    damagedQuantity = 0,
                    barcodeOrTag = "VID-CAM-01",
                    storageLocation = "Pelican PTZ Case",
                    status = ItemStatus.PENDING_PACK
                ),
                EquipmentItemEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event3Id,
                    name = "Sennheiser EW-DX Wireless Bodypack & Lav",
                    category = AvlCategory.AUDIO,
                    targetQuantity = 6,
                    packedQuantity = 6,
                    returnedQuantity = 0,
                    damagedQuantity = 0,
                    barcodeOrTag = "AUD-RF-02",
                    storageLocation = "Mic Trunk A",
                    status = ItemStatus.LOADED
                ),
                EquipmentItemEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event3Id,
                    name = "150ft Ruggedized Cat6 Ethercon Reel",
                    category = AvlCategory.CABLES_ACCESSORIES,
                    targetQuantity = 4,
                    packedQuantity = 2,
                    returnedQuantity = 0,
                    damagedQuantity = 0,
                    barcodeOrTag = "CBL-ETH-01",
                    storageLocation = "Network Reel",
                    status = ItemStatus.PACKED
                )
            )
            equipmentDao.insertAll(event3Equipment)

            // Seed initial Event Availabilities for team members
            val availabilities = listOf(
                // Event 1 Roster
                EventAvailabilityEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event1Id,
                    userId = userOwner.id,
                    userName = userOwner.fullName,
                    userRole = userOwner.role,
                    department = userOwner.department,
                    status = MemberAvailability.CONFIRMED_ASSIGNED,
                    note = "Executive On-Site Oversight",
                    assignedPosition = "Executive Supervisor",
                    isAssigned = true,
                    callTimeNote = "Call: 07:00 at Arena Dock B"
                ),
                EventAvailabilityEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event1Id,
                    userId = userManager.id,
                    userName = userManager.fullName,
                    userRole = userManager.role,
                    department = userManager.department,
                    status = MemberAvailability.CONFIRMED_ASSIGNED,
                    note = "Leading load-in and rigging checks",
                    assignedPosition = "Production Stage Mgr",
                    isAssigned = true,
                    callTimeNote = "Call: 06:30 at Truck 01"
                ),
                EventAvailabilityEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event1Id,
                    userId = userCrewAudio.id,
                    userName = userCrewAudio.fullName,
                    userRole = userCrewAudio.role,
                    department = userCrewAudio.department,
                    status = MemberAvailability.CONFIRMED_ASSIGNED,
                    note = "Ready with Dante controller & wireless RF gear",
                    assignedPosition = "Lead FOH A1 Audio",
                    isAssigned = true,
                    callTimeNote = "Call: 07:30 at FOH Mix"
                ),
                EventAvailabilityEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event1Id,
                    userId = userCrewLight.id,
                    userName = userCrewLight.fullName,
                    userRole = userCrewLight.role,
                    department = userCrewLight.department,
                    status = MemberAvailability.AVAILABLE,
                    note = "Available all day, can run GrandMA3 or truss spot",
                    assignedPosition = "L1 Lighting Op",
                    isAssigned = true,
                    callTimeNote = "Call: 08:00 at Truss Cart 1"
                ),
                EventAvailabilityEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event1Id,
                    userId = userCrewStage.id,
                    userName = userCrewStage.fullName,
                    userRole = userCrewStage.role,
                    department = userCrewStage.department,
                    status = MemberAvailability.AVAILABLE,
                    note = "Full shift ready",
                    assignedPosition = "Stage Tech / Rigging",
                    isAssigned = false,
                    callTimeNote = ""
                ),

                // Event 3 (Upcoming Program) Availability Responses
                EventAvailabilityEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event3Id,
                    userId = userAdmin.id,
                    userName = userAdmin.fullName,
                    userRole = userAdmin.role,
                    department = userAdmin.department,
                    status = MemberAvailability.CONFIRMED_ASSIGNED,
                    note = "Directing dual 4K video feeds and ATEM switcher",
                    assignedPosition = "V1 Video Director",
                    isAssigned = true,
                    callTimeNote = "Call: 06:00 Grand Hyatt Dock"
                ),
                EventAvailabilityEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event3Id,
                    userId = userCrewAudio.id,
                    userName = userCrewAudio.fullName,
                    userRole = userCrewAudio.role,
                    department = userCrewAudio.department,
                    status = MemberAvailability.AVAILABLE,
                    note = "Available for corporate panel audio setup",
                    assignedPosition = "A2 Audio Tech",
                    isAssigned = true,
                    callTimeNote = "Call: 07:00 Ballroom A"
                ),
                EventAvailabilityEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event3Id,
                    userId = userCrewLight.id,
                    userName = userCrewLight.fullName,
                    userRole = userCrewLight.role,
                    department = userCrewLight.department,
                    status = MemberAvailability.TENTATIVE,
                    note = "Can arrive after 13:00 due to university exam",
                    assignedPosition = "",
                    isAssigned = false,
                    callTimeNote = ""
                ),
                EventAvailabilityEntity(
                    id = UUID.randomUUID().toString(),
                    eventId = event3Id,
                    userId = userCrewStage.id,
                    userName = userCrewStage.fullName,
                    userRole = userCrewStage.role,
                    department = userCrewStage.department,
                    status = MemberAvailability.AVAILABLE,
                    note = "Available for morning pack and load-in",
                    assignedPosition = "Warehouse Stage Hand",
                    isAssigned = false,
                    callTimeNote = ""
                )
            )
            availDao.insertAll(availabilities)

            // -------------------------------------------------------------
            // Seed Master Warehouse Inventory Quantities
            // -------------------------------------------------------------
            val initialInventory = listOf(
                // Audio
                WarehouseInventoryEntity(
                    id = "inv-aud-001",
                    skuOrBarcode = "AUD-RF-01",
                    itemName = "Shure Axient Digital Dual Handheld Mics",
                    category = AvlCategory.AUDIO,
                    department = WorkDepartment.AUDIO,
                    modelNumber = "AD4D-US",
                    manufacturer = "Shure",
                    totalStockQty = 12,
                    availableQty = 4,
                    allocatedQty = 8,
                    maintenanceQty = 0,
                    minimumThresholdQty = 3,
                    warehouseAisleBin = "Aisle 1 - Bay A - Shelf 2 (Mic Locker)",
                    unitWeightLbs = 18.5,
                    unitReplacementCost = 5400.0,
                    notes = "Includes dual handheld transmitters, antennas, and network card"
                ),
                WarehouseInventoryEntity(
                    id = "inv-aud-002",
                    skuOrBarcode = "AUD-RF-02",
                    itemName = "Sennheiser EW-DX Wireless Bodypack & Lav",
                    category = AvlCategory.AUDIO,
                    department = WorkDepartment.AUDIO,
                    modelNumber = "EW-DX SK",
                    manufacturer = "Sennheiser",
                    totalStockQty = 16,
                    availableQty = 6,
                    allocatedQty = 10,
                    maintenanceQty = 0,
                    minimumThresholdQty = 4,
                    warehouseAisleBin = "Aisle 1 - Bay A - Shelf 3 (Mic Locker)",
                    unitWeightLbs = 12.0,
                    unitReplacementCost = 2800.0,
                    notes = "Dante enabled dual-channel digital wireless"
                ),
                WarehouseInventoryEntity(
                    id = "inv-aud-003",
                    skuOrBarcode = "AUD-MIX-01",
                    itemName = "Yamaha QL5 32ch Digital Audio Mixer",
                    category = AvlCategory.AUDIO,
                    department = WorkDepartment.AUDIO,
                    modelNumber = "QL5",
                    manufacturer = "Yamaha",
                    totalStockQty = 3,
                    availableQty = 1,
                    allocatedQty = 2,
                    maintenanceQty = 0,
                    minimumThresholdQty = 1,
                    warehouseAisleBin = "Console Staging Bay 1",
                    unitWeightLbs = 110.0,
                    unitReplacementCost = 14500.0,
                    notes = "Fitted in custom road case with doghouse"
                ),
                WarehouseInventoryEntity(
                    id = "inv-aud-004",
                    skuOrBarcode = "AUD-SB-01",
                    itemName = "Rio 3224-D2 Dante Stage Box",
                    category = AvlCategory.AUDIO,
                    department = WorkDepartment.AUDIO,
                    modelNumber = "Rio3224-D2",
                    manufacturer = "Yamaha",
                    totalStockQty = 6,
                    availableQty = 2,
                    allocatedQty = 4,
                    maintenanceQty = 0,
                    minimumThresholdQty = 2,
                    warehouseAisleBin = "Rack Bay 2",
                    unitWeightLbs = 55.0,
                    unitReplacementCost = 6200.0,
                    notes = "Primary & Secondary Ethercon ports"
                ),
                WarehouseInventoryEntity(
                    id = "inv-aud-005",
                    skuOrBarcode = "AUD-SPK-01",
                    itemName = "d&b audiotechnik V-Series Array Tops",
                    category = AvlCategory.AUDIO,
                    department = WorkDepartment.AUDIO,
                    modelNumber = "V8",
                    manufacturer = "d&b audiotechnik",
                    totalStockQty = 24,
                    availableQty = 8,
                    allocatedQty = 16,
                    maintenanceQty = 0,
                    minimumThresholdQty = 4,
                    warehouseAisleBin = "Speaker Yard - Bay 1",
                    unitWeightLbs = 75.0,
                    unitReplacementCost = 8900.0,
                    notes = "3-way passive line array element with rigging pins"
                ),
                WarehouseInventoryEntity(
                    id = "inv-aud-006",
                    skuOrBarcode = "AUD-SUB-01",
                    itemName = "d&b V-SUB Flyable Subwoofers",
                    category = AvlCategory.AUDIO,
                    department = WorkDepartment.AUDIO,
                    modelNumber = "V-SUB",
                    manufacturer = "d&b audiotechnik",
                    totalStockQty = 12,
                    availableQty = 4,
                    allocatedQty = 8,
                    maintenanceQty = 0,
                    minimumThresholdQty = 2,
                    warehouseAisleBin = "Speaker Yard - Bay 2",
                    unitWeightLbs = 141.0,
                    unitReplacementCost = 9200.0,
                    notes = "Cardioid subwoofer with integrated rigging"
                ),
                WarehouseInventoryEntity(
                    id = "inv-aud-007",
                    skuOrBarcode = "AUD-AMP-01",
                    itemName = "d&b D80 4-Channel Power Amplifiers",
                    category = AvlCategory.AUDIO,
                    department = WorkDepartment.AUDIO,
                    modelNumber = "D80",
                    manufacturer = "d&b audiotechnik",
                    totalStockQty = 8,
                    availableQty = 2,
                    allocatedQty = 6,
                    maintenanceQty = 0,
                    minimumThresholdQty = 2,
                    warehouseAisleBin = "Amp Rack Alpha Bay",
                    unitWeightLbs = 62.0,
                    unitReplacementCost = 12800.0,
                    notes = "4x 4000W into 4 ohms"
                ),
                WarehouseInventoryEntity(
                    id = "inv-aud-008",
                    skuOrBarcode = "AUD-DI-01",
                    itemName = "Radial J48 Active Direct Box (DI)",
                    category = AvlCategory.AUDIO,
                    department = WorkDepartment.AUDIO,
                    modelNumber = "J48",
                    manufacturer = "Radial Engineering",
                    totalStockQty = 20,
                    availableQty = 8,
                    allocatedQty = 11,
                    maintenanceQty = 1,
                    minimumThresholdQty = 5,
                    warehouseAisleBin = "Pelican DI Locker Shelf 1",
                    unitWeightLbs = 2.0,
                    unitReplacementCost = 249.0,
                    notes = "1 unit currently on bench testing for ground hum"
                ),
                WarehouseInventoryEntity(
                    id = "inv-aud-009",
                    skuOrBarcode = "AUD-STD-01",
                    itemName = "K&M Heavy Duty Boom Mic Stands",
                    category = AvlCategory.AUDIO,
                    department = WorkDepartment.AUDIO,
                    modelNumber = "210/9",
                    manufacturer = "K&M",
                    totalStockQty = 40,
                    availableQty = 15,
                    allocatedQty = 25,
                    maintenanceQty = 0,
                    minimumThresholdQty = 8,
                    warehouseAisleBin = "Stand Cart 1",
                    unitWeightLbs = 7.0,
                    unitReplacementCost = 89.0,
                    notes = "Heavy die-cast base with clutch locks"
                ),

                // Video
                WarehouseInventoryEntity(
                    id = "inv-vid-001",
                    skuOrBarcode = "VID-LED-01",
                    itemName = "Absen 2.9mm Indoor LED Wall Panel (500x500)",
                    category = AvlCategory.VIDEO,
                    department = WorkDepartment.VIDEO,
                    modelNumber = "PL2.9 Pro",
                    manufacturer = "Absen",
                    totalStockQty = 96,
                    availableQty = 40,
                    allocatedQty = 56,
                    maintenanceQty = 0,
                    minimumThresholdQty = 16,
                    warehouseAisleBin = "LED Flight Cases A1-A12",
                    unitWeightLbs = 18.0,
                    unitReplacementCost = 1200.0,
                    notes = "NovaStar A8s-N receiving cards installed"
                ),
                WarehouseInventoryEntity(
                    id = "inv-vid-002",
                    skuOrBarcode = "VID-PROC-01",
                    itemName = "Novastar VX1000 All-in-One Video Processor",
                    category = AvlCategory.VIDEO,
                    department = WorkDepartment.VIDEO,
                    modelNumber = "VX1000",
                    manufacturer = "NovaStar",
                    totalStockQty = 6,
                    availableQty = 2,
                    allocatedQty = 4,
                    maintenanceQty = 0,
                    minimumThresholdQty = 2,
                    warehouseAisleBin = "Video Rack Alpha Shelf 2",
                    unitWeightLbs = 24.0,
                    unitReplacementCost = 4500.0,
                    notes = "10x Gigabit Ethernet output ports"
                ),
                WarehouseInventoryEntity(
                    id = "inv-vid-003",
                    skuOrBarcode = "VID-SW-01",
                    itemName = "Blackmagic ATEM Constellation 2 M/E 4K",
                    category = AvlCategory.VIDEO,
                    department = WorkDepartment.VIDEO,
                    modelNumber = "ATEM 2 M/E 4K",
                    manufacturer = "Blackmagic Design",
                    totalStockQty = 3,
                    availableQty = 1,
                    allocatedQty = 2,
                    maintenanceQty = 0,
                    minimumThresholdQty = 1,
                    warehouseAisleBin = "Flypack Trunk 1",
                    unitWeightLbs = 65.0,
                    unitReplacementCost = 8995.0,
                    notes = "20x 12G-SDI inputs, 12x 12G-SDI outputs"
                ),
                WarehouseInventoryEntity(
                    id = "inv-vid-004",
                    skuOrBarcode = "VID-CAM-01",
                    itemName = "Panasonic 4K PTZ Camera AW-UE150",
                    category = AvlCategory.VIDEO,
                    department = WorkDepartment.VIDEO,
                    modelNumber = "AW-UE150W",
                    manufacturer = "Panasonic",
                    totalStockQty = 8,
                    availableQty = 2,
                    allocatedQty = 6,
                    maintenanceQty = 0,
                    minimumThresholdQty = 2,
                    warehouseAisleBin = "PTZ Pelican Case Set 1-4",
                    unitWeightLbs = 14.0,
                    unitReplacementCost = 10500.0,
                    notes = "4K 60p, 12G-SDI and NDI|HX support"
                ),
                WarehouseInventoryEntity(
                    id = "inv-vid-005",
                    skuOrBarcode = "VID-CBL-01",
                    itemName = "150ft 12G-SDI BNC Cable Drum",
                    category = AvlCategory.VIDEO,
                    department = WorkDepartment.VIDEO,
                    modelNumber = "4794R-150",
                    manufacturer = "Belden",
                    totalStockQty = 16,
                    availableQty = 6,
                    allocatedQty = 10,
                    maintenanceQty = 0,
                    minimumThresholdQty = 4,
                    warehouseAisleBin = "Cable Mezzanine Caddy 2",
                    unitWeightLbs = 28.0,
                    unitReplacementCost = 350.0,
                    notes = "Ruggedized rubber jacket with Neutrik BNCs"
                ),

                // Lighting
                WarehouseInventoryEntity(
                    id = "inv-lgt-001",
                    skuOrBarcode = "LGT-MOV-01",
                    itemName = "Robe MegaPointe Hybrid Moving Heads",
                    category = AvlCategory.LIGHTING,
                    department = WorkDepartment.LIGHTING,
                    modelNumber = "MegaPointe",
                    manufacturer = "Robe",
                    totalStockQty = 24,
                    availableQty = 8,
                    allocatedQty = 16,
                    maintenanceQty = 0,
                    minimumThresholdQty = 4,
                    warehouseAisleBin = "Lighting Yard - Cases 1-12",
                    unitWeightLbs = 72.0,
                    unitReplacementCost = 9800.0,
                    notes = "Discharge lamp checked; includes dual omega brackets"
                ),
                WarehouseInventoryEntity(
                    id = "inv-lgt-002",
                    skuOrBarcode = "LGT-PAR-01",
                    itemName = "Chauvet COLORado 2 Quad Zoom LED",
                    category = AvlCategory.LIGHTING,
                    department = WorkDepartment.LIGHTING,
                    modelNumber = "COLORado 2-Quad",
                    manufacturer = "Chauvet Professional",
                    totalStockQty = 36,
                    availableQty = 15,
                    allocatedQty = 20,
                    maintenanceQty = 1,
                    minimumThresholdQty = 6,
                    warehouseAisleBin = "Lighting Yard - Road Cases A-F",
                    unitWeightLbs = 22.0,
                    unitReplacementCost = 1150.0,
                    notes = "1 unit under repair for lens replacement"
                ),
                WarehouseInventoryEntity(
                    id = "inv-lgt-003",
                    skuOrBarcode = "LGT-CNS-01",
                    itemName = "GrandMA3 Command Wing onPC Setup",
                    category = AvlCategory.LIGHTING,
                    department = WorkDepartment.LIGHTING,
                    modelNumber = "gMA3 onPC Wing",
                    manufacturer = "MA Lighting",
                    totalStockQty = 3,
                    availableQty = 1,
                    allocatedQty = 2,
                    maintenanceQty = 0,
                    minimumThresholdQty = 1,
                    warehouseAisleBin = "Console Staging Bay 2",
                    unitWeightLbs = 58.0,
                    unitReplacementCost = 8400.0,
                    notes = "Custom shock-mounted flight case with dual touchscreens"
                ),
                WarehouseInventoryEntity(
                    id = "inv-lgt-004",
                    skuOrBarcode = "LGT-AST-01",
                    itemName = "Astera Titan Tube 8-Way Wireless Kit",
                    category = AvlCategory.LIGHTING,
                    department = WorkDepartment.LIGHTING,
                    modelNumber = "FP1-SET",
                    manufacturer = "Astera",
                    totalStockQty = 4,
                    availableQty = 2,
                    allocatedQty = 2,
                    maintenanceQty = 0,
                    minimumThresholdQty = 1,
                    warehouseAisleBin = "Astera Charging Rack",
                    unitWeightLbs = 78.0,
                    unitReplacementCost = 7200.0,
                    notes = "Includes PowerBox, ART7 transmitter, and rigging eyes"
                ),
                WarehouseInventoryEntity(
                    id = "inv-lgt-005",
                    skuOrBarcode = "LGT-HAZ-01",
                    itemName = "Ultratec Radiance Hazer (DMX)",
                    category = AvlCategory.LIGHTING,
                    department = WorkDepartment.LIGHTING,
                    modelNumber = "Radiance DMX",
                    manufacturer = "Ultratec Special Effects",
                    totalStockQty = 4,
                    availableQty = 1,
                    allocatedQty = 3,
                    maintenanceQty = 0,
                    minimumThresholdQty = 1,
                    warehouseAisleBin = "Atmospherics Staging Shelf",
                    unitWeightLbs = 38.0,
                    unitReplacementCost = 2100.0,
                    notes = "Pre-filled with Luminous 7 fluid"
                ),

                // Rigging & Power
                WarehouseInventoryEntity(
                    id = "inv-rig-001",
                    skuOrBarcode = "RIG-MOT-01",
                    itemName = "CM Lodestar 1-Ton Electric Chain Hoist",
                    category = AvlCategory.RIGGING_POWER,
                    department = WorkDepartment.RIGGING_POWER,
                    modelNumber = "Lodestar Classic 1T",
                    manufacturer = "Columbus McKinnon",
                    totalStockQty = 16,
                    availableQty = 4,
                    allocatedQty = 12,
                    maintenanceQty = 0,
                    minimumThresholdQty = 4,
                    warehouseAisleBin = "Rigging Motor Bay - Boxes 1-8",
                    unitWeightLbs = 180.0,
                    unitReplacementCost = 4800.0,
                    notes = "60ft lift chain, 7-pin Socapex connector, load inspection tags current"
                ),
                WarehouseInventoryEntity(
                    id = "inv-rig-002",
                    skuOrBarcode = "PWR-DST-01",
                    itemName = "Motion Labs 100A 3-Phase Power Distro",
                    category = AvlCategory.RIGGING_POWER,
                    department = WorkDepartment.RIGGING_POWER,
                    modelNumber = "ML-100A-3P",
                    manufacturer = "Motion Laboratories",
                    totalStockQty = 4,
                    availableQty = 1,
                    allocatedQty = 3,
                    maintenanceQty = 0,
                    minimumThresholdQty = 1,
                    warehouseAisleBin = "Power Distro Room",
                    unitWeightLbs = 95.0,
                    unitReplacementCost = 5200.0,
                    notes = "Camlock 400A thru, branch breakers for Edison and L21-30"
                ),
                WarehouseInventoryEntity(
                    id = "inv-rig-003",
                    skuOrBarcode = "RIG-TRS-01",
                    itemName = "Global Truss 12in Aluminum Box Truss (10ft)",
                    category = AvlCategory.RIGGING_POWER,
                    department = WorkDepartment.RIGGING_POWER,
                    modelNumber = "F34-300",
                    manufacturer = "Global Truss",
                    totalStockQty = 32,
                    availableQty = 8,
                    allocatedQty = 24,
                    maintenanceQty = 0,
                    minimumThresholdQty = 6,
                    warehouseAisleBin = "Truss Yard Dolly Cart 1-4",
                    unitWeightLbs = 42.0,
                    unitReplacementCost = 650.0,
                    notes = "Standard conical coupling system with steel pins"
                ),
                WarehouseInventoryEntity(
                    id = "inv-rig-004",
                    skuOrBarcode = "RIG-RMP-01",
                    itemName = "Yellow Jacket 5-Channel Heavy Duty Ramps",
                    category = AvlCategory.RIGGING_POWER,
                    department = WorkDepartment.RIGGING_POWER,
                    modelNumber = "YJ5-125",
                    manufacturer = "Checkers Safety",
                    totalStockQty = 30,
                    availableQty = 10,
                    allocatedQty = 20,
                    maintenanceQty = 0,
                    minimumThresholdQty = 5,
                    warehouseAisleBin = "Ramp Cart Staging",
                    unitWeightLbs = 26.0,
                    unitReplacementCost = 220.0,
                    notes = "ADA crossover caps included"
                ),

                // Cables & Accessories
                WarehouseInventoryEntity(
                    id = "inv-cbl-001",
                    skuOrBarcode = "CBL-XLR-100",
                    itemName = "100ft ProCo Heavy Duty XLR Cable",
                    category = AvlCategory.CABLES_ACCESSORIES,
                    department = WorkDepartment.AUDIO,
                    modelNumber = "ArmorFlex-100",
                    manufacturer = "ProCo",
                    totalStockQty = 50,
                    availableQty = 20,
                    allocatedQty = 30,
                    maintenanceQty = 0,
                    minimumThresholdQty = 10,
                    warehouseAisleBin = "Cable Trunk #1",
                    unitWeightLbs = 6.5,
                    unitReplacementCost = 65.0,
                    notes = "Neutrik NC3-XX black gold connectors"
                ),
                WarehouseInventoryEntity(
                    id = "inv-cbl-002",
                    skuOrBarcode = "CBL-ETH-01",
                    itemName = "150ft Ruggedized Cat6 Ethercon Reel",
                    category = AvlCategory.CABLES_ACCESSORIES,
                    department = WorkDepartment.AUDIO,
                    modelNumber = "ProPlex-Cat6-150",
                    manufacturer = "TMB ProPlex",
                    totalStockQty = 12,
                    availableQty = 4,
                    allocatedQty = 8,
                    maintenanceQty = 0,
                    minimumThresholdQty = 3,
                    warehouseAisleBin = "Network Cable Reel Caddy",
                    unitWeightLbs = 18.0,
                    unitReplacementCost = 280.0,
                    notes = "Shielded tactical Cat6 with Schill reel"
                ),
                WarehouseInventoryEntity(
                    id = "inv-cbl-003",
                    skuOrBarcode = "CBL-PWR-25",
                    itemName = "25ft 12/3 SOOW Edison Stingers",
                    category = AvlCategory.CABLES_ACCESSORIES,
                    department = WorkDepartment.RIGGING_POWER,
                    modelNumber = "SOOW-12/3-25",
                    manufacturer = "Carol Cable",
                    totalStockQty = 60,
                    availableQty = 25,
                    allocatedQty = 35,
                    maintenanceQty = 0,
                    minimumThresholdQty = 15,
                    warehouseAisleBin = "Stinger Trunks A & B",
                    unitWeightLbs = 5.0,
                    unitReplacementCost = 55.0,
                    notes = "Heavy duty Hubbell industrial plugs"
                )
            )
            inventoryDao.insertAllInventoryItems(initialInventory)

            // -------------------------------------------------------------
            // Seed Checklist Templates & Event Checklist Items
            // -------------------------------------------------------------
            val checklistTemplates = listOf(
                ChecklistTemplateEntity(
                    id = "tmpl-prep-01",
                    phase = ChecklistPhase.PREP_PULL,
                    department = WorkDepartment.PRODUCTION_MGMT,
                    title = "Pull sheet verification & truck loading manifest match",
                    description = "Cross-reference target quantities against warehouse pull sheets before staging.",
                    isCritical = true,
                    sortOrder = 1
                ),
                ChecklistTemplateEntity(
                    id = "tmpl-prep-02",
                    phase = ChecklistPhase.PREP_PULL,
                    department = WorkDepartment.AUDIO,
                    title = "Battery condition & RF transmitter scan",
                    description = "Ensure all handhelds & bodypacks have fresh AA/rechargeable packs at >85% capacity.",
                    isCritical = false,
                    sortOrder = 2
                ),
                ChecklistTemplateEntity(
                    id = "tmpl-truck-01",
                    phase = ChecklistPhase.TRUCK_PACK,
                    department = WorkDepartment.RIGGING_POWER,
                    title = "Heavy motor trunks against front bulkhead",
                    description = "Position CM Lodestar hoists and main distro cases over axle load points.",
                    isCritical = true,
                    sortOrder = 1
                ),
                ChecklistTemplateEntity(
                    id = "tmpl-truck-02",
                    phase = ChecklistPhase.TRUCK_PACK,
                    department = WorkDepartment.PRODUCTION_MGMT,
                    title = "Load-lock bars and ratchet straps tensioned",
                    description = "Verify cargo containment before truck departure.",
                    isCritical = true,
                    sortOrder = 2
                ),
                ChecklistTemplateEntity(
                    id = "tmpl-rig-01",
                    phase = ChecklistPhase.RIGGING_SAFETY,
                    department = WorkDepartment.RIGGING_POWER,
                    title = "Inspect motor hoist chains and safety steels",
                    description = "Inspect chain for twists, verify safety latch on top/bottom hooks, attach secondary rated steels.",
                    isCritical = true,
                    sortOrder = 1
                ),
                ChecklistTemplateEntity(
                    id = "tmpl-rig-02",
                    phase = ChecklistPhase.RIGGING_SAFETY,
                    department = WorkDepartment.RIGGING_POWER,
                    title = "Check load cell balance before trim elevation",
                    description = "Ensure weight is evenly distributed within safe working load (SWL) limits.",
                    isCritical = true,
                    sortOrder = 2
                ),
                ChecklistTemplateEntity(
                    id = "tmpl-pwr-01",
                    phase = ChecklistPhase.POWER_DISTRO,
                    department = WorkDepartment.RIGGING_POWER,
                    title = "Camlock connection sequence (Ground, Neutral, Hots)",
                    description = "Verify Green -> White -> Black -> Red -> Blue sequence before energizing main breaker.",
                    isCritical = true,
                    sortOrder = 1
                ),
                ChecklistTemplateEntity(
                    id = "tmpl-pwr-02",
                    phase = ChecklistPhase.POWER_DISTRO,
                    department = WorkDepartment.RIGGING_POWER,
                    title = "Voltage meter verification (L1-N, L2-N, L3-N ~120V)",
                    description = "Confirm phase-to-neutral voltages are within 118-122V with zero ground potential.",
                    isCritical = true,
                    sortOrder = 2
                ),
                ChecklistTemplateEntity(
                    id = "tmpl-sig-01",
                    phase = ChecklistPhase.SIGNAL_FLOW,
                    department = WorkDepartment.AUDIO,
                    title = "Dante network lock & master word clock sync",
                    description = "Ensure Rio stage boxes, QL5 console, and D80 amps show green sync locks in Dante Controller.",
                    isCritical = true,
                    sortOrder = 1
                ),
                ChecklistTemplateEntity(
                    id = "tmpl-sig-02",
                    phase = ChecklistPhase.SIGNAL_FLOW,
                    department = WorkDepartment.VIDEO,
                    title = "Video processor EDID and genlock verification",
                    description = "Test NovaStar VX1000 input handshake with ATEM switcher at 3840x2160@60Hz.",
                    isCritical = false,
                    sortOrder = 2
                ),
                ChecklistTemplateEntity(
                    id = "tmpl-chk-01",
                    phase = ChecklistPhase.SOUNDCHECK_FOCUS,
                    department = WorkDepartment.AUDIO,
                    title = "Pink noise line check & array phase alignment",
                    description = "Verify all 8 tops and 4 subs fire with correct acoustic delay and polar response.",
                    isCritical = true,
                    sortOrder = 1
                ),
                ChecklistTemplateEntity(
                    id = "tmpl-chk-02",
                    phase = ChecklistPhase.SOUNDCHECK_FOCUS,
                    department = WorkDepartment.LIGHTING,
                    title = "Moving head home positions & DMX universe check",
                    description = "Verify all Robe MegaPointes respond to pan/tilt calibration and GrandMA3 presets.",
                    isCritical = false,
                    sortOrder = 2
                ),
                ChecklistTemplateEntity(
                    id = "tmpl-show-01",
                    phase = ChecklistPhase.SHOW_READY,
                    department = WorkDepartment.PRODUCTION_MGMT,
                    title = "FOH to Stage Manager comms & talkback hot",
                    description = "Establish clear wireless comms channel between A1, V1, L1, and Stage Management.",
                    isCritical = true,
                    sortOrder = 1
                ),
                ChecklistTemplateEntity(
                    id = "tmpl-show-02",
                    phase = ChecklistPhase.SHOW_READY,
                    department = WorkDepartment.AUDIO,
                    title = "Spare artist handheld mic on standby",
                    description = "Verify backup RF handheld channel is unmuted and placed on stage right console.",
                    isCritical = true,
                    sortOrder = 2
                ),
                ChecklistTemplateEntity(
                    id = "tmpl-str-01",
                    phase = ChecklistPhase.STRIKE_AUDIT,
                    department = WorkDepartment.AUDIO,
                    title = "All wireless transmitters & capsule audit",
                    description = "Collect all 4 Shure Axient handhelds and bodypacks into locked mic trunk.",
                    isCritical = true,
                    sortOrder = 1
                ),
                ChecklistTemplateEntity(
                    id = "tmpl-str-02",
                    phase = ChecklistPhase.STRIKE_AUDIT,
                    department = WorkDepartment.PRODUCTION_MGMT,
                    title = "Warehouse return count & damage sign-off",
                    description = "Log any damaged fixtures, missing cables, or discrepancies before releasing crew.",
                    isCritical = true,
                    sortOrder = 2
                )
            )
            checklistDao.insertAllTemplates(checklistTemplates)

            // Seed Event 1 (Neon Horizon Tour) Live Checklist Items
            val event1Checklist = checklistTemplates.mapIndexed { idx, tmpl ->
                val isCompleted = when (tmpl.phase) {
                    ChecklistPhase.PREP_PULL,
                    ChecklistPhase.TRUCK_PACK,
                    ChecklistPhase.RIGGING_SAFETY,
                    ChecklistPhase.POWER_DISTRO -> true
                    ChecklistPhase.SIGNAL_FLOW -> idx % 2 == 0
                    else -> false
                }
                ChecklistItemEntity(
                    id = "chk-evt1-${tmpl.id}",
                    eventId = event1Id,
                    phase = tmpl.phase,
                    department = tmpl.department,
                    title = tmpl.title,
                    description = tmpl.description,
                    isCompleted = isCompleted,
                    completedBy = if (isCompleted) "Elena Ramos (Lead)" else "",
                    completedAt = if (isCompleted) now - (oneHourMs * 2) else null,
                    isCritical = tmpl.isCritical,
                    sortOrder = tmpl.sortOrder,
                    verificationNote = if (isCompleted) "Verified OK during load-in" else ""
                )
            }
            checklistDao.insertAllChecklistItems(event1Checklist)

            // Seed Event 3 (Tech Summit) Checklist Items
            val event3Checklist = checklistTemplates.map { tmpl ->
                val isCompleted = tmpl.phase == ChecklistPhase.PREP_PULL
                ChecklistItemEntity(
                    id = "chk-evt3-${tmpl.id}",
                    eventId = event3Id,
                    phase = tmpl.phase,
                    department = tmpl.department,
                    title = tmpl.title,
                    description = tmpl.description,
                    isCompleted = isCompleted,
                    completedBy = if (isCompleted) "Carlos Ruiz (Video Director)" else "",
                    completedAt = if (isCompleted) now - (oneHourMs * 1) else null,
                    isCritical = tmpl.isCritical,
                    sortOrder = tmpl.sortOrder,
                    verificationNote = if (isCompleted) "Pulled from warehouse racks" else ""
                )
            }
            checklistDao.insertAllChecklistItems(event3Checklist)

            // -------------------------------------------------------------
            // Seed Equipment Loadouts & Items
            // -------------------------------------------------------------
            val loadout1Id = "ldo-arena-pa-001"
            val loadout1 = EquipmentLoadoutEntity(
                id = loadout1Id,
                eventId = event1Id,
                name = "Main Stage Concert PA & Audio Rig",
                department = WorkDepartment.AUDIO,
                truckOrVehicle = "Truck 01 (53ft Semi)",
                targetWeightLbs = 8400.0,
                powerDrawAmps = 120,
                status = LoadoutStatus.LOADED_ON_TRUCK,
                assignedLeadTech = "Dave Miller (Chief Audio)",
                notes = "FOH array & subs package with Dante primary & secondary network cards",
                createdAt = now - (oneDayMs * 2)
            )

            val loadout1Items = listOf(
                LoadoutItemEntity(
                    id = UUID.randomUUID().toString(),
                    loadoutId = loadout1Id,
                    inventoryItemId = "inv-aud-005",
                    itemName = "d&b audiotechnik V-Series Array Tops",
                    category = AvlCategory.AUDIO,
                    requiredQuantity = 8,
                    packedQuantity = 8,
                    loadedQuantity = 8,
                    caseOrFlightRack = "Speaker Dolly Array 1",
                    barcodeOrRfid = "AUD-SPK-01",
                    isVerified = true
                ),
                LoadoutItemEntity(
                    id = UUID.randomUUID().toString(),
                    loadoutId = loadout1Id,
                    inventoryItemId = "inv-aud-006",
                    itemName = "d&b V-SUB Flyable Subwoofers",
                    category = AvlCategory.AUDIO,
                    requiredQuantity = 4,
                    packedQuantity = 4,
                    loadedQuantity = 4,
                    caseOrFlightRack = "Speaker Dolly Array 2",
                    barcodeOrRfid = "AUD-SUB-01",
                    isVerified = true
                ),
                LoadoutItemEntity(
                    id = UUID.randomUUID().toString(),
                    loadoutId = loadout1Id,
                    inventoryItemId = "inv-aud-007",
                    itemName = "d&b D80 4ch Power Amplifiers",
                    category = AvlCategory.AUDIO,
                    requiredQuantity = 2,
                    packedQuantity = 2,
                    loadedQuantity = 2,
                    caseOrFlightRack = "Amp Rack Alpha",
                    barcodeOrRfid = "AUD-AMP-01",
                    isVerified = true
                ),
                LoadoutItemEntity(
                    id = UUID.randomUUID().toString(),
                    loadoutId = loadout1Id,
                    inventoryItemId = "inv-aud-003",
                    itemName = "Yamaha QL5 32ch Digital Audio Mixer",
                    category = AvlCategory.AUDIO,
                    requiredQuantity = 1,
                    packedQuantity = 1,
                    loadedQuantity = 1,
                    caseOrFlightRack = "Console Road Case #1",
                    barcodeOrRfid = "AUD-MIX-01",
                    isVerified = true
                ),
                LoadoutItemEntity(
                    id = UUID.randomUUID().toString(),
                    loadoutId = loadout1Id,
                    inventoryItemId = "inv-aud-001",
                    itemName = "Shure Axient Digital Dual Handheld Mics",
                    category = AvlCategory.AUDIO,
                    requiredQuantity = 4,
                    packedQuantity = 4,
                    loadedQuantity = 4,
                    caseOrFlightRack = "Mic Trunk A",
                    barcodeOrRfid = "AUD-RF-01",
                    isVerified = true
                )
            )

            val loadout2Id = "ldo-arena-lgt-002"
            val loadout2 = EquipmentLoadoutEntity(
                id = loadout2Id,
                eventId = event1Id,
                name = "Arena Intelligent Lighting & Truss Loadout",
                department = WorkDepartment.LIGHTING,
                truckOrVehicle = "Truck 01 (53ft Semi)",
                targetWeightLbs = 4200.0,
                powerDrawAmps = 80,
                status = LoadoutStatus.LOADED_ON_TRUCK,
                assignedLeadTech = "Sarah Chen (Lighting Director)",
                notes = "8 moving heads, 12 zoom pars, hazer and GrandMA3 command wing",
                createdAt = now - (oneDayMs * 2)
            )

            val loadout2Items = listOf(
                LoadoutItemEntity(
                    id = UUID.randomUUID().toString(),
                    loadoutId = loadout2Id,
                    inventoryItemId = "inv-lgt-001",
                    itemName = "Robe MegaPointe Hybrid Moving Heads",
                    category = AvlCategory.LIGHTING,
                    requiredQuantity = 8,
                    packedQuantity = 8,
                    loadedQuantity = 8,
                    caseOrFlightRack = "Dual Flight Cases 1-4",
                    barcodeOrRfid = "LGT-MOV-01",
                    isVerified = true
                ),
                LoadoutItemEntity(
                    id = UUID.randomUUID().toString(),
                    loadoutId = loadout2Id,
                    inventoryItemId = "inv-lgt-002",
                    itemName = "Chauvet COLORado 2 Quad Zoom LED",
                    category = AvlCategory.LIGHTING,
                    requiredQuantity = 12,
                    packedQuantity = 12,
                    loadedQuantity = 12,
                    caseOrFlightRack = "6-Way Road Cases A & B",
                    barcodeOrRfid = "LGT-PAR-01",
                    isVerified = true
                ),
                LoadoutItemEntity(
                    id = UUID.randomUUID().toString(),
                    loadoutId = loadout2Id,
                    inventoryItemId = "inv-lgt-003",
                    itemName = "GrandMA3 Command Wing onPC Setup",
                    category = AvlCategory.LIGHTING,
                    requiredQuantity = 1,
                    packedQuantity = 1,
                    loadedQuantity = 1,
                    caseOrFlightRack = "Lighting FOH Case #1",
                    barcodeOrRfid = "LGT-CNS-01",
                    isVerified = true
                )
            )

            val loadout3Id = "ldo-video-wall-003"
            val loadout3 = EquipmentLoadoutEntity(
                id = loadout3Id,
                eventId = event3Id,
                name = "Corporate 4K Video LED Wall Flypack",
                department = WorkDepartment.VIDEO,
                truckOrVehicle = "Truck 02 (26ft Box)",
                targetWeightLbs = 5100.0,
                powerDrawAmps = 60,
                status = LoadoutStatus.STAGED_IN_BAY,
                assignedLeadTech = "Carlos Ruiz (Video Director)",
                notes = "24 panels of Absen 2.9mm wall with Novastar processor and PTZ cameras",
                createdAt = now - (oneDayMs * 1)
            )

            val loadout3Items = listOf(
                LoadoutItemEntity(
                    id = UUID.randomUUID().toString(),
                    loadoutId = loadout3Id,
                    inventoryItemId = "inv-vid-001",
                    itemName = "Absen 2.9mm Indoor LED Wall Panel",
                    category = AvlCategory.VIDEO,
                    requiredQuantity = 24,
                    packedQuantity = 16,
                    loadedQuantity = 0,
                    caseOrFlightRack = "LED Cases 1-3",
                    barcodeOrRfid = "VID-LED-01",
                    isVerified = false
                ),
                LoadoutItemEntity(
                    id = UUID.randomUUID().toString(),
                    loadoutId = loadout3Id,
                    inventoryItemId = "inv-vid-002",
                    itemName = "Novastar VX1000 All-in-One Video Processor",
                    category = AvlCategory.VIDEO,
                    requiredQuantity = 2,
                    packedQuantity = 2,
                    loadedQuantity = 0,
                    caseOrFlightRack = "Video Rack 1",
                    barcodeOrRfid = "VID-PROC-01",
                    isVerified = true
                ),
                LoadoutItemEntity(
                    id = UUID.randomUUID().toString(),
                    loadoutId = loadout3Id,
                    inventoryItemId = "inv-vid-004",
                    itemName = "Panasonic 4K PTZ Camera AW-UE150",
                    category = AvlCategory.VIDEO,
                    requiredQuantity = 3,
                    packedQuantity = 0,
                    loadedQuantity = 0,
                    caseOrFlightRack = "Pelican PTZ Case A",
                    barcodeOrRfid = "VID-CAM-01",
                    isVerified = false
                )
            )

            loadoutDao.insertAllLoadouts(listOf(loadout1, loadout2, loadout3))
            loadoutDao.insertAllLoadoutItems(loadout1Items + loadout2Items + loadout3Items)

            // -------------------------------------------------------------
            // Seed Initial Inventory Transactions
            // -------------------------------------------------------------
            val initialTransactions = listOf(
                InventoryTransactionEntity(
                    id = UUID.randomUUID().toString(),
                    inventoryItemId = "inv-aud-005",
                    eventId = event1Id,
                    loadoutId = loadout1Id,
                    transactionType = InventoryTransactionType.DISPATCH_LOADOUT,
                    quantityDelta = -8,
                    previousAvailableQty = 16,
                    newAvailableQty = 8,
                    technicianName = "Dave Miller",
                    timestamp = now - (oneDayMs * 2),
                    referenceNotes = "Allocated 8 tops to Neon Horizon Tour"
                ),
                InventoryTransactionEntity(
                    id = UUID.randomUUID().toString(),
                    inventoryItemId = "inv-lgt-001",
                    eventId = event1Id,
                    loadoutId = loadout2Id,
                    transactionType = InventoryTransactionType.DISPATCH_LOADOUT,
                    quantityDelta = -16,
                    previousAvailableQty = 24,
                    newAvailableQty = 8,
                    technicianName = "Sarah Chen",
                    timestamp = now - (oneDayMs * 2),
                    referenceNotes = "Dispatched 16 MegaPointes on Truck 01"
                ),
                InventoryTransactionEntity(
                    id = UUID.randomUUID().toString(),
                    inventoryItemId = "inv-lgt-002",
                    eventId = event2Id,
                    loadoutId = null,
                    transactionType = InventoryTransactionType.BENCH_MAINTENANCE,
                    quantityDelta = -1,
                    previousAvailableQty = 16,
                    newAvailableQty = 15,
                    technicianName = "Rachel Adams",
                    timestamp = now - (oneDayMs * 1),
                    referenceNotes = "Sent Chauvet par with cracked lens to repair bench"
                ),
                InventoryTransactionEntity(
                    id = UUID.randomUUID().toString(),
                    inventoryItemId = "inv-aud-008",
                    eventId = event2Id,
                    loadoutId = null,
                    transactionType = InventoryTransactionType.BENCH_MAINTENANCE,
                    quantityDelta = -1,
                    previousAvailableQty = 9,
                    newAvailableQty = 8,
                    technicianName = "Rachel Adams",
                    timestamp = now - (oneDayMs * 1),
                    referenceNotes = "Flagged DI box for phantom power switch replacement"
                )
            )
            inventoryDao.insertAllTransactions(initialTransactions)
        }
    }

    private class AvlDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }
    }
}
