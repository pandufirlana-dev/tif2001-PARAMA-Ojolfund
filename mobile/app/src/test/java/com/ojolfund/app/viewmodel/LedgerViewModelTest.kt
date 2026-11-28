package com.ojolfund.app.viewmodel

import com.ojolfund.app.data.SampleData
import com.ojolfund.app.data.TagKind
import com.ojolfund.app.data.TransactionDraft
import com.ojolfund.app.data.toDraft
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Unit test JVM lokal untuk operasi tambah, ubah, dan hapus transaksi di [LedgerViewModel] (data di memori). */
class LedgerViewModelTest {

    private val baseCount = SampleData.transactions.size

    @Test
    fun addExpenseIsStoredAsNegativeAndShownFirst() {
        val vm = LedgerViewModel()

        vm.addTransaction(TransactionDraft(isIncome = false, amount = 25_000L, category = TagKind.PARKIR))

        val list = vm.transactions.value
        assertEquals(baseCount + 1, list.size)
        assertEquals(-25_000L, list.first().amount)
        assertFalse(list.first().isIncome)
        assertEquals(TagKind.PARKIR, list.first().tagKind)
    }

    @Test
    fun addIncomeIsStoredAsPositive() {
        val vm = LedgerViewModel()

        vm.addTransaction(TransactionDraft(isIncome = true, amount = 18_000L, category = TagKind.GRAB))

        assertEquals(18_000L, vm.transactions.value.first().amount)
        assertTrue(vm.transactions.value.first().isIncome)
    }

    @Test
    fun blankTitleFallsBackToCategoryLabel() {
        val vm = LedgerViewModel()

        vm.addTransaction(TransactionDraft(amount = 5_000L, category = TagKind.MAKAN, title = "   "))

        assertEquals("Makan", vm.transactions.value.first().title)
    }

    @Test
    fun addTransactionGivesNewUniqueId() {
        val vm = LedgerViewModel()

        vm.addTransaction(TransactionDraft(amount = 1_000L))
        vm.addTransaction(TransactionDraft(amount = 2_000L))

        val ids = vm.transactions.value.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun updateChangesOnlyTargetTransaction() {
        val vm = LedgerViewModel()
        val target = vm.transactions.value.first()
        val others = vm.transactions.value.drop(1)

        vm.updateTransaction(target.id, target.toDraft().copy(amount = 99_000L, title = "Diubah"))

        val expectedAmount = if (target.isIncome) 99_000L else -99_000L
        val updated = vm.transactions.value.first { it.id == target.id }
        assertEquals("Diubah", updated.title)
        assertEquals(expectedAmount, updated.amount)
        assertEquals(target.time, updated.time)
        assertEquals(others, vm.transactions.value.filter { it.id != target.id })
    }

    @Test
    fun updateKeepsTagWhenCategoryUnchanged() {
        val vm = LedgerViewModel()
        val target = vm.transactions.value.first()

        vm.updateTransaction(target.id, target.toDraft().copy(note = "Catatan baru"))

        val updated = vm.transactions.value.first { it.id == target.id }
        assertEquals(target.tag, updated.tag)
        assertEquals("Catatan baru", updated.subtitle)
    }

    @Test
    fun updateCanSwitchFromExpenseToIncome() {
        val vm = LedgerViewModel()
        vm.addTransaction(TransactionDraft(isIncome = false, amount = 10_000L, category = TagKind.BBM))
        val added = vm.transactions.value.first()

        vm.updateTransaction(
            added.id,
            TransactionDraft(isIncome = true, amount = 10_000L, category = TagKind.MAXIM)
        )

        val updated = vm.transactions.value.first { it.id == added.id }
        assertTrue(updated.isIncome)
        assertEquals(TagKind.MAXIM, updated.tagKind)
    }

    @Test
    fun updateWithUnknownIdChangesNothing() {
        val vm = LedgerViewModel()
        val before = vm.transactions.value

        vm.updateTransaction(-1, TransactionDraft(amount = 1_000L))

        assertEquals(before, vm.transactions.value)
    }

    @Test
    fun deleteRemovesOnlyTargetTransaction() {
        val vm = LedgerViewModel()
        val target = vm.transactions.value.first()

        vm.deleteTransaction(target.id)

        assertEquals(baseCount - 1, vm.transactions.value.size)
        assertTrue(vm.transactions.value.none { it.id == target.id })
    }

    @Test
    fun deleteWithUnknownIdChangesNothing() {
        val vm = LedgerViewModel()

        vm.deleteTransaction(-1)

        assertEquals(baseCount, vm.transactions.value.size)
    }

    @Test
    fun toDraftKeepsAmountPositiveAndDirection() {
        val expense = SampleData.transactions.first { !it.isIncome }

        val draft = expense.toDraft()

        assertEquals(-expense.amount, draft.amount)
        assertFalse(draft.isIncome)
        assertEquals(expense.tagKind, draft.category)
    }
}
