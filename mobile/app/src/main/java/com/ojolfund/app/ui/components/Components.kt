package com.ojolfund.app.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ojolfund.app.ui.Routes
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

// ---------------------------------------------------------------- Card

@Composable
fun OjolCard(
    modifier: Modifier = Modifier,
    color: Color = OjolColors.Card,
    shape: Shape = RoundedCornerShape(16.dp),
    padding: Dp = 16.dp,
    borderColor: Color = OjolColors.Border,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val clickMod = if (onClick != null) Modifier.clip(shape).clickable(onClick = onClick) else Modifier
    Surface(
        modifier = modifier,
        shape = shape,
        color = color,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = 1.dp,
    ) {
        Column(modifier = clickMod.padding(padding), content = content)
    }
}

// ---------------------------------------------------------------- Pill / chip

@Composable
fun Pill(
    text: String,
    bg: Color,
    fg: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    dot: Boolean = false,
    textSize: TextUnit = 11.sp,
    weight: FontWeight = FontWeight.SemiBold,
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        if (dot) Box(Modifier.size(6.dp).clip(CircleShape).background(fg))
        if (icon != null) Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(13.dp))
        Text(text, color = fg, fontSize = textSize, fontWeight = weight, maxLines = 1)
    }
}

@Composable
fun SyncPill(modifier: Modifier = Modifier) {
    Pill("Tersinkron", bg = OjolColors.Aqua, fg = OjolColors.PrimaryDeep, modifier = modifier, dot = true)
}

@Composable
fun IconBox(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    bg: Color = OjolColors.Aqua,
    tint: Color = OjolColors.Primary,
    size: Dp = 40.dp,
    radius: Dp = 10.dp,
    iconSize: Dp = 22.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(radius))
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(iconSize))
    }
}

// ---------------------------------------------------------------- Buttons

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    color: Color = OjolColors.Primary,
    enabled: Boolean = true,
    height: Dp = 52.dp,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(height),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White,
            disabledContainerColor = color.copy(alpha = 0.5f),
            disabledContentColor = Color.White,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = OjolText.LabelLg.copy(fontSize = 16.sp), color = Color.White)
        if (trailingIcon != null) {
            Spacer(Modifier.width(8.dp))
            Icon(trailingIcon, contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun SoftButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    bg: Color = OjolColors.GrayBox,
    fg: Color = OjolColors.Primary,
    height: Dp = 48.dp,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(height),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bg, contentColor = fg),
        elevation = null,
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = OjolText.LabelLg, color = fg)
    }
}

// ---------------------------------------------------------------- Section header

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: String? = null,
    onAction: (() -> Unit)? = null,
    actionColor: Color = OjolColors.Primary,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold), color = OjolColors.TextPrimary)
        if (action != null) {
            Text(
                action,
                style = OjolText.LabelMd,
                color = actionColor,
                modifier = if (onAction != null) Modifier.clickable(onClick = onAction) else Modifier,
            )
        }
    }
}

// ---------------------------------------------------------------- Progress line

@Composable
fun ProgressLine(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    track: Color = OjolColors.GrayBox2,
    height: Dp = 8.dp,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(track),
    ) {
        Box(
            Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(height)
                .clip(CircleShape)
                .background(color),
        )
    }
}

// ---------------------------------------------------------------- Logo & avatar

@Composable
fun OjolLogo(modifier: Modifier = Modifier, size: Dp = 36.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size / 3.2f))
            .background(OjolColors.Primary),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Filled.AccountBalanceWallet,
            contentDescription = "OjoLFund",
            tint = Color.White,
            modifier = Modifier.size(size * 0.55f),
        )
    }
}

@Composable
fun AvatarCircle(modifier: Modifier = Modifier, size: Dp = 40.dp, onClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(OjolColors.Primary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Filled.Person, contentDescription = "Profil", tint = Color.White, modifier = Modifier.size(size * 0.55f))
    }
}

