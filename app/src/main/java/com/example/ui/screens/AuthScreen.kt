package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.UserAccountEntity
import com.example.model.UserRole
import com.example.model.WorkDepartment
import com.example.ui.components.DepartmentBadge
import com.example.ui.components.UserRoleBadge
import com.example.ui.theme.AmberConcert
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DenseBackgroundDark
import com.example.ui.theme.DenseBorderDark
import com.example.ui.theme.DenseBorderSubtleDark
import com.example.ui.theme.DenseSurfaceDark
import com.example.ui.theme.DenseSurfaceElevatedDark
import com.example.ui.theme.DenseSurfaceHighlightDark
import com.example.ui.theme.DenseTextPrimaryDark
import com.example.ui.theme.DenseTextSecondaryDark
import com.example.ui.theme.LaserGreen
import com.example.viewmodel.AvlViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AvlViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val errorMessage by viewModel.authErrorMessage.collectAsStateWithLifecycle()
    val successMessage by viewModel.authSuccessMessage.collectAsStateWithLifecycle()

    var selectedTab by rememberSaveable { mutableIntStateOf(if (currentUser == null) 0 else 0) }

    // Login Form State
    var loginUsername by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var showLoginPassword by remember { mutableStateOf(false) }

    // Register Form State
    var regUsername by remember { mutableStateOf("") }
    var regFullName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regRole by remember { mutableStateOf(UserRole.CREW) }
    var regDepartment by remember { mutableStateOf(WorkDepartment.AUDIO) }
    var showRegPassword by remember { mutableStateOf(false) }

    var roleDropdownExpanded by remember { mutableStateOf(false) }
    var deptDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DenseBackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App / Auth Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(DenseSurfaceDark)
                .border(1.dp, DenseBorderDark, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF00384D))
                            .border(1.dp, CyanNeon, RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Engineering,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AVL TEAM ACCESS PORTAL",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Audio • Video • Lighting • Staging Roster",
                            fontSize = 10.sp,
                            color = CyanNeon
                        )
                    }
                }

                currentUser?.let { user ->
                    UserRoleBadge(role = user.role)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Active User Status Card
        currentUser?.let { user ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DenseSurfaceHighlightDark),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(CyanNeon),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.initials,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DenseBackgroundDark
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = user.fullName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "@${user.username} • ${user.email}",
                                    fontSize = 10.sp,
                                    color = DenseTextSecondaryDark,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        UserRoleBadge(role = user.role)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DepartmentBadge(department = user.department)
                        Row {
                            TextButton(
                                onClick = onAuthSuccess,
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Continue to App →", fontSize = 11.sp, color = CyanNeon, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            TextButton(
                                onClick = { viewModel.logout() },
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Log Out", fontSize = 11.sp, color = CrimsonAlert)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Tabs: Sign In / Register
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DenseSurfaceDark,
            contentColor = CyanNeon,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CyanNeon,
                    height = 2.dp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .border(1.dp, DenseBorderDark, RoundedCornerShape(6.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign In", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_login")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Register New Member", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_register")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Feedback banners
        errorMessage?.let { err ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF3B1414))
                    .border(1.dp, CrimsonAlert, RoundedCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                Text(text = "⚠️ $err", color = CrimsonAlert, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        successMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF0E2E1D))
                    .border(1.dp, LaserGreen, RoundedCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                Text(text = "✅ $msg", color = LaserGreen, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Tab Content
        if (selectedTab == 0) {
            // -------------------------------------------------------------
            // SIGN IN TAB
            // -------------------------------------------------------------
            Card(
                colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "ACCOUNT LOGIN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DenseTextPrimaryDark
                    )
                    Text(
                        text = "Sign in to access your assigned programs and call-time alerts.",
                        fontSize = 10.sp,
                        color = DenseTextSecondaryDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = loginUsername,
                        onValueChange = { loginUsername = it },
                        label = { Text("Username or Email") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = DenseBorderDark,
                            focusedContainerColor = DenseSurfaceDark,
                            unfocusedContainerColor = DenseSurfaceDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_login_username")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = loginPassword,
                        onValueChange = { loginPassword = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp)) },
                        trailingIcon = {
                            IconButton(onClick = { showLoginPassword = !showLoginPassword }) {
                                Icon(
                                    if (showLoginPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = DenseTextSecondaryDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        visualTransformation = if (showLoginPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = DenseBorderDark,
                            focusedContainerColor = DenseSurfaceDark,
                            unfocusedContainerColor = DenseSurfaceDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_login_password")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (viewModel.login(loginUsername, loginPassword)) {
                                onAuthSuccess()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = DenseBackgroundDark),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("btn_submit_login")
                    ) {
                        Text("Sign In to AVL Console", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Switch Demo Profiles Section
                    Text(
                        text = "⚡ QUICK SWITCH DEMO PROFILES (4 ROLE CLASSES)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanNeon,
                        letterSpacing = 0.4.sp
                    )
                    Text(
                        text = "1-tap switch to test role permissions (Owner, Admin, Manager, Crew):",
                        fontSize = 9.sp,
                        color = DenseTextSecondaryDark
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        allUsers.forEach { u ->
                            val isCurrent = u.id == currentUser?.id
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isCurrent) DenseSurfaceHighlightDark else DenseSurfaceElevatedDark)
                                    .border(0.5.dp, if (isCurrent) CyanNeon else DenseBorderSubtleDark, RoundedCornerShape(4.dp))
                                    .clickable {
                                        viewModel.switchUser(u)
                                        loginUsername = u.username
                                        loginPassword = u.password
                                    }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .testTag("quick_switch_${u.username}")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = u.fullName,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp,
                                            color = DenseTextPrimaryDark
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        DepartmentBadge(department = u.department)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        UserRoleBadge(role = u.role)
                                        if (isCurrent) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("● ACTIVE", fontSize = 8.sp, color = LaserGreen, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // -------------------------------------------------------------
            // REGISTRATION TAB
            // -------------------------------------------------------------
            Card(
                colors = CardDefaults.cardColors(containerColor = DenseSurfaceDark),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DenseBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "NEW TEAM MEMBER REGISTRATION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DenseTextPrimaryDark
                    )
                    Text(
                        text = "Register with a specific role tier to participate in production rosters and call notifications.",
                        fontSize = 10.sp,
                        color = DenseTextSecondaryDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Full Name
                    OutlinedTextField(
                        value = regFullName,
                        onValueChange = { regFullName = it },
                        label = { Text("Full Name *") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = DenseBorderDark,
                            focusedContainerColor = DenseSurfaceDark,
                            unfocusedContainerColor = DenseSurfaceDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_reg_fullname")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Username
                    OutlinedTextField(
                        value = regUsername,
                        onValueChange = { regUsername = it },
                        label = { Text("Username *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = DenseBorderDark,
                            focusedContainerColor = DenseSurfaceDark,
                            unfocusedContainerColor = DenseSurfaceDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_reg_username")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Email
                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = { regEmail = it },
                        label = { Text("Email Address *") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = DenseBorderDark,
                            focusedContainerColor = DenseSurfaceDark,
                            unfocusedContainerColor = DenseSurfaceDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_reg_email")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Password
                    OutlinedTextField(
                        value = regPassword,
                        onValueChange = { regPassword = it },
                        label = { Text("Password *") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp)) },
                        trailingIcon = {
                            IconButton(onClick = { showRegPassword = !showRegPassword }) {
                                Icon(
                                    if (showRegPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = DenseTextSecondaryDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        visualTransformation = if (showRegPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = DenseBorderDark,
                            focusedContainerColor = DenseSurfaceDark,
                            unfocusedContainerColor = DenseSurfaceDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_reg_password")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Phone
                    OutlinedTextField(
                        value = regPhone,
                        onValueChange = { regPhone = it },
                        label = { Text("Mobile Phone / Dispatch Contact") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = DenseBorderDark,
                            focusedContainerColor = DenseSurfaceDark,
                            unfocusedContainerColor = DenseSurfaceDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_reg_phone")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Role Tier Selection (4 Classes: Admin, Owner, Manager, Crew)
                    Text(
                        text = "ROLE TIER CLASS *",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanNeon
                    )

                    ExposedDropdownMenuBox(
                        expanded = roleDropdownExpanded,
                        onExpandedChange = { roleDropdownExpanded = !roleDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = "${regRole.displayName} (${when(regRole){ UserRole.OWNER -> "Full Control & Governance"; UserRole.ADMIN -> "Operations & Roster Lead"; UserRole.MANAGER -> "Stage & Warehouse Lead"; UserRole.CREW -> "Checklist, Scan & RSVP" }})",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanNeon,
                                unfocusedBorderColor = DenseBorderDark,
                                focusedContainerColor = DenseSurfaceDark,
                                unfocusedContainerColor = DenseSurfaceDark
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("dropdown_role_select")
                        )

                        ExposedDropdownMenu(
                            expanded = roleDropdownExpanded,
                            onDismissRequest = { roleDropdownExpanded = false },
                            modifier = Modifier.background(DenseSurfaceDark)
                        ) {
                            UserRole.entries.forEach { role ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column {
                                                Text(role.displayName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DenseTextPrimaryDark)
                                                Text(
                                                    when (role) {
                                                        UserRole.OWNER -> "Full system control, financials & event deletion"
                                                        UserRole.ADMIN -> "Gig scheduling, call-time dispatches & user management"
                                                        UserRole.MANAGER -> "Stage management, crew assignment & warehouse audit"
                                                        UserRole.CREW -> "Checklist execution, packing, return check-in & RSVP"
                                                    },
                                                    fontSize = 9.sp,
                                                    color = DenseTextSecondaryDark
                                                )
                                            }
                                            UserRoleBadge(role = role)
                                        }
                                    },
                                    onClick = {
                                        regRole = role
                                        roleDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Department Selection
                    Text(
                        text = "WORK DEPARTMENT *",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanNeon
                    )

                    ExposedDropdownMenuBox(
                        expanded = deptDropdownExpanded,
                        onExpandedChange = { deptDropdownExpanded = !deptDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = regDepartment.displayName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptDropdownExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanNeon,
                                unfocusedBorderColor = DenseBorderDark,
                                focusedContainerColor = DenseSurfaceDark,
                                unfocusedContainerColor = DenseSurfaceDark
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("dropdown_dept_select")
                        )

                        ExposedDropdownMenu(
                            expanded = deptDropdownExpanded,
                            onDismissRequest = { deptDropdownExpanded = false },
                            modifier = Modifier.background(DenseSurfaceDark)
                        ) {
                            WorkDepartment.entries.forEach { dept ->
                                DropdownMenuItem(
                                    text = {
                                        Text(dept.displayName, fontSize = 12.sp, color = DenseTextPrimaryDark)
                                    },
                                    onClick = {
                                        regDepartment = dept
                                        deptDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val success = viewModel.register(
                                username = regUsername,
                                fullName = regFullName,
                                email = regEmail,
                                password = regPassword,
                                role = regRole,
                                department = regDepartment,
                                phone = regPhone
                            )
                            if (success) {
                                onAuthSuccess()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LaserGreen, contentColor = DenseBackgroundDark),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("btn_submit_register")
                    ) {
                        Text("Complete Registration & Sign In", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
