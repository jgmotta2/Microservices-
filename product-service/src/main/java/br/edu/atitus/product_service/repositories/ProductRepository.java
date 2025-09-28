package br.edu.atitus.product_service.repositories;

import br.edu.atitus.product_service.entities.ProductEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntry, Long> {
}