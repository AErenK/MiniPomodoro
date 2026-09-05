package org.example.project

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class PomodoroMode(val title: String, var durationInSeconds: Int) {
    WORK("Odak", 25 * 60),
    SHORT_BREAK("Kısa Mola", 5 * 60),
    LONG_BREAK("Uzun Mola", 15 * 60)
}

enum class AppTab(val title: String) {
    TIMER("Sayaç"),
    TASKS("Görevler"),
    MEMO("Notlar"),
    STATS("İstatistik")
}

enum class AppTheme(val title: String, val topColor: Long, val bottomColor: Long) {
    MIDNIGHT("Derin Gece", 0xFF1C1C1E, 0xFF000000),
    OCEAN("Okyanus Mavisi", 0xFF0F172A, 0xFF020617),
    GRAPHITE("Apple Grafit", 0xFF27272A, 0xFF09090B)
}

data class TaskItem(val id: Int, val title: String, var isCompleted: Boolean)

class TimerViewModel {
    private val _currentTab = MutableStateFlow(AppTab.TIMER)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _currentMode = MutableStateFlow(PomodoroMode.WORK)
    val currentMode: StateFlow<PomodoroMode> = _currentMode.asStateFlow()

    private val _timeLeft = MutableStateFlow(PomodoroMode.WORK.durationInSeconds)
    val timeLeft: StateFlow<Int> = _timeLeft.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _completedSessions = MutableStateFlow(4)
    val completedSessions: StateFlow<Int> = _completedSessions.asStateFlow()

    private val _totalFocusMinutes = MutableStateFlow(100)
    val totalFocusMinutes: StateFlow<Int> = _totalFocusMinutes.asStateFlow()

    private val _currentTheme = MutableStateFlow(AppTheme.OCEAN)
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    private val _tasks = MutableStateFlow(
        mutableListOf(
            TaskItem(1, "Kotlin Multiplatform mimarisini incele", true),
            TaskItem(2, "Apple tarzı iOS arayüzünü optimize et", false)
        )
    )
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    private val _memoText = MutableStateFlow("Tasarım minimalist, akıcı ve tamamen Apple Human Interface Guidelines uyumlu.")
    val memoText: StateFlow<String> = _memoText.asStateFlow()

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setTheme(theme: AppTheme) {
        _currentTheme.value = theme
    }

    fun setMode(mode: PomodoroMode) {
        pauseTimer()
        _currentMode.value = mode
        _timeLeft.value = mode.durationInSeconds
    }

    fun startTimer() {
        if (_isRunning.value) return
        _isRunning.value = true
        job = scope.launch {
            while (_timeLeft.value > 0 && _isRunning.value) {
                delay(1000L)
                _timeLeft.value -= 1
            }
            if (_timeLeft.value == 0) {
                _isRunning.value = false
                if (_currentMode.value == PomodoroMode.WORK) {
                    _completedSessions.value += 1
                    _totalFocusMinutes.value += (_currentMode.value.durationInSeconds / 60)
                }
            }
        }
    }

    fun pauseTimer() {
        _isRunning.value = false
        job?.cancel()
    }

    fun resetTimer() {
        pauseTimer()
        _timeLeft.value = _currentMode.value.durationInSeconds
    }

    fun addTask(title: String) {
        if (title.isBlank()) return
        val currentList = _tasks.value.toMutableList()
        currentList.add(TaskItem(currentList.size + 1, title, false))
        _tasks.value = currentList
    }

    fun toggleTask(id: Int) {
        val currentList = _tasks.value.map {
            if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
        }.toMutableList()
        _tasks.value = currentList
    }

    fun deleteTask(id: Int) {
        _tasks.value = _tasks.value.filter { it.id != id }.toMutableList()
    }

    fun updateMemo(text: String) {
        _memoText.value = text
    }
}