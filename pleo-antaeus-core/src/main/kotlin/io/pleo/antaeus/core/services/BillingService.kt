package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.constants.AwsVpcSla
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.data.AntaeusDal

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val dal: AntaeusDal
) {
    fun chargeAll(): MutableMap<String, MutableList<Int>> {
        val invoices: List<Invoice> = dal.fetchInvoices()
        var response = mutableMapOf(
            "success" to mutableListOf<Int>(),
            "failure" to mutableListOf<Int>())
        for (invoice in invoices) {
            try {
                charge(invoice)
                response["success"]?.add(invoice.id)
            }
            catch(Exception e) {
                response["error"]?.add(invoice.id)
            }
        }
        return response
    }

   fun charge(invoice: Invoice): Boolean {
       var customer: Customer
       customer = dal.fetchCustomer(invoice.customerId) ?: throw CustomerNotFoundException(invoice.customerId)
       if (invoice.amount.currency != customer.currency) {
           throw CurrencyMismatchException(invoice.id, customer.id)
       }
       if (Math.random() > AwsVpcSla) {
           throw NetworkException()
       }
       return true
   }
}