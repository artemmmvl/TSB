package com.example.tsb.repositories;

import com.example.tsb.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);
    User getUserByEmail(String username);
    User getById(Long id);
}
