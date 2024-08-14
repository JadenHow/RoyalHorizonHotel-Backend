package com.hotel.RoyalHorizonHotel_Backend.service;

import com.hotel.RoyalHorizonHotel_Backend.model.User;

import java.util.List;

public interface IUserService {
    User registerUser(User user);

    List<User> getUsers();

    User deleteUser(String email);

    User getUserByEmail(String email);

    User getUserById(String email);
}
