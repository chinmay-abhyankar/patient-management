package com.pm.authservice.model;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * Represents a user in the authentication service.
 * <p> This class is used to store user information such as email, password, and role.
 * It is mapped to the "users" table in the database. 
 */
@Entity
@Table(name = "users")
public class User {
    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * User's email address. Must be unique and not null.
     */
    @Column(unique = true,nullable = false)
    private String email;

    /**
     * User's password. Not null.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Role assigned to the user. Not null.
     */
    @Column(nullable = false)
    private String role;

    /**
     * Gets the unique identifier of the user.
     * @return the user's UUID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the user.
     * @param id the UUID to set
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Gets the user's email address.
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's password.
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's role.
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the user's role.
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }
    /**
     * Default constructor for the User class.
     */
    public User() {
    }
    /**
     * Constructor for creating a new User object.
     *
     * @param email    The email address of the user.
     * @param password The password of the user.
     * @param role     The role of the user.
     */
    public User(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
