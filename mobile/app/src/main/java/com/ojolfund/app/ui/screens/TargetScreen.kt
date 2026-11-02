package com.ojolfund.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ojolfund.app.data.Bill
import com.ojolfund.app.data.Envelope
import com.ojolfund.app.data.formatNumber
import com.ojolfund.app.data.formatRupiah
import com.ojolfund.app.ui.components.AmountDialog
import com.ojolfund.app.ui.components.AmountField
import com.ojolfund.app.ui.components.DetailHeader
import com.ojolfund.app.ui.components.IconBox
import com.ojolfund.app.ui.components.OjolCard
import com.ojolfund.app.ui.components.OjolTextField
import com.ojolfund.app.ui.components.Pill
import com.ojolfund.app.ui.components.PrimaryButton
import com.ojolfund.app.ui.components.ProgressLine
import com.ojolfund.app.ui.components.SoftButton
import com.ojolfund.app.ui.components.toast
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText
import com.ojolfund.app.viewmodel.PlanViewModel

private enum class PlanDialog { NONE, SAVE, WITHDRAW, BILL }

/** Target & Pos Keuangan: target bulanan, amplop digital, cicilan leasing, tagihan, dan dana darurat. */
@Composable
fun TargetScreen(vm: PlanViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val state by vm.state.collectAsState()
    var dialog by remember { mutableStateOf(PlanDialog.NONE) }

    LaunchedEffect(state.message) {
        state.message?.let {
            context.toast(it)
            vm.consumeMessage()
        }
    }

    Column(Modifier.fillMaxSize().background(OjolColors.Canvas)) {
        DetailHeader(
            title = "Target & Pos Keuangan",
            subtitle = "Kelola Tupo, Cicilan Leasing & Pos Dana Darurat",
            onBack = onBack,
            trailing = {
                Pill("Finansial Aman", bg = OjolColors.Aqua, fg = OjolColors.PrimaryDeep, icon = Icons.Filled.Shield)
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OjolCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBox(Icons.Filled.HealthAndSafety, size = 44.dp, radius = 12.dp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Kesiapan Cicilan Bulan Ini", style = OjolText.TitleMd, color = OjolColors.TextPrimary)
                        Text(
                            if (state.leasingPaid) {
                                "Cicilan utama sudah lunas bulan ini"
                            } else {
                                "Sisa 1 pos cicilan lagi menuju aman"
                            },
                            style = OjolText.BodySm,
                            color = OjolColors.TextVariant
                        )
                    }
                    Text(
                        "${state.readinessPct}%",
                        style = OjolText.TitleLg,
                        color = OjolColors.Primary,
                        modifier = Modifier.testTag("plan_readiness")
                    )
                }
            }

            MonthlyTargetCard(
                earned = state.monthlyEarned,
                target = state.monthlyTarget,
                pct = state.targetPct,
                progress = state.targetProgress,
                daysLeft = state.workDaysLeft,
                remaining = state.remaining,
                perDay = state.neededPerDay
            )

            Text("Amplop Digital Otomatis", style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold))
            Text(
                "Cuan trip harian disisihkan tanpa bikin pusing",
                style = OjolText.BodySm,
                color = OjolColors.TextVariant
            )
            state.envelopes.chunked(2).forEachIndexed { rowIndex, pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    pair.forEachIndexed { i, env ->
                        EnvelopeCard(env, envelopeIcons[(rowIndex * 2 + i) % envelopeIcons.size], Modifier.weight(1f))
                    }
                }
            }

            LeasingCard(
                name = state.leasingName,
                due = state.leasingDueLabel,
                daysLeft = state.leasingDaysLeft,
                amount = state.leasingAmount,
                dailySetAside = state.leasingDailySetAside,
                paid = state.leasingPaid,
                onPay = vm::payLeasing
            )

            BillsCard(bills = state.bills, onAdd = { dialog = PlanDialog.BILL })

            EmergencyCard(
                fund = state.emergencyFund,
                target = state.emergencyTarget,
                progress = state.fundProgress,
                coverDays = state.fundCoverDays,
                onAdd = { dialog = PlanDialog.SAVE },
                onWithdraw = { dialog = PlanDialog.WITHDRAW }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(OjolColors.GrayBox)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconBox(Icons.Filled.Tune, size = 40.dp, radius = 10.dp, bg = OjolColors.GrayBox2)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Widget Layar Kunci Aktif", style = OjolText.TitleMd, color = OjolColors.TextPrimary)
                    Text(
                        "Pantau sisa target Tupo langsung dari holder motor.",
                        style = OjolText.BodySm,
                        color = OjolColors.TextVariant
                    )
                }
                Text(
                    "Pasang",
                    style = OjolText.LabelLg,
                    color = OjolColors.Primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .clickableNoRipple { context.toast("Widget layar kunci (demo)") }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }

    when (dialog) {
        PlanDialog.SAVE -> AmountDialog(
            title = "Tambah Simpanan",
            confirmLabel = "Simpan",
            hint = "Nominal masuk ke Kas Dana Darurat.",
            onDismiss = { dialog = PlanDialog.NONE },
            onConfirm = {
                vm.addSavings(it)
                dialog = PlanDialog.NONE
            }
        )
        PlanDialog.WITHDRAW -> AmountDialog(
            title = "Tarik Dana",
            confirmLabel = "Tarik",
            hint = "Saldo tersedia ${formatRupiah(state.emergencyFund)}.",
            onDismiss = { dialog = PlanDialog.NONE },
            onConfirm = {
                vm.withdraw(it)
                dialog = PlanDialog.NONE
            }
        )
        PlanDialog.BILL -> AddBillDialog(
            onDismiss = { dialog = PlanDialog.NONE },
            onConfirm = { name, amount ->
                vm.addBill(name, amount)
                dialog = PlanDialog.NONE
            }
        )
        PlanDialog.NONE -> Unit
    }
}

