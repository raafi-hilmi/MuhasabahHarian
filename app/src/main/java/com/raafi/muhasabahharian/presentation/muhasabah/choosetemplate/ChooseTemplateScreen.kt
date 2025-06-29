package com.raafi.muhasabahharian.presentation.muhasabah.choosetemplate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raafi.muhasabahharian.presentation.common.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseTemplateScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToStatistik: () -> Unit,
    onTemplateSelected: (String) -> Unit
) {
    val templates = listOf(
        TemplateItem(
            type = "produktif",
            title = "Refleksi Produktif",
            description = "Fokus pada pekerjaan, prioritas, hasil",
            emoji = "😊",
            color = Color(0xFF8BC34A)
        ),
        TemplateItem(
            type = "emosional",
            title = "Refleksi Emosional",
            description = "Fokus pada perasaan, hubungan, luka hati",
            emoji = "😐",
            color = Color(0xFF8BC34A)
        ),
        TemplateItem(
            type = "islami",
            title = "Refleksi Islami",
            description = "Pertanyaan dosa, amal, hubungan dengan Allah",
            emoji = "🤲",
            color = Color(0xFF8BC34A)
        ),
        TemplateItem(
            type = "olahraga",
            title = "Refleksi Olahraga",
            description = "Fokus pada olahraga yang dilakukan dan kondisi tubuh",
            emoji = "💚",
            color = Color(0xFF8BC34A)
        )
    )
    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pilih Template Muhasabah",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        content = {
            innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White).padding(innerPadding)
            ) {


                // Template List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(templates) { template ->
                        TemplateCard(
                            template = template,
                            onClick = { onTemplateSelected(template.type) }
                        )
                    }
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "muhasabah",
                onNavigateToHome = onNavigateToHome,
                onNavigateToMuhasabah = {  },
                onNavigateToStatistik = onNavigateToStatistik
            )
        }
    )

}

@Composable
private fun TemplateCard(
    template: TemplateItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(template.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = template.emoji,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = template.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = template.description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }

            // More options
            IconButton(
                onClick = { /* Handle more options */ }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = Color.Gray
                )
            }
        }
    }
}

data class TemplateItem(
    val type: String,
    val title: String,
    val description: String,
    val emoji: String,
    val color: Color
)