package com.smart.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USER")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2,max = 20,message = "min 2 and max 20 characters are allowed")
    private String name;

    private String dob;
    @Column(unique = true)
    private String email;

    private String password;
    private String role;
    private boolean enabled;
    private String imageUrl;

    @Column(length = 500)
    private String about;

    /* One User can have multiple contact, so let's create a list having multiple Contact
    * Cascade.All means as soon as we create User then all the contact of that user will be created automatically
    * and if delete the user then all the contact related to the User will be deleted automatically
    *  */
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY, mappedBy = "user")
   private  List<Contact> contactList = new ArrayList<>();
}
