package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.data.AntaeusDal
import kotlin.random.Random

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val dal: AntaeusDal,
    private val rng: Random
) {
    fun chargeAll(): MutableMap<String, MutableList<Int>> {
        val invoices: List<Invoice> = dal.fetchInvoices()
        var response = mutableMapOf(
            "success" to mutableListOf<Int>(),
            "failure" to mutableListOf<Int>(),
            "already_paid" to mutableListOf<Int>())
        for (invoice in invoices) {
            if (invoice.status == InvoiceStatus.PENDING) {
                if (chargeInvoice(invoice)) {
                    dal.setPaidInvoice(invoice)
                    response["success"]?.add(invoice.id)
                }
                else response["failure"]?.add(invoice.id)
            }
            else if (invoice.status == InvoiceStatus.PAID) {
                response["already_paid"]?.add(invoice.id)
            }
        }
        return response
    }

    fun chargeOne(id: Int): Map<String, Int> {
        val invoice: Invoice = dal.fetchInvoice(id) ?: throw InvoiceNotFoundException(id)
        if (invoice.status == InvoiceStatus.PAID) return mapOf("already_paid" to id)
        if (chargeInvoice(invoice)) {
            dal.setPaidInvoice(invoice)
            return mapOf("success" to id)
        }
        else return mapOf("failure" to id)
    }

    fun chargeInvoice(invoice: Invoice): Boolean {
        try {
            if (paymentProvider.charge(invoice, dal, rng)) return true
            else return false
        }
        catch(e: Exception) {
            return false
        }
    }
}