package com.hotel.RoyalHorizonHotel_Backend.service;

import com.hotel.RoyalHorizonHotel_Backend.model.BookedRoom;
import com.hotel.RoyalHorizonHotel_Backend.model.Room;
import com.hotel.RoyalHorizonHotel_Backend.repository.BookingRepository;
import com.hotel.RoyalHorizonHotel_Backend.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {

    private final BookingRepository bookingRepository;
    private final IRoomService roomService;
    private final MongoTemplate mongoTemplate;
    private final RoomRepository roomRepository;

    @Override
    public List<BookedRoom> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public List<BookedRoom> getBookingsByUserEmail(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        return mongoTemplate.find(query, BookedRoom.class);
    }

    @Override
    public BookedRoom cancelBooking(String bookingId) {
        BookedRoom bookedRoom = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        Room room = roomService.getRoomById(bookedRoom.getRoomId());
        room.getBookings().removeIf(b -> b.equals(bookingId));

        roomRepository.save(room);
        bookingRepository.deleteById(bookingId);

        return bookedRoom;
    }

    @Override
    public List<BookedRoom> getAllBookingsByRoomId(String roomId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("room._id").is(roomId));
        return mongoTemplate.find(query, BookedRoom.class);
    }

    @Override
    public String saveBooking(String roomId, BookedRoom bookingRequest) {
        validateBookingDates(bookingRequest);

        Room room = roomService.getRoomById(roomId);

        List<BookedRoom> existingBookings = room.getBookings().stream()
                .map(bookingRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        if (roomIsAvailable(bookingRequest, existingBookings)) {
            BookedRoom bookedRoom = setBookedRoom(bookingRequest, room);
            BookedRoom savedBookings = bookingRepository.save(bookedRoom);

            room.addBooking(savedBookings);
            roomRepository.save(room);

            return bookedRoom.getBookingConfirmationCode();
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room is not available for the selected dates");
    }

    @Override
    public BookedRoom findByBookingConfirmationCode(String confirmationCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("bookingConfirmationCode").is(confirmationCode));

        return Optional.ofNullable(mongoTemplate.findOne(query, BookedRoom.class))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No booking found with confirmation code: " + confirmationCode));
    }

    // Helpers
    private void validateBookingDates(BookedRoom bookingRequest) {
        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check-in date must come before check-out date");
        }
    }

    private boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
        return existingBookings.stream().noneMatch(existingBooking ->
            (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()) &&
                bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckInDate())));
    }

    private static BookedRoom setBookedRoom(BookedRoom bookingRequest, Room room) {
        BookedRoom bookedRoom = new BookedRoom();
        bookedRoom.setCheckInDate(bookingRequest.getCheckInDate());
        bookedRoom.setCheckOutDate(bookingRequest.getCheckOutDate());
        bookedRoom.setGuestFullName(bookingRequest.getGuestFullName());
        bookedRoom.setGuestEmail(bookingRequest.getGuestEmail());
        bookedRoom.setNumOfAdults(bookingRequest.getNumOfAdults());
        bookedRoom.setNumOfChildren(bookingRequest.getNumOfChildren());
        bookedRoom.setRoomId(room.getId());

        String bookingCode = RandomStringUtils.randomNumeric(10);
        bookedRoom.setBookingConfirmationCode(bookingCode);
        return bookedRoom;
    }
}
