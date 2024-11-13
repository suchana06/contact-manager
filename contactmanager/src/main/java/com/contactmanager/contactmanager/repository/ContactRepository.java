package com.contactmanager.contactmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.contactmanager.contactmanager.entities.Contact;
import com.contactmanager.contactmanager.entities.User;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer>{

    @Query("from Contact as c where c.user.id=:userId")
    public List<Contact> findContactsByUser(@Param("userId") int userId);

    public List<Contact> findByNameContainingAndUser(String keywords, User user);

}
