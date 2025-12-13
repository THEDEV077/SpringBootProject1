# SpringBootProject1

## Database Configuration

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/jee_ventes_db
spring.datasource.username=root
spring.datasource.password=Yahya123.
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## CSV Data Loading

This application automatically loads product data from a CSV file on startup. 

### How it works

- The CSV file is located at `src/main/resources/Cleaned1..csv`
- Products are automatically loaded into the database when the application starts
- The loader checks if products already exist in the database and skips loading if they do
- This prevents duplicate data on application restarts

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

### Testing

The project includes tests for the CSV loading functionality. Run tests with:
```bash
./mvnw test
```