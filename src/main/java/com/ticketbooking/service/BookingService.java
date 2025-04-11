package com.ticketbooking.service;

import com.ticketbooking.dto.Receipt;
import com.ticketbooking.entity.Ticket;
import com.ticketbooking.entity.User;
import com.ticketbooking.exception.ResourceNotFoundException;
import com.ticketbooking.repository.TicketRepository;
import com.ticketbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class BookingService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TicketRepository ticketRepository;

    // Initial seat availability setup
    private final Map<String, List<Integer>> availableSeats = new HashMap<>() {{
        put("A", new ArrayList<>(List.of(1,2,3,4,5,6,7,8,9,10)));
        put("B", new ArrayList<>(List.of(1,2,3,4,5,6,7,8,9,10)));
    }};

    public Receipt bookTicket(User user) {
        String section = selectSection();  // To select a section randomly
        Integer seatNumber = selectSeat(section);  // To select a seat randomly

        if (seatNumber == null) {
            throw new RuntimeException("No available seats in section " + section);
        }

        return saveBooking(user, section, seatNumber);
    }

    private Receipt saveBooking(User user, String section, Integer seatNumber) {
        availableSeats.get(section).remove(seatNumber);

        User savedUserObj = userRepository.save(user);

        Ticket ticket = new Ticket();
        ticket.setFromStation("London");
        ticket.setToStation("France");
        ticket.setPrice(5.0);
        ticket.setSection(section);
        ticket.setSeatNumber(seatNumber);
        ticket.setUser(savedUserObj);

        Ticket ticketBooked = ticketRepository.save(ticket);

        return new Receipt(ticketBooked.getId(),
                ticketBooked.getFromStation(), ticketBooked.getToStation(),
                ticketBooked.getUser(), ticketBooked.getPrice(),
                ticketBooked.getSection(), ticketBooked.getSeatNumber());
    }


    public String getTicketReceiptByUserEmailId(String emailId) {
        List<Ticket> ticketList = ticketRepository.getTicketDetailsByUserEmailId(emailId);
        StringBuilder sbuilder = new StringBuilder();
        for (Ticket obj: ticketList) {
            sbuilder.append(new Receipt(obj.getId(),
                    obj.getFromStation(), obj.getToStation(),
                    obj.getUser(), obj.getPrice(),
                    obj.getSection(), obj.getSeatNumber()));
        }
        return sbuilder.toString();
    }



    public List<String> getUsersBySection(String section) {
        List<Ticket> ticketList = ticketRepository.findAll()
                .stream().filter(r -> r.getSection().equals(section)).toList();
        List<String> userAndSeatDetailsList = new ArrayList<>();
        for ( Ticket ticket: ticketList) {
            String userAndSeatDetails = String.format("User = { Name = %s, emailId= %s }, " +
                            "Ticket = {section = %s, seatNumber = %d }",
                    ticket.getUser().generateFullName(), ticket.getUser().getEmailId(),
                    ticket.getSection(), ticket.getSeatNumber());
            userAndSeatDetailsList.add(userAndSeatDetails);
        }
        return userAndSeatDetailsList;
    }

    public void deleteUser(String emailId) {
        List<Ticket> tickets = ticketRepository.getTicketDetailsByUserEmailId(emailId);
        for (Ticket ticket : tickets) {
            availableSeats.get(ticket.getSection()).add(ticket.getSeatNumber());
        }
        userRepository.deleteUserByEmailId(emailId);
    }


    public Ticket modifySeat(Long ticketId, String newSection, Integer newSeatNumber) {
        Ticket ticket = ticketRepository
                .findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with ID " + ticketId + " not found"));

        if (!availableSeats.containsKey(newSection) || !availableSeats.get(newSection).contains(newSeatNumber)) {
            throw new ResourceNotFoundException("Requested seat not available");
        }

        // Free old seat
        availableSeats.get(ticket.getSection()).add(ticket.getSeatNumber());

        // Assign new seat
        ticket.setSection(newSection);
        ticket.setSeatNumber(newSeatNumber);
        availableSeats.get(newSection).remove(newSeatNumber);

        return ticketRepository.save(ticket);
    }



    public boolean doesUserExistByEmail(String emailId) {
        return userRepository.existsByEmailId(emailId);
    }


    private String selectSection() {
        return new Random().nextBoolean() ? "A" : "B";
    }

    private Integer selectSeat(String section) {
        List<Integer> seats = availableSeats.get(section);
        if (seats.isEmpty()) {
            return null;
        }
        return seats.remove(ThreadLocalRandom.current().nextInt(seats.size()));
    }

    /*public Receipt bookTicketManual(User user, String section, Integer seat) {
        if (!availableSeats.containsKey(section)) {
            throw new RuntimeException("Invalid section " + section);
        }
        if (!availableSeats.get(section).contains(seat)) {
            throw new RuntimeException("Seat not available in section " + section);
        }

        return saveBooking(user, section, seat);
    }*/
}
