package org.example.project

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun App() {
    val viewModel = remember { TimerViewModel() }
    val currentTab by viewModel.currentTab.collectAsState()
    val currentMode by viewModel.currentMode.collectAsState()
    val timeLeft by viewModel.timeLeft.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val completedSessions by viewModel.completedSessions.collectAsState()
    val totalFocusMinutes by viewModel.totalFocusMinutes.collectAsState()
    val currentTheme by viewModel.currentTheme.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val memoText by viewModel.memoText.collectAsState()

    var newTaskTitle by remember { mutableStateOf("") }
    var statsPeriod by remember { mutableStateOf("Haftalık") }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeString = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"

    val totalDuration = currentMode.durationInSeconds.toFloat()
    val progress = if (totalDuration > 0) timeLeft.toFloat() / totalDuration else 0f

    val bgTop by animateColorAsState(targetValue = Color(currentTheme.topColor), animationSpec = tween(600))
    val bgBottom by animateColorAsState(targetValue = Color(currentTheme.bottomColor), animationSpec = tween(600))

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(bgTop, bgBottom))),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Üst Header & Tema Seçici
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Focus",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )

                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AppTheme.entries.forEach { theme ->
                            val isSelected = currentTheme == theme
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { viewModel.setTheme(theme) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = theme.title.split(" ")[0],
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color.White else Color.Gray
                                )
                            }
                        }
                    }
                }

                // Orta Alan: Sekmeler
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (currentTab) {
                        AppTab.TIMER -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(22.dp))
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .padding(3.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.wrapContentWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        PomodoroMode.entries.forEach { mode ->
                                            val isSelected = currentMode == mode
                                            val buttonBg by animateColorAsState(
                                                targetValue = if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent,
                                                animationSpec = tween(250)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(19.dp))
                                                    .background(buttonBg)
                                                    .clickable { viewModel.setMode(mode) }
                                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = mode.title,
                                                    fontSize = 13.sp,
                                                    color = if (isSelected) Color.White else Color.Gray,
                                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(40.dp))

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(260.dp)
                                ) {
                                    CircularProgressIndicator(
                                        progress = { 1f },
                                        modifier = Modifier.fillMaxSize(),
                                        color = Color.White.copy(alpha = 0.1f),
                                        strokeWidth = 5.dp,
                                    )
                                    CircularProgressIndicator(
                                        progress = { progress },
                                        modifier = Modifier.fillMaxSize(),
                                        color = Color.White,
                                        strokeWidth = 5.dp,
                                        trackColor = Color.Transparent,
                                    )

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = timeString,
                                            fontSize = 54.sp,
                                            fontWeight = FontWeight.Light,
                                            color = Color.White,
                                            letterSpacing = (-1).sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (isRunning) "Odaklanılıyor" else "Duraklatıldı",
                                            fontSize = 13.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(40.dp))

                                Row(
                                    modifier = Modifier.wrapContentWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            if (isRunning) viewModel.pauseTimer()
                                            else viewModel.startTimer()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isRunning) Color(0xFFFF453A) else Color.White,
                                            contentColor = if (isRunning) Color.White else Color.Black
                                        ),
                                        shape = RoundedCornerShape(24.dp),
                                        modifier = Modifier.height(50.dp).width(140.dp),
                                        elevation = ButtonDefaults.buttonElevation(0.dp)
                                    ) {
                                        Text(
                                            text = if (isRunning) "Durdur" else "Başlat",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Button(
                                        onClick = { viewModel.resetTimer() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White.copy(alpha = 0.12f),
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(24.dp),
                                        modifier = Modifier.height(50.dp).width(110.dp),
                                        elevation = ButtonDefaults.buttonElevation(0.dp)
                                    ) {
                                        Text("Sıfırla", fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                        AppTab.TASKS -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(0.92f).fillMaxHeight(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Görevler",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    letterSpacing = (-0.5).sp
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextField(
                                        value = newTaskTitle,
                                        onValueChange = { newTaskTitle = it },
                                        placeholder = { Text("Yeni görev...", color = Color.Gray) },
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.White.copy(alpha = 0.08f),
                                            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.weight(1f).height(50.dp)
                                    )

                                    Button(
                                        onClick = {
                                            viewModel.addTask(newTaskTitle)
                                            newTaskTitle = ""
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.height(50.dp),
                                        elevation = ButtonDefaults.buttonElevation(0.dp)
                                    ) {
                                        Text("Ekle", fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth().weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(tasks) { task ->
                                        AppleCard {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    modifier = Modifier.weight(1f).clickable { viewModel.toggleTask(task.id) },
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    Text(text = if (task.isCompleted) "✓" else "○", fontSize = 18.sp, color = if (task.isCompleted) Color(0xFF32D74B) else Color.Gray)
                                                    Text(
                                                        text = task.title,
                                                        color = if (task.isCompleted) Color.Gray else Color.White,
                                                        fontSize = 15.sp
                                                    )
                                                }
                                                TextButton(onClick = { viewModel.deleteTask(task.id) }) {
                                                    Text("Sil", color = Color(0xFFFF453A), fontSize = 13.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        AppTab.MEMO -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(0.92f).fillMaxHeight(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Notlar",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    letterSpacing = (-0.5).sp
                                )

                                TextField(
                                    value = memoText,
                                    onValueChange = { viewModel.updateMemo(it) },
                                    modifier = Modifier.fillMaxWidth().weight(1f),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White.copy(alpha = 0.08f),
                                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    placeholder = { Text("Düşüncelerini buraya yaz...", color = Color.Gray) }
                                )
                            }
                        }
                        AppTab.STATS -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(0.92f).fillMaxHeight(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "İstatistikler",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = (-0.5).sp
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .padding(3.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.wrapContentWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        listOf("Günlük", "Haftalık", "Aylık").forEach { period ->
                                            val isSelected = statsPeriod == period
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(13.dp))
                                                    .background(if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent)
                                                    .clickable { statsPeriod = period }
                                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = period,
                                                    fontSize = 12.sp,
                                                    color = if (isSelected) Color.White else Color.Gray,
                                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                                )
                                            }
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        AppleStatCard("Toplam Süre", "$totalFocusMinutes dk")
                                    }
                                    Box(modifier = Modifier.weight(1f)) {
                                        AppleStatCard("Seans", "$completedSessions adet")
                                    }
                                }

                                AppleCard {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = "$statsPeriod Odak Dağılımı",
                                            color = Color.Gray,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )

                                        val chartData = when (statsPeriod) {
                                            "Günlük" -> listOf("Pzt" to 40, "Sal" to 60, "Çar" to 80, "Per" to 50, "Cum" to 90, "Cmt" to 30, "Paz" to 70)
                                            "Haftalık" -> listOf("1.H" to 300, "2.H" to 450, "3.H" to 500, "4.H" to 620)
                                            else -> listOf("Oca" to 1200, "Şub" to 1500, "Mar" to 1800, "Nis" to 1400)
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth().height(140.dp),
                                            horizontalArrangement = Arrangement.SpaceAround,
                                            verticalAlignment = Alignment.Bottom
                                        ) {
                                            val maxVal = chartData.maxOf { it.second }.toFloat()
                                            chartData.forEach { (label, value) ->
                                                val barHeightFraction = if (maxVal > 0) (value / maxVal).coerceIn(0.1f, 1f) else 0.1f

                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Bottom,
                                                    modifier = Modifier.fillMaxHeight()
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .width(24.dp)
                                                            .fillMaxHeight(barHeightFraction)
                                                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                                            .background(Brush.verticalGradient(listOf(Color.White, Color.White.copy(alpha = 0.4f))))
                                                    )
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Text(text = label, color = Color.Gray, fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Alt Dock
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(64.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppTab.entries.forEach { tab ->
                            val isSelected = currentTab == tab
                            val textColor = if (isSelected) Color.White else Color.Gray

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { viewModel.setTab(tab) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tab.title,
                                    fontSize = 12.sp,
                                    color = textColor,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppleCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
    ) {
        content()
    }
}

@Composable
fun AppleStatCard(title: String, value: String) {
    AppleCard {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = title, color = Color.Gray, fontSize = 13.sp)
            Text(text = value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}