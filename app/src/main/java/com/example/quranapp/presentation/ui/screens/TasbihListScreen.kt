package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.quranapp.domain.model.TasbihItem
import com.example.quranapp.presentation.navigation.Screen
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight
import com.example.quranapp.presentation.viewmodel.TasbihListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbihListScreen(
    navController: NavController,
    viewModel: TasbihListViewModel = hiltViewModel()
) {
    val tasbihs by viewModel.tasbihs.collectAsState()
    val totalLifetime by viewModel.totalLifetimeCompletions.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = GreenPrimaryLight,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Tasbih")
            }
        }
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
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Right in RTL
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Text(
                    text = "التسبيح والذكر",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Box(modifier = Modifier.size(40.dp)) // Spacer for balance
            }

            // Stats Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TasbihStatCard(
                    title = "إجمالي التسابيح",
                    value = tasbihs.size.toString(),
                    modifier = Modifier.weight(1f)
                )
                TasbihStatCard(
                    title = "إجمالي المرات",
                    value = totalLifetime.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Check if lists are populated
            if (tasbihs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimaryLight)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Separate default and custom tasbihs
                    val defaultTasbihs = tasbihs.filter { it.isDefault }
                    val customTasbihs = tasbihs.filter { !it.isDefault }

                    if (defaultTasbihs.isNotEmpty()) {
                        item {
                            Text(
                                "التسابيح الأساسية",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(defaultTasbihs, key = { it.id }) { item ->
                            TasbihItemCard(
                                item = item,
                                onClick = { navController.navigate(Screen.TasbihCounting.createRoute(item.id)) },
                                onDelete = { viewModel.deleteTasbih(item) },
                                onUpdate = { updated -> viewModel.updateTasbih(updated) }
                            )
                        }
                    }

                    if (customTasbihs.isNotEmpty()) {
                        item {
                            Text(
                                "تسابيحي المخصصة",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        items(customTasbihs, key = { it.id }) { item ->
                            TasbihItemCard(
                                item = item,
                                onClick = { navController.navigate(Screen.TasbihCounting.createRoute(item.id)) },
                                onDelete = { viewModel.deleteTasbih(item) },
                                onUpdate = { updated -> viewModel.updateTasbih(updated) }
                            )
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // Avoid FAB overlap
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        TasbihEditorDialog(
            initialPhrase = "",
            initialTarget = 33,
            onDismiss = { showAddDialog = false },
            onSave = { phrase, target ->
                viewModel.addCustomTasbih(phrase, target)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TasbihStatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = GreenPrimaryLight
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun TasbihItemCard(
    item: TasbihItem,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onUpdate: (TasbihItem) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Number identifier
                Surface(
                    shape = CircleShape,
                    color = GreenPrimaryLight.copy(alpha = 0.1f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = item.id.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = GreenPrimaryLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Options Menu
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .size(28.dp)
                            .offset(x = 8.dp, y = (-8).dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        DropdownMenuItem(
                            text = { Text("نسخ الذكر") },
                            onClick = {
                                clipboardManager.setText(AnnotatedString(item.phrase))
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.ContentCopy, null, Modifier.size(20.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("تعديل") },
                            onClick = {
                                showMenu = false
                                showEditDialog = true
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, null, Modifier.size(20.dp)) }
                        )
                        if (!item.isDefault) {
                            DropdownMenuItem(
                                text = { Text("حذف", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                },
                                leadingIcon = { Icon(Icons.Default.DeleteOutline, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.error) }
                            )
                        }
                    }
                }
            }

            // Phrase
            Text(
                text = item.phrase,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = com.example.quranapp.presentation.ui.theme.ScheherazadeNew,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 40.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )

            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

            // Footer Details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "الهدف المخصص:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${item.targetCount} مرّة",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "إجمالي المرات:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.totalCompletions.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimaryLight
                    )
                }
            }
        }
    }

    if (showEditDialog) {
        TasbihEditorDialog(
            initialPhrase = item.phrase,
            initialTarget = item.targetCount,
            onDismiss = { showEditDialog = false },
            onSave = { phrase, target ->
                onUpdate(item.copy(phrase = phrase, targetCount = target))
                showEditDialog = false
            }
        )
    }
}

@Composable
fun TasbihEditorDialog(
    initialPhrase: String,
    initialTarget: Int,
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit
) {
    var phrase by remember { mutableStateOf(initialPhrase) }
    var targetStr by remember { mutableStateOf(if (initialTarget > 0) initialTarget.toString() else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = if (initialPhrase.isEmpty()) "إضافة تسبيح جديد" else "تعديل التسبيح",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = phrase,
                    onValueChange = { phrase = it },
                    label = { Text("الذكر / التسبيح") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimaryLight,
                        focusedLabelColor = GreenPrimaryLight
                    )
                )

                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it.filter { char -> char.isDigit() } },
                    label = { Text("عدد المرات المستهدف") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimaryLight,
                        focusedLabelColor = GreenPrimaryLight
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetStr.toIntOrNull() ?: 33
                    onSave(phrase, target)
                },
                enabled = phrase.isNotBlank() && targetStr.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimaryLight)
            ) {
                Text("حفظ", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    )
}
