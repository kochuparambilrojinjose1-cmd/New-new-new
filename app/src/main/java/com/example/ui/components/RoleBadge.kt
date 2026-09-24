package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MemberAvailability
import com.example.model.UserRole
import com.example.model.WorkDepartment
import com.example.ui.theme.AmberConcert
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.LaserGreen

@Composable
fun UserRoleBadge(
    role: UserRole,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (role) {
        UserRole.OWNER -> Triple(Color(0xFF331E00), Color(0xFFFFB74D), "👑")
        UserRole.ADMIN -> Triple(Color(0xFF2A103D), Color(0xFFCE93D8), "🛡️")
        UserRole.MANAGER -> Triple(Color(0xFF002738), Color(0xFF4FC3F7), "📋")
        UserRole.CREW -> Triple(Color(0xFF132A1B), Color(0xFF81C784), "🛠️")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .border(0.5.dp, textColor.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = icon,
                fontSize = 9.sp
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = role.displayName.uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                letterSpacing = 0.4.sp
            )
        }
    }
}

@Composable
fun MemberAvailabilityBadge(
    status: MemberAvailability,
    modifier: Modifier = Modifier
) {
    val (dotColor, bgColor, textColor) = when (status) {
        MemberAvailability.AVAILABLE -> Triple(LaserGreen, Color(0xFF0F291E), LaserGreen)
        MemberAvailability.TENTATIVE -> Triple(AmberConcert, Color(0xFF2C220E), AmberConcert)
        MemberAvailability.UNAVAILABLE -> Triple(CrimsonAlert, Color(0xFF2C1111), CrimsonAlert)
        MemberAvailability.CONFIRMED_ASSIGNED -> Triple(CyanNeon, Color(0xFF0B2533), CyanNeon)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .border(0.5.dp, textColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status.displayName,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun DepartmentBadge(
    department: WorkDepartment,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(Color(0xFF1E232A))
            .border(0.5.dp, Color(0xFF3B4758), RoundedCornerShape(3.dp))
            .padding(horizontal = 5.dp, vertical = 1.5.dp)
    ) {
        Text(
            text = department.displayName,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF90CAF9)
        )
    }
}
