package com.ojolfund.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ojolfund.app.data.CommunityReport
import com.ojolfund.app.data.ReportCategory
import com.ojolfund.app.ui.components.ChoiceChip
import com.ojolfund.app.ui.components.DetailHeader
import com.ojolfund.app.ui.components.OjolCard
import com.ojolfund.app.ui.components.OjolTextField
import com.ojolfund.app.ui.components.Pill
import com.ojolfund.app.ui.components.PrimaryButton
import com.ojolfund.app.ui.components.SoftButton
import com.ojolfund.app.ui.components.StaggerIn
import com.ojolfund.app.ui.components.toast
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText
import com.ojolfund.app.viewmodel.CommunityViewModel

/** Papan Pantauan Jalan: laporan anonim komunitas (razia, spot gacor, banjir, SPBU) + lapor cepat. */
@Composable
fun CommunityScreen(vm: CommunityViewModel, onBack: () -> Unit, onOpenChat: () -> Unit) {
    val context = LocalContext.current
    val reports by vm.reports.collectAsState()
    var filter by remember { mutableStateOf<ReportCategory?>(null) }
    var showReport by remember { mutableStateOf(false) }

    val visible = reports.filter { filter == null || it.category == filter }

    Box(Modifier.fillMaxSize().background(OjolColors.Canvas)) {
        Column(Modifier.fillMaxSize()) {
            DetailHeader(
                title = "Komunitas Driver",
                subtitle = "Info razia, pangkalan & warung santai",
                onBack = onBack,
                trailing = { Pill("OFFLINE SIAP", bg = OjolColors.Aqua, fg = OjolColors.PrimaryDeep, dot = true) }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TabSwitch(onChat = onOpenChat)

                OjolCard(color = OjolColors.AquaSoft, borderColor = OjolColors.Aqua) {
                    Text("RADAR LAPANGAN AKTIF", style = OjolText.LabelMd, color = OjolColors.PrimaryDeep)
                    Text("Pantauan Radius 5 KM", style = OjolText.TitleLg, color = OjolColors.TextPrimary)
                    Text(
                        "Margonda Raya & Lenteng Agung sekitarnya",
                        style = OjolText.BodySm,
                        color = OjolColors.TextVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(6.dp).clip(CircleShape).background(OjolColors.Primary))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Info diperbarui 3 menit lalu oleh 42 mitra",
                            style = OjolText.BodySm,
                            color = OjolColors.TextVariant
                        )
                    }
                }

                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ChoiceChip(
                        text = "Semua  ${reports.size}",
                        selected = filter == null,
                        onClick = { filter = null },
                        modifier = Modifier.testTag("community_filter_all")
                    )
                    ReportCategory.entries.forEach { cat ->
                        ChoiceChip(text = cat.filter, selected = filter == cat, onClick = { filter = cat })
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(OjolColors.GrayBox2)
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Filled.Shield,
                        contentDescription = null,
                        tint = OjolColors.Primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Semua laporan diproses secara anonim tanpa membuka identitas pengemudi demi privasi dan keamanan bersama.",
                        style = OjolText.BodySm,
                        color = OjolColors.TextVariant
                    )
                }

                if (visible.isEmpty()) {
                    Text(
                        "Belum ada laporan untuk kategori ini.",
                        style = OjolText.BodyMd,
                        color = OjolColors.TextVariant,
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    )
                }
                visible.forEachIndexed { index, report ->
                    StaggerIn(index) {
                        ReportCard(
                            report = report,
                            onPrimary = { vm.vote(report.id, true) },
                            onSecondary = {
                                if (report.secondaryIsVote) {
                                    vm.vote(
                                        report.id,
                                        false
                                    )
                                } else {
                                    context.toast("Membuka jalur alternatif (demo)")
                                }
                            }
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
                .clip(CircleShape)
                .background(OjolColors.Primary)
                .clickable { showReport = true }
                .padding(horizontal = 22.dp, vertical = 14.dp)
                .testTag("community_fab"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(6.dp))
            Text("Lapor Cepat Jalan", style = OjolText.LabelLg.copy(fontSize = 15.sp), color = Color.White)
        }
    }

    if (showReport) {
        QuickReportDialog(
            onDismiss = { showReport = false },
            onSubmit = { category, title, body ->
                if (vm.addReport(category, title, body)) {
                    showReport = false
                    context.toast("Laporan terkirim secara anonim")
                } else {
                    context.toast("Isi judul laporan dulu")
                }
            }
        )
    }
}

@Composable
private fun TabSwitch(onChat: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(OjolColors.GrayBox2)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(11.dp))
                .background(Color.White)
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Navigation,
                contentDescription = null,
                tint = OjolColors.Primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text("Papan Pantauan", style = OjolText.LabelLg, color = OjolColors.Primary)
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(11.dp))
                .clickable(onClick = onChat)
                .padding(vertical = 10.dp)
                .testTag("community_tab_chat"),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Chat,
                contentDescription = null,
                tint = OjolColors.TextVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text("Chat Wilayah", style = OjolText.LabelLg, color = OjolColors.TextVariant)
            Spacer(Modifier.width(6.dp))
            Pill("38+", bg = OjolColors.Aqua, fg = OjolColors.PrimaryDeep, textSize = 10.sp)
        }
    }
}

