## Process

Here is the process begind this solution, listed by implementation order

- Understand Kotolin language
- Creating Exceptions rules for the `charge` method inside the BillingService class
- Getting all invoices and calling the `charge` method without any validation
- Creating the response object returned in the `chargeAll` function
- Creating two endpoint to register an Invoice and a Customer
- Learn how to parse the body from the POST request into a object from type Invoice or Customer
- Manually testing the Customer and Invoice creation endpoints
- Create the enpoint to charge all the invoices and testing it
- Creating an endpoint to charge a single invoice and testing it
- Chaging the invoice status to PAID and implementing the `already_paid` key from the payment endpoint response to indicate which invoices were already paid before the function call
- Moving the implementation of the `charge` method to the PaymentProvider interface and calling it via `paymentProvider` argument received in the `BillingService` class
- Creating the `PaymentService` class and moved the implementation of the method `charge` to this new service
- Trying to refactor the `charge` method in the `PaymentService` class so it wouldn't receive the `dal` argument needed to get the customer from the invoice.
- Studying and creating unit test for the scenario where nothing goes wrong when charging all the invoices
- Studying how to create a test database
- Using the `Random` class as an argument in the `charge` method so I could mock it in the BillingService tests
- Creating the rest of the unit tests for BillingService, InvoiceService and CustomerService

## Endpoints

For this challenge, I've created four new endpoints:

- GET /rest/v1/pay: Pay all the invoices

- GET /rest/v1/pay/{:id}: Pay a single invoice of od {:id}

- POST /rest/v1/customers: Create a new customer

  ```json
  {
  	"currency": "USD"
  }
  ```

  

- POST /rest/v1/invoices: Create a new invoice. The body of this request is a json in the format:

  ```json
  {
  	"amount": {
  		"value": 10.00,
  		"currency": "USD"
  	},
  	"customerId": 100,
  	"status": "PENDING"
  }
  ```



## Time spent

To finish this project, it took me four to five hours on Sunday to understand the current code, create the basic BillingService class and the endpoints for creating a new Invoice and Customer.

On Monday, it took me about three hours to create both payment endpoints, refactor the `charge` method and create the first basic test for the BillingService. It was in this last refactor that I created the PaymentService class

On Tuesday, it also took me about three hours to create the testing database model, finish the tests for BillingService, start using the `Random` object as an argument in the `charge` method, create the CustomerService and InvoiceService tests and finally, write this README