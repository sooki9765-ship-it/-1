package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DeepNavyPrimary
import com.example.ui.theme.GoldAccent
import kotlinx.coroutines.delay

data class ReadingQuote(
    val id: Int,
    val quote: String,
    val author: String,
    val bookOrRole: String,
    val category: String
)

val sampleQuotes = listOf(
    ReadingQuote(
        id = 1,
        quote = "책을 읽는 것은 과거의 가장 뛰어난 사람들과 따뜻한 대화를 나누는 것이다.",
        author = "르네 데카르트",
        bookOrRole = "철학자",
        category = "지혜"
    ),
    ReadingQuote(
        id = 2,
        quote = "오늘의 나를 만든 것은 우리 동네의 작은 도서관이었다. 하버드 졸업장보다 소중한 것은 독서하는 습관이다.",
        author = "빌 게이츠",
        bookOrRole = "마이크로소프트 창업자",
        category = "성장"
    ),
    ReadingQuote(
        id = 3,
        quote = "한 권의 책을 읽는 것은 새로운 세계의 문을 열고 새로운 삶을 얻는 것과 같다.",
        author = "세르반테스",
        bookOrRole = "‘돈키호테’ 저자",
        category = "꿈과 희망"
    ),
    ReadingQuote(
        id = 4,
        quote = "책 속에 모든 길과 지혜가 있다. 읽는 자만이 미래를 생각하고 세상을 밝게 이끈다.",
        author = "에이브러햄 링컨",
        bookOrRole = "미국 16대 대통령",
        category = "지혜"
    ),
    ReadingQuote(
        id = 5,
        quote = "책 없는 방은 마치 영혼이 없는 몸과 같고, 햇살 없는 아침과 같다.",
        author = "키케로",
        bookOrRole = "고대 로마 정치가",
        category = "감성"
    ),
    ReadingQuote(
        id = 6,
        quote = "좋은 책을 읽는 것은 평생 함께할 가장 진실하고 현명한 친구를 만나 이야기를 듣는 것이다.",
        author = "프랜시스 베이컨",
        bookOrRole = "철학자",
        category = "성장"
    ),
    ReadingQuote(
        id = 7,
        quote = "네가 읽는 책이 곧 너의 마음과 미래를 비추는 거울이 된다.",
        author = "소크라테스",
        bookOrRole = "철학자",
        category = "감성"
    )
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ReadingQuotesSection() {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("전체") }
    var currentIndex by remember { mutableIntStateOf(0) }
    var isAutoPlay by remember { mutableStateOf(true) }
    var bookmarkedQuoteIds by remember { mutableStateOf(setOf<Int>()) }

    val categories = listOf("전체", "지혜", "성장", "꿈과 희망", "감성")

    val filteredQuotes = remember(selectedCategory) {
        if (selectedCategory == "전체") sampleQuotes
        else sampleQuotes.filter { it.category == selectedCategory }
    }

    // Auto rotate every 5 seconds if playing
    LaunchedEffect(isAutoPlay, currentIndex, filteredQuotes) {
        if (isAutoPlay && filteredQuotes.isNotEmpty()) {
            delay(5000L)
            currentIndex = (currentIndex + 1) % filteredQuotes.size
        }
    }

    // Ensure valid index when filter changes
    val safeIndex = if (filteredQuotes.isEmpty()) 0 else currentIndex % filteredQuotes.size
    val currentQuote = filteredQuotes.getOrNull(safeIndex) ?: sampleQuotes.first()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reading_quotes_section")
    ) {
        // Portal Header with Bookstore Image Card
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_bookstore_banner),
                    contentDescription = "인터넷 서점 도서관 포털",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    DeepNavyPrimary.copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(GoldAccent.copy(alpha = 0.95f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = DeepNavyPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "온라인 서점 & 독서 포털",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavyPrimary
                                )
                            }
                        }

                        // Play/Pause button for dynamic animation
                        IconButton(
                            onClick = { isAutoPlay = !isAutoPlay },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = if (isAutoPlay) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "자동 재생/일시정지",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "📚 우리반 인터넷 독서 서점",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "지혜와 꿈을 키우는 오늘의 감성 독서 명언 모음",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedCategory = cat
                        currentIndex = 0
                    },
                    label = {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DeepNavyPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = DeepNavyPrimary
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dynamic Animated Reading Quote Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("quote_card_display")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFDFBF7),
                                Color(0xFFF4EFE6)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                // Top controls inside card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = "명언",
                            tint = GoldAccent,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "오늘의 한 줄 (${safeIndex + 1}/${filteredQuotes.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavyPrimary
                        )
                    }

                    Row {
                        // Copy Button
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Reading Quote", "\"${currentQuote.quote}\" - ${currentQuote.author}")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "명언이 복사되었습니다!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "명언 복사",
                                tint = DeepNavyPrimary.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Bookmark Button
                        val isBookmarked = bookmarkedQuoteIds.contains(currentQuote.id)
                        IconButton(
                            onClick = {
                                bookmarkedQuoteIds = if (isBookmarked) {
                                    bookmarkedQuoteIds - currentQuote.id
                                } else {
                                    bookmarkedQuoteIds + currentQuote.id
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "명언 북마크",
                                tint = if (isBookmarked) GoldAccent else DeepNavyPrimary.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Animated AnimatedContent for smooth dynamic quote transition
                AnimatedContent(
                    targetState = currentQuote,
                    transitionSpec = {
                        slideInHorizontally { width -> width } + fadeIn() with
                                slideOutHorizontally { width -> -width } + fadeOut()
                    },
                    label = "quote_transition"
                ) { quoteItem ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "“ ${quoteItem.quote} ”",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontStyle = FontStyle.Italic,
                            color = DeepNavyPrimary,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(2.dp)
                                    .background(GoldAccent)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${quoteItem.author} (${quoteItem.bookOrRole})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepNavyPrimary.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(2.dp)
                                    .background(GoldAccent)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Navigation Prev / Next Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (filteredQuotes.isNotEmpty()) {
                                currentIndex = (currentIndex - 1 + filteredQuotes.size) % filteredQuotes.size
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DeepNavyPrimary.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "이전 명언",
                            tint = DeepNavyPrimary
                        )
                    }

                    // Dot indicators
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        filteredQuotes.forEachIndexed { index, _ ->
                            val isSelected = index == safeIndex
                            Box(
                                modifier = Modifier
                                    .size(if (isSelected) 10.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) DeepNavyPrimary else Color.Gray.copy(alpha = 0.3f))
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            if (filteredQuotes.isNotEmpty()) {
                                currentIndex = (currentIndex + 1) % filteredQuotes.size
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DeepNavyPrimary.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "다음 명언",
                            tint = DeepNavyPrimary
                        )
                    }
                }
            }
        }
    }
}
