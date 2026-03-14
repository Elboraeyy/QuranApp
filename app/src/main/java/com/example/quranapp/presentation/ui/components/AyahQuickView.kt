package com.example.quranapp.presentation.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.quranapp.domain.model.Ayah
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight
import com.example.quranapp.presentation.ui.theme.ScheherazadeNew

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyahQuickView(
    ayah: Ayah?,
    surahName: String,
    tafsirText: String?,
    meaningsText: String?,
    onDismiss: () -> Unit,
    onPlayAyah: () -> Unit,
    onBookmark: () -> Unit,
    isBookmarked: Boolean = false
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (ayah != null) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 8.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ayah reference badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = GreenPrimaryLight.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = "سُورَةُ $surahName — آية ${ayah.numberInSurah}",
                        style = MaterialTheme.typography.labelMedium,
                        color = GreenPrimaryLight,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Ayah text in beautiful Arabic font
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.background,
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = ayah.text,
                        fontFamily = ScheherazadeNew,
                        fontSize = 22.sp,
                        lineHeight = 42.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickActionButton(
                        icon = Icons.Default.PlayArrow,
                        label = "استمع",
                        color = GreenPrimaryLight,
                        onClick = onPlayAyah
                    )
                    QuickActionButton(
                        icon = Icons.Default.ContentCopy,
                        label = "نسخ",
                        color = Color(0xFFC9A24D),
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("ayah", ayah.text))
                            Toast.makeText(context, "تم نسخ الآية", Toast.LENGTH_SHORT).show()
                        }
                    )
                    QuickActionButton(
                        icon = Icons.Default.Share,
                        label = "مشاركة",
                        color = Color(0xFF5B8DEF),
                        onClick = {
                            val shareText = "${ayah.text}\n\n— سورة $surahName، آية ${ayah.numberInSurah}"
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "مشاركة الآية"))
                        }
                    )
                    QuickActionButton(
                        icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        label = "حفظ",
                        color = if (isBookmarked) GreenPrimaryLight else Color(0xFFE57373),
                        onClick = onBookmark
                    )
                }

                // Word Meanings section
                if (!meaningsText.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeader(icon = Icons.Default.MenuBook, title = "معاني الكلمات (الميسر)")
                    Spacer(modifier = Modifier.height(12.dp))
                    TextCard(text = meaningsText)
                }

                // Tafsir section
                if (!tafsirText.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeader(icon = Icons.Default.FormatQuote, title = "التفسير (الجلالين)")
                    Spacer(modifier = Modifier.height(12.dp))
                    TextCard(text = tafsirText)
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = GreenPrimaryLight.copy(alpha = 0.1f),
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GreenPrimaryLight,
                modifier = Modifier.padding(6.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TextCard(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 28.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.1f),
            modifier = Modifier.size(52.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.padding(14.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
    }
}
