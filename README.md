# Bank Account

Project is implemented using Java 17 Spring Boot framework, Maven and lombok.

# Problem Statement
1. We need to implement a solution that is able to track the balance of a bank account supporting credits, debits and balance enquiries.  
1. This system has an audit requirement where the last 1000 transactions need to be sent to a downstream audit system.  
1. This audit system requires transactions to be sent in batches however the total value of each batch is limited.  
1. We are charged for each batch sent therefore to save costs must minimize the number of batches sent.  
1. The expectation is that the solution will have the following key components:
   * Producer which is responsible for generating transactions 
   * Balance Tracker which is responsible for:
     * Processing the transactions
     * Tracking the balance resulting from the transactions
     * Publishing batches of balances to an audit system

# Solution

## The solution consists of three main entities with specific capabilities:
* Transaction 
* Submission 
* Batch


## Below are the main components of the system
* TransactionProducerService 
  * Responsible for generating Credit & Debit Transactions (25 each/second)
* BankAccountService
  * Process transaction
  * Create batches that are later sent to AuditSystem
* AuditSystem
  * Print Batches
