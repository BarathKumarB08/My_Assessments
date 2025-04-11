package com.ticketbooking.repository;

import com.ticketbooking.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query(name = "select * from tbl_tickets t, tbl_users u where t.user_id = u.id and u.email_Id = : emailId", nativeQuery = true)
    List<Ticket> getTicketDetailsByUserEmailId(@Param("emailId") String emailId);

    @Modifying
    @Transactional
    //@Query(name = "delete from tbl_tickets t, tbl_users u where t.user_id = u.id and u.email_Id = : emailId", nativeQuery = true)
    //@Query(name = "delete t from tbl_tickets t JOIN tbl_users u on t.user_id = u.id where u.email_Id = : emailId", nativeQuery = true)
    @Query("DELETE FROM Ticket t WHERE t.user.emailId = :emailId")
    Integer deleteTicketsForUserByEmailId(@Param("emailId") String emailId);
}
