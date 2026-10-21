package com.ojolfund.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText

/** Logo besar untuk header layar autentikasi. */
@Composable
fun AuthHeroLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(84.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(OjolColors.Aqua),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier.size(58.dp).clip(RoundedCornerShape(18.dp)).background(OjolColors.Primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.AccountBalanceWallet,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/** Banner pesan: merah untuk galat dari server, hijau-mint untuk informasi sukses. */
@Composable
fun MessageBanner(message: String, isError: Boolean, modifier: Modifier = Modifier) {
    val bg = if (isError) OjolColors.ExpenseBg else OjolColors.Mint
    val fg = if (isError) OjolColors.ExpenseText else OjolColors.PrimaryDeep
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            if (isError) Icons.Filled.Info else Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = fg,
            modifier = Modifier.size(20.dp)
        )
        Text(message, style = OjolText.BodyMd, color = fg)
    }
}
