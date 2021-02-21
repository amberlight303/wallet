[![Build Status](https://www.travis-ci.com/amberlight303/wallet.svg?branch=master)](https://www.travis-ci.com/amberlight303/wallet)

#Wallet

It's a simple java REST demo web app that implements wallet features. 

## Technologies in use

- Java 11
- Spring Boot
- Spring Data JPA
- H2 (embedded, disk storage)
- Lombok
- Log4j2
- jUnit
- Mockito
- Jacoco
- Maven
- Travis CI
- Docker

## Description

#### Features
The demo app implements wallet features.

Player can:

- Retrieve a current wallet's state
- Retrieve all wallet transactions
- Withdraw funds from a wallet
- Add funds to a wallet

All transactions have unique IDs.

#### Database
The app automatically initializes H2 db with tables and data. There are 3 tables: players, wallets, transactions. 
`data.sql` and `schema.sql` are picked up automatically on start. Data is saved across app restarts.
The persistence layer is done using Spring Data `CrudRepository` that provides a domain specific languages for 
making requests to the database.

#### Logging
The app writes logs via Log4j2 in stdout. Per each request the custom `ThreadLogContextFilter` filter adds 
automatically to each log record:
- *`RID`* (request ID based on UUID)
- *`IP`* (client's IP address)
- *`HOST`* (server name and port)

#### Exception handling
There is `ExceptionHandlerControllerAdvice` that handles exceptions. There are custom basic exceptions: 
`BusinessLogicException` and `ServerException`. `ExceptionHandlerControllerAdvice` responds with HTTP 450 to 
`BusinessLogicException` and to all inheritors by default. HTTP 500 is for `Exception` and `ServerException`.

#### Testing

The app's controller and the business logic class are tested. Jacoco plugin creates a report and 
shows code coverage.

### Services
___
#### 1) GET: /wallet/state?playerId=1
Response: HTTP 200 OK

    {
        "id": 1,
        "balance": 100.00
    }
___
#### 2) GET: /wallet/transactions?walletId=1
Response: HTTP 200 OK

    [
        {
            "id": "2417a710-27b1-4cf0-9ecb-34c81c5df715",
            "amount": 50.00,
            "date": "2021-02-01T11:00:00.000+00:00"
        },
        {
            "id": "34df047f-0500-4933-9718-374f62dc32b8",
            "amount": 50.00,
            "date": "2021-02-01T10:00:00.000+00:00"
        }
    ]
___
##### 3) POST: /wallet/debit
Request body:

    {
        "id": "1ef3c7e4-2d31-4198-b7dd-e72034cfa639",
        "walletId": 1,
        "amount": 150.52
    }


Response: HTTP 204 No Content
___
##### 4) POST: /wallet/credit
Request body:

    {
        "id": "87b8afe1-469d-42ed-a5ab-4a8494519dcb",
        "walletId": 1,
        "amount": 150.52
    }

Response: HTTP 204 No Content
___
## Deployment

```
docker-compose -f docker-compose.yml up -d
```
After the app starts, it will take requests on the default `8585` port.