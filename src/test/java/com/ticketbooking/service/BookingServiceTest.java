package com.ticketbooking.service;

import com.ticketbooking.entity.Ticket;
import com.ticketbooking.entity.User;
import com.ticketbooking.exception.ResourceNotFoundException;
import com.ticketbooking.repository.TicketRepository;
import com.ticketbooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private BookingService bookingService;

    private User sampleUser;
    private Ticket sampleTicket;

    @BeforeEach
    void setup() {
        sampleUser = new User();
        sampleUser.setFirstName("John");
        sampleUser.setLastName("Doe");
        sampleUser.setEmailId("john.doe@example.com");

        sampleTicket = new Ticket();
        sampleTicket.setId(1L);
        sampleTicket.setFromStation("London");
        sampleTicket.setToStation("France");
        sampleTicket.setPrice(5.0);
        sampleTicket.setSection("A");
        sampleTicket.setSeatNumber(1);
        sampleTicket.setUser(sampleUser);
    }

    @Test
    void testGetTicketReceiptByUserEmailId_Success() {
        when(ticketRepository.getTicketDetailsByUserEmailId("john.doe@example.com"))
                .thenReturn(List.of(sampleTicket));

        String receiptDetails = bookingService.getTicketReceiptByUserEmailId("john.doe@example.com");

        assertNotNull(receiptDetails);
        assertTrue(receiptDetails.contains("John Doe"));
        verify(ticketRepository, times(1)).getTicketDetailsByUserEmailId("john.doe@example.com");
    }

    @Test
    void testGetUsersBySection() {
        when(ticketRepository.findAll()).thenReturn(List.of(sampleTicket));

        List<String> users = bookingService.getUsersBySection("A");

        assertEquals(1, users.size());
        assertTrue(users.get(0).contains("John Doe"));
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void testDeleteUser() {
        when(ticketRepository.getTicketDetailsByUserEmailId("john.doe@example.com"))
                .thenReturn(List.of(sampleTicket));

        bookingService.deleteUser("john.doe@example.com");

        verify(userRepository, times(1)).deleteUserByEmailId("john.doe@example.com");
    }

    @Test
    void testModifySeat_Success() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(sampleTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(sampleTicket);

        Ticket modifiedTicket = bookingService.modifySeat(1L, "B", 2);

        assertNotNull(modifiedTicket);
        assertEquals("B", modifiedTicket.getSection());
        assertEquals(2, modifiedTicket.getSeatNumber());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void testModifySeat_TicketNotFound() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.modifySeat(1L, "B", 2);
        });

        assertTrue(exception.getMessage().contains("Ticket with ID 1 not found"));
    }

    @Test
    void testDoesUserExistByEmail() {
        when(userRepository.existsByEmailId("john.doe@example.com")).thenReturn(true);

        boolean exists = bookingService.doesUserExistByEmail("john.doe@example.com");

        assertTrue(exists);
        verify(userRepository, times(1)).existsByEmailId("john.doe@example.com");
    }
}
