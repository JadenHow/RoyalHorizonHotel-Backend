package com.hotel.RoyalHorizonHotel_Backend.service;

import com.hotel.RoyalHorizonHotel_Backend.model.BookedRoom;
import com.hotel.RoyalHorizonHotel_Backend.model.Room;
import com.hotel.RoyalHorizonHotel_Backend.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService implements IRoomService {

    private final RoomRepository roomRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Room createRoom(String image, String roomType, BigDecimal roomPrice) {
        Room room = new Room();
        room.setImage(image);
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);
        return roomRepository.save(room);
    }

    @Override
    public List<String> getAllRoomTypes() {
        return mongoTemplate.query(Room.class)
            .distinct("roomType")
            .as(String.class)
            .all();
    }

    @Override
    public Room getRoomById(String roomId) {
        return roomRepository.findById(roomId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
    }

    @Override
    public List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        Query bookedRoomsQuery = new Query();
        bookedRoomsQuery.addCriteria(Criteria.where("checkInDate").lt(checkOutDate)
            .and("checkOutDate").gt(checkInDate));

        List<String> bookedRoomIds = mongoTemplate.findDistinct(bookedRoomsQuery, "roomId", BookedRoom.class, String.class);

        Query availableRoomsQuery = new Query();
        availableRoomsQuery.addCriteria(Criteria.where("roomType").regex(roomType, "i")
            .and("_id").nin(bookedRoomIds));

        return mongoTemplate.find(availableRoomsQuery, Room.class);
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Room updateRoom(String roomId, String roomType, BigDecimal roomPrice, String image) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        if (roomType != null && !roomType.trim().isEmpty()) room.setRoomType(roomType);
        if (roomPrice != null) room.setRoomPrice(roomPrice);
        if (image != null && !image.trim().isEmpty()) room.setImage(image);

        return roomRepository.save(room);
    }

    @Override
    public Room deleteRoom(String roomId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId));
        mongoTemplate.remove(query, BookedRoom.class);

        roomRepository.deleteById(roomId);
        return room;
    }
}