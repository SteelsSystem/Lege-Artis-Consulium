package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.Biotech
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.ConsultingPortalScreen
import com.example.ui.screens.MetasimulatorAiScreen
import com.example.ui.screens.NgsDashboardScreen
import com.example.ui.screens.QMaxCypherScreen
import com.example.ui.theme.LegeArtisTheme
import com.example.util.configureAppCheck
import com.example.viewmodel.AiCopilotViewModel
import com.example.viewmodel.LegeArtisViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        // STEP 2: Enable Edge-to-Edge layout
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val legeArtisViewModel: LegeArtisViewModel = viewModel(
                factory = LegeArtisViewModel.provideFactory(context)
            )
            val aiCopilotViewModel: AiCopilotViewModel = viewModel()
            val isDarkTheme by legeArtisViewModel.isDarkTheme.collectAsStateWithLifecycle()

            LegeArtisTheme(darkTheme = isDarkTheme) {
                LegeArtisAppShell(
                    legeArtisViewModel = legeArtisViewModel,
                    aiCopilotViewModel = aiCopilotViewModel,
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

private data class NavDestination(
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun LegeArtisAppShell(
    legeArtisViewModel: LegeArtisViewModel,
    aiCopilotViewModel: AiCopilotViewModel,
    isDarkTheme: Boolean
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val activeRole by legeArtisViewModel.activeRole.collectAsStateWithLifecycle()

    if (selectedTab != 0) {
        BackHandler {
            selectedTab = 0
        }
    }

    val destinations = listOf(
        NavDestination(
            R.string.nav_ngs_dashboard,
            Icons.Filled.Biotech,
            Icons.Outlined.Biotech,
            "nav_tab_ngs"
        ),
        NavDestination(
            R.string.nav_qmax_cypher,
            Icons.Filled.Lock,
            Icons.Outlined.Lock,
            "nav_tab_qmax"
        ),
        NavDestination(
            R.string.nav_metasim_ai,
            Icons.Filled.Hub,
            Icons.Outlined.Hub,
            "nav_tab_metasim"
        ),
        NavDestination(
            R.string.nav_consulting_portal,
            Icons.Filled.VerifiedUser,
            Icons.Outlined.VerifiedUser,
            "nav_tab_portal"
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            LegeArtisTopHeader(
                isDarkTheme = isDarkTheme,
                activeRole = activeRole,
                onToggleTheme = legeArtisViewModel::toggleTheme
            )
        },
        bottomBar = {
            NavigationBar {
                destinations.forEachIndexed { index, dest ->
                    val selected = selectedTab == index
                    NavigationBarItem(
                        selected = selected,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                contentDescription = stringResource(dest.labelRes)
                            )
                        },
                        label = { Text(stringResource(dest.labelRes)) },
                        modifier = Modifier.testTag(dest.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> NgsDashboardScreen(
                viewModel = legeArtisViewModel,
                modifier = Modifier.padding(innerPadding)
            )
            1 -> QMaxCypherScreen(
                viewModel = legeArtisViewModel,
                modifier = Modifier.padding(innerPadding)
            )
            2 -> MetasimulatorAiScreen(
                legeArtisViewModel = legeArtisViewModel,
                aiViewModel = aiCopilotViewModel,
                modifier = Modifier.padding(innerPadding)
            )
            3 -> ConsultingPortalScreen(
                viewModel = legeArtisViewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun LegeArtisTopHeader(
    isDarkTheme: Boolean,
    activeRole: String,
    onToggleTheme: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_hero_bio_cypher),
                        contentDescription = "Bio-Cypher Telemetry Emblem",
                        modifier = Modifier
                            .width(56.dp)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "LEGE ARTIS CONSULIUM",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "NGS Bioinformatics • Q-MAX Offline DB • $activeRole",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("btn_toggle_theme")
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = stringResource(R.string.toggle_theme_desc),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
