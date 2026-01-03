# API Endpoints Documentation

This document lists all available REST API endpoints for the e-commerce backend.

**Base URL**: `http://localhost:8080`

---

## 📦 Product Endpoints (`/api/produits`)

### 1. Get All Products
- **Endpoint**: `GET /api/produits`
- **Description**: Retrieve all products
- **Response**: Array of Product objects

### 2. Get Product by ID
- **Endpoint**: `GET /api/produits/{id}`
- **Description**: Get detailed information about a specific product
- **Parameters**: 
  - `id` (path): Product ID
- **Response**: Product object

### 3. Search Products by Keyword
- **Endpoint**: `GET /api/produits/search`
- **Description**: Search products by title keyword (case-insensitive)
- **Query Parameters**:
  - `keyword` (required): Search term
- **Example**: `/api/produits/search?keyword=laptop`
- **Response**: Array of matching products

### 4. Advanced Product Filter
- **Endpoint**: `GET /api/produits/filter`
- **Description**: Filter products with multiple criteria
- **Query Parameters** (all optional):
  - `keyword`: Search term for title/description
  - `categoryId`: Filter by category ID
  - `minPrice`: Minimum price
  - `maxPrice`: Maximum price
  - `minRating`: Minimum rating (0-5)
- **Example**: `/api/produits/filter?keyword=phone&minPrice=100&maxPrice=500&minRating=4.0`
- **Response**: Array of filtered products

### 5. Get Products by Category
- **Endpoint**: `GET /api/produits/category/{categoryId}`
- **Description**: Get all products in a specific category
- **Parameters**:
  - `categoryId` (path): Category ID
- **Response**: Array of products

### 6. Get Product by ASIN
- **Endpoint**: `GET /api/produits/asin/{asin}`
- **Description**: Lookup product by Amazon ASIN code
- **Parameters**:
  - `asin` (path): Product ASIN
- **Response**: Product object

### 7. Get Products by Price Range
- **Endpoint**: `GET /api/produits/price-range`
- **Description**: Filter products within a price range
- **Query Parameters**:
  - `minPrice` (required): Minimum price
  - `maxPrice` (required): Maximum price
- **Example**: `/api/produits/price-range?minPrice=50&maxPrice=200`
- **Response**: Array of products

### 8. Get Products by Minimum Rating
- **Endpoint**: `GET /api/produits/rating/{minRating}`
- **Description**: Get products with rating >= specified value
- **Parameters**:
  - `minRating` (path): Minimum rating (0-5)
- **Response**: Array of products

---

## ⭐ Review/Rating Endpoints (`/api/produits`)

### 9. Get Product Reviews
- **Endpoint**: `GET /api/produits/{productId}/reviews`
- **Description**: Get all reviews for a specific product
- **Parameters**:
  - `productId` (path): Product ID
- **Response**: Array of Rating objects

### 10. Add Product Review
- **Endpoint**: `POST /api/produits/{productId}/reviews`
- **Description**: Submit a new review for a product
- **Parameters**:
  - `productId` (path): Product ID
- **Request Body**:
```json
{
  "stars": 5,
  "comment": "Great product!"
}
```
- **Validation**:
  - `stars`: 1-5 (required)
  - `comment`: Non-empty, max 500 characters (required)
- **Response**: Created Rating object

---

## 🛒 Cart Endpoints (`/api/panier`)

### 11. Add Product to Cart
- **Endpoint**: `POST /api/panier/add/{productId}`
- **Description**: Add a product to shopping cart
- **Parameters**:
  - `productId` (path): Product ID
  - `quantity` (query, optional): Quantity (default: 1)
- **Example**: `/api/panier/add/123?quantity=2`
- **Response**: CartItem object

### 12. Get Cart Items
- **Endpoint**: `GET /api/panier`
- **Description**: Get all items in the shopping cart
- **Response**: Array of CartItem objects

### 13. Update Cart Item Quantity
- **Endpoint**: `PUT /api/panier/{cartItemId}`
- **Description**: Update quantity of a cart item
- **Parameters**:
  - `cartItemId` (path): Cart item ID
  - `quantity` (query, required): New quantity
- **Example**: `/api/panier/5?quantity=3`
- **Response**: Updated CartItem object

### 14. Remove Cart Item
- **Endpoint**: `DELETE /api/panier/{cartItemId}`
- **Description**: Remove an item from cart
- **Parameters**:
  - `cartItemId` (path): Cart item ID
