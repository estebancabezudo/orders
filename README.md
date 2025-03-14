# Create a Spring Boot application that exposes a REST API

## Run the server
To run the server, we first need to start the database and Redis. To do this, simply clone the repository and run the command to start the container.

```sudo docker-compose up --build```

Then we can run the JAR.

```java -jar orders-1.0.jar```

## Exposed Services

After starting the server, the first thing we need to do is obtain the access token.    

```shell
curl --location 'localhost:8080/login' \
--header 'Content-Type: application/json' \
  --header 'Cookie: webClientData=1' \
--data '{
"username": "username",
"password": "password"
}'
```
This will return an object from which we can extract the token for future calls.
```json
{
  "message":"Login successfully",
  "code":"OK",
  "data":{
    "token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSJ9.2fVmxtdLwTtI_cRffjxKuYqvUgMn6zAA1CGObM5tdOE",
    "type":"Bearer"
  }
}
```

### Placing an order `POST /orders`
```shell
curl --location 'localhost:8080/orders' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSJ9.2fVmxtdLwTtI_cRffjxKuYqvUgMn6zAA1CGObM5tdOE' \
--data '{
    "customerId": 1,
    "productId": 1,
    "quantity": 10,
    "price": 10.4
}'
```
### Retrieving an order by ID `GET /orders/{orderId}`
```shell
curl --location 'localhost:8080/orders/1' \
--header 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSJ9.2fVmxtdLwTtI_cRffjxKuYqvUgMn6zAA1CGObM5tdOE'
```
### Updating an order status `PATCH /orders/{orderId}/status`
```shell
curl --location --request PATCH 'localhost:8080/orders/1/status' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSJ9.2fVmxtdLwTtI_cRffjxKuYqvUgMn6zAA1CGObM5tdOE' \
--data '{ "status": "SHIPPED" }'
```
### Fetching orders by customer ID `GET /orders/customer/{customerId}`
```shell
curl --location 'localhost:8080/orders/customer/1' \
--header 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSJ9.2fVmxtdLwTtI_cRffjxKuYqvUgMn6zAA1CGObM5tdOE'
```
## Business Logic & Order Processing
### Implement an **OrderService** that handles:
#### Order validation (e.g., checking product availability).
The method `net.cabezudo.orders.OrderService.save()` performs product availability validation by calling a third-party web service.
#### Storing orders in a relational database (PostgreSQL or MySQL).
We use JPA and PostgreSQL to store orders.
#### Updating order status (e.g., "Pending" → "Shipped" → "Delivered").
When performing a `PATCH`, the system validates the order of the status update and throws an exception if it is incorrect.
#### Integrating with a **Commerce Tools API** (or a mock service) to fetch product details before confirming an order.
We use a third-party web service to fetch product details each time it is needed, utilizing a cache to reduce load.
## Data Persistence
### Use **Spring Data JPA** to manage order persistence.
### Design an **Order Entity** with fields: `id`, `customerId`, `productId`, `quantity`, `price`, `status`, `createdAt`, `updatedAt`.
The entity can be found in `net.cabezudo.orders.persistence.OrderEntity`
#### Ensure database migrations using **Flyway** or **Liquibase**.
We chose Liquibase because it is the technology we are most familiar with.
## Security & Exception Handling
### Secure the APIs using **Spring Security** with JWT authentication.
We implemented a simple and insecure token, which I wouldn't use in production. The generated token uses a hardcoded key for the hash, and we don't use symmetric keys. We use an external service to obtain the username and password, represented by `net.cabezudo.security.AuthenticationWebService`.
### Implement proper **exception handling** (e.g., `OrderNotFoundException`, `InvalidOrderException`).
We use a global exception handler `net.cabezudo.orders.GlobalExceptionHandler` for common exceptions across all controllers and `org.springframework.web.bind.annotation.ExceptionHandler` for those directly related to the controller.
## Caching & Performance Optimization (Bonus)
### Use **Redis** or **Ehcache** to cache frequently accessed order details.
We use Redis for caching because it is the technology we know best.

## Notes
* The service is structured into three layers: presentation (REST), business (Services), and persistence (Repositories).
* We use different objects for the three layers and an object that performs the conversion (mapping) between the different objects of the layers. This way, the view layer only shows the information relevant to the view in the format and with the values needed for the client. The business object can have the information and operations needed for the business, and the persistence object represents the data in the database in the required format.
* The view and persistence objects are transfer objects that only hold data in the necessary format, and in the case of persistence, inform the database on how to handle the data.
* The business objects representing lists are not collections because there may be a need to encapsulate operations with the list that cannot be performed with collections.
* The product service `net.cabezudo.products.ProductService` is an example of an external connection with caching, which can serve as an example of calling other microservices. The object returned by the web service is fixed and does not have any logic.
* The unit tests only cover the requested web services and some exceptions but are far from what I personally expect. They are only there to facilitate the smoke tests I needed for development.