@Composable
private fun ReportCard(report: CommunityReport, onPrimary: () -> Unit, onSecondary: () -> Unit) {
    val isRazia = report.category == ReportCategory.RAZIA
    OjolCard(modifier = Modifier.testTag("report_${report.id}")) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Pill(
                report.category.label,
                bg = if (isRazia) OjolColors.ExpenseBg else OjolColors.Aqua,
                fg = if (isRazia) OjolColors.ExpenseText else OjolColors.PrimaryDeep,
                textSize = 10.sp
            )
            Spacer(Modifier.weight(1f))
            Pill(
                report.expiresLabel,
                bg = OjolColors.GrayBox2,
                fg = OjolColors.TextVariant,
                icon = Icons.Filled.Schedule,
                textSize = 10.sp
            )
        }
        Text(report.ageLabel, style = OjolText.BodySm, color = OjolColors.TextSecondary)
        Spacer(Modifier.height(4.dp))
        Text(report.title, style = OjolText.TitleLg.copy(fontWeight = FontWeight.Bold), color = OjolColors.TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text(report.body, style = OjolText.BodyMd, color = OjolColors.TextVariant)

        if (report.tags.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                report.tags.forEach { Pill(it, bg = OjolColors.GrayBox2, fg = OjolColors.TextVariant) }
            }
        }
        if (report.warning != null) {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(OjolColors.WarningBg)
                    .padding(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = null,
                    tint = OjolColors.ExpenseText,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(report.warning, style = OjolText.BodySm, color = OjolColors.TextPrimary)
            }
        }
        if (report.verifiedNote != null) {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(OjolColors.GrayBox)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = OjolColors.Primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    report.verifiedNote,
                    style = OjolText.LabelMd,
                    color = OjolColors.TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                if (report.footnote != null) {
                    Text(report.footnote, style = OjolText.LabelMd, color = OjolColors.TextVariant)
                }
            }
        } else if (report.footnote != null) {
            Spacer(Modifier.height(6.dp))
            Text(report.footnote, style = OjolText.LabelMd, color = OjolColors.TextPrimary)
        }

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            VoteButton(
                text = report.primaryLabel + (report.primaryCount?.let { " ($it)" } ?: ""),
                selected = report.userVote == 1,
                danger = isRazia,
                onClick = onPrimary,
                modifier = Modifier.weight(1f).testTag("report_${report.id}_primary")
            )
            if (report.secondaryLabel != null) {
                VoteButton(
                    text = report.secondaryLabel + (report.secondaryCount?.let { " ($it)" } ?: ""),
                    selected = report.userVote == 2,
                    danger = false,
                    onClick = onSecondary,
                    outlined = true,
                    modifier = Modifier.weight(1f).testTag("report_${report.id}_secondary")
                )
            }
        }
    }
}

@Composable
private fun VoteButton(
    text: String,
    selected: Boolean,
    danger: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    outlined: Boolean = false
) {
    val activeColor = if (danger) OjolColors.ShiftRed else OjolColors.Primary
    val bg = when {
        selected -> activeColor
        outlined -> OjolColors.GrayBox
        else -> activeColor.copy(alpha = 0.12f)
    }
    val fg = when {
        selected -> Color.White
        outlined -> OjolColors.TextPrimary
        else -> activeColor
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = OjolText.LabelLg, color = fg, maxLines = 2)
    }
}

@Composable
private fun QuickReportDialog(onDismiss: () -> Unit, onSubmit: (ReportCategory, String, String) -> Unit) {
    var category by remember { mutableStateOf(ReportCategory.MACET) }
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = Color.White) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Lapor Cepat Jalan", style = OjolText.TitleLg, color = OjolColors.TextPrimary)
                Text(
                    "Laporan dikirim anonim tanpa nama dan lokasi pribadi.",
                    style = OjolText.BodySm,
                    color = OjolColors.TextVariant
                )
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReportCategory.entries.forEach { cat ->
                        ChoiceChip(text = cat.filter, selected = category == cat, onClick = { category = cat })
                    }
                }
                OjolTextField(
                    label = "Judul Laporan",
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "Contoh: Macet total depan Stasiun",
                    testTag = "report_title"
                )
                OjolTextField(
                    label = "Keterangan (Opsional)",
                    value = body,
                    onValueChange = { body = it },
                    placeholder = "Detail lokasi, kondisi, saran jalur",
                    testTag = "report_body"
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SoftButton(
                        text = "Batal",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        fg = OjolColors.TextVariant
                    )
                    PrimaryButton(
                        text = "Kirim",
                        onClick = { onSubmit(category, title, body) },
                        modifier = Modifier.weight(1f).testTag("report_submit"),
                        height = 48.dp
                    )
                }
            }
        }
    }
}
