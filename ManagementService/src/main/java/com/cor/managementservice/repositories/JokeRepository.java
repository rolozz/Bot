package com.cor.managementservice.repositories;

import com.cor.managementservice.entities.Joke;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JokeRepository extends JpaRepository<Joke, UUID> {
}
