package com.smart.dao;

import com.smart.entities.Contact;
import com.smart.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

     @Query("from Contact as c where c.user.id=:userId")
    public List<Contact> findContactsByUser(@Param("userId") int userId);


    @Query("from Contact as c where c.user.id=:userId")
    public Page<Contact> findContactsPerPageByUser(@Param("userId") int userId, Pageable pageable);

    /* Search Functionality : Step4: getting the list of users based on name to search and logged-in user
       The below method is provided by JPA. It will implement the method prepare the query based on the name
       and input parameter and provide the List of contact. We just need to call this method and receive the result in List variable.
       But if we write our own custom method then we need to write HQL query. like given above two method. Method name we can search in the Google
   */
    public List<Contact> findByNameContainingAndUser(String name, User user);

}
