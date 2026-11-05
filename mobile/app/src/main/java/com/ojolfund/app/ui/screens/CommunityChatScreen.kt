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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ojolfund.app.data.ChatMessage
import com.ojolfund.app.data.CommunityData
import com.ojolfund.app.ui.components.DetailHeader
import com.ojolfund.app.ui.components.IconBox
import com.ojolfund.app.ui.components.Pill
import com.ojolfund.app.ui.components.toast
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText
import com.ojolfund.app.viewmodel.CommunityViewModel

/** Detail Komunitas: chat wilayah dengan reaksi, pesan cepat, dan tombol jadikan info pantauan. */
@Composable
fun CommunityChatScreen(vm: CommunityViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val messages by vm.messages.collectAsState()
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex + 1)
    }

    fun send() {
        if (vm.sendMessage(input)) input = "" else context.toast("Tulis pesan dulu")
    }

    Column(Modifier.fillMaxSize().background(OjolColors.Canvas).imePadding()) {
        DetailHeader(
            title = "Detail Komunitas",
            onBack = onBack,
            trailing = {
                Icon(
                    Icons.Filled.Notifications,
                    contentDescription = "Notifikasi",
                    tint = OjolColors.TextVariant,
                    modifier = Modifier.clickable { context.toast("Notifikasi grup dibisukan 1 jam (demo)") }
                )
            }
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    GroupCard()
                    PrivacyCard()
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Pill(
                            "Hari Ini • Pantauan Realtime Jalur",
                            bg = OjolColors.GrayBox2,
                            fg = OjolColors.TextVariant
                        )
                    }
                }
            }
            items(messages, key = { it.id }) { msg ->
                MessageBubble(
                    msg = msg,
                    onAction = {
                        if (vm.promoteToReport(msg.id)) {
                            context.toast("Dijadikan info Papan Pantauan (anonim)")
                        }
                    },
                    onReact = { index -> vm.toggleReaction(msg.id, index) }
                )
            }
        }

        Column(Modifier.background(OjolColors.Card).padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CommunityData.quickReplies.forEach { text ->
                    Text(
                        text,
                        style = OjolText.LabelLg,
                        color = OjolColors.TextPrimary,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(OjolColors.GrayBox)
                            .clickable { input = text }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
            Spacer(Modifier.size(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(OjolColors.GrayBox)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (input.isEmpty()) {
                        Text("Tulis kabar lapangan...", style = OjolText.BodyMd, color = OjolColors.TextSecondary)
                    }
                    BasicTextField(
                        value = input,
                        onValueChange = { input = it },
                        singleLine = true,
                        textStyle = OjolText.BodyMd.copy(color = OjolColors.TextPrimary),
                        cursorBrush = SolidColor(OjolColors.Primary),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { send() }),
                        modifier = Modifier.fillMaxWidth().testTag("chat_input")
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(OjolColors.Primary)
                        .clickable { send() }
                        .testTag("chat_send"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Kirim",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupCard() {
    Surface(shape = RoundedCornerShape(16.dp), color = OjolColors.Card, shadowElevation = 1.dp) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            IconBox(Icons.Filled.Groups, size = 52.dp, radius = 26.dp, bg = OjolColors.Aqua, iconSize = 26.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Paguyuban Mitra Margonda",
                        style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                        color = OjolColors.TextPrimary,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.Filled.Verified,
                        contentDescription = null,
                        tint = OjolColors.Primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text("1.420 Driver On-Bid • Aktif & Ramah", style = OjolText.BodySm, color = OjolColors.TextVariant)
            }
            Icon(
                Icons.Filled.Info,
                contentDescription = "Info grup",
                tint = OjolColors.TextVariant,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun PrivacyCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(OjolColors.AquaSoft)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Shield,
                contentDescription = null,
                tint = OjolColors.PrimaryDeep,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "PRIVASI & MODERASI KOMUNITAS",
                style = OjolText.LabelMd,
                color = OjolColors.PrimaryDeep,
                modifier = Modifier.weight(1f)
            )
            Pill("Anonim", bg = OjolColors.Aqua, fg = OjolColors.PrimaryDeep)
        }
        Spacer(Modifier.size(6.dp))
        Text(
            "Pesan bermanfaat dapat dipromosikan ke Papan Pantauan Bersama oleh AI/Mod secara anonim. " +
                "Bebas hoaks, tanpa bagi data pribadi.",
            style = OjolText.BodySm,
            color = OjolColors.TextPrimary
        )
    }
}

@Composable
private fun MessageBubble(msg: ChatMessage, onAction: () -> Unit, onReact: (Int) -> Unit) {
    val align = if (msg.isMine) Alignment.End else Alignment.Start
    Column(Modifier.fillMaxWidth(), horizontalAlignment = align) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (!msg.isMine) Avatar(msg)
            Column(horizontalAlignment = if (msg.isMine) Alignment.End else Alignment.Start) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(msg.author, style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                    if (msg.isBot) Pill("BOT", bg = OjolColors.Aqua, fg = OjolColors.PrimaryDeep, textSize = 10.sp)
                    Text(msg.time, style = OjolText.BodySm, color = OjolColors.TextSecondary)
                }
            }
        }
        Spacer(Modifier.size(4.dp))
        Column(
            modifier = Modifier
                .padding(start = if (msg.isMine) 40.dp else 0.dp, end = if (msg.isMine) 0.dp else 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    when {
                        msg.isMine -> OjolColors.Primary
                        msg.isBot -> OjolColors.AquaSoft
                        else -> OjolColors.Card
                    }
                )
                .padding(12.dp)
        ) {
            Text(
                msg.text,
                style = OjolText.BodyMd,
                color = if (msg.isMine) Color.White else OjolColors.TextPrimary
            )
            if (msg.aiTags.isNotEmpty() || msg.actionLabel != null) {
                Spacer(Modifier.size(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(OjolColors.GrayBox)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("AI Deteksi Otomatis", style = OjolText.LabelMd, color = OjolColors.TextVariant)
                    if (msg.aiTags.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            msg.aiTags.forEach { tag ->
                                val danger = tag.contains("Macet", ignoreCase = true)
                                Pill(
                                    tag,
                                    bg = if (danger) OjolColors.ExpenseBg else OjolColors.GrayBox2,
                                    fg = if (danger) OjolColors.ExpenseText else OjolColors.TextVariant
                                )
                            }
                        }
                    }
                    if (msg.actionLabel != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (msg.actionDone) OjolColors.GrayBox2 else OjolColors.Aqua)
                                .clickable(enabled = !msg.actionDone, onClick = onAction)
                                .padding(10.dp)
                                .testTag("chat_action_${msg.id}"),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Bolt,
                                contentDescription = null,
                                tint = OjolColors.PrimaryDeep,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                if (msg.actionDone) "Sudah jadi info pantauan ✓" else msg.actionLabel,
                                style = OjolText.LabelLg,
                                color = OjolColors.PrimaryDeep
                            )
                        }
                    }
                }
            }
            if (msg.footnote != null) {
                Spacer(Modifier.size(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Visibility,
                        contentDescription = null,
                        tint = OjolColors.TextVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(msg.footnote, style = OjolText.BodySm, color = OjolColors.TextVariant)
                }
            }
            if (msg.reactions.isNotEmpty()) {
                Spacer(Modifier.size(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    msg.reactions.forEachIndexed { index, r ->
                        Text(
                            "${r.emoji} ${r.label} ${r.count}",
                            style = OjolText.LabelMd,
                            color = if (r.mine) Color.White else OjolColors.TextPrimary,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (r.mine) OjolColors.Primary else OjolColors.GrayBox2)
                                .clickable { onReact(index) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("chat_reaction_${msg.id}_$index")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Avatar(msg: ChatMessage) {
    Box(
        Modifier.size(36.dp).clip(CircleShape).background(if (msg.isBot) OjolColors.Primary else OjolColors.GrayBox2),
        contentAlignment = Alignment.Center
    ) {
        if (msg.isBot) {
            Icon(
                Icons.Filled.SupportAgent,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                msg.author.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase(),
                style = OjolText.LabelMd,
                color = OjolColors.TextVariant
            )
        }
    }
}
