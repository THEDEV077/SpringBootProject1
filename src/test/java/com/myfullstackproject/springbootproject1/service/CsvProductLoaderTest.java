package com.myfullstackproject.springbootproject1.service;

import com.myfullstackproject.springbootproject1.model.Product;
import com.myfullstackproject.springbootproject1.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CsvProductLoaderTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CsvProductLoader csvProductLoader;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        productRepository.deleteAll();
    }

    @Test
    void testCsvLoadingIntoDatabase() {
        // Run the CSV loader
        csvProductLoader.run();

        // Verify products were loaded
        List<Product> products = productRepository.findAll();
        assertFalse(products.isEmpty(), "Products should be loaded from CSV");

        // Verify at least one product has expected data
        Product firstProduct = products.get(0);
        assertNotNull(firstProduct.getAsin(), "ASIN should not be null");
        assertNotNull(firstProduct.getTitle(), "Title should not be null");
        assertNotNull(firstProduct.getCategory(), "Category should not be null");
    }

    @Test
    void testSkipLoadingWhenProductsExist() {
        // First load
        csvProductLoader.run();
        long countAfterFirstLoad = productRepository.count();
        assertTrue(countAfterFirstLoad > 0, "Products should be loaded");

        // Second load should skip
        csvProductLoader.run();
        long countAfterSecondLoad = productRepository.count();
        assertEquals(countAfterFirstLoad, countAfterSecondLoad, 
            "Second load should skip and not duplicate products");
    }
}
