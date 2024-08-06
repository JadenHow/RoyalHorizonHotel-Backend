package com.hotel.RoyalHorizonHotel_Backend.repository;

import com.hotel.RoyalHorizonHotel_Backend.model.BookedRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingRepository extends MongoRepository<BookedRoom, String> {
}
