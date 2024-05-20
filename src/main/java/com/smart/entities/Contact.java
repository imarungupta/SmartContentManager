package com.smart.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.File;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CONTACT")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cId;

    private String name;
    private String secondName;
    private String work;

    @Column(unique = true)
    private String email;

    private String phone;
    private String image;

    @Column(length = 500)
    private String description;

    @ManyToOne
    @JsonIgnore                   // We don't want to serialize this User and that is why use @JsonIgnore, because if you go in the User then has Contacts which in-turn serialize the Contacts, and it will make Circular that is not good
    private User user;

    // Getters Setters

}
