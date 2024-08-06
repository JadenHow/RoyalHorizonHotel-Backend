package com.hotel.RoyalHorizonHotel_Backend.repository;

import com.hotel.RoyalHorizonHotel_Backend.model.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<Room, String> {
}
