package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GasSyncService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    val codeGsSnippet = """
/**
 * [우리반 전자 독서기록장] Google Apps Script (Code.gs)
 *
 * 사용 설명:
 * 1. 구글 스프레드시트 생성 > 상단 메뉴 '확장 프로그램' > 'Apps Script' 선택
 * 2. 기존 코드를 삭제하고 이 코드를 그대로 복사/붙여넣기 합니다.
 * 3. 우측 상단 '배포' > '새 배포' 클릭
 * 4. 유형 선택: '웹 앱'
 * 5. 액세스 권한: '모든 사용자' (Anyone)로 설정 후 배포!
 * 6. 생성된 '웹 앱 URL (Web App URL)'을 앱 연동 설정에 입력하세요.
 */

function doPost(e) {
  try {
    var sheet = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet();
    var contents = e.postData.contents;
    var data = JSON.parse(contents);
    
    // 시트가 비어있으면 헤더(제목 행) 자동 생성
    if (sheet.getLastRow() === 0) {
      sheet.appendRow([
        "작성일시", "학년", "반", "이름", "도서명", "지은이", "출판사", "줄거리", "소감"
      ]);
      sheet.getRange(1, 1, 1, 9).setFontWeight("bold").setBackground("#F1F5F9");
    }
    
    // 학생 독서 기록 행 추가
    sheet.appendRow([
      data.date || new Date().toLocaleString("ko-KR"),
      data.grade + "학년" || "",
      data.classNum + "반" || "",
      data.studentName || "",
      data.bookTitle || "",
      data.author || "",
      data.publisher || "",
      data.summary || "",
      data.thoughts || ""
    ]);
    
    var output = JSON.stringify({ "result": "success", "message": "성공적으로 저장되었습니다." });
    return ContentService.createTextOutput(output)
      .setMimeType(ContentService.MimeType.JSON);
      
  } catch (err) {
    var errOutput = JSON.stringify({ "result": "error", "message": err.toString() });
    return ContentService.createTextOutput(errOutput)
      .setMimeType(ContentService.MimeType.JSON);
  }
}

function doGet(e) {
  return ContentService.createTextOutput(
    JSON.stringify({ "status": "online", "app": "ClassReadingJournal" })
  ).setMimeType(ContentService.MimeType.JSON);
}
""".trimIndent()

    suspend fun postReadingLog(webAppUrl: String, log: ReadingLog): Result<Boolean> = withContext(Dispatchers.IO) {
        if (webAppUrl.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("웹 앱 URL이 설정되지 않았습니다."))
        }

        try {
            val json = JSONObject().apply {
                put("date", log.dateFormatted)
                put("grade", log.grade)
                put("classNum", log.classNum)
                put("studentName", log.studentName)
                put("bookTitle", log.bookTitle)
                put("author", log.author)
                put("publisher", log.publisher)
                put("summary", log.summary)
                put("thoughts", log.thoughts)
            }

            val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(webAppUrl)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    Result.failure(Exception("HTTP 에러: ${response.code}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun testConnection(webAppUrl: String): Result<Boolean> = withContext(Dispatchers.IO) {
        if (webAppUrl.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("웹 앱 URL을 입력해 주세요."))
        }
        try {
            val request = Request.Builder()
                .url(webAppUrl)
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    Result.failure(Exception("연동 테스트 실패: HTTP ${response.code}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
