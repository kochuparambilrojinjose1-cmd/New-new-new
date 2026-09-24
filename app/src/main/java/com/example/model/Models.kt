package com.example.model

enum class UserRole(
    val displayName: String,
    val rank: Int,
    val iconEmoji: String,
    val roleBadgeLabel: String,
    val description: String
) {
    OWNER("Owner", 4, "👑", "OWNER", "Full organization ownership, role assignment, event deletion & signoffs"),
    ADMIN("Admin", 3, "🛡️", "ADMIN", "Production planning, event creation, crew dispatch, full inventory editing"),
    MANAGER("Manager", 2, "📋", "MANAGER", "Stage / warehouse management, checklists, return audits & setup logs"),
    CREW("Crew", 1, "🛠️", "CREW", "Field technician: availability RSVP, pack scanning, setup logs & comms ack")
}

enum class MemberAvailability(
    val displayName: String,
    val iconEmoji: String,
    val badgeLabel: String
) {
    AVAILABLE("Available", "🟢", "AVAILABLE"),
    TENTATIVE("Tentative", "🟡", "TENTATIVE"),
    UNAVAILABLE("Unavailable", "🔴", "UNAVAILABLE"),
    CONFIRMED_ASSIGNED("Confirmed on Call", "🔵", "ASSIGNED")
}

enum class WorkDepartment(
    val displayName: String,
    val iconEmoji: String,
    val shortCode: String
) {
    AUDIO("Audio Tech (A1/A2)", "🔊", "AUD"),
    VIDEO("Video Tech (V1/LED)", "📺", "VID"),
    LIGHTING("Lighting Op (L1/L2)", "💡", "LGT"),
    RIGGING_POWER("Rigging & Power", "🏗️", "RIG"),
    STAGE_HAND("Stage Ops / General", "🎪", "STG"),
    PRODUCTION_MGMT("Production / PM", "📋", "MGMT")
}

enum class AvlCategory(val displayName: String, val iconEmoji: String) {
    AUDIO("Audio", "🔊"),
    VIDEO("Video", "📺"),
    LIGHTING("Lighting", "💡"),
    RIGGING_POWER("Rigging & Power", "🏗️"),
    CABLES_ACCESSORIES("Cables & Accs", "🔌")
}

enum class EventStatus(val displayName: String, val stepIndex: Int) {
    PLANNING("Planning & Prep", 0),
    PACKING("Outbound Packing", 1),
    LOADED("Truck Loaded", 2),
    ON_SITE_SETUP("On-Site Setup", 3),
    LIVE_SHOW("Live Show / Run", 4),
    STRIKE_RETURN("Strike & Return", 5),
    COMPLETED_RECONCILED("Reconciled & Closed", 6)
}

enum class ItemStatus(val displayName: String) {
    PENDING_PACK("Pending Pack"),
    PACKED("Packed & Staged"),
    LOADED("Loaded on Truck"),
    RETURNED_OK("Returned OK"),
    FLAGGED_DISCREPANCY("Missing / Incomplete"),
    BENCH_REPAIR("Needs Repair / Damaged")
}

enum class LogType(val displayName: String, val iconEmoji: String) {
    INFO("General Note", "📝"),
    MILESTONE("Milestone Reached", "🎯"),
    SOUNDCHECK("Sound & Patch Test", "🔊"),
    VOLTAGE_CHECK("Voltage / Power Check", "⚡"),
    ISSUE_ALERT("Issue / Problem Found", "⚠️"),
    SIGN_OFF("Crew Sign-Off", "✅")
}

enum class NotificationPriority(val displayName: String, val iconEmoji: String) {
    CRITICAL("Critical Alert", "🚨"),
    ALERT("High Priority", "⚠️"),
    LOGISTICS("Truck / Logistics", "🚚"),
    GENERAL("Team Update", "ℹ️")
}

data class EquipmentPreset(
    val name: String,
    val category: AvlCategory,
    val defaultQty: Int,
    val storageCase: String,
    val suggestedBarcode: String,
    val notes: String = ""
)

