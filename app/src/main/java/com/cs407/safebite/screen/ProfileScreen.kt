package com.cs407.safebite.screen

import com.cs407.safebite.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cs407.safebite.ui.theme.AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit,
    onAddMoreAllergens: () -> Unit = {} // optional, hook to navigate to "input_allergies"
) {
    val gradientTopColor = AppTheme.customColors.gradientTop
    val gradientBottomColor = AppTheme.customColors.gradientBottom

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(gradientTopColor, gradientBottomColor)
                )
            ),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // Top bar: "Menu" (left) + centered title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back/Menu action (left)
                IconButton(onClick = onNavigateToHome) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Menu / Back"
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "User allergens",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.width(24.dp)) // visual balance on the right
            }

            // Section label
            Text(
                text = "Saved User allergens",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            // ✓ Peanut row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✓",
                    fontSize = 28.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "Peanut",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(Modifier.weight(1f))

            // "Add more allergens" row with a square box (checkbox look)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onAddMoreAllergens() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Use an unchecked Checkbox for the square visual from the mock
                Checkbox(
                    checked = false,
                    onCheckedChange = { onAddMoreAllergens() }
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Add more allergens",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}