package com.ticketbooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketbooking.dto.Receipt;
import com.ticketbooking.entity.Ticket;
import com.ticketbooking.entity.User;
import com.ticketbooking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketBookingController.class)
@Import(TicketBookingControllerTest.TestConfig.class)
class TicketBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Ticket ticket;
    private Receipt receipt;

    @BeforeEach
    void setUp() {
        user = new User(1L, "John", "Doe", "john@example.com", null);
        ticket = new Ticket(1L, "London", "Paris", 50.0, "A", 1, user);
        receipt = new Receipt(1L, "London", "Paris", user, 50.0, "A", 1);
    }

    @Test
    void bookTicketTest() throws Exception {
        when(bookingService.bookTicket(any(User.class))).thenReturn(receipt);

        mockMvc.perform(post("/booking/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ticket Booked successfully")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(receipt.toString())));
    }

    @Test
    void getTicketReceiptByUserEmailId_UserExists() throws Exception {
        when(bookingService.doesUserExistByEmail(anyString())).thenReturn(true);
        when(bookingService.getTicketReceiptByUserEmailId(anyString())).thenReturn(receipt.toString());

        mockMvc.perform(get("/booking/tickets/{emailId}", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Receipt Details")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(receipt.toString())));
    }

    @Test
    void getTicketReceiptByUserEmailId_UserNotFound() throws Exception {
        when(bookingService.doesUserExistByEmail(anyString())).thenReturn(false);

        mockMvc.perform(get("/booking/tickets/{emailId}", "unknown@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("User does not exist for the given email id")));
    }

    @Test
    void getUsersBySectionTest() throws Exception {
        List<String> users = List.of("John Doe - Seat 1A");
        when(bookingService.getUsersBySection(anyString())).thenReturn(users);

        mockMvc.perform(get("/booking/tickets/section/{section}", "A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("John Doe - Seat 1A"));
    }

    @Test
    void deleteUser_UserExists() throws Exception {
        when(bookingService.doesUserExistByEmail(anyString())).thenReturn(true);
        doNothing().when(bookingService).deleteUser(anyString());

        mockMvc.perform(delete("/booking/users/{emailId}", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("User for the given email id deleted")));
    }

    @Test
    void deleteUser_UserNotFound() throws Exception {
        when(bookingService.doesUserExistByEmail(anyString())).thenReturn(false);

        mockMvc.perform(delete("/booking/users/{emailId}", "unknown@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("User does not exist for the given email id")));
    }

    @Test
    void modifySeatTest() throws Exception {
        when(bookingService.modifySeat(anyLong(), anyString(), any())).thenReturn(ticket);

        mockMvc.perform(put("/booking/tickets/{ticketId}", 1L)
                        .param("section", "B")
                        .param("seatNumber", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticket.getId()))
                .andExpect(jsonPath("$.section").value(ticket.getSection()));
    }

    // --- Test Configuration Class ---
    @TestConfiguration
    static class TestConfig {
        @Bean
        public BookingService bookingService() {
            return mock(BookingService.class);
        }
    }
}
