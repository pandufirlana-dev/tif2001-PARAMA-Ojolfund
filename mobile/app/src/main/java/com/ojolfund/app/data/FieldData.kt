package com.ojolfund.app.data

/** Platform mitra untuk pencatatan order beserta komisi bawaannya. */
enum class Platform(val label: String, val commissionPct: Int, val tagKind: TagKind) {
    GORIDE("GoRide", 20, TagKind.GORIDE),
    GRAB("GrabBike", 20, TagKind.GRAB),
    MAXIM("Maxim", 15, TagKind.MAXIM),
    LAINNYA("Lainnya", 10, TagKind.LAINNYA)
}

/** Pilihan komisi aplikasi pada layar Tambah Order. */
data class CommissionOption(val pct: Int, val title: String, val subtitle: String)

val commissionOptions: List<CommissionOption> = listOf(
    CommissionOption(20, "20%", "GoRide / GrabBike"),
    CommissionOption(15, "15%", "Maxim Motor"),
    CommissionOption(10, "10%", "Promo / Kurir"),
    CommissionOption(0, "0% (Bebas)", "Langganan / Tip")
)

/** Jenis BBM dengan harga per liter. [pricePerLiter] = 0 berarti harga diisi sendiri. */
enum class FuelType(val label: String, val ron: String, val pricePerLiter: Long) {
    PERTALITE("Pertalite", "RON 90", 10_000),
    PERTAMAX("Pertamax", "RON 92", 12_950),
    SHELL_SUPER("Shell Super", "RON 92 Swasta", 13_250),
    LAINNYA("Lainnya", "BP / Vivo / Solar", 0)
}

object VehicleData {
    const val NAME = "Honda Vario 160 CBS"
    const val PLATE = "B 4821 TGB"
    const val LAST_ODOMETER = 24_850L
    const val LAST_FILL = "24 Okt • Pertalite"
    const val STANDARD_KM_PER_LITER = 42.0
}

// ------------------------------------------------------------------ Ringkasan & laporan

data class PlatformShare(
    val code: String,
    val name: String,
    val trips: Int,
    val sharePct: Double,
    val net: Long,
    val gross: Long
)

data class DaySummary(
    val dateLabel: String,
    val isToday: Boolean,
    val dailyTarget: Long,
    val gross: Long,
    val commission: Long,
    val operational: Long,
    val orders: Int,
    val cancelled: Int,
    val onBidMinutes: Int,
    val platforms: List<PlatformShare>,
    val fuelNote: String,
    val fuelCost: Long,
    val parkingNote: String,
    val parkingCost: Long,
    val distanceKm: Double,
    val kmPerLiter: Double,
    val shiftName: String,
    val shiftStart: String,
    val shiftEnd: String,
    val restLabel: String,
    val restPlace: String,
    val note: String
) {
    val net: Long get() = gross - commission - operational
    val avgNetPerTrip: Long get() = if (orders == 0) 0 else net / orders
    val netPerHour: Long get() = if (onBidMinutes == 0) 0 else net * 60 / onBidMinutes
    val targetPct: Int get() = if (dailyTarget == 0L) 0 else (net * 100 / dailyTarget).toInt()
    val commissionPct: Double get() = if (gross == 0L) 0.0 else commission * 100.0 / gross
}

