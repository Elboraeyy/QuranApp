package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.data.local.entity.*
import com.example.quranapp.domain.usecase.*
import com.example.quranapp.domain.repository.ReligiousTaskRepository
import com.example.quranapp.domain.repository.UserStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class TaskWithCompletion(
    val task: ReligiousTaskEntity,
    val completion: TaskCompletionEntity?
)

data class TasksState(
    val selectedPeriod: TaskPeriod = TaskPeriod.DAILY,
    val tasks: List<TaskWithCompletion> = emptyList(),
    val completionPercentage: Float = 0f,
    val isLoading: Boolean = false,
    val userStats: UserStatsEntity = UserStatsEntity()
)

@HiltViewModel
class ReligiousTasksViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val processTaskCompletionUseCase: ProcessTaskCompletionUseCase,
    private val getCompletionsUseCase: GetCompletionsUseCase,
    private val repository: ReligiousTaskRepository,
    private val statsRepository: UserStatsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TasksState())
    val state: StateFlow<TasksState> = _state.asStateFlow()

    private val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())

    init {
        seedInitialTasks()
        observeTasks()
    }

    private fun observeTasks() {
        combine(
            _state.map { it.selectedPeriod }.distinctUntilChanged().flatMapLatest { period ->
                getTasksUseCase(period)
            },
            getCompletionsUseCase(currentDate)
        ) { tasks, completions ->
            val tasksWithCompletion = tasks.map { task ->
                TaskWithCompletion(
                    task = task,
                    completion = completions.find { it.taskId == task.id }
                )
            }
            
            val total = tasksWithCompletion.size
            val completed = tasksWithCompletion.count { it.completion?.isCompleted == true }
            val percentage = if (total > 0) completed.toFloat() / total else 0f

            _state.update { 
                it.copy(
                    tasks = tasksWithCompletion,
                    completionPercentage = percentage
                )
            }
        }.launchIn(viewModelScope)

        statsRepository.getUserStats().onEach { stats ->
            if (stats != null) {
                _state.update { it.copy(userStats = stats) }
            }
        }.launchIn(viewModelScope)
    }

    fun selectPeriod(period: TaskPeriod) {
        _state.update { it.copy(selectedPeriod = period) }
    }

    fun toggleTask(taskId: Long) {
        viewModelScope.launch {
            processTaskCompletionUseCase(taskId, currentDate)
        }
    }

    private fun seedInitialTasks() {
        viewModelScope.launch {
            repository.getAllTasks().first().let { existing ->
                if (existing.isEmpty()) {
                    val initialTasks = listOf(
                        ReligiousTaskEntity(title = "صلاة الفجر", description = "في وقتها جماعة", category = TaskCategory.PRAYER, period = TaskPeriod.DAILY, points = 20),
                        ReligiousTaskEntity(title = "أذكار الصباح", description = "التحصين اليومي", category = TaskCategory.ADHKAR, period = TaskPeriod.DAILY, points = 10),
                        ReligiousTaskEntity(title = "ورد القرآن", description = "قراءة جزء يومياً", category = TaskCategory.QURAN, period = TaskPeriod.DAILY, points = 50),
                        ReligiousTaskEntity(title = "صلاة الضحى", description = "صلاة الأوابين", category = TaskCategory.PRAYER, period = TaskPeriod.DAILY, points = 15),
                        ReligiousTaskEntity(title = "صلاة الظهر", description = "في وقتها جماعة", category = TaskCategory.PRAYER, period = TaskPeriod.DAILY, points = 20),
                        ReligiousTaskEntity(title = "صلاة العصر", description = "في وقتها جماعة", category = TaskCategory.PRAYER, period = TaskPeriod.DAILY, points = 20),
                        ReligiousTaskEntity(title = "أذكار المساء", description = "التحصين اليومي", category = TaskCategory.ADHKAR, period = TaskPeriod.DAILY, points = 10),
                        ReligiousTaskEntity(title = "صلاة المغرب", description = "في وقتها جماعة", category = TaskCategory.PRAYER, period = TaskPeriod.DAILY, points = 20),
                        ReligiousTaskEntity(title = "صلاة العشاء", description = "في وقتها جماعة", category = TaskCategory.PRAYER, period = TaskPeriod.DAILY, points = 20),
                        ReligiousTaskEntity(title = "صلاة الوتر", description = "ختام اليوم", category = TaskCategory.PRAYER, period = TaskPeriod.DAILY, points = 15),
                        
                        // Weekly
                        ReligiousTaskEntity(title = "سورة الكهف", description = "نور ما بين الجمعتين", category = TaskCategory.QURAN, period = TaskPeriod.WEEKLY, points = 100),
                        ReligiousTaskEntity(title = "صيام الخميس", description = "سنة المصطفى", category = TaskCategory.FASTING, period = TaskPeriod.WEEKLY, points = 150),
                        
                        // Monthly
                        ReligiousTaskEntity(title = "صيام الأيام البيض", description = "13، 14، 15 من الشهر العربي", category = TaskCategory.FASTING, period = TaskPeriod.MONTHLY, points = 300),
                        ReligiousTaskEntity(title = "ختمة شهرية", description = "إتمام قراءة المصحف كاملاً", category = TaskCategory.QURAN, period = TaskPeriod.MONTHLY, points = 1000)
                    )
                    initialTasks.forEach { repository.insertTask(it) }
                }
            }
        }
    }
}