private val envelopeIcons = listOf(
    Icons.Filled.LocalGasStation,
    Icons.Filled.Payments,
    Icons.Filled.Build,
    Icons.Filled.Home
)

@Composable
private fun MonthlyTargetCard(
    earned: Long,
    target: Long,
    pct: Int,
    progress: Float,
    daysLeft: Int,
    remaining: Long,
    perDay: Long
) {
    OjolCard(padding = 18.dp) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text("TARGET CUAN BULAN INI", style = OjolText.LabelMd, color = OjolColors.TextVariant)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "Rp " + formatNumber(earned),
                        style = OjolText.HeadlineLg.copy(fontSize = 26.sp, fontWeight = FontWeight.Bold),
                        color = OjolColors.TextPrimary
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("/ Rp ${formatDecimalJuta(target)}", style = OjolText.BodySm, color = OjolColors.TextVariant)
                }
            }
            Pill("$pct% Tercapai", bg = OjolColors.Aqua, fg = OjolColors.PrimaryDeep)
        }
        Spacer(Modifier.height(10.dp))
        ProgressLine(progress = progress, color = OjolColors.Primary)
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = OjolColors.TextVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text("Sisa $daysLeft hari kerja", style = OjolText.BodySm, color = OjolColors.TextVariant)
            }
            Text("Kurang Rp ${formatNumber(remaining)}", style = OjolText.LabelMd, color = OjolColors.Primary)
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(OjolColors.GrayBox)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.TwoWheeler,
                contentDescription = null,
                tint = OjolColors.Primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "Butuh Rp ${formatNumber(perDay)} / hari untuk tembus target bulanan. Gas tipis-tipis!",
                style = OjolText.BodySm,
                color = OjolColors.TextPrimary
            )
        }
        Spacer(Modifier.height(14.dp))
        Text("PROGRES INSENTIF & BONUS MITRA", style = OjolText.LabelMd, color = OjolColors.TextVariant)
        Spacer(Modifier.height(6.dp))
        IncentiveRow(
            Icons.Filled.Flag,
            "Gojek Jawara",
            "Mingguan",
            "120 / 150 Poin (Sisa 30 Poin)",
            "+Rp 150.000",
            "Reward Tupo"
        )
        IncentiveRow(
            Icons.Filled.EmojiEvents,
            "Grab Jam Sibuk",
            "Hari Ini",
            "32 / 40 Trip (Sisa 8 Trip)",
            "+Rp 80.000",
            "Bonus Target"
        )
    }
}

