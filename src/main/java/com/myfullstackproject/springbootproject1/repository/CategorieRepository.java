package com.myfullstackproject.springbootproject1.repository;

import com.myfullstackproject.springbootproject1.model.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
}
