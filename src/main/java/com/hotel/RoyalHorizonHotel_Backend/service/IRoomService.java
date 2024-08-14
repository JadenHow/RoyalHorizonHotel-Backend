package com.hotel.RoyalHorizonHotel_Backend.service;

import com.hotel.RoyalHorizonHotel_Backend.model.Room;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IRoomService {
    Room createRoom(String images, String roomType, BigDecimal roomPrice);

    List<String> getAllRoomTypes();

    Room getRoomById(String roomId);

    List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType);

    List<Room> getAllRooms();

    Room updateRoom(String roomId, String roomType, BigDecimal roomPrice, String image);

    Room deleteRoom(String roomId);
}