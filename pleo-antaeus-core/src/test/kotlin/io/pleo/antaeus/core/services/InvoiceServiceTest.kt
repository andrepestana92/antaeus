package io.pleo.antaeus.core.services

import setUpTestDal
import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import java.math.BigDecimal
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertThrows
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money

class InvoiceServiceTest {
    private val dal = setUpTestDal()

    private val invoiceService = InvoiceService(dal = dal)

    @Test
    fun `will throw if customer is not found`() {
        assertThrows<InvoiceNotFoundException> {
            invoiceService.fetch(404)
        }
    }

    @Test
    fun `will return one extra invoice after creation`() {
        val totalBefore = invoiceService.fetchAll()
        val mockCustomer = dal.createCustomer(Currency.USD)
        val newInvoice = invoiceService.create(Invoice(
            1, mockCustomer!!.id, Money(BigDecimal(10.00), Currency.USD), InvoiceStatus.PENDING
        ))
        val totalAfter = invoiceService.fetchAll()
        Assertions.assertEquals(totalBefore.size + 1, totalAfter.size)
        dal.deleteCustomer(mockCustomer.id)
        dal.deleteCustomer(newInvoice!!.id)
    }
}