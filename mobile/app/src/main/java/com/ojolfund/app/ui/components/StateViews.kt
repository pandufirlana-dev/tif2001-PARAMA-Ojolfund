package com.ojolfund.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText

/** Keadaan galat untuk layar yang memuat data: pesan singkat dan tombol coba lagi. */
@Composable
fun ErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            Modifier.size(64.dp).clip(CircleShape).background(OjolColors.ExpenseBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.ErrorOutline,
                contentDescription = null,
                tint = OjolColors.ExpenseText,
                modifier = Modifier.size(32.dp)
            )
        }
        Text(
            "Gagal memuat data",
            style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
            color = OjolColors.TextPrimary
        )
        Text(message, style = OjolText.BodyMd, color = OjolColors.TextVariant, textAlign = TextAlign.Center)
        SoftButton(text = "Coba lagi", onClick = onRetry, leadingIcon = Icons.Filled.Refresh)
    }
}
