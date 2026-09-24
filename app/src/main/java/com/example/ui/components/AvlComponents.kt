package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AvlCategory
import com.example.model.EventStatus
import com.example.model.ItemStatus
import com.example.model.LogType
import com.example.model.NotificationPriority
import com.example.ui.theme.AmberConcert
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CategoryAudio
import com.example.ui.theme.CategoryCables
import com.example.ui.theme.CategoryLighting
import com.example.ui.theme.CategoryRigging
import com.example.ui.theme.CategoryVideo
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.CyanNeonGlow
import com.example.ui.theme.DenseBorderDark
import com.example.ui.theme.DenseBorderSubtleDark
import com.example.ui.theme.DenseSurfaceDark
import com.example.ui.theme.DenseSurfaceElevatedDark
import com.example.ui.theme.DenseSurfaceHighlightDark
import com.example.ui.theme.DenseTextPrimaryDark
import com.example.ui.theme.DenseTextSecondaryDark
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.LaserGreen
import com.example.ui.theme.LaserGreenGlow
import com.example.ui.theme.OrangeDamaged

@Composable
fun AvlCategoryBadge(category: AvlCategory, modifier: Modifier = Modifier) {
    val (color, glow) = when (category) {
        AvlCategory.AUDIO -> Pair(CategoryAudio, AmberGlow)
        AvlCategory.VIDEO -> Pair(CategoryVideo, CyanNeonGlow)
        AvlCategory.LIGHTING -> Pair(CategoryLighting, LaserGreenGlow)
        AvlCategory.RIGGING_POWER -> Pair(CategoryRigging, Color(0x22A78BFA))
        AvlCategory.CABLES_ACCESSORIES -> Pair(CategoryCables, Color(0x22FB923C))
    }

    Surface(
        color = glow,
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = category.iconEmoji,
                fontSize = 11.sp,
                modifier = Modifier.padding(end = 3.dp)
            )
            Text(
                text = category.displayName.uppercase(),
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun EventStatusBadge(status: EventStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        EventStatus.PLANNING -> Pair(DenseSurfaceElevatedDark, DenseTextSecondaryDark)
        EventStatus.PACKING -> Pair(AmberGlow, AmberConcert)
        EventStatus.LOADED -> Pair(CyanNeonGlow, CyanNeon)
        EventStatus.ON_SITE_SETUP -> Pair(Color(0x248B5CF6), ElectricViolet)
        EventStatus.LIVE_SHOW -> Pair(LaserGreenGlow, LaserGreen)
        EventStatus.STRIKE_RETURN -> Pair(Color(0x24F97316), OrangeDamaged)
        EventStatus.COMPLETED_RECONCILED -> Pair(LaserGreenGlow, LaserGreen)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.45f)),
        modifier = modifier
    ) {
        Text(
            text = status.displayName.uppercase(),
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.4.sp,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
        )
    }
}

@Composable
fun PriorityBadge(priority: NotificationPriority, modifier: Modifier = Modifier) {
    val (color, bg) = when (priority) {
        NotificationPriority.CRITICAL -> Pair(CrimsonAlert, CrimsonGlow)
        NotificationPriority.ALERT -> Pair(OrangeDamaged, Color(0x24F97316))
        NotificationPriority.LOGISTICS -> Pair(CyanNeon, CyanNeonGlow)
        NotificationPriority.GENERAL -> Pair(DenseTextPrimaryDark, DenseSurfaceElevatedDark)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.6f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
        ) {
            Text(
                text = priority.iconEmoji,
                fontSize = 10.sp,
                modifier = Modifier.padding(end = 3.dp)
            )
            Text(
                text = priority.displayName.uppercase(),
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp
            )
        }
    }
}

@Composable
fun LogTypeBadge(logType: LogType, modifier: Modifier = Modifier) {
    val color = when (logType) {
        LogType.INFO -> CyanNeon
        LogType.MILESTONE -> LaserGreen
        LogType.SOUNDCHECK -> AmberConcert
        LogType.VOLTAGE_CHECK -> ElectricViolet
        LogType.ISSUE_ALERT -> CrimsonAlert
        LogType.SIGN_OFF -> LaserGreen
    }

    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.45f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = logType.iconEmoji,
                fontSize = 10.sp,
                modifier = Modifier.padding(end = 3.dp)
            )
            Text(
                text = logType.displayName.uppercase(),
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp
            )
        }
    }
}

@Composable
fun ProgressStatsCard(
    title: String,
    currentCount: Int,
    targetCount: Int,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (targetCount > 0) (currentCount.toFloat() / targetCount.toFloat()).coerceIn(0f, 1f) else 0f
    val percentage = (progress * 100).toInt()

    Card(
        colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = DenseTextSecondaryDark,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "$percentage%",
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                color = accentColor,
                trackColor = DenseSurfaceHighlightDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "$currentCount / $targetCount units",
                style = MaterialTheme.typography.bodySmall,
                color = DenseTextSecondaryDark,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun QuickQuantityStepper(
    currentValue: Int,
    targetValue: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onMaxOut: () -> Unit,
    accentColor: Color = CyanNeon,
    modifier: Modifier = Modifier
) {
    val isFulfilled = currentValue >= targetValue && targetValue > 0

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = DenseSurfaceElevatedDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(4.dp))
                .clickable(onClick = onDecrement)
                .testTag("qty_decrement")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease count",
                    tint = DenseTextPrimaryDark,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(4.dp),
            color = if (isFulfilled) accentColor.copy(alpha = 0.16f) else DenseSurfaceElevatedDark,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isFulfilled) accentColor.copy(alpha = 0.7f) else DenseBorderDark
            ),
            modifier = Modifier
                .height(30.dp)
                .padding(horizontal = 1.dp)
                .clickable(onClick = onMaxOut)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "$currentValue / $targetValue",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isFulfilled) accentColor else DenseTextPrimaryDark,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(4.dp),
            color = if (isFulfilled) LaserGreenGlow else DenseSurfaceElevatedDark,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isFulfilled) LaserGreen.copy(alpha = 0.7f) else DenseBorderDark
            ),
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(4.dp))
                .clickable(onClick = onIncrement)
                .testTag("qty_increment")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase count",
                    tint = if (isFulfilled) LaserGreen else DenseTextPrimaryDark,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

