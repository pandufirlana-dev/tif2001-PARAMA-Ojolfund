
package com.ojolfund.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ojolfund.app.data.Transaction
import com.ojolfund.app.data.formatRupiah
import com.ojolfund.app.ui.components.IconBox
import com.ojolfund.app.ui.components.OjolCard
import com.ojolfund.app.ui.components.OjolTopBar
import com.ojolfund.app.ui.components.Pill
import com.ojolfund.app.ui.components.SectionHeader
import com.ojolfund.app.ui.components.TagIcon
import com.ojolfund.app.ui.components.tagStyle
import com.ojolfund.app.ui.components.toast
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText
import com.ojolfund.app.viewmodel.LedgerViewModel

@Composable
fun HomeScreen(
    ledgerVm: LedgerViewModel,
    onAddExpense: () -> Unit,
    onStartShift: () -> Unit,
    onSeeAll: () -> Unit,
    onProfile: () -> Unit,
) {
    val context = LocalContext.current
    val transactions by ledgerVm.transactions.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .background(OjolColors.Canvas)
    ) {
        OjolTopBar(
            subtitle = "Beranda",
            onBell = { context.toast("Belum ada notifikasi baru") },
            onAvatar = onProfile,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Sapaan
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Selamat pagi, Driver Budi! 👋",
                        style = OjolText.HeadlineLg,
                        color = OjolColors.TextPrimary
                    )
                    Text(
                        "Siap cari cuan hari ini?",
                        style = OjolText.BodyMd,
                        color = OjolColors.TextVariant
                    )
                }
            }

            // Kartu untung bersih
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color.White,
                                Color.White,
                                Color(0xFFE6F8F5)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        OjolColors.Border,
                        RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp),
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Filled.AccountBalanceWallet,
                            contentDescription = null,
                            tint = OjolColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Untung Bersih Hari Ini",
                            style = OjolText.LabelLg,
                            color = OjolColors.TextPrimary
                        )
                    }

                    Pill(
                        "Kamis, 24 Okt 2024",
                        bg = OjolColors.GrayBox2,
                        fg = OjolColors.TextVariant,
                        textSize = 10.sp
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    "Rp125.000",
                    style = OjolText.Display.copy(fontSize = 38.sp),
                    color = OjolColors.Primary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Filled.TrendingUp,
                        contentDescription = null,
                        tint = OjolColors.PrimaryDeep,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        "+18% dibanding kemarin di jam yang sama",
                        style = OjolText.BodySm,
                        color = OjolColors.PrimaryDeep
                    )
                }

                Spacer(Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MiniStat(
                        "Kotor",
                        "Rp165.000",
                        OjolColors.TextPrimary,
                        OjolColors.GrayBox,
                        Modifier.weight(1f)
                    )
                    MiniStat(
                        "Potongan",
                        "-Rp25.000",
                        OjolColors.ExpenseText,
                        OjolColors.GrayBox,
                        Modifier.weight(1f)
                    )
                    MiniStat(
                        "Operasional",
                        "-Rp15.000",
                        OjolColors.ExpenseText,
                        OjolColors.ExpenseBg,
                        Modifier.weight(1f)
                    )
                }
            }

            // Aksi cepat
            SectionHeader(
                "Aksi Cepat",
                action = "Satu Ketukan",
                actionColor = OjolColors.TextVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAction(
                    icon = Icons.Filled.TwoWheeler,
                    iconBg = OjolColors.Aqua,
                    iconTint = OjolColors.PrimaryDeep,
                    title = "Tambah Order",
                    subtitle = "Multi-aplikasi",
                    trailing = {
                        Icon(
                            Icons.Filled.Add,
                            null,
                            tint = OjolColors.TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    onClick = {
                        context.toast(
                            "Tambah order (demo): GoRide / Grab / Maxim"
                        )
                    },
                )

                QuickAction(
                    icon = Icons.Filled.Receipt,
                    iconBg = OjolColors.GrayBox2,
                    iconTint = OjolColors.TextVariant,
                    title = "Pengeluaran",
                    subtitle = "Makan & parkir",
                    trailing = {
                        Text(
                            "–",
                            color = OjolColors.TextSecondary,
                            fontSize = 18.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    onClick = onAddExpense,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAction(
                    icon = Icons.Filled.PlayArrow,
                    iconBg = OjolColors.Aqua,
                    iconTint = OjolColors.PrimaryDeep,
                    title = "Mulai Shift",
                    subtitle = "Lacak jam on-bid",
                    trailing = {
                        Pill(
                            "Siap",
                            bg = OjolColors.Aqua,
                            fg = OjolColors.PrimaryDeep,
                            textSize = 10.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    onClick = onStartShift,
                )

                QuickAction(
                    icon = Icons.Filled.LocalGasStation,
                    iconBg = OjolColors.Aqua,
                    iconTint = OjolColors.PrimaryDeep,
                    title = "Catat BBM",
                    subtitle = "Isi bensin kilat",
                    trailing = {
                        Pill(
                            "Cepat",
                            bg = OjolColors.GrayBox2,
                            fg = OjolColors.TextVariant,
                            textSize = 10.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    onClick = onAddExpense,
                )
            }

            // Ringkasan
            SectionHeader(
                "Ringkasan Hari Ini",
                action = "24 Okt",
                actionColor = OjolColors.Primary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    Icons.Filled.Payments,
                    "Pendapatan Kotor",
                    "Rp165.000",
                    "Total ongkos pelanggan",
                    OjolColors.TextPrimary,
                    Modifier.weight(1f)
                )
                StatCard(
                    Icons.Filled.Percent,
                    "Potongan Platform",
                    "Rp25.000",
                    "Bagi hasil rata-rata",
                    OjolColors.TextPrimary,
                    Modifier.weight(1f),
                    suffix = " (15%)"
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    Icons.Filled.ShoppingBag,
                    "Total Pengeluaran",
                    "Rp15.000",
                    "Parkir & konsumsi",
                    OjolColors.ExpenseText,
                    Modifier.weight(1f),
                    iconTint = OjolColors.ExpenseText
                )
                StatCard(
                    Icons.Filled.CheckCircle,
                    "Jumlah Order",
                    "9 Order",
                    "Semua selesai lancar",
                    OjolColors.PrimaryDeep,
                    Modifier.weight(1f)
                )
            }

            // Aktivitas terbaru
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "Aktivitas Terbaru",
                        style = OjolText.TitleMd.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = OjolColors.TextPrimary
                    )
                    Box(
                        Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(OjolColors.Primary)
                    )
                }

                Row(
                    modifier = Modifier.clickable(onClick = onSeeAll),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        "Lihat Semua",
                        style = OjolText.LabelMd,
                        color = OjolColors.Primary
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = OjolColors.Primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            OjolCard(
                modifier = Modifier.fillMaxWidth(),
                padding = 0.dp
            ) {
                transactions.take(4).forEachIndexed { index, tx ->
                    if (index > 0) {
                        HorizontalDivider(color = OjolColors.Border)
                    }
                    ActivityRow(
                        tx,
                        onClick = {
                            context.toast("${tx.tag}: ${tx.title}")
                        }
                    )
                }
            }

            // Fitur berikutnya
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.RocketLaunch,
                        contentDescription = null,
                        tint = OjolColors.TextVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "Fitur Berikutnya",
                        style = OjolText.TitleMd.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = OjolColors.TextPrimary
                    )
                }

                Text(
                    "Roadmap Driver",
                    style = OjolText.LabelMd,
                    color = OjolColors.TextSecondary
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(OjolColors.GrayBox)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            ) {
                UpcomingRow(
                    Icons.Filled.Analytics,
                    "Analisis Jam Ramai",
                    "Peta rekomendasi order gacor"
                )
                UpcomingRow(
                    Icons.Filled.Groups,
                    "Komunitas Driver Ojol",
                    "Info razia, pangkalan & warung santai"
                )
                UpcomingRow(
                    Icons.Filled.EmojiEvents,
                    "Target Harian & Bonus",
                    "Lacak tupo dan insentif multi-app"
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun MiniStat(
    label: String,
    value: String,
    valueColor: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 10.dp),
    ) {
        Text(
            label,
            style = OjolText.LabelSm.copy(fontSize = 11.sp),
            color = if (valueColor == OjolColors.ExpenseText) {
                OjolColors.ExpenseText
            } else {
                OjolColors.TextVariant
            },
            maxLines = 1
        )
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            style = OjolText.TitleMd.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            ),
            color = valueColor,
            maxLines = 1
        )
    }
}

@Composable
private fun QuickAction(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    OjolCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        padding = 14.dp,
        onClick = onClick
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            IconBox(
                icon,
                bg = iconBg,
                tint = iconTint,
                size = 38.dp,
                radius = 10.dp,
                iconSize = 22.dp
            )
            trailing()
        }

        Spacer(Modifier.height(12.dp))

        Text(
            title,
            style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
            color = OjolColors.TextPrimary,
            maxLines = 1
        )
        Text(
            subtitle,
            style = OjolText.BodySm,
            color = OjolColors.TextVariant,
            maxLines = 1
        )
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    caption: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
    suffix: String = "",
    iconTint: Color = OjolColors.TextVariant,
) {
    OjolCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        padding = 14.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
            Text(
                label,
                style = OjolText.LabelMd,
                color = OjolColors.TextVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                value,
                style = OjolText.HeadlineMd.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = valueColor,
                maxLines = 1
            )
            if (suffix.isNotEmpty()) {
                Text(
                    suffix,
                    style = OjolText.BodySm,
                    color = OjolColors.TextSecondary
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            caption,
            style = OjolText.LabelSm.copy(fontSize = 11.sp),
            color = OjolColors.TextVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ActivityRow(
    tx: Transaction,
    onClick: () -> Unit
) {
    val style = tagStyle(tx.tagKind)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TagIcon(tx.tagKind, size = 44.dp)
        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    tx.tag,
                    style = OjolText.TitleMd.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = OjolColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Pill(
                    style.label.uppercase(),
                    bg = style.bg,
                    fg = style.fg,
                    textSize = 9.sp
                )
            }

            Text(
                "${tx.title} • ${tx.time}",
                style = OjolText.BodySm,
                color = OjolColors.TextVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                tx.subtitle,
                style = OjolText.LabelSm.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = OjolColors.TextVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                formatRupiah(tx.amount, withSign = true),
                style = OjolText.TitleMd.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (tx.isIncome) {
                    OjolColors.Emerald
                } else {
                    OjolColors.ExpenseText
                },
                maxLines = 1,
            )
            Text(
                tx.method,
                style = OjolText.LabelSm.copy(fontSize = 11.sp),
                color = OjolColors.TextVariant
            )
        }
    }
}

@Composable
private fun UpcomingRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                context.toast("$title — segera hadir")
            }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = OjolColors.TextVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                title,
                style = OjolText.LabelLg,
                color = OjolColors.TextPrimary
            )
            Text(
                subtitle,
                style = OjolText.BodySm,
                color = OjolColors.TextVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Pill(
            "Segera Hadir",
            bg = OjolColors.GrayBox2,
            fg = OjolColors.TextVariant,
            textSize = 10.sp
        )
    }
}

// Preview HomeScreen
@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Home Screen Preview"
)
@Composable
fun HomeScreenPreview() {
    val ledgerVm: LedgerViewModel = viewModel()

    HomeScreen(
        ledgerVm = ledgerVm,
        onAddExpense = {},
        onStartShift = {},
        onSeeAll = {},
        onProfile = {}
    )
}