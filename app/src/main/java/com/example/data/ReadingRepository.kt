package com.example.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReadingRepository(
    private val dao: ReadingLogDao,
    private val gasSyncService: GasSyncService,
    private val preferencesManager: PreferencesManager
) {
    val allLogs: Flow<List<ReadingLog>> = dao.getAllLogs()

    fun getLogsByClass(grade: Int, classNum: Int): Flow<List<ReadingLog>> {
        return dao.getLogsByClass(grade, classNum)
    }

    suspend fun addReadingLog(
        grade: Int,
        classNum: Int,
        studentName: String,
        bookTitle: String,
        author: String,
        publisher: String,
        summary: String,
        thoughts: String
    ): Pair<ReadingLog, Boolean> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
        val nowFormatted = dateFormat.format(Date())

        val initialLog = ReadingLog(
            grade = grade,
            classNum = classNum,
            studentName = studentName,
            bookTitle = bookTitle,
            author = author,
            publisher = publisher,
            summary = summary,
            thoughts = thoughts,
            timestamp = System.currentTimeMillis(),
            dateFormatted = nowFormatted,
            isSynced = false
        )

        // 1. Always save to Local Room Database first
        val generatedId = dao.insertLog(initialLog)
        val savedLog = initialLog.copy(id = generatedId)

        // 2. Try syncing to Google Apps Script if URL exists
        val webAppUrl = preferencesManager.gasWebAppUrl
        var syncSuccess = false
        if (webAppUrl.isNotBlank()) {
            val syncResult = gasSyncService.postReadingLog(webAppUrl, savedLog)
            if (syncResult.isSuccess) {
                syncSuccess = true
                dao.updateLog(savedLog.copy(isSynced = true))
            }
        }

        return Pair(savedLog, syncSuccess)
    }

    suspend fun deleteLogById(id: Long) {
        dao.deleteLogById(id)
    }

    suspend fun retrySync(log: ReadingLog): Boolean {
        val webAppUrl = preferencesManager.gasWebAppUrl
        if (webAppUrl.isBlank()) return false
        val result = gasSyncService.postReadingLog(webAppUrl, log)
        if (result.isSuccess) {
            dao.updateLog(log.copy(isSynced = true))
            return true
        }
        return false
    }

    suspend fun checkAndPrepopulateSamples() {
        if (preferencesManager.isFirstLaunch) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
            val now = System.currentTimeMillis()
            val dayMs = 86400000L

            val sampleLogs = listOf(
                ReadingLog(
                    grade = 5,
                    classNum = 2,
                    studentName = "김민준",
                    bookTitle = "어린 왕자",
                    author = "앙투안 드 생텍쥐페리",
                    publisher = "열린책들",
                    summary = "지구에 온 어린 왕자가 사막에서 조종사를 만나고 자신의 별에 두고 온 장미와 여우와의 만남을 통해 소중한 가치를 깨닫는 이야기입니다.",
                    thoughts = "마음으로 보아야만 분명하게 볼 수 있다는 여우의 말이 가장 인상 깊었습니다. 겉모습보다 마음이 중요하다는 것을 깨달았습니다.",
                    timestamp = now - dayMs * 1,
                    dateFormatted = dateFormat.format(Date(now - dayMs * 1)),
                    isSynced = true
                ),
                ReadingLog(
                    grade = 5,
                    classNum = 2,
                    studentName = "이지은",
                    bookTitle = "해리 포터와 마법사의 돌",
                    author = "J.K. 롤링",
                    publisher = "문학수첩",
                    summary = "이모네 집에서 구박받던 해리 포터가 호그와트 마법학교 입학 통지서를 받고 친구 론, 헤르미온느와 함께 마법 세계를 구하는 이야기입니다.",
                    thoughts = "상상력이 가득한 마법 세계 이야기가 정말 재미있었습니다! 용기와 우정의 중요성을 느꼈습니다.",
                    timestamp = now - dayMs * 2,
                    dateFormatted = dateFormat.format(Date(now - dayMs * 2)),
                    isSynced = true
                ),
                ReadingLog(
                    grade = 5,
                    classNum = 2,
                    studentName = "박서준",
                    bookTitle = "아몬드",
                    author = "손원평",
                    publisher = "창비",
                    summary = "뇌 속 아몬드 모양의 편도체가 작아 감정을 느끼지 못하는 윤재가 비극적인 사건 이후 친구 곤이와 만나며 성장하는 소설입니다.",
                    thoughts = "타인의 아픔에 공감하는 능력이 얼마나 소중한지 다시 한번 생각하게 되었습니다.",
                    timestamp = now - dayMs * 3,
                    dateFormatted = dateFormat.format(Date(now - dayMs * 3)),
                    isSynced = true
                ),
                ReadingLog(
                    grade = 5,
                    classNum = 2,
                    studentName = "김민준",
                    bookTitle = "자전거 도둑",
                    author = "박완서",
                    publisher = "다림",
                    summary = "시골에서 올라와 상회에서 일하는 수남이가 실수로 자전거를 두고 번뇌하며 양심을 지켜내는 이야기입니다.",
                    thoughts = "유혹 앞에서도 양심을 지키려고 노력하는 수남이의 모습에서 정직함의 가치를 배웠습니다.",
                    timestamp = now - dayMs * 4,
                    dateFormatted = dateFormat.format(Date(now - dayMs * 4)),
                    isSynced = true
                ),
                ReadingLog(
                    grade = 5,
                    classNum = 1,
                    studentName = "최현우",
                    bookTitle = "마당을 나온 암탉",
                    author = "황선미",
                    publisher = "사계절",
                    summary = "양계장을 탈출한 암탉 잎싹이가 초록머리 청둥오리 아기를 키우며 자유와 모성애를 실천하는 동화입니다.",
                    thoughts = "잎싹이의 끝없는 사랑과 희생정신에 가슴이 뭉클했습니다.",
                    timestamp = now - dayMs * 5,
                    dateFormatted = dateFormat.format(Date(now - dayMs * 5)),
                    isSynced = true
                ),
                ReadingLog(
                    grade = 5,
                    classNum = 2,
                    studentName = "이지은",
                    bookTitle = "몽실 언니",
                    author = "권정생",
                    publisher = "창비",
                    summary = "한국전쟁의 비극 속에서도 어린 동생들을 보살피며 삶의 고난을 이겨내는 몽실이의 가슴 따뜻하고 슬픈 이야기입니다.",
                    thoughts = "전쟁의 아픔 속에서도 인내하는 몽실 언니의 따뜻한 마음을 닮고 싶습니다.",
                    timestamp = now - dayMs * 6,
                    dateFormatted = dateFormat.format(Date(now - dayMs * 6)),
                    isSynced = true
                ),
                ReadingLog(
                    grade = 5,
                    classNum = 2,
                    studentName = "김민준",
                    bookTitle = "모모",
                    author = "미하엘 엔데",
                    publisher = "비룡소",
                    summary = "시간 도둑들에게 시간을 빼앗긴 사람들을 구하기 위해 시간의 비밀을 간직한 모모가 펼치는 모험 이야기입니다.",
                    thoughts = "시간의 소중함과 친구들의 말을 진심으로 들어주는 귀 기울임의 진정한 가치를 알게 되었습니다.",
                    timestamp = now - dayMs * 7,
                    dateFormatted = dateFormat.format(Date(now - dayMs * 7)),
                    isSynced = true
                ),
                ReadingLog(
                    grade = 4,
                    classNum = 3,
                    studentName = "정다은",
                    bookTitle = "이상한 과자가게 전천당",
                    author = "히로시마 레이코",
                    publisher = "길벗스쿨",
                    summary = "신비로운 행운의 과자가게 전천당의 주인 베니코가 손님들의 고민을 해결해 주는 기묘한 이야기입니다.",
                    thoughts = "상상력이 넘치는 과자들이 정말 재미있고 신기했습니다.",
                    timestamp = now - dayMs * 8,
                    dateFormatted = dateFormat.format(Date(now - dayMs * 8)),
                    isSynced = true
                )
            )

            sampleLogs.forEach { dao.insertLog(it) }
            preferencesManager.isFirstLaunch = false
        }
    }
}
