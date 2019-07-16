package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.just
import io.mockk.Runs
import io.mockk.mockk
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.AntaeusDal
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.models.Money
import java.math.BigDecimal


class BillingServiceTest {
    private val mock_invoice = Invoice(
        1, 1, Money(BigDecimal(10.00), Currency.USD), InvoiceStatus.PENDING
    )
    private val mock_customer = Customer(
        1, Currency.USD
    )
    private val dal = mockk<AntaeusDal> {
        every { fetchCustomer(404) } returns null
        every {fetchInvoice(404)} returns null
        every { fetchInvoices()} returns listOf(mock_invoice)
        every { setPaidInvoice(mock_invoice)} just Runs
    }

    @Test
    fun `will return a valid json with a successfull charge`() {
        val mockPaymentProvider = mockk<PaymentProvider>{
            every { charge(mock_invoice, dal)} returns true
        }
        val billingService = BillingService(mockPaymentProvider, dal)
        val expected = mutableMapOf(
            "success" to mutableListOf<Int>(), 
            "failure" to mutableListOf<Int>(), 
            "already_paid" to mutableListOf<Int>()
        )
        expected["success"]?.add(1)
        val result = billingService.chargeAll()
        Assertions.assertEquals(expected, result)
    }
}