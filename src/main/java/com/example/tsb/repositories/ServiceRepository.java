package com.example.tsb.repositories;

import com.example.tsb.models.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service,Long> {
    Service getServiceByName(String name);

    Service findById(long id);

}
