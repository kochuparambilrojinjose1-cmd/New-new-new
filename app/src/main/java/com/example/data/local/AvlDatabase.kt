package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.AvlCategory
import com.example.model.AvlPresets
import com.example.model.EventStatus
import com.example.model.ItemStatus
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
        WarehouseAuditEntity::class
    ],
    version = 2,
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

        private suspend fun populateInitialData(db: AvlDatabase) {
            val userDao = db.userAccountDao()
            val availDao = db.eventAvailabilityDao()
            val eventDao = db.eventDao()
            val equipmentDao = db.equipmentDao()
            val logDao = db.setupLogDao()
            val notifDao = db.teamNotificationDao()

            val now = System.currentTimeMillis()
            val oneDayMs = 24 * 60 * 60 * 1000L
            val oneHourMs = 60 * 60 * 1000L

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

            userDao.insertAllUsers(listOf(userOwner, userAdmin, userManager, userCrewAudio, userCrewLight, userCrewStage))

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
        }
    }
}
