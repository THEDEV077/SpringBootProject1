package com.myfullstackproject.springbootproject1.controller;

import com.myfullstackproject.springbootproject1.model.Categorie;
import com.myfullstackproject.springbootproject1.repository.CategorieRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class CategorieController {

    private final CategorieRepository categorieRepository;

    public CategorieController(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    // 1) Liste de toutes les catégories
    @GetMapping
    public List<Categorie> getAllCategories() {
        return categorieRepository.findAll();
    }

    // 2) Détail d'une catégorie par id
    @GetMapping("/{id}")
    public Categorie getCategoryById(@PathVariable Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
    }
}
