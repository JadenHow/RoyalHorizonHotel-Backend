package com.hotel.RoyalHorizonHotel_Backend.repository;

import com.hotel.RoyalHorizonHotel_Backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
