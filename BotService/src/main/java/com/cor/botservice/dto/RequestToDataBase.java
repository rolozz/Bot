package com.cor.botservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestToDataBase implements Serializable {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;
}
