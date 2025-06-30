package com.raafi.muhasabahharian.presentation.setting

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.raafi.muhasabahharian.core.auth.AuthManager
import com.raafi.muhasabahharian.core.notification.NotificationScheduler
import com.raafi.muhasabahharian.core.preference.NotificationPreferences
import com.raafi.muhasabahharian.core.preference.UserPreferences
import com.raafi.muhasabahharian.presentation.riwayat.DetailRiwayatViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    onNavigateBack: () -> Unit,
    isLoggedIn: Boolean = true,
    userEmail: String = "",
    onLogout: () -> Unit = {},
    viewModel: SettingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val notifState by NotificationPreferences.getNotifEnabled(context)
        .collectAsState(initial = false)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            coroutineScope.launch{
                if (isGranted) {
                    NotificationPreferences.setNotifEnabled(context, true)
                    NotificationScheduler.scheduleDailyNotification(context)
                } else {
                    NotificationPreferences.setNotifEnabled(context, false)
                    Toast.makeText(context, "Izin notifikasi ditolak", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    text = "Pengaturan",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF2D3748)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {

            // Notification Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Notification Icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Notification Text
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Notifikasi",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2D3748)
                        )
                        Text(
                            text = "Ingatkan saya untuk muhasabah tiap malam",
                            fontSize = 12.sp,
                            color = Color(0xFF718096),
                            lineHeight = 16.sp
                        )
                    }

                    // Toggle Switch
                    Switch(
                        checked = notifState,
                        onCheckedChange = { isChecked ->
                           coroutineScope.launch {
                                if (isChecked) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        NotificationPreferences.setNotifEnabled(context, true)
                                        NotificationScheduler.scheduleDailyNotification(context)
                                    }
                                } else {
                                    NotificationPreferences.setNotifEnabled(context, false)
                                    NotificationScheduler.cancelDailyNotification(context)
                                }
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE2E8F0)
                        )
                    )
                }
            }

            AccountCard(
                isLoggedIn = isLoggedIn,
                email = userEmail,
                onLogoutClicked = {
                    AuthManager.signOut()
                    coroutineScope.launch {
                        UserPreferences.clear(context)
                        viewModel.clearReflection()
                    }
                    NotificationScheduler.cancelDailyNotification(context)
                    onLogout()
                },
            )
        }

    }
}

@Composable
private fun AccountCard(
    isLoggedIn: Boolean,
    email: String,
    onLogoutClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Status Akun",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))

            if (isLoggedIn) {
                Text("Masuk dengan Google", color = Color(0xFF718096))
                if (email.isNotEmpty()) {
                    Text(email, fontSize = 12.sp, color = Color(0xFF718096))
                }
            } else {
                Text("Sedang menggunakan mode tamu", color = Color(0xFF718096))
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onLogoutClicked,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFED7D7)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Keluar", color = Color(0xFFE53E3E))
            }
        }
    }
}