- **Response**: 200 OK (no content)

---

## 👤 Vendor Endpoints (`/api/vendeur`)

### Product Management

### 15. Add New Product
- **Endpoint**: `POST /api/vendeur/produits`
- **Description**: Create a new product (vendor only)
- **Request Body**:
```json
{
  "title": "Product Name",
  "description": "Product description",
  "price": 99.99,
  "quantityAvailable": 100,
  "categorieId": 1,
  "imageUrls": ["url1", "url2"],
  "asin": "ABC123" // optional, auto-generated if not provided
}
```
- **Response**: Created Product object (201)

### 16. Update Product
- **Endpoint**: `PUT /api/vendeur/produits/{id}`
- **Description**: Update an existing product
- **Parameters**:
  - `id` (path): Product ID
- **Request Body**: Same as Add Product (all fields optional)
- **Response**: Updated Product object

### 17. Delete Product
- **Endpoint**: `DELETE /api/vendeur/produits/{id}`
- **Description**: Delete a product
- **Parameters**:
  - `id` (path): Product ID
- **Response**: Success message

### 18. Get Vendor Products
- **Endpoint**: `GET /api/vendeur/produits`
- **Description**: List all products belonging to the vendor
- **Response**: Array of Product objects

### 19. Get Vendor Product Details
- **Endpoint**: `GET /api/vendeur/produits/{id}`
- **Description**: Get detailed info about vendor's product
- **Parameters**:
  - `id` (path): Product ID
- **Response**: Product object

### Image Management

### 20. Add Product Images
- **Endpoint**: `POST /api/vendeur/produits/{productId}/images`
- **Description**: Add multiple images to a product
- **Parameters**:
  - `productId` (path): Product ID
- **Request Body**: Array of image URLs
```json
["https://example.com/image1.jpg", "https://example.com/image2.jpg"]
```
- **Response**: Array of ProductImage objects (201)

### 21. Delete Product Image
- **Endpoint**: `DELETE /api/vendeur/produits/{productId}/images/{imageId}`
- **Description**: Remove an image from a product
- **Parameters**:
  - `productId` (path): Product ID
  - `imageId` (path): Image ID
- **Response**: Success message

### 22. Get Product Images
- **Endpoint**: `GET /api/vendeur/produits/{productId}/images`
- **Description**: List all images for a product
- **Parameters**:
  - `productId` (path): Product ID
- **Response**: Array of ProductImage objects

### Analytics & Reviews

### 23. Get Product Reviews (Vendor)
- **Endpoint**: `GET /api/vendeur/produits/{productId}/reviews`
- **Description**: View customer reviews for vendor's product
- **Parameters**:
  - `productId` (path): Product ID
- **Response**: Array of Rating objects

### 24. Get Product Statistics
- **Endpoint**: `GET /api/vendeur/produits/{productId}/stats`
- **Description**: Get comprehensive review statistics
- **Parameters**:
  - `productId` (path): Product ID
- **Response**:
```json
{
  "productId": 1,
  "productTitle": "Product Name",
  "totalReviews": 50,
  "averageRating": 4.2,
  "fiveStarCount": 25,
  "fourStarCount": 15,
  "threeStarCount": 5,
  "twoStarCount": 3,
  "oneStarCount": 2
}
```

---

## 📂 Category Endpoints (`/api/categories`)

### 25. Get All Categories
- **Endpoint**: `GET /api/categories`
- **Description**: Retrieve all product categories
- **Response**: Array of Categorie objects

### 26. Get Category by ID
- **Endpoint**: `GET /api/categories/{id}`
- **Description**: Get details of a specific category
- **Parameters**:
  - `id` (path): Category ID
- **Response**: Categorie object

---

## 📝 Notes

- **CORS**: All endpoints support CORS from `http://localhost:5173` (Vite default port)
- **Demo User ID**: Currently using hardcoded demo user ID = 1 for cart and reviews
- **Demo Vendor ID**: Currently using hardcoded demo vendor ID = 1 for vendor operations
- **Error Handling**: All endpoints return appropriate error messages with HTTP status codes

---

## Summary

**Total Endpoints**: 26

**By Category**:
- Product Search & Filter: 8 endpoints
- Reviews/Ratings: 2 endpoints
- Shopping Cart: 4 endpoints
- Vendor Product Management: 5 endpoints
- Vendor Image Management: 3 endpoints
- Vendor Analytics: 2 endpoints
- Categories: 2 endpoints
