package com.hotel.RoyalHorizonHotel_Backend.controller;

import com.hotel.RoyalHorizonHotel_Backend.model.Room;
import com.hotel.RoyalHorizonHotel_Backend.service.RoomService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createRoom(@RequestBody Room room) {
        try {
            validateRoom(room);
            Room createdRoom = roomService.createRoom(room.getImages(), room.getRoomType(), room.getRoomPrice());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create room: " + e.getMessage());
        }
    }

    @GetMapping("/types")
    public ResponseEntity<List<String>> getRoomTypes() {
        List<String> roomTypes = roomService.getAllRoomTypes();
        return ResponseEntity.ok(roomTypes);
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @DeleteMapping("/{roomId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRoom(@PathVariable String roomId) {
        try {
            Room deletedRoom = roomService.deleteRoom(roomId);
            return ResponseEntity.ok(deletedRoom);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete room: " + e.getMessage());
        }
    }

    @PutMapping("/{roomId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateRoom(@PathVariable String roomId, @RequestBody Room room) {
        try {
            validateRoom(room);
            Room updatedRoom = roomService.updateRoom(roomId, room.getRoomType(), room.getRoomPrice(), room.getImages());
            return ResponseEntity.ok(updatedRoom);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update room: " + e.getMessage());
        }
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoomById(@PathVariable String roomId) {
        Room room = roomService.getRoomById(roomId);
        if (room != null) {
            return ResponseEntity.ok(room);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found with ID: " + roomId);
        }
    }

    @PostMapping("/available")
    public ResponseEntity<List<Room>> getAvailableRooms(@RequestBody AvailableRoomRequestDTO availableRoomRequestDTO) {
        List<Room> availableRooms = roomService.getAvailableRooms(
                availableRoomRequestDTO.getCheckInDate(),
                availableRoomRequestDTO.getCheckOutDate(),
                availableRoomRequestDTO.getRoomType()
        );
        return ResponseEntity.ok(availableRooms);
    }

    private void validateRoom(Room room) {
        if (room.getRoomType() == null || room.getRoomType().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room type is required.");
        }
        if (room.getRoomPrice() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room price is required.");
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AvailableRoomRequestDTO {
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private String roomType;
    }
}