private fun formatDecimalJuta(value: Long): String =
    com.ojolfund.app.data.formatDecimal(value / 1_000_000.0, 1).replace(",0", "") + " jt"

@Composable
private fun IncentiveRow(
    icon: ImageVector,
    title: String,
    tag: String,
    progress: String,
    reward: String,
    rewardNote: String
) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBox(
            icon,
            size = 36.dp,
            radius = 10.dp,
            bg = OjolColors.GrayBox2,
            tint = OjolColors.TextVariant,
            iconSize = 20.dp
        )
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                Spacer(Modifier.width(6.dp))
                Pill(tag, bg = OjolColors.GrayBox2, fg = OjolColors.TextVariant, textSize = 10.sp)
            }
            Text(progress, style = OjolText.BodySm, color = OjolColors.TextVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(reward, style = OjolText.TitleMd, color = OjolColors.Primary)
            Text(rewardNote, style = OjolText.BodySm, color = OjolColors.TextVariant)
        }
    }
}

@Composable
private fun EnvelopeCard(env: Envelope, icon: ImageVector, modifier: Modifier = Modifier) {
    OjolCard(modifier = modifier, padding = 14.dp) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconBox(
                icon,
                size = 32.dp,
                radius = 8.dp,
                bg = OjolColors.GrayBox2,
                tint = OjolColors.TextVariant,
                iconSize = 18.dp
            )
            Spacer(Modifier.weight(1f))
            Pill(env.status, bg = OjolColors.Aqua, fg = OjolColors.PrimaryDeep, textSize = 10.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text(env.title, style = OjolText.BodySm, color = OjolColors.TextVariant)
        Text(
            "Rp ${formatNumber(env.amount)}",
            style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
            color = OjolColors.Primary
        )
        Text(env.subtitle, style = OjolText.BodySm.copy(fontSize = 11.sp), color = OjolColors.TextVariant)
    }
}

@Composable
private fun LeasingCard(
    name: String,
    due: String,
    daysLeft: Int,
    amount: Long,
    dailySetAside: Long,
    paid: Boolean,
    onPay: () -> Unit
) {
    OjolCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.TwoWheeler,
                contentDescription = null,
                tint = OjolColors.Primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Cicilan Leasing Utama",
                style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            Pill(
                if (paid) "Lunas ✓" else "Tersisa $daysLeft Hari",
                bg = OjolColors.Aqua,
                fg = OjolColors.PrimaryDeep
            )
        }
        Spacer(Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(OjolColors.GrayBox)
                .padding(14.dp)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text(name, style = OjolText.TitleMd, color = OjolColors.TextPrimary)
                    Text(due, style = OjolText.BodySm, color = OjolColors.TextVariant)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Rp ${formatNumber(amount)}", style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold))
                    Text(
                        if (paid) "✔ Sudah dibayar" else "✔ 100% Siap Bayar",
                        style = OjolText.LabelMd,
                        color = OjolColors.Primary
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            ProgressLine(progress = 1f, color = OjolColors.Primary)
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "Celengan Harian: Rp ${formatNumber(dailySetAside)}",
                    style = OjolText.BodySm,
                    color = OjolColors.TextVariant
                )
                Text("Target Lunas", style = OjolText.BodySm, color = OjolColors.TextVariant)
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PrimaryButton(
                text = if (paid) "Sudah Dibayar" else "Bayar Sekarang",
                onClick = onPay,
                enabled = !paid,
                leadingIcon = Icons.Filled.AccountBalanceWallet,
                modifier = Modifier.weight(1.4f).testTag("plan_pay"),
                height = 48.dp
            )
            SoftButton(
                text = "Tandai Lunas",
                onClick = onPay,
                modifier = Modifier.weight(1f),
                fg = OjolColors.TextPrimary
            )
        }
    }
}

