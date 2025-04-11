package com.ticketbooking.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tbl_tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromStation; // (default: London)

    private String toStation; //(default: France)

    private Double price;

    private String section; //(A or B)

    private Integer seatNumber;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return "Ticket = { " +
                "id=" + id +
                ", fromStation='" + fromStation + '\'' + ", toStation='" + toStation + '\'' +
                ", price=" + price +
                ", section='" + section + '\'' + ", seatNumber=" + seatNumber +
                ", " + user.toString() +
                " }";
    }
}
