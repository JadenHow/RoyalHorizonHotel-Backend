package com.hotel.RoyalHorizonHotel_Backend.service;

import com.hotel.RoyalHorizonHotel_Backend.model.BookedRoom;
import java.util.List;

public interface IBookingService {
    List<BookedRoom> getAllBookings();

    List<BookedRoom> getBookingsByUserEmail(String email);

    BookedRoom cancelBooking(String bookingId);

    List<BookedRoom> getAllBookingsByRoomId(String roomId);

    String saveBooking(String roomId, BookedRoom bookingRequest);

    BookedRoom findByBookingConfirmationCode(String confirmationCode);
}