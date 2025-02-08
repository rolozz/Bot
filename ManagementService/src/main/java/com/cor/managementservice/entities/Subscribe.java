package com.cor.managementservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subscribe {

    @Id
    UUID uuid = UUID.randomUUID();
    String username;
    String name;
    String city;
    Long count;

    @PrePersist
    private void initCount() {
        if (count == null) {
            count = 1L;
        }
    }

    @PreUpdate
    private void incrementCount() {
        count++;
    }
}
