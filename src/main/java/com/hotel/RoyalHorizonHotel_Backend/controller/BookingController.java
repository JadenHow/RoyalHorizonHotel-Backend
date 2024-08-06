package com.hotel.RoyalHorizonHotel_Backend.controller;

import com.hotel.RoyalHorizonHotel_Backend.model.BookedRoom;
import com.hotel.RoyalHorizonHotel_Backend.service.IBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingController {

    private final IBookingService bookingService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookedRoom>> getAllBookings() {
        List<BookedRoom> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/room/{roomId}")
    public ResponseEntity<String> saveBooking(@PathVariable String roomId, @RequestBody BookedRoom bookingRequest) {
        try {
            String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(confirmationCode);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to save booking: " + e.getMessage());
        }
    }

    @GetMapping("/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode) {
        BookedRoom bookedRoom = bookingService.findByBookingConfirmationCode(confirmationCode);
        if (bookedRoom != null) {
            return ResponseEntity.ok(bookedRoom);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found with confirmation code: " + confirmationCode);
        }
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<List<BookedRoom>> getBookingsByUserEmail(@PathVariable String email) {
        List<BookedRoom> bookings = bookingService.getBookingsByUserEmail(email);
        return ResponseEntity.ok(bookings);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable String bookingId) {
        try {
            BookedRoom canceledBooking = bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok(canceledBooking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to cancel booking: " + e.getMessage());
        }
    }
}
