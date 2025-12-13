package com.myfullstackproject.springbootproject1.service;

import com.myfullstackproject.springbootproject1.model.Product;
import com.myfullstackproject.springbootproject1.repository.ProductRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@Component
public class CsvProductLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CsvProductLoader.class);
    private final ProductRepository productRepository;

    public CsvProductLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            log.info("Products already loaded in database. Skipping CSV import.");
            return;
        }

        log.info("Loading products from CSV file...");

        try (InputStream is = getClass().getResourceAsStream("/Cleaned1..csv");
             InputStreamReader reader = new InputStreamReader(is);
             CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> allRows = csvReader.readAll();
            List<Product> products = new ArrayList<>();

            // Skip header row (index 0)
            for (int i = 1; i < allRows.size(); i++) {
                String[] row = allRows.get(i);
                
                try {
                    // CSV columns: ASIN, Category, Product Link, No of Sellers, Rank, Rating, Reviews Count, Price, 
                    // Books, Camera & Photo, Clothing/Shoes/Jewelry, Electronics, Gift Cards, Toys & Games, Video Games, Product Title
                    Product product = Product.builder()
                            .asin(row[0])                                           // ASIN
                            .category(row[1])                                       // Category
                            .productLink(row[2])                                    // Product Link
                            .rank(parseInteger(row[4]))                             // Rank
                            .rating(parseDouble(row[5]))                            // Rating
                            .ratingCount(parseLong(row[6]))                         // Reviews Count
                            .price(parseDouble(row[7]))                             // Price
                            .title(row.length > 15 ? row[15] : "")                  // Product Title
                            .build();

                    products.add(product);
                } catch (Exception e) {
                    log.warn("Error parsing row {}: {}", i, e.getMessage());
                }
            }

            productRepository.saveAll(products);
            log.info("Successfully loaded {} products from CSV into database.", products.size());

        } catch (IOException | CsvException e) {
            log.error("Error loading CSV file: {}", e.getMessage(), e);
        }
    }

    private Integer parseInteger(String value) {
        try {
            return value != null && !value.trim().isEmpty() ? Integer.parseInt(value.trim()) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long parseLong(String value) {
        try {
            return value != null && !value.trim().isEmpty() ? Long.parseLong(value.trim()) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        try {
            return value != null && !value.trim().isEmpty() ? Double.parseDouble(value.trim()) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}