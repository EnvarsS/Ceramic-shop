package org.envycorp.productservice.repository;

import org.envycorp.productservice.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = """
            SELECT * FROM products
                        WHERE MATCH(name, description) AGAINST(:fuzzyQuery IN BOOLEAN MODE)
                                    OR name like CONCAT('%', :originalQuery, '%')
                                                OR SOUNDEX(name) = SOUNDEX(:originalQuery)
            """, nativeQuery = true)
    Page<Product> searchByName(
            @Param("originalQuery") String originalQuery,
            @Param("fuzzyQuery") String fuzzyQuery,
            Pageable pageable);

    boolean existsByName(String name);
}
