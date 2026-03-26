// Name: Peter Kinfe
// ID: ATE/7749/15
package com.shopwave.repository;

import com.shopwave.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {}