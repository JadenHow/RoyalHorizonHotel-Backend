package com.hotel.RoyalHorizonHotel_Backend.model;

import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "Room")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    private String id;

    private String roomType;

    private BigDecimal roomPrice;

    private List<String> images = new ArrayList<>();

    // BookedRoom Ids
    private List<String> bookings = new ArrayList<>();

    public void addBooking(BookedRoom booking) {
        bookings.add(booking.getBookingId());
        String bookingCode = RandomStringUtils.randomNumeric(10);
        booking.setBookingConfirmationCode(bookingCode);
    }
}