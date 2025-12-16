package com.myfullstackproject.springbootproject1.repository;

import com.myfullstackproject.springbootproject1.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
}
