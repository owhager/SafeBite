package com.cs407.safebite.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.safebite.component.UnifiedTopBar
import com.cs407.safebite.viewmodel.BarcodeLookupViewModel
import com.cs407.safebite.viewmodel.RecentItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentsScreen(
    barcodeModel: BarcodeLookupViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRecents: () -> Unit,
    onNavigateToInput: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToResults: () -> Unit,
    onLogout: () -> Unit
) {
    val recentItems by barcodeModel.recentItems.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            UnifiedTopBar(
                title = "Recent Scans",
                onNavigateBack = { onNavigateBack() },
                onNavigateToProfile = { onNavigateToProfile() },
                onNavigateToRecents = { onNavigateToRecents() },
                onNavigateToInput = { onNavigateToInput() },
                onNavigateToScan = { onNavigateToScan() },
                onLogout = { onLogout() }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Past Scanned Items",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            if (recentItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No items scanned yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recentItems) { item ->
                        RecentProductCard(
                            item = item,
                            onClick = {
                                // Push this item into the shared ViewModel…
                                barcodeModel.selectRecentItem(item)
                                // …then navigate to the Result screen.
                                onNavigateToResults()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentProductCard(
    item: RecentItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(vertical = 14.dp, horizontal = 18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = item.foodName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (item.brandName.isNotBlank()) {
                Text(
                    text = item.brandName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Barcode: ${item.barcode}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
