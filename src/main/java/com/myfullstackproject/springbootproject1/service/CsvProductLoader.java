package com.myfullstackproject.springbootproject1.service;

import com.myfullstackproject.springbootproject1.model.Product;
import com.myfullstackproject.springbootproject1.repository.ProductRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvProductLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CsvProductLoader.class);
    private static final String CSV_FILE_PATH = "/Cleaned1..csv";
    
    // CSV column indices
    private static final int ASIN_COLUMN = 0;
    private static final int CATEGORY_COLUMN = 1;
    private static final int PRODUCT_LINK_COLUMN = 2;
    private static final int RANK_COLUMN = 4;
    private static final int RATING_COLUMN = 5;
    private static final int REVIEWS_COUNT_COLUMN = 6;
    private static final int PRICE_COLUMN = 7;
    private static final int TITLE_COLUMN = 15;

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

        try (InputStream is = getClass().getResourceAsStream(CSV_FILE_PATH)) {
            if (is == null) {
                log.error("CSV file not found at: {}", CSV_FILE_PATH);
                return;
            }

            try (InputStreamReader reader = new InputStreamReader(is);
                 CSVReader csvReader = new CSVReader(reader)) {

                List<String[]> allRows = csvReader.readAll();
                List<Product> products = new ArrayList<>();

                // Skip header row (index 0)
                for (int i = 1; i < allRows.size(); i++) {
                    String[] row = allRows.get(i);
                    
                    try {
                        Product product = Product.builder()
                                .asin(row[ASIN_COLUMN])
                                .category(row[CATEGORY_COLUMN])
                                .productLink(row[PRODUCT_LINK_COLUMN])
                                .rank(parseInteger(row[RANK_COLUMN]))
                                .rating(parseDouble(row[RATING_COLUMN]))
                                .ratingCount(parseLong(row[REVIEWS_COUNT_COLUMN]))
                                .price(parseDouble(row[PRICE_COLUMN]))
                                .title(row.length > TITLE_COLUMN ? row[TITLE_COLUMN] : "")
                                .build();

                        products.add(product);
                    } catch (Exception e) {
                        log.warn("Error parsing row {}: {}", i, e.getMessage());
                    }
                }

                productRepository.saveAll(products);
                log.info("Successfully loaded {} products from CSV into database.", products.size());
            }

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