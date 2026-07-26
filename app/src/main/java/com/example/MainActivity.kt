package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GASSettingsModal
import com.example.ui.components.PasscodeModal
import com.example.ui.screens.HallOfFameScreen
import com.example.ui.screens.MyLibraryScreen
import com.example.ui.screens.RecordFormScreen
import com.example.ui.screens.TeacherDashboardScreen
import com.example.ui.theme.ClassReadingJournalTheme
import com.example.ui.theme.DeepNavyPrimary
import com.example.ui.theme.GoldAccent
import com.example.viewmodel.ReadingJournalViewModel

enum class NavigationTab(val title: String, val icon: ImageVector, val tag: String) {
    FORM("독서기록 작성", Icons.Default.EditNote, "tab_form"),
    MY_LIBRARY("나의 독서 서재", Icons.Default.LibraryBooks, "tab_library"),
    HALL_OF_FAME("이달의 독서왕", Icons.Default.EmojiEvents, "tab_hall_of_fame"),
    TEACHER_DASHBOARD("교사 대시보드", Icons.Default.AdminPanelSettings, "tab_teacher")
}

class MainActivity : ComponentActivity() {

    private val viewModel: ReadingJournalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ClassReadingJournalTheme {
                ClassReadingApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassReadingApp(viewModel: ReadingJournalViewModel) {
    var currentTab by remember { mutableStateOf(NavigationTab.FORM) }

    val showPasswordDialog by viewModel.showPasswordDialog.collectAsState()
    val passwordInput by viewModel.passwordInput.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()

    val showGasSettings by viewModel.showGasSettings.collectAsState()
    val gasUrlInput by viewModel.gasUrlInput.collectAsState()
    val isTestingGasUrl by viewModel.isTestingGasUrl.collectAsState()
    val gasTestMessage by viewModel.gasTestMessage.collectAsState()

    val snackbarMessage by viewModel.submitSnackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_reading_app_icon),
                            contentDescription = "우리반 전자 독서기록장 로고",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "우리반 전자 독서기록장",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.showGasSettings.value = true },
                        modifier = Modifier.testTag("top_bar_gas_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "구글 시트 연동 설정",
                            tint = GoldAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepNavyPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DeepNavyPrimary,
                contentColor = Color.White
            ) {
                NavigationTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = if (isSelected) GoldAccent else Color.White.copy(alpha = 0.6f)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) GoldAccent else Color.White.copy(alpha = 0.6f)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = DeepNavyPrimary.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavigationTab.FORM -> RecordFormScreen(viewModel = viewModel)
                NavigationTab.MY_LIBRARY -> MyLibraryScreen(viewModel = viewModel)
                NavigationTab.HALL_OF_FAME -> HallOfFameScreen(viewModel = viewModel)
                NavigationTab.TEACHER_DASHBOARD -> TeacherDashboardScreen(viewModel = viewModel)
            }
        }
    }

    // Teacher Passcode Modal
    if (showPasswordDialog) {
        PasscodeModal(
            passwordInput = passwordInput,
            onPasswordChange = { viewModel.passwordInput.value = it },
            errorText = passwordError,
            onConfirm = { viewModel.verifyTeacherPassword() },
            onDismiss = {
                viewModel.showPasswordDialog.value = false
                viewModel.passwordInput.value = ""
                viewModel.passwordError.value = null
            }
        )
    }

    // GAS Web App Settings Modal
    if (showGasSettings) {
        GASSettingsModal(
            gasUrlInput = gasUrlInput,
            onUrlChange = { viewModel.gasUrlInput.value = it },
            codeGsSnippet = viewModel.codeGsSnippet,
            isTesting = isTestingGasUrl,
            testMessage = gasTestMessage,
            onSaveAndTest = { viewModel.testGasConnection() },
            onDismiss = {
                viewModel.showGasSettings.value = false
                viewModel.gasTestMessage.value = null
            }
        )
    }
}