object SummaryData {
    val days: List<DaySummary> = listOf(
        DaySummary(
            dateLabel = "Kamis, 24 Okt 2024",
            isToday = true,
            dailyTarget = 100_000,
            gross = 165_000,
            commission = 25_000,
            operational = 15_000,
            orders = 14,
            cancelled = 0,
            onBidMinutes = 315,
            platforms = listOf(
                PlatformShare("GR", "GoRide (Gojek)", 8, 57.6, 72_000, 95_000),
                PlatformShare("GB", "GrabBike (Grab)", 4, 30.4, 38_000, 51_000),
                PlatformShare("MX", "Maxim Motor", 2, 12.0, 15_000, 19_000)
            ),
            fuelNote = "Pertalite SPBU (1,0 Liter)",
            fuelCost = 10_000,
            parkingNote = "Parkir Resto & Mall (3 Titik)",
            parkingCost = 5_000,
            distanceKm = 45.2,
            kmPerLiter = 45.2,
            shiftName = "Shift Pagi",
            shiftStart = "07:30",
            shiftEnd = "12:45",
            restLabel = "30 Mnt",
            restPlace = "Warung Kopi",
            note = "Target Tupo yang tercapai lancar, cuaca mendung bersahabat di area Jaksel & Gandaria."
        ),
        DaySummary(
            dateLabel = "Rabu, 23 Okt 2024",
            isToday = false,
            dailyTarget = 100_000,
            gross = 148_000,
            commission = 22_000,
            operational = 21_000,
            orders = 12,
            cancelled = 1,
            onBidMinutes = 280,
            platforms = listOf(
                PlatformShare("GR", "GoRide (Gojek)", 6, 50.0, 62_000, 81_000),
                PlatformShare("GB", "GrabBike (Grab)", 3, 28.0, 30_000, 40_000),
                PlatformShare("MX", "Maxim Motor", 3, 22.0, 13_000, 27_000)
            ),
            fuelNote = "Pertalite SPBU (1,5 Liter)",
            fuelCost = 15_000,
            parkingNote = "Parkir Stasiun (2 Titik)",
            parkingCost = 6_000,
            distanceKm = 52.0,
            kmPerLiter = 34.7,
            shiftName = "Shift Siang",
            shiftStart = "11:00",
            shiftEnd = "16:40",
            restLabel = "20 Mnt",
            restPlace = "Warung Nasi",
            note = "Satu order dibatalkan penumpang. Jalur Tebet cukup padat setelah jam makan siang."
        )
    )
}

data class ReportBar(val label: String, val gross: Long, val net: Long)

data class PeriodReport(
    val rangeLabel: String,
    val net: Long,
    val deltaLabel: String,
    val statusLabel: String,
    val onBidLabel: String,
    val perHourLabel: String,
    val distanceKm: Int,
    val efficiencyLabel: String,
    val bars: List<ReportBar>,
    val recordLabel: String,
    val recordTag: String
)

object ReportData {
    val tabs = listOf("Mingguan", "Bulanan", "Kustom")

    val periods: List<PeriodReport> = listOf(
        PeriodReport(
            rangeLabel = "14 - 20 Okt 2024",
            net = 1_420_000,
            deltaLabel = "+18% vs mgg lalu",
            statusLabel = "Makin Produktif 🚀 (Target 92% tercapai)",
            onBidLabel = "38,5 Jam",
            perHourLabel = "Rp 36.800/jam kerja",
            distanceKm = 482,
            efficiencyLabel = "Irit ~43,8 km/Liter",
            bars = listOf(
                ReportBar("Sen", 175_000, 140_000),
                ReportBar("Sel", 190_000, 152_000),
                ReportBar("Rab", 160_000, 128_000),
                ReportBar("Kam", 210_000, 168_000),
                ReportBar("Jum", 245_000, 196_000),
                ReportBar("Sab", 310_000, 248_000),
                ReportBar("Min", 130_000, 104_000)
            ),
            recordLabel = "Puncak Rekor: Sabtu (Rp 310.000 Bersih)",
            recordTag = "Tupo Max"
        ),
        PeriodReport(
            rangeLabel = "Oktober 2024",
            net = 5_380_000,
            deltaLabel = "+12% vs bln lalu",
            statusLabel = "Stabil naik 📈 (Target 72% tercapai)",
            onBidLabel = "151 Jam",
            perHourLabel = "Rp 35.600/jam kerja",
            distanceKm = 1_910,
            efficiencyLabel = "Irit ~43,1 km/Liter",
            bars = listOf(
                ReportBar("Mgg 1", 1_150_000, 1_020_000),
                ReportBar("Mgg 2", 1_320_000, 1_180_000),
                ReportBar("Mgg 3", 1_590_000, 1_420_000),
                ReportBar("Mgg 4", 1_410_000, 1_260_000)
            ),
            recordLabel = "Puncak Rekor: Minggu ke-3 (Rp 1.420.000 Bersih)",
            recordTag = "Tupo Max"
        ),
        PeriodReport(
            rangeLabel = "Pilih rentang tanggal",
            net = 2_780_000,
            deltaLabel = "+9% vs periode lalu",
            statusLabel = "Produktif ✅ (Target 80% tercapai)",
            onBidLabel = "76 Jam",
            perHourLabel = "Rp 36.600/jam kerja",
            distanceKm = 940,
            efficiencyLabel = "Irit ~43,4 km/Liter",
            bars = listOf(
                ReportBar("Mgg 1", 1_150_000, 1_020_000),
                ReportBar("Mgg 2", 1_320_000, 1_180_000)
            ),
            recordLabel = "Puncak Rekor: Minggu ke-2 (Rp 1.180.000 Bersih)",
            recordTag = "Tupo Max"
        )
    )

