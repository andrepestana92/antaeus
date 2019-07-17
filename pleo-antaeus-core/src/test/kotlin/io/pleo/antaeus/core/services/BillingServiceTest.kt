package io.pleo.antaeus.core.services

import setUpTestDal
import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.external.PaymentProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertThrows
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.models.Money
import java.math.BigDecimal
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import kotlin.random.Random


class BillingServiceTest {
    private val dal = setUpTestDal()
    private val paymentProvider = PaymentService()
    private val rng = Random
    

    @Test
    fun `will return a valid json with a successfull charge`() {
        val mockCustomer = dal.createCustomer(Currency.USD)
        val mockInvoice = dal.createInvoice(
            Money(BigDecimal(10.00), Currency.USD), mockCustomer!!, InvoiceStatus.PENDING
        )
        val rng_mock = mockk<Random> {
            every { nextDouble() } returns 0.0
            every { nextBoolean() } returns true
        }
        val billingService = BillingService(paymentProvider, dal, rng_mock)
        val expected = mutableMapOf(
            "success" to mutableListOf<Int>(),
            "failure" to mutableListOf<Int>(), 
            "already_paid" to mutableListOf<Int>()
        )
        expected["success"]?.add(1)
        val result = billingService.chargeAll()
        Assertions.assertEquals(expected, result)
        dal.deleteCustomer(mockCustomer!!.id)
        dal.deleteInvoice(mockInvoice!!.id)
    }

    @Test
    fun `will trow error for diferent currency`() {
        val mockCustomer = dal.createCustomer(Currency.EUR)
        val mockInvoice = dal.createInvoice(
            Money(BigDecimal(10.00), Currency.USD), mockCustomer!!, InvoiceStatus.PENDING
        )
        val expected = mutableMapOf(
            "success" to mutableListOf<Int>(),
            "failure" to mutableListOf<Int>(), 
            "already_paid" to mutableListOf<Int>()
        )
        expected["failure"]?.add(1)

        val billingService = BillingService(paymentProvider, dal, rng)
        val result = billingService.chargeAll()
        Assertions.assertEquals(expected, result)
        assertThrows<CurrencyMismatchException> {
            paymentProvider.charge(mockInvoice!!, dal, rng)
        }
        dal.deleteCustomer(mockCustomer!!.id)
        dal.deleteInvoice(mockInvoice!!.id)
    }

    @Test
    fun `will trow error for when user doesnt exist`() {
        val mockInvoice = dal.saveInvoice(Invoice(
        1, 1, Money(BigDecimal(10.00), Currency.USD), InvoiceStatus.PENDING
        ))
        val expected = mutableMapOf(
            "success" to mutableListOf<Int>(),
            "failure" to mutableListOf<Int>(), 
            "already_paid" to mutableListOf<Int>()
        )
        expected["failure"]?.add(1)
        
        val billingService = BillingService(paymentProvider, dal, rng)
        val result = billingService.chargeAll()
        Assertions.assertEquals(expected, result)
        assertThrows<CustomerNotFoundException> {
            paymentProvider.charge(mockInvoice!!, dal, rng)
        }
        dal.deleteInvoice(mockInvoice!!.id)
    }

    @Test
    fun `will trow error for when invoice doesnt exist`() {
        val billingService = BillingService(paymentProvider, dal, rng)
        assertThrows<InvoiceNotFoundException> {
            billingService.chargeOne(1)
        }
    }

    @Test
    fun `will trow error for network error`() {
        val mockCustomer = dal.createCustomer(Currency.USD)
        val mockInvoice = dal.createInvoice(
            Money(BigDecimal(10.00), Currency.USD), mockCustomer!!, InvoiceStatus.PENDING
        )
        val rng_mock = mockk<Random> {
            every { nextDouble() } returns 1.0
        }
        val expected = mutableMapOf(
            "success" to mutableListOf<Int>(),
            "failure" to mutableListOf<Int>(), 
            "already_paid" to mutableListOf<Int>()
        )
        expected["failure"]?.add(1)
        
        val billingService = BillingService(paymentProvider, dal, rng_mock)
        val result = billingService.chargeAll()
        Assertions.assertEquals(expected, result)
        assertThrows<NetworkException> {
            paymentProvider.charge(mockInvoice!!, dal, rng_mock)
        }
        dal.deleteCustomer((mockCustomer!!.id))
        dal.deleteInvoice(mockInvoice!!.id)
    }

    @Test
    fun `will return invoice already paid`() {
        val mockCustomer = dal.createCustomer(Currency.USD)
        val mockInvoice = dal.createInvoice(
            Money(BigDecimal(10.00), Currency.USD), mockCustomer!!, InvoiceStatus.PAID
        )
        val expected = mutableMapOf(
            "success" to mutableListOf<Int>(),
            "failure" to mutableListOf<Int>(), 
            "already_paid" to mutableListOf<Int>()
        )
        expected["already_paid"]?.add(1)
        
        val billingService = BillingService(paymentProvider, dal, rng)
        val result = billingService.chargeAll()
        Assertions.assertEquals(expected, result)
        dal.deleteInvoice(mockInvoice!!.id)
        dal.deleteCustomer(mockCustomer.id)
    }
}