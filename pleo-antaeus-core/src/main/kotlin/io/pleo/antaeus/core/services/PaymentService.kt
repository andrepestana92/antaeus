package io.pleo.antaeus.core.services

import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.constants.AwsVpcSla
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.models.Invoice

class PaymentService : PaymentProvider {
    
    override fun charge(invoice: Invoice, dal: AntaeusDal): Boolean {
        var customer: Customer
        customer = dal.fetchCustomer(invoice.customerId) ?: throw CustomerNotFoundException(invoice.customerId)
        if (invoice.amount.currency != customer.currency) throw CurrencyMismatchException(invoice.id, customer.id)

        if (Math.random() > AwsVpcSla) throw NetworkException()
        return true
    }
}