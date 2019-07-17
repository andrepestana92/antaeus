## Process

Here is the process begind this solution, listed by implementation order

- Understand Kotolin language
- Creating Exceptions rules for the charge method inside the BillingService class
- Getting all invoices and calling the charge method without any validation
- Creating the response object returned in the chargeAll function
- Creating two endpoint to register an Invoice and a Customer
- Learn how to parse the body from the POST request into a object from type Invoice or Customer
- Manually testing the Customer and Invoice creation endpoints
- Create the enpoint to charge all the invoices and testing it
- Creating an endpoint to charge a single invoice and testing it
- Moving the implementation of the charge method to the PaymentProvider interface and calling it via paymentProvider argument received in the BillingService class
- Creating the PaymentService class and moved the implementation of the method charge to this new service
- Trying to refactor the charge method in thePaymentService class so it would not receive the dal argument needed to get the customer from the invoice.
- Studying and creating unit test for the scenario where nothing goes wrong when charging all the invoices
- Studying how to create a test database
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



### Structure
The code given is structured as follows. Feel free however to modify the structure to fit your needs.
```
├── pleo-antaeus-app
|
|       Packages containing the main() application. 
|       This is where all the dependencies are instantiated.
|
├── pleo-antaeus-core
|
|       This is where you will introduce most of your new code.
|       Pay attention to the PaymentProvider and BillingService class.
|
├── pleo-antaeus-data
|
|       Module interfacing with the database. Contains the models, mappings and access layer.
|
├── pleo-antaeus-models
|
|       Definition of models used throughout the application.
|
├── pleo-antaeus-rest
|
|        Entry point for REST API. This is where the routes are defined.
└──
```

## Instructions
Fork this repo with your solution. We want to see your progression through commits (don’t commit the entire solution in 1 step) and don't forget to create a README.md to explain your thought process.

Please let us know how long the challenge takes you. We're not looking for how speedy or lengthy you are. It's just really to give us a clearer idea of what you've produced in the time you decided to take. Feel free to go as big or as small as you want.

Happy hacking 😁!

## How to run
```
./docker-start.sh
```

## Libraries currently in use
* [Exposed](https://github.com/JetBrains/Exposed) - DSL for type-safe SQL
* [Javalin](https://javalin.io/) - Simple web framework (for REST)
* [kotlin-logging](https://github.com/MicroUtils/kotlin-logging) - Simple logging framework for Kotlin
* [JUnit 5](https://junit.org/junit5/) - Testing framework
* [Mockk](https://mockk.io/) - Mocking library
