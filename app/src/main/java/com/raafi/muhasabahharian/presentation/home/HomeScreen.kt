package com.raafi.muhasabahharian.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.raafi.muhasabahharian.core.preference.UserPreferences
import com.raafi.muhasabahharian.core.utils.QuoteUtils
import com.raafi.muhasabahharian.presentation.common.components.BottomNavigationBar
import com.raafi.muhasabahharian.presentation.common.components.QuoteCard
import com.raafi.muhasabahharian.presentation.profile.ProfileViewModel

@Composable
fun HomeScreen(
    onNavigateToMuhasabah: (String) -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToStatistics: () -> Unit = {},
    onNavigateToRiwayat: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    profileVM: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val user by UserPreferences.observe(context).collectAsState(initial = null)
    val randomQuote = remember { QuoteUtils.getRandomQuote() }
    val level     by profileVM.levelLabel.collectAsState()
    val activeDay by profileVM.activeDays.collectAsState()
    val target    = if (level == "Pemula") 7 else if (level == "Konsisten") 21 else "Setiap"

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateToProfile,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Profile")
                }

                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "home",
                onNavigateToHome = {},
                onNavigateToMuhasabah = { onNavigateToMuhasabah("hati") },
                onNavigateToStatistik = { onNavigateToStatistics() }
            )
        }
    ) { innerPadding ->
        val listState = rememberLazyListState()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding),
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Selamat Datang, ${user?.name}!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Text(
                    text = "Aksi Cepat",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MenuCard(
                        modifier = Modifier.weight(1f),
                        title = "Muhasabah",
                        icon = Icons.Default.Edit,
                        iconDesc = "muhasabah icon",
                        color = MaterialTheme.colorScheme.primary,
                        onClick = { onNavigateToMuhasabah("tubuh") }
                    )

                    MenuCard(
                        modifier = Modifier.weight(1f),
                        title = "Riwayat Harian",
                        icon = Icons.Default.Face,
                        iconDesc = "riwayat icon",
                        color = MaterialTheme.colorScheme.primary,
                        onClick = { onNavigateToRiwayat() }
                    )

                    MenuCard(
                        modifier = Modifier.weight(1f),
                        title = "Lihat Statistik",
                        icon = Icons.Default.DateRange,
                        iconDesc = "statistik icon",
                        color = MaterialTheme.colorScheme.primary,
                        onClick = { onNavigateToStatistics() }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Kutipan Hari Ini", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        QuoteCard(
                            arabic = randomQuote.first,
                            translation = randomQuote.second,
                            source = randomQuote.third
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Profile Singkat", style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Level",
                        value = level,
                        onClick = { onNavigateToProfile() },
                        backgroundColor = Color(0xFFE8F5E8)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Hari Aktif",
                        value = "$activeDay Hari",
                        onClick = { onNavigateToProfile() },
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Target",
                        value = "$target Hari Konsisten",
                        onClick = { onNavigateToProfile() },
                        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun MenuCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    iconDesc: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(color)
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = icon,
                contentDescription = iconDesc,
                tint = MaterialTheme.colorScheme.onSecondary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    onClick: () -> Unit,
    backgroundColor: Color
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.primary)
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = value,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
            )
        }
    }
}
