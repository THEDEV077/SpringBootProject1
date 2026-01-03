# SpringBootProject1

E-Commerce Backend API built with Spring Boot. This application provides REST APIs for an e-commerce platform with separate interfaces for buyers and sellers.

## Features

- **Product Management**: CRUD operations for products
- **Vendor Dashboard**: Sellers can manage their products, view reviews, and track statistics
- **Shopping Cart**: Add, update, and remove items from cart
- **Product Reviews**: Buyers can rate and review products
- **Category Management**: Organize products by categories
- **Image Management**: Support for multiple product images
- **API Documentation**: Swagger/OpenAPI integration

## Tech Stack

- Java 17
- Spring Boot 3.2.1
- Spring Data JPA
- Spring Security
- PostgreSQL (Production)
- H2 Database (Testing)
- OpenAPI/Swagger for API documentation
- Lombok for reducing boilerplate code

## Database Configuration

Set the following environment variables for database connection:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/jeeProject
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
```

Or use application.properties with your local settings:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/jeeProject
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_URL` | Database connection URL | `jdbc:postgresql://localhost:5432/jeeProject` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | _(empty)_ |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | `http://localhost:5173,http://localhost:3000` |
| `DEMO_VENDOR_ID` | Demo vendor ID for testing | `1` |
| `DEMO_USER_ID` | Demo user ID for testing | `1` |

## Building and Running

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

### Build

```bash
./mvnw clean install
```

### Run

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### Testing

```bash
./mvnw test
```

## API Documentation

Once the application is running, access the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

API docs in JSON format:

```
http://localhost:8080/api-docs
```

## API Endpoints

### Product Endpoints (Public)

- `GET /api/produits` - Get all products
- `GET /api/produits/{id}` - Get product by ID
- `GET /api/produits/{id}/reviews` - Get product reviews
- `POST /api/produits/{id}/reviews` - Add review to product

### Vendor Endpoints

- `GET /api/vendeur/produits` - Get vendor's products
- `POST /api/vendeur/produits` - Add new product
- `PUT /api/vendeur/produits/{id}` - Update product
- `DELETE /api/vendeur/produits/{id}` - Delete product
- `GET /api/vendeur/produits/{id}/stats` - Get product statistics

### Cart Endpoints

- `GET /api/panier` - Get cart items
- `POST /api/panier/add/{productId}` - Add item to cart
- `PUT /api/panier/{cartItemId}` - Update cart item quantity
- `DELETE /api/panier/{cartItemId}` - Remove item from cart

## Frontend Repository

The React frontend for this project is available at:
[https://github.com/THEDEV077/reactProject1](https://github.com/THEDEV077/reactProject1)

## Development Notes

- The application currently uses demo user IDs for testing purposes
- Authentication and authorization are planned for future releases
- CORS is configured to allow requests from the React frontend

## CSV Data Loading

This application can automatically load product data from a CSV file on startup (currently disabled).

### CSV Format

The CSV file should contain the following columns:
- ASIN (Amazon Standard Identification Number)
- Category
- Product Link
- No of Sellers
- Rank
- Rating
- Reviews Count
- Price
- Category flags (Books, Camera & Photo, Clothing/Shoes/Jewelry, Electronics, Gift Cards, Toys & Games, Video Games)
- Product Title

## License

MIT License
