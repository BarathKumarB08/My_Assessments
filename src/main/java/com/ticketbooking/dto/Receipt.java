package com.ticketbooking.dto;

import com.ticketbooking.entity.User;

public record Receipt(Long ticketId, String fromStation, String toStation, User user, Double pricePaid, String section, Integer seatNumber) {

    public String toString(){
        return "\n[ TicketId: " + ticketId()
                + ", \n  From: " + fromStation()
                + ",  To: " + toStation()
                + ", \n  Price: " + pricePaid()
                + ", \n  Section: " + section()
                + ",  Seat Number: " + seatNumber()
                + ", \n  User Name: " + user().generateFullName()
                + ",  Email Id: " + user().getEmailId() + " ]";
    }
}
