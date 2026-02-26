package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quranapp.presentation.navigation.Screen
import com.example.quranapp.presentation.ui.theme.spacing
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TafsirMenuScreen(navController: NavController) {
    val spacing = MaterialTheme.spacing
    
    val menuItems = listOf(
        TafsirCategoryData(
            title = "تفسير القرآن",
            description = "فهم مبسط لآيات القرآن يعينك على التدبر.",
            iconType = "quran" // We'll use a placeholder colored box or simple icon for now until assets are provided
        ),
        TafsirCategoryData(
            title = "تفسير الأذكار",
            description = "افهم ما تردده من أذكار، ليكون أقرب إلى قلبك.",
            iconType = "adhkar"
        ),
        TafsirCategoryData(
            title = "تفسير الأحاديث النبوية",
            description = "شرح مبسط لمعاني أحاديث النبي ﷺ يوضح المقصود بها.",
            iconType = "hadith"
        ),
        TafsirCategoryData(
            title = "تفسير الأدعية",
            description = "افهم ما تردده من الأدعية، ليكون أقرب إلى قلبك.",
            iconType = "dua"
        ),
        TafsirCategoryData(
            title = "تفسير السيرة النبوية",
            description = "افهم السيرة النبوية، ليكون أقرب إلى قلبك.",
            iconType = "seerah"
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.gridMargin, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(48.dp))
                
                Text(
                    text = "التفسير والمعاني",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(48.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Introduction Text
            Text(
                text = "تفسير مبسط يساعدك على التدبر، ويقرب المعنى إلى قلبك، لتعيش الآية والذكر بطمأنينة.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.gridMargin, vertical = 24.dp)
            )

            // Category List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.gridMargin),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(menuItems) { category ->
                    TafsirCategoryCard(category = category, onClick = {
                        if (category.title == "تفسير القرآن") {
                            navController.navigate(Screen.TafsirSurahList.route)
                        } else {
                            // Placeholder for other categories
                            navController.navigate(Screen.TafsirSurahList.route) 
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun TafsirCategoryCard(category: TafsirCategoryData, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = GreenPrimaryLight.copy(alpha = 0.3f), // Light green background from design
        border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Arrow Button
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward, // Using ArrowForward, realistically should be ArrowBack or custom diagonal arrow
                    contentDescription = "Go",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(8.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Text Content (Right Aligned logically, center visually here)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Icon Placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // We'll just put the first letter or a generic shape for now
                Box(
                    modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                )
            }
        }
    }
}

data class TafsirCategoryData(
    val title: String,
    val description: String,
    val iconType: String
)