    const val DEPRECIATION_PER_KM = 120L
}

// ------------------------------------------------------------------ Target & cicilan

data class Envelope(val title: String, val amount: Long, val subtitle: String, val status: String)

data class Bill(val name: String, val dueLabel: String, val amount: Long, val statusLabel: String)

// ------------------------------------------------------------------ Komunitas

enum class ReportCategory(val label: String, val filter: String) {
    RAZIA("RAZIA DISHUB & POLISI", "Razia Aparat"),
    GACOR("SPOT GACOR • TINGGI DEMAND", "Spot Gacor"),
    BANJIR("GENANGAN AIR & BANJIR", "Banjir"),
    SPBU("INFO SPBU & BAHAN BAKAR", "SPBU"),
    MACET("MACET & KECELAKAAN", "Macet")
}

data class CommunityReport(
    val id: Int,
    val category: ReportCategory,
    val ageLabel: String,
    val expiresLabel: String,
    val title: String,
    val body: String,
    val tags: List<String> = emptyList(),
    val verifiedNote: String? = null,
    val warning: String? = null,
    val footnote: String? = null,
    val primaryLabel: String,
    val primaryCount: Int? = null,
    val secondaryLabel: String? = null,
    val secondaryCount: Int? = null,
    /** true jika tombol kedua adalah suara (bisa dibatalkan), false jika hanya aksi biasa. */
    val secondaryIsVote: Boolean = true,
    /** 0 = belum memilih, 1 = memilih tombol utama, 2 = memilih tombol kedua. */
    val userVote: Int = 0
)

data class Reaction(val emoji: String, val label: String, val count: Int, val mine: Boolean = false)

data class ChatMessage(
    val id: Int,
    val author: String,
    val badge: String? = null,
    val time: String,
    val text: String,
    val isBot: Boolean = false,
    val isMine: Boolean = false,
    val aiTags: List<String> = emptyList(),
    val actionLabel: String? = null,
    val actionDone: Boolean = false,
    val reactions: List<Reaction> = emptyList(),
    val footnote: String? = null
)

