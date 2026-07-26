package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.GasSyncService
import com.example.data.PreferencesManager
import com.example.data.ReadingLog
import com.example.data.ReadingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class StudentStat(
    val grade: Int,
    val classNum: Int,
    val studentName: String,
    val count: Int,
    val latestBook: String
)

data class ClassStat(
    val grade: Int,
    val classNum: Int,
    val totalBooks: Int
)

class ReadingJournalViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val gasSyncService = GasSyncService()
    val preferencesManager = PreferencesManager(application)
    val repository = ReadingRepository(db.readingLogDao(), gasSyncService, preferencesManager)

    val logs: StateFlow<List<ReadingLog>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Form States ---
    var gradeInput = MutableStateFlow(5)
    var classNumInput = MutableStateFlow(2)
    var studentNameInput = MutableStateFlow("")
    var bookTitleInput = MutableStateFlow("")
    var authorInput = MutableStateFlow("")
    var publisherInput = MutableStateFlow("")
    var summaryInput = MutableStateFlow("")
    var thoughtsInput = MutableStateFlow("")

    var isSubmitting = MutableStateFlow(false)
    var submitSnackbarMessage = MutableStateFlow<String?>(null)

    // --- My Library Filter & Search ---
    var librarySearchQuery = MutableStateFlow("")
    var libraryGradeFilter = MutableStateFlow<Int?>(null)
    var libraryClassFilter = MutableStateFlow<Int?>(null)

    // --- Teacher Mode States ---
    var isTeacherMode = MutableStateFlow(false)
    var showPasswordDialog = MutableStateFlow(false)
    var passwordInput = MutableStateFlow("")
    var passwordError = MutableStateFlow<String?>(null)

    var teacherSearchQuery = MutableStateFlow("")
    var teacherGradeFilter = MutableStateFlow<Int?>(null)
    var teacherClassFilter = MutableStateFlow<Int?>(null)

    // --- GAS Settings States ---
    var showGasSettings = MutableStateFlow(false)
    var gasUrlInput = MutableStateFlow(preferencesManager.gasWebAppUrl)
    var isTestingGasUrl = MutableStateFlow(false)
    var gasTestMessage = MutableStateFlow<String?>(null)

    val codeGsSnippet = gasSyncService.codeGsSnippet

    init {
        viewModelScope.launch {
            repository.checkAndPrepopulateSamples()
        }
    }

    // --- Form Submission ---
    fun submitReadingLog() {
        val name = studentNameInput.value.trim()
        val title = bookTitleInput.value.trim()
        val author = authorInput.value.trim()
        val publisher = publisherInput.value.trim()
        val summary = summaryInput.value.trim()
        val thoughts = thoughtsInput.value.trim()

        if (name.isBlank() || title.isBlank() || summary.isBlank() || thoughts.isBlank()) {
            submitSnackbarMessage.value = "이름, 도서명, 줄거리, 소감을 모두 작성해 주세요."
            return
        }

        viewModelScope.launch {
            isSubmitting.value = true

            val (savedLog, syncSuccess) = repository.addReadingLog(
                grade = gradeInput.value,
                classNum = classNumInput.value,
                studentName = name,
                bookTitle = title,
                author = if (author.isBlank()) "미상" else author,
                publisher = if (publisher.isBlank()) "자체" else publisher,
                summary = summary,
                thoughts = thoughts
            )

            isSubmitting.value = false

            if (syncSuccess) {
                submitSnackbarMessage.value = "🎉 독서기록이 로컬 서재 및 구글 시트에 성공적으로 연동되었습니다!"
            } else if (preferencesManager.gasWebAppUrl.isNotBlank()) {
                submitSnackbarMessage.value = "💾 독서기록이 저장되었습니다 (구글 시트 연동 대기 중)."
            } else {
                submitSnackbarMessage.value = "💾 독서기록이 로컬 서재에 안전하게 저장되었습니다."
            }

            // Clear inputs
            bookTitleInput.value = ""
            authorInput.value = ""
            publisherInput.value = ""
            summaryInput.value = ""
            thoughtsInput.value = ""
        }
    }

    // --- Teacher Password Check ---
    fun verifyTeacherPassword() {
        if (passwordInput.value == preferencesManager.teacherPassword) {
            isTeacherMode.value = true
            showPasswordDialog.value = false
            passwordInput.value = ""
            passwordError.value = null
        } else {
            passwordError.value = "비밀번호가 일치하지 않습니다. (기본: 1234)"
        }
    }

    fun exitTeacherMode() {
        isTeacherMode.value = false
    }

    fun deleteLog(logId: Long) {
        viewModelScope.launch {
            repository.deleteLogById(logId)
        }
    }

    // --- GAS Web App URL Save & Test ---
    fun saveGasUrl(url: String) {
        val trimmed = url.trim()
        preferencesManager.gasWebAppUrl = trimmed
        gasUrlInput.value = trimmed
    }

    fun testGasConnection() {
        val url = gasUrlInput.value.trim()
        if (url.isBlank()) {
            gasTestMessage.value = "⚠️ 웹 앱 URL을 먼저 입력해 주세요."
            return
        }

        viewModelScope.launch {
            isTestingGasUrl.value = true
            gasTestMessage.value = null

            val result = gasSyncService.testConnection(url)
            isTestingGasUrl.value = false

            if (result.isSuccess) {
                saveGasUrl(url)
                gasTestMessage.value = "✅ 연동 성공! 구글 시트 웹 앱 연결이 정상입니다."
            } else {
                gasTestMessage.value = "❌ 연동 실패: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun retrySyncLog(log: ReadingLog) {
        viewModelScope.launch {
            val success = repository.retrySync(log)
            if (success) {
                submitSnackbarMessage.value = "구글 시트 동기화가 완료되었습니다."
            } else {
                submitSnackbarMessage.value = "동기화 실패. 연동 설정 URL을 확인해 주세요."
            }
        }
    }

    fun clearSnackbar() {
        submitSnackbarMessage.value = null
    }

    // --- Helper calculations for Hall of Fame & Statistics ---
    fun getTopReaders(logsList: List<ReadingLog>): List<StudentStat> {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val monthlyLogs = logsList.filter { log ->
            val cal = Calendar.getInstance().apply { timeInMillis = log.timestamp }
            cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
        }

        return monthlyLogs.groupBy { "${it.grade}-${it.classNum}-${it.studentName}" }
            .map { (key, list) ->
                val first = list.first()
                StudentStat(
                    grade = first.grade,
                    classNum = first.classNum,
                    studentName = first.studentName,
                    count = list.size,
                    latestBook = list.maxByOrNull { it.timestamp }?.bookTitle ?: ""
                )
            }
            .sortedByDescending { it.count }
    }

    fun getClassStatistics(logsList: List<ReadingLog>): List<ClassStat> {
        return logsList.groupBy { "${it.grade}-${it.classNum}" }
            .map { (key, list) ->
                val first = list.first()
                ClassStat(
                    grade = first.grade,
                    classNum = first.classNum,
                    totalBooks = list.size
                )
            }
            .sortedWith(compareBy({ it.grade }, { it.classNum }))
    }
}
