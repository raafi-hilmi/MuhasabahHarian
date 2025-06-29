package com.raafi.muhasabahharian.presentation.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.firebase.auth.FirebaseAuth
import com.raafi.muhasabahharian.core.auth.AuthManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSetting: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user = remember { AuthManager.getCurrentUser() }
    val isAnonymous = user?.isAnonymous ?: true
    val displayName = user?.displayName?.ifBlank { null } ?: "Tamu"
    val email = user?.email ?: ""
    val joinDateStr = remember(user) {
        user?.metadata?.creationTimestamp?.let {
            SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(it))
        } ?: "—"
    }
    val activeDays by viewModel.activeDays.collectAsState()
    val levelPemula      = activeDays in    0..6
    val levelKonsisten   = activeDays in   7..20
    val levelAktif       = activeDays >=  21
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Profil",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.Black
                    )
                }
            },
            actions = {
                IconButton(onClick = onNavigateToSetting) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Pengaturan",
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // User Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8BC34A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = displayName.first().uppercase(),
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }


                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = displayName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "Sejak $joinDateStr",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = email,
                                fontSize = 14.sp,
                                color = Color.Gray,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Status Refleksi Title
                    Text(
                        text = "Status Refleksi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Status Cards
                    StatusRefleksiCard(
                        title = "Pemula",
                        subtitle = "0-6 hari aktif",
                        description = "Baru memulai langkah muhasabah. Masih dalam tahap membiasakan diri dan menjalani proses refleksi harian.",
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        isActive = levelPemula
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    StatusRefleksiCard(
                        title = "Konsisten",
                        subtitle = "7-20 hari aktif",
                        description = "Sudah mulai terbiasa melakukan muhasabah. Konsistensi mulai terbangun.",
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        isActive = levelKonsisten
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    StatusRefleksiCard(
                        title = "Reflektif Aktif",
                        subtitle = "21+ hari aktif",
                        description = "Muhasabah telah menjadi kebiasaan. Refleksi harian mulai berdampak pada kegiatan sehari-hari.",
                        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                        isActive = levelAktif
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusRefleksiCard(
    title: String,
    subtitle: String,
    description: String,
    backgroundColor: Color,
    isActive: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Icon
            if (isActive) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2E7D32)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Aktif",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.errorContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Belum Aktif",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Status Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.5f),
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.7f),
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}