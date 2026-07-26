package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepNavyPrimary
import com.example.ui.theme.GoldAccent

data class RecommendedBook(
    val title: String,
    val author: String,
    val publisher: String,
    val recommendedForGrade: String,
    val badge: String,
    val coverColor: Color
)

val recommendedBooksList = listOf(
    RecommendedBook(
        title = "어린 왕자",
        author = "앙투안 드 생텍쥐페리",
        publisher = "열린책들",
        recommendedForGrade = "전학년",
        badge = "🔥 베스트셀러",
        coverColor = Color(0xFF1E3A8A)
    ),
    RecommendedBook(
        title = "아몬드",
        author = "손원평",
        publisher = "창비",
        recommendedForGrade = "5~6학년",
        badge = "⭐ 감동 추천",
        coverColor = Color(0xFF831843)
    ),
    RecommendedBook(
        title = "마당을 나온 암탉",
        author = "황선미",
        publisher = "사계절",
        recommendedForGrade = "3~4학년",
        badge = "🏆 필독서",
        coverColor = Color(0xFF065F46)
    ),
    RecommendedBook(
        title = "자전거 도둑",
        author = "박완서",
        publisher = "다림",
        recommendedForGrade = "4~6학년",
        badge = "📚 교과서 수록",
        coverColor = Color(0xFF78350F)
    ),
    RecommendedBook(
        title = "지구를 살리는 멋진 어린이",
        author = "김은주",
        publisher = "웅진주니어",
        recommendedForGrade = "1~3학년",
        badge = "🌱 환경 추천",
        coverColor = Color(0xFF047857)
    )
)

@Composable
fun BookstoreInspirationSection(
    onSelectBook: (title: String, author: String, publisher: String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = "추천 도서",
                    tint = DeepNavyPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "📖 서점 인기 추천 필독서 (클릭 시 자동 입력)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepNavyPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 12.dp)
        ) {
            items(recommendedBooksList) { book ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .width(160.dp)
                        .clickable { onSelectBook(book.title, book.author, book.publisher) }
                ) {
                    Column {
                        // Simulated Book Spine Cover
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            book.coverColor,
                                            book.coverColor.copy(alpha = 0.8f)
                                        )
                                    )
                                )
                                .padding(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White.copy(alpha = 0.25f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = book.badge,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Column {
                                    Text(
                                        text = book.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = book.author,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.85f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Bottom Action Info
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = book.recommendedForGrade,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "선택",
                                        tint = DeepNavyPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "기록하기",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepNavyPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
