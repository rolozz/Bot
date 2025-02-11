package com.cor.managementservice.repositories;

import com.cor.managementservice.entities.Joke;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JokeRepository extends JpaRepository<Joke, UUID> {

    @Query(value = "SELECT * FROM joke WHERE is_active = true ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Joke> findRandomActiveJoke();

    @Modifying
    @Query("UPDATE Joke j SET j.isActive = false WHERE j.uuid = :uuid AND j.isActive = true")
    int deactivateJoke(@Param("uuid") UUID uuid);

    @Modifying
    @Query("UPDATE Joke j SET j.isActive = true WHERE j.isActive = false")
    void activateAllJokes();
}
