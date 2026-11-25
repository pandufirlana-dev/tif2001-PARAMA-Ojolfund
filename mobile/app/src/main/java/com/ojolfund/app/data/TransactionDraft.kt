package com.ojolfund.app.data

/** Isian form Catat/Ubah Transaksi sebelum dijadikan [Transaction]. Nominal selalu positif; arah lewat [isIncome]. */
data class TransactionDraft(
    val isIncome: Boolean = false,
    val amount: Long = 0L,
    val category: TagKind = TagKind.BBM,
    val title: String = "",
    val note: String = "",
    val method: String = paymentMethods.first()
)

val paymentMethods: List<String> = listOf("Tunai", "Dompet", "QRIS")

val incomeCategories: List<TagKind> = listOf(TagKind.GORIDE, TagKind.GRAB, TagKind.MAXIM, TagKind.LAINNYA)

val expenseCategories: List<TagKind> = listOf(TagKind.BBM, TagKind.PARKIR, TagKind.MAKAN, TagKind.LAINNYA)

/** Nama kategori untuk chip filter, form, dan detail. */
val TagKind.categoryLabel: String
    get() = when (this) {
        TagKind.GORIDE -> "GoRide"
        TagKind.GRAB -> "GrabBike"
        TagKind.MAXIM -> "Maxim"
        TagKind.BBM -> "BBM"
        TagKind.PARKIR -> "Parkir"
        TagKind.MAKAN -> "Makan"
        TagKind.LAINNYA -> "Lainnya"
    }

/** Mengubah transaksi yang sudah ada menjadi isian form (untuk layar Ubah). */
fun Transaction.toDraft(): TransactionDraft = TransactionDraft(
    isIncome = isIncome,
    amount = kotlin.math.abs(amount),
    category = tagKind,
    title = title,
    note = subtitle,
    method = method
)
