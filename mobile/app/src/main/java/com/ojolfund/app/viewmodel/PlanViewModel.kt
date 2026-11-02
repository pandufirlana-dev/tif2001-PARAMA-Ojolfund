package com.ojolfund.app.viewmodel

import androidx.lifecycle.ViewModel
import com.ojolfund.app.data.Bill
import com.ojolfund.app.data.Envelope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PlanUiState(
    val monthlyTarget: Long = 4_500_000,
    val monthlyEarned: Long = 3_250_000,
    val workDaysLeft: Int = 9,
    val envelopes: List<Envelope> = listOf(
        Envelope("Operasional Besok", 35_000, "BBM & Makan", "Terisi"),
        Envelope("Cicilan Motor", 30_000, "Harian FIF", "Auto-Debit"),
        Envelope("Servis & Ban", 10_000, "Ganti oli & kampas", "Aman"),
        Envelope("Kas Bersih Dapur", 65_000, "Siap transfer istri", "Bisa Tarik")
    ),
    val leasingName: String = "FIF - Honda Vario 160",
    val leasingAmount: Long = 850_000,
    val leasingDueLabel: String = "Jatuh Tempo: 30 Okt 2024",
    val leasingDaysLeft: Int = 6,
    val leasingDailySetAside: Long = 850_000,
    val leasingPaid: Boolean = false,
    val bills: List<Bill> = listOf(
        Bill("BPJS Ketenagakerjaan BPU", "Jatuh Tempo: 05 Nov 2024", 16_800, "Tersisihkan"),
        Bill("Cicilan HP Narik (Kredivo)", "Sisa 2 Bulan • 12 Nov", 220_000, "Terkumpul 60%")
    ),
    val emergencyFund: Long = 1_850_000,
    val emergencyTarget: Long = 3_000_000,
    val message: String? = null
) {
    val remaining: Long get() = (monthlyTarget - monthlyEarned).coerceAtLeast(0)
    val targetProgress: Float get() = (monthlyEarned.toFloat() / monthlyTarget).coerceIn(0f, 1f)
    val targetPct: Int get() = (targetProgress * PERCENT).toInt()
    val neededPerDay: Long get() = if (workDaysLeft == 0) remaining else remaining / workDaysLeft
    val fundProgress: Float get() = (emergencyFund.toFloat() / emergencyTarget).coerceIn(0f, 1f)

    /** Indikator kesiapan (demo): dasar 85%, naik bila cicilan utama sudah lunas. */
    val readinessPct: Int get() = if (leasingPaid) READY_PAID else READY_BASE

    /** Berapa hari biaya hidup yang tertutup dana darurat (asumsi Rp154.000/hari). */
    val fundCoverDays: Int get() = (emergencyFund / DAILY_LIVING_COST).toInt()

    private companion object {
        const val PERCENT = 100
        const val READY_BASE = 85
        const val READY_PAID = 92
        const val DAILY_LIVING_COST = 154_000L
    }
}

/** Target bulanan, amplop, cicilan, dan dana darurat (in-memory, tanpa backend). */
class PlanViewModel : ViewModel() {

    private val _state = MutableStateFlow(PlanUiState())
    val state: StateFlow<PlanUiState> = _state.asStateFlow()

    fun payLeasing() = _state.update {
        if (it.leasingPaid) {
            it
        } else {
            it.copy(
                leasingPaid = true,
                message = "Cicilan leasing dibayar. Alhamdulillah lunas!"
            )
        }
    }

    fun addSavings(amount: Long) = _state.update {
        if (amount <= 0) {
            it.copy(message = "Nominal harus lebih dari 0")
        } else {
            it.copy(emergencyFund = it.emergencyFund + amount, message = "Dana darurat bertambah")
        }
    }

    fun withdraw(amount: Long) = _state.update {
        when {
            amount <= 0 -> it.copy(message = "Nominal harus lebih dari 0")
            amount > it.emergencyFund -> it.copy(message = "Saldo dana darurat tidak cukup")
            else -> it.copy(emergencyFund = it.emergencyFund - amount, message = "Dana ditarik dari kas darurat")
        }
    }

    fun addBill(name: String, amount: Long) = _state.update {
        if (name.isBlank() || amount <= 0) {
            it.copy(message = "Isi nama tagihan dan nominalnya")
        } else {
            it.copy(
                bills = it.bills + Bill(name.trim(), "Pos baru", amount, "Belum terkumpul"),
                message = "Pos tagihan ditambahkan"
            )
        }
    }

    fun consumeMessage() = _state.update { it.copy(message = null) }
}
