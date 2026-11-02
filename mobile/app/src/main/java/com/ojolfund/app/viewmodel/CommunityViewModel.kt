package com.ojolfund.app.viewmodel

import androidx.lifecycle.ViewModel
import com.ojolfund.app.data.ChatMessage
import com.ojolfund.app.data.CommunityData
import com.ojolfund.app.data.CommunityReport
import com.ojolfund.app.data.ReportCategory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Papan pantauan & chat komunitas (in-memory; belum terhubung backend). */
class CommunityViewModel : ViewModel() {

    private val _reports = MutableStateFlow(CommunityData.reports)
    val reports: StateFlow<List<CommunityReport>> = _reports.asStateFlow()

    private val _messages = MutableStateFlow(CommunityData.messages)
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    /** Memilih / membatalkan suara pada laporan. [primary] true = tombol utama. */
    fun vote(reportId: Int, primary: Boolean) = _reports.update { list ->
        list.map { r ->
            if (r.id != reportId) {
                r
            } else {
                val target = if (primary) 1 else 2
                val newVote = if (r.userVote == target) 0 else target
                r.copy(
                    userVote = newVote,
                    primaryCount = r.primaryCount?.let {
                        it + delta(r.userVote, newVote, 1)
                    },
                    secondaryCount = r.secondaryCount?.let {
                        it + delta(r.userVote, newVote, 2)
                    }
                )
            }
        }
    }

    /** +1 jika pilihan baru = [slot], -1 jika pilihan lama = [slot], 0 jika tidak berubah. */
    private fun delta(old: Int, new: Int, slot: Int): Int = (if (new == slot) 1 else 0) - (if (old == slot) 1 else 0)

    fun addReport(category: ReportCategory, title: String, body: String): Boolean {
        if (title.isBlank()) return false
        val report = CommunityReport(
            id = (_reports.value.maxOfOrNull { it.id } ?: 0) + 1,
            category = category,
            ageLabel = "Baru saja",
            expiresLabel = "Habis dlm 2 jam",
            title = title.trim(),
            body = body.trim().ifBlank { "Dilaporkan anonim oleh mitra di lapangan." },
            verifiedNote = "Menunggu verifikasi driver lain",
            primaryLabel = "Valid",
            primaryCount = 1,
            userVote = 1
        )
        _reports.update { listOf(report) + it }
        return true
    }

    fun sendMessage(text: String): Boolean {
        val clean = text.trim()
        if (clean.isEmpty()) return false
        val msg = ChatMessage(
            id = nextMessageId(),
            author = "Kamu",
            time = nowLabel(),
            text = clean,
            isMine = true
        )
        _messages.update { it + msg }
        return true
    }

    fun toggleReaction(messageId: Int, index: Int) = _messages.update { list ->
        list.map { m ->
            if (m.id != messageId) {
                m
            } else {
                m.copy(
                    reactions = m.reactions.mapIndexed { i, r ->
                        if (i != index) r else r.copy(mine = !r.mine, count = r.count + if (r.mine) -1 else 1)
                    }
                )
            }
        }
    }

    /** Menjadikan pesan chat sebagai laporan di Papan Pantauan (anonim). */
    fun promoteToReport(messageId: Int): Boolean {
        val msg = _messages.value.firstOrNull { it.id == messageId } ?: return false
        if (msg.actionDone) return false
        val category = when {
            msg.aiTags.any { it.contains("SPBU", true) || it.contains("Pertalite", true) } -> ReportCategory.SPBU
            else -> ReportCategory.MACET
        }
        addReport(category, msg.aiTags.joinToString(" • ").ifBlank { "Info dari komunitas" }, msg.text)
        _messages.update { list -> list.map { if (it.id == messageId) it.copy(actionDone = true) else it } }
        return true
    }

    private fun nextMessageId(): Int = (_messages.value.maxOfOrNull { it.id } ?: 0) + 1

    private fun nowLabel(): String = SimpleDateFormat("HH:mm", Locale.forLanguageTag("id-ID")).format(Date())
}
