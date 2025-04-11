package com.ticketbooking.controller;

import com.ticketbooking.dto.Receipt;
import com.ticketbooking.entity.Ticket;
import com.ticketbooking.entity.User;
import com.ticketbooking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/booking")
public class TicketBookingController {

    @Autowired
    BookingService bookingService;

    // Book a ticket for the user
    @PostMapping("/tickets")
    public ResponseEntity<String> bookTicket(@RequestBody User user) {
        Receipt receipt =  bookingService.bookTicket(user);
        return new ResponseEntity<>("Ticket Booked successfully. \n\n" +
                "Receipt details:" + receipt.toString(),
                HttpStatus.OK);
    }

    // Get Receipt details (list of receipts) for given user (emailId)
    @GetMapping("/tickets/{emailId}")
    public ResponseEntity<String> getTicketReceiptByUserEmailId(@PathVariable String emailId) {
        if(!bookingService.doesUserExistByEmail(emailId)) {
            return new ResponseEntity<>("User does not exist for the given email id",
                    HttpStatus.NOT_FOUND);
        } else {
            String receiptDetails = bookingService.getTicketReceiptByUserEmailId(emailId);
            return new ResponseEntity<>("Receipt Details: \n" + receiptDetails, HttpStatus.OK);
        }
    }

    // View the list of users and the seat they are allocated by the given section
    @GetMapping("/tickets/section/{section}")
    public List<String> getUsersBySection(@PathVariable String section) {
        return bookingService.getUsersBySection(section);
    }

    // Delete user by Email Id
    @DeleteMapping("/users/{emailId}")
    public ResponseEntity<String> deleteUser(@PathVariable String emailId) {
        if(!bookingService.doesUserExistByEmail(emailId)) {
            return new ResponseEntity<>("User does not exist for the given email id",
                    HttpStatus.NOT_FOUND);
        } else {
            bookingService.deleteUser(emailId);
            return new ResponseEntity<>("User for the given email id deleted",
                    HttpStatus.OK);
        }
    }

    /*
    Task 5: Modify seat details (change seat selection)
    Input: existing ticketId, new section, new seatNumber
    Output: Modified Ticket details.
    */
    @PutMapping("/tickets/{ticketId}")
    public ResponseEntity<Ticket> modifySeat(@PathVariable Long ticketId,
                 @RequestParam String section, @RequestParam Integer seatNumber) {
        return ResponseEntity.ok(bookingService.modifySeat(ticketId, section, seatNumber));
    }
}