object CommunityData {
    val reports: List<CommunityReport> = listOf(
        CommunityReport(
            id = 1,
            category = ReportCategory.RAZIA,
            ageLabel = "12 mnt lalu",
            expiresLabel = "Habis dlm 48 mnt",
            title = "Razia Surat & Kelayakan Motor",
            body = "Kolong Flyover Pasar Minggu arah Pancoran. Pemeriksaan fokus pada SIM, " +
                "STNK hidup, spion lengkap, dan pelat nomor resmi.",
            verifiedNote = "Terverifikasi 14 Driver Aktif",
            footnote = "Akurasi 94%",
            primaryLabel = "Masih Ada",
            primaryCount = 14,
            secondaryLabel = "Sudah Bubar",
            secondaryCount = 2
        ),
        CommunityReport(
            id = 2,
            category = ReportCategory.GACOR,
            ageLabel = "25 mnt lalu",
            expiresLabel = "Habis dlm 1j 35m",
            title = "Titik Gacor Orderan Food & Ride Siang",
            body = "Cilandak Town Square & Area RS Fatmawati. Puluhan resto ramai order " +
                "GoFood & GrabFood, durasi jemput singkat di lobi barat.",
            tags = listOf("GoFood +40%", "GrabFood Ramai", "Parkir Mitra Luas"),
            primaryLabel = "Valid / Ikutan Melipir",
            primaryCount = 28
        ),
        CommunityReport(
            id = 3,
            category = ReportCategory.BANJIR,
            ageLabel = "40 mnt lalu",
            expiresLabel = "Habis dlm 1j 20m",
            title = "Genangan Air 25 cm - Depan Stasiun Tebet",
            body = "Jalur motor tersendat akibat limpasan hujan. Motor matic rendah rawan " +
                "mogok filter udara. Disarankan ambil jalur layang Casablanca / " +
                "Saharjo.",
            warning = "3 motor matic dilaporkan mati mesin di jalur paling kiri. Tetap di " +
                "lajur tengah jika terpaksa melintas.",
            primaryLabel = "Bantu Pantau",
            primaryCount = 9,
            secondaryLabel = "Lihat Jalur Alternatif",
            secondaryIsVote = false
        ),
        CommunityReport(
            id = 4,
            category = ReportCategory.SPBU,
            ageLabel = "1 jam lalu",
            expiresLabel = "Habis dlm 15 mnt",
            title = "SPBU Margonda 34-12401 - Antrean Pertalite Mengular",
            body = "Antrean roda dua mencapai 10-15 motor di kedua nozzle. Pertamax sedang " +
                "restock tanki truk tangki. Mitra disarankan isi di SPBU Pondok Cina.",
            footnote = "Estimasi menunggu: 12-18 Menit",
            primaryLabel = "Kondisi Masih Sama",
            primaryCount = null
        )
    )

    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            id = 1,
            author = "Kang Asep",
            badge = "GJK",
            time = "13:42",
            text = "Info lur, jalur TB Simatupang arah Cilandak macet total ada truk mogok " +
                "di lajur tengah. Antrean udah lewat flyover Antasari.",
            aiTags = listOf("Macet Parah", "TB Simatupang"),
            actionLabel = "Jadikan Info Pantauan Jalan (Anonim)"
        ),
        ChatMessage(
            id = 2,
            author = "OjolFund Radar AI",
            badge = "BOT",
            time = "13:43",
            text = "📢 Laporan Otomatis: Jalur TB Simatupang arah Cilandak dimasukkan ke " +
                "Papan Pantauan Publik. Driver sekitar menerima saran re-route otomatis " +
                "ke Jl. Fatmawati.",
            isBot = true,
            footnote = "418 driver terdekat terinfo"
        ),
        ChatMessage(
            id = 3,
            author = "Bang Joko",
            badge = "GRB",
            time = "13:48",
            text = "SPBU Tanjung Barat Pertalite lancar jaya ya, nggak antre sama sekali. " +
                "Yang mau ngisi monggo mampir sekalian rehat mushola adem.",
            aiTags = listOf("Stok Pertalite Tersedia (Sepi)"),
            actionLabel = "Jadikan Info SPBU"
        ),
        ChatMessage(
            id = 4,
            author = "Rian Maxim",
            badge = "MXM",
            time = "13:52",
            text = "Hujan mulai lebat daerah Kemang, yang lewat Antasari hati-hati " +
                "genangan depan pom bensin. Siapin jas hujan rekan-rekan! 🌧️🛵",
            reactions = listOf(
                Reaction("🙏", "Siap lur", 14),
                Reaction("🌧️", "Neduh", 8)
            )
        )
    )

    val quickReplies = listOf("Macet Parah", "Hujan Lebat", "Cek SPBU")
}
