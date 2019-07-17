package io.pleo.antaeus.core.services

import setUpTestDal
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertThrows
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.models.Currency

class CustomerServiceTest {
    private val dal = setUpTestDal()

    private val customerService = CustomerService(dal = dal)

    @Test
    fun `will throw if customer is not found`() {
        assertThrows<CustomerNotFoundException> {
            customerService.fetch(404)
        }
    }

    @Test
    fun `will return one extra customer after creation`() {
        val totalBefore = customerService.fetchAll()
        val newCustomer = customerService.create(Customer(
            1, Currency.USD
        ))
        val totalAfter = customerService.fetchAll()
        Assertions.assertEquals(totalBefore.size + 1, totalAfter.size)
        dal.deleteCustomer(newCustomer!!.id)
    }
    
}