// ---------------------------------------------------------------- Top bars

/** Top bar ala Beranda / Transaksi / Profil: logo + "OjolFund [Tersinkron]" + subjudul + lonceng + avatar. */
@Composable
fun OjolTopBar(
    subtitle: String,
    modifier: Modifier = Modifier,
    onBell: () -> Unit = {},
    onAvatar: () -> Unit = {},
) {
    Surface(color = Color.White, shadowElevation = 2.dp, modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OjolLogo(size = 36.dp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("OjolFund", style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold), color = OjolColors.TextPrimary)
                    SyncPill()
                }
                Text(subtitle, style = OjolText.LabelSm, color = OjolColors.TextSecondary)
            }
            Icon(
                Icons.Filled.Notifications,
                contentDescription = "Notifikasi",
                tint = OjolColors.TextPrimary,
                modifier = Modifier.size(24.dp).clickable(onClick = onBell),
            )
            Spacer(Modifier.width(14.dp))
            AvatarCircle(size = 38.dp, onClick = onAvatar)
        }
    }
}

/** Header sederhana ala Shift / Kendaraan: judul besar + "Tersinkron" + lonceng + avatar. */
@Composable
fun PageHeader(
    title: String,
    modifier: Modifier = Modifier,
    onBell: () -> Unit = {},
    onAvatar: () -> Unit = {},
) {
    Surface(color = Color.White, shadowElevation = 2.dp, modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, style = OjolText.HeadlineLg, color = OjolColors.TextPrimary)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(Modifier.size(7.dp).clip(CircleShape).background(OjolColors.Primary))
                    Text("Tersinkron", style = OjolText.LabelMd, color = OjolColors.Primary)
                }
            }
            Icon(
                Icons.Filled.Notifications,
                contentDescription = "Notifikasi",
                tint = OjolColors.TextPrimary,
                modifier = Modifier.size(24.dp).clickable(onClick = onBell),
            )
            Spacer(Modifier.width(14.dp))
            AvatarCircle(size = 40.dp, onClick = onAvatar)
        }
    }
}

/** Top bar layar autentikasi: panah kembali + "OJOLFUND" + judul + avatar. */
@Composable
fun AuthTopBar(title: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Surface(color = Color.White, shadowElevation = 2.dp, modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = OjolColors.TextPrimary,
                modifier = Modifier.size(26.dp).clickable(onClick = onBack),
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(Modifier.size(7.dp).clip(CircleShape).background(OjolColors.Primary))
                    Text("OJOLFUND", style = OjolText.LabelSm.copy(letterSpacing = 1.2.sp), color = OjolColors.Primary)
                }
                Text(title, style = OjolText.TitleLg, color = OjolColors.TextPrimary)
            }
            AvatarCircle(size = 40.dp)
        }
    }
}

// ---------------------------------------------------------------- Bottom bar

private data class NavItem(val route: String, val label: String, val icon: ImageVector)

private val navItems = listOf(
    NavItem(Routes.HOME, "Beranda", Icons.Filled.Home),
    NavItem(Routes.TRANSACTIONS, "Transaksi", Icons.Filled.AccountBalanceWallet),
    NavItem(Routes.SHIFT, "Shift", Icons.Filled.Speed),
    NavItem(Routes.VEHICLE, "Kendaraan", Icons.Filled.TwoWheeler),
    NavItem(Routes.PROFILE, "Profil", Icons.Filled.ManageAccounts),
)

@Composable
fun OjolBottomBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    Column {
        HorizontalDivider(color = OjolColors.Border)
        NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
            navItems.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = { onNavigate(item.route) },
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = {
                        Text(
                            item.label,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OjolColors.Primary,
                        selectedTextColor = OjolColors.Primary,
                        unselectedIconColor = OjolColors.TextSecondary,
                        unselectedTextColor = OjolColors.TextSecondary,
                        indicatorColor = Color.Transparent,
                    ),
                )
            }
        }
    }
}