@Composable
private fun BillsCard(bills: List<Bill>, onAdd: () -> Unit) {
    OjolCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "TAGIHAN PENDUKUNG DRIVER",
                style = OjolText.LabelMd,
                color = OjolColors.TextVariant,
                modifier = Modifier.weight(1f)
            )
            Row(
                modifier = Modifier.clickableNoRipple(onAdd).testTag("plan_add_bill"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null,
                    tint = OjolColors.Primary,
                    modifier = Modifier.size(16.dp)
                )
                Text("Tambah Pos", style = OjolText.LabelMd, color = OjolColors.Primary)
            }
        }
        Spacer(Modifier.height(8.dp))
        bills.forEach { bill ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconBox(
                    Icons.Filled.AddCircleOutline,
                    size = 36.dp,
                    radius = 10.dp,
                    bg = OjolColors.Aqua,
                    iconSize = 20.dp
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(bill.name, style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                    Text(bill.dueLabel, style = OjolText.BodySm, color = OjolColors.TextVariant)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Rp ${formatNumber(bill.amount)}", style = OjolText.TitleMd, color = OjolColors.TextPrimary)
                    Text(bill.statusLabel, style = OjolText.LabelMd, color = OjolColors.Primary)
                }
            }
        }
    }
}

@Composable
private fun EmergencyCard(
    fund: Long,
    target: Long,
    progress: Float,
    coverDays: Int,
    onAdd: () -> Unit,
    onWithdraw: () -> Unit
) {
    OjolCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBox(Icons.Filled.Savings, size = 44.dp, radius = 22.dp, bg = OjolColors.Aqua)
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Kas Dana Darurat", style = OjolText.TitleLg.copy(fontWeight = FontWeight.Bold))
                Text(
                    "Bantalan aman saat mogok atau sepi order",
                    style = OjolText.BodySm,
                    color = OjolColors.TextVariant
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(OjolColors.GrayBox)
                .padding(14.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Total Terkumpul", style = OjolText.BodySm, color = OjolColors.TextVariant)
                    Text(
                        "Rp ${formatNumber(fund)}",
                        style = OjolText.HeadlineMd.copy(fontWeight = FontWeight.Bold),
                        color = OjolColors.Primary,
                        modifier = Modifier.testTag("plan_fund")
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Target Minimal", style = OjolText.BodySm, color = OjolColors.TextVariant)
                    Text("Rp ${formatNumber(target)}", style = OjolText.TitleMd)
                }
            }
            Spacer(Modifier.height(8.dp))
            ProgressLine(progress = progress, color = OjolColors.Primary)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = OjolColors.Primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Cukup untuk $coverDays hari biaya hidup jika mendadak sakit atau servis besar.",
                    style = OjolText.BodySm,
                    color = OjolColors.TextVariant
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PrimaryButton(
                text = "Tambah Simpanan",
                onClick = onAdd,
                leadingIcon = Icons.Filled.Add,
                modifier = Modifier.weight(1.3f).testTag("plan_add_savings"),
                height = 48.dp
            )
            SoftButton(
                text = "Tarik Dana",
                onClick = onWithdraw,
                modifier = Modifier.weight(1f),
                fg = OjolColors.TextPrimary
            )
        }
    }
}

@Composable
private fun AddBillDialog(onDismiss: () -> Unit, onConfirm: (String, Long) -> Unit) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableLongStateOf(0L) }
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = Color.White) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Tambah Pos Tagihan", style = OjolText.TitleLg, color = OjolColors.TextPrimary)
                OjolTextField(
                    label = "Nama Tagihan",
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Contoh: Pulsa & Paket Data"
                )
                Text("Nominal per Bulan", style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                AmountField(value = amount, onValueChange = { amount = it })
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SoftButton(
                        text = "Batal",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        fg = OjolColors.TextVariant
                    )
                    PrimaryButton(
                        text = "Tambah",
                        onClick = { onConfirm(name, amount) },
                        modifier = Modifier.weight(1f),
                        height = 48.dp
                    )
                }
            }
        }
    }
}

/** Klik tanpa efek ripple agar teks kecil tidak terlihat berat. */
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.then(
    Modifier.clickable(onClick = onClick)
)