object AvlPresets {
    val defaultInventory = listOf(
        // Audio
        EquipmentPreset("Shure Axient Digital Dual Handheld Mics", AvlCategory.AUDIO, 4, "Mic Trunk A", "AUD-RF-01", "Check frequency scan on-site"),
        EquipmentPreset("Sennheiser EW-DX Wireless Bodypack & Lav", AvlCategory.AUDIO, 4, "Mic Trunk A", "AUD-RF-02", "Spare AAA batteries included"),
        EquipmentPreset("Yamaha QL5 32ch Digital Audio Mixer", AvlCategory.AUDIO, 1, "Console Road Case #1", "AUD-MIX-01", "With Dante card & doghouse"),
        EquipmentPreset("Rio 3224-D2 Stage Box", AvlCategory.AUDIO, 2, "Stage Box Case #2", "AUD-SB-01", "Primary + Redundant Ethercon"),
        EquipmentPreset("d&b audiotechnik V-Series Array Tops", AvlCategory.AUDIO, 8, "Speaker Dolly Array 1", "AUD-SPK-01", "Rigging pins verified"),
        EquipmentPreset("d&b V-SUB Flyable Subwoofers", AvlCategory.AUDIO, 4, "Speaker Dolly Array 2", "AUD-SUB-01", "Cardioid dispersion mode"),
        EquipmentPreset("d&b D80 4ch Power Amplifiers", AvlCategory.AUDIO, 2, "Amp Rack Alpha", "AUD-AMP-01", "32A CEE Form input"),
        EquipmentPreset("Whirlwind 100ft 12ch XLR Sub-Snake", AvlCategory.AUDIO, 2, "Cable Trunk #3", "AUD-CBL-01", "Drop snake for stage left/right"),
        EquipmentPreset("Radial J48 Active Direct Box (DI)", AvlCategory.AUDIO, 6, "DI Pelican Case", "AUD-DI-01", "Requires 48V phantom power"),
        EquipmentPreset("K&M Heavy Duty Boom Mic Stands", AvlCategory.AUDIO, 10, "Mic Stand Bag 1", "AUD-STD-01", "With clutch tighteners"),

        // Video
        EquipmentPreset("Absen 2.9mm Indoor LED Wall Panel (500x500)", AvlCategory.VIDEO, 32, "LED Wall Flight Cases 1-4", "VID-LED-01", "Each case holds 8 panels"),
        EquipmentPreset("Novastar VX1000 All-in-One Video Processor", AvlCategory.VIDEO, 2, "Video Rack 1", "VID-PROC-01", "10 Ethernet ports configured"),
        EquipmentPreset("Blackmagic ATEM Constellation 2 M/E 4K", AvlCategory.VIDEO, 1, "Video Flypack A", "VID-SW-01", "With 1 M/E Advanced Panel"),
        EquipmentPreset("Panasonic 4K PTZ Camera AW-UE150", AvlCategory.VIDEO, 3, "Pelican PTZ Case", "VID-CAM-01", "12G-SDI & NDI outputs"),
        EquipmentPreset("Panasonic PTZ Controller AW-RP150", AvlCategory.VIDEO, 1, "Pelican PTZ Case", "VID-CTRL-01", "Touchscreen joystick"),
        EquipmentPreset("Atomos Shogun 7 HDR Field Monitor/Recorder", AvlCategory.VIDEO, 2, "Monitor Pelican", "VID-MON-01", "With Master Caddy SSDs"),
        EquipmentPreset("150ft 12G-SDI BNC Cable Drum", AvlCategory.VIDEO, 4, "Video Cable Drum Caddy", "VID-CBL-01", "Belden 4794R rated"),

        // Lighting
        EquipmentPreset("Robe MegaPointe Hybrid Moving Heads", AvlCategory.LIGHTING, 8, "Dual Road Cases 1-4", "LGT-MOV-01", "Discharge lamp checked"),
        EquipmentPreset("Chauvet Professional COLORado 2 Quad Zoom LED", AvlCategory.LIGHTING, 12, "6-Way Road Cases A & B", "LGT-PAR-01", "IP65 rated zoom pars"),
        EquipmentPreset("GrandMA3 Command Wing onPC Setup", AvlCategory.LIGHTING, 1, "Lighting FOH Case", "LGT-CNS-01", "Touch laptop + motorized faders"),
        EquipmentPreset("Astera Titan Tube 8-Way Wireless Kit", AvlCategory.LIGHTING, 1, "Astera Charging Case", "LGT-AST-01", "Includes ART7 transmitter box"),
        EquipmentPreset("Ultratec Radiance Hazer (DMX)", AvlCategory.LIGHTING, 2, "Hazer Road Case", "LGT-HAZ-01", "Full fluid tank + 2 spare gallons"),
        EquipmentPreset("50ft 5-Pin DMX Cable Bundle", AvlCategory.LIGHTING, 10, "Lighting Cable Trunk", "LGT-DMX-01", "120-ohm impedance"),

        // Rigging & Power
        EquipmentPreset("CM Lodestar 1-Ton Electric Chain Hoist (60ft lift)", AvlCategory.RIGGING_POWER, 4, "Motor Road Cases 1-2", "RIG-MOT-01", "Inspection tag current"),
        EquipmentPreset("Applied Electronics 8ch Motor Controller Distro", AvlCategory.RIGGING_POWER, 1, "Rigging Power Rack", "RIG-CTRL-01", "With handheld pickle remote"),
        EquipmentPreset("Global Truss 12in Aluminum Box Truss (10ft)", AvlCategory.RIGGING_POWER, 8, "Truss Cart 1", "RIG-TRS-01", "Includes spigots, pins, R-clips"),
        EquipmentPreset("Motion Labs 100A 3-Phase Power Distro", AvlCategory.RIGGING_POWER, 1, "Main Distro Rack", "PWR-DST-01", "Camlock in / L21-30 & Edison out"),
        EquipmentPreset("50ft 4/0 Camlock Feeder Cable Set (5-wire)", AvlCategory.RIGGING_POWER, 1, "Feeder Cable Bin", "PWR-CAM-01", "Green, White, Black, Red, Blue"),
        EquipmentPreset("Yellow Jacket 5-Channel Heavy Duty Cable Ramps", AvlCategory.RIGGING_POWER, 8, "Ramp Dolly", "RIG-RMP-01", "ADA compliant crossover caps"),

        // Cables & Accessories
        EquipmentPreset("100ft ProCo Heavy Duty XLR Cable", AvlCategory.CABLES_ACCESSORIES, 12, "Cable Trunk #1", "CBL-XLR-100", "Neutrik gold connectors"),
        EquipmentPreset("50ft ProCo Heavy Duty XLR Cable", AvlCategory.CABLES_ACCESSORIES, 20, "Cable Trunk #1", "CBL-XLR-50", "Neutrik gold connectors"),
        EquipmentPreset("25ft ProCo Heavy Duty XLR Cable", AvlCategory.CABLES_ACCESSORIES, 30, "Cable Trunk #2", "CBL-XLR-25", "Neutrik gold connectors"),
        EquipmentPreset("150ft Ruggedized Cat6 Ethercon Reel (Shielded)", AvlCategory.CABLES_ACCESSORIES, 4, "Network Cable Caddy", "CBL-ETH-01", "Dante / Digital Snake spec"),
        EquipmentPreset("25ft 12/3 SOOW Edison Extension Stingers", AvlCategory.CABLES_ACCESSORIES, 25, "Stinger Trunk", "CBL-PWR-25", "Heavy duty quad boxes"),
        EquipmentPreset("Gaffers Tape 2-inch Pro-Gaff Black (Rolls)", AvlCategory.CABLES_ACCESSORIES, 6, "Expendables Box", "EXP-GAF-01", "Stage crew consumables")
    )
}
