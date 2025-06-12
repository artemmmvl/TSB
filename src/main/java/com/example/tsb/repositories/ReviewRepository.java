package com.example.tsb.repositories;

import com.example.tsb.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Review findById(long id);
    List<Review> findAllByConfirmation(boolean c);
}
