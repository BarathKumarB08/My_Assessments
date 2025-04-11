package com.ticketbooking.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tbl_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private String emailId;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets;

    public String generateFullName() {
        return this.firstName + " " + this.lastName;
    }

    @Override
    public String toString() {
        return "User = { " +
                "firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' +
                ", emailId='" + emailId + '\'' +
                " }";
    }
}
