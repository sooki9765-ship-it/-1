package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DeepNavyPrimary
import com.example.ui.theme.GoldAccent
import com.example.viewmodel.ReadingJournalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordFormScreen(viewModel: ReadingJournalViewModel) {
    val grade by viewModel.gradeInput.collectAsState()
    val classNum by viewModel.classNumInput.collectAsState()
    val name by viewModel.studentNameInput.collectAsState()
    val title by viewModel.bookTitleInput.collectAsState()
    val author by viewModel.authorInput.collectAsState()
    val publisher by viewModel.publisherInput.collectAsState()
    val summary by viewModel.summaryInput.collectAsState()
    val thoughts by viewModel.thoughtsInput.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()

    var gradeExpanded by remember { mutableStateOf(false) }
    var classExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Hero Image Header
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.height(140.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.img_library_hero),
                    contentDescription = "도서관 배경",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = "📖 나의 독서기록 작성",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "오늘 읽은 좋은 책의 마음속 깊은 소감을 기록해 보세요",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Student Info Section
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "학생 정보",
                        tint = DeepNavyPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "학생 인적사항",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepNavyPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Grade Dropdown
                    ExposedDropdownMenuBox(
                        expanded = gradeExpanded,
                        onExpandedChange = { gradeExpanded = !gradeExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = "${grade}학년",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("학년") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gradeExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DeepNavyPrimary,
                                focusedLabelColor = DeepNavyPrimary
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("grade_dropdown")
                        )
                        ExposedDropdownMenu(
                            expanded = gradeExpanded,
                            onDismissRequest = { gradeExpanded = false }
                        ) {
                            (1..6).forEach { g ->
                                DropdownMenuItem(
                                    text = { Text("${g}학년") },
                                    onClick = {
                                        viewModel.gradeInput.value = g
                                        gradeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Class Dropdown
                    ExposedDropdownMenuBox(
                        expanded = classExpanded,
                        onExpandedChange = { classExpanded = !classExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = "${classNum}반",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("반") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DeepNavyPrimary,
                                focusedLabelColor = DeepNavyPrimary
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("class_dropdown")
                        )
                        ExposedDropdownMenu(
                            expanded = classExpanded,
                            onDismissRequest = { classExpanded = false }
                        ) {
                            (1..12).forEach { c ->
                                DropdownMenuItem(
                                    text = { Text("${c}반") },
                                    onClick = {
                                        viewModel.classNumInput.value = c
                                        classExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { viewModel.studentNameInput.value = it },
                    label = { Text("학생 이름") },
                    placeholder = { Text("예: 김민준") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepNavyPrimary,
                        focusedLabelColor = DeepNavyPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_name_input")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Book Info Section
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = "도서 정보",
                        tint = DeepNavyPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "읽은 책 정보",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepNavyPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { viewModel.bookTitleInput.value = it },
                    label = { Text("도서명 (필수)") },
                    placeholder = { Text("예: 어린 왕자") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepNavyPrimary,
                        focusedLabelColor = DeepNavyPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("book_title_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = author,
                        onValueChange = { viewModel.authorInput.value = it },
                        label = { Text("지은이") },
                        placeholder = { Text("생텍쥐페리") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeepNavyPrimary,
                            focusedLabelColor = DeepNavyPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("author_input")
                    )

                    OutlinedTextField(
                        value = publisher,
                        onValueChange = { viewModel.publisherInput.value = it },
                        label = { Text("출판사") },
                        placeholder = { Text("열린책들") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeepNavyPrimary,
                            focusedLabelColor = DeepNavyPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("publisher_input")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Summary & Thoughts Section
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = "줄거리 및 소감",
                        tint = DeepNavyPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "줄거리 요약 및 독서 소감",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepNavyPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = summary,
                    onValueChange = { viewModel.summaryInput.value = it },
                    label = { Text("줄거리 (여러 줄 작성 가능)") },
                    placeholder = { Text("책의 주요 내용이나 줄거리를 요약해서 적어주세요...") },
                    minLines = 3,
                    maxLines = 6,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepNavyPrimary,
                        focusedLabelColor = DeepNavyPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("summary_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = thoughts,
                    onValueChange = { viewModel.thoughtsInput.value = it },
                    label = { Text("느낀 점 및 소감 (여러 줄 작성 가능)") },
                    placeholder = { Text("이 책을 읽고 새롭게 알게 된 점, 인상 깊었던 명대사, 깨달은 생각을 적어주세요...") },
                    minLines = 3,
                    maxLines = 6,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepNavyPrimary,
                        focusedLabelColor = DeepNavyPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("thoughts_input")
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Submit Button
        Button(
            onClick = { viewModel.submitReadingLog() },
            enabled = !isSubmitting,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepNavyPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("submit_reading_log_button")
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "저장 및 제출",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "독서기록 저장하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
