package com.cor.managementservice.repositories;

import com.cor.managementservice.entities.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, UUID> {

    Boolean existsById(Long id);

    Optional<Subscribe> findById(Long id);

    Subscribe findSubscribeById(Long id);
}
