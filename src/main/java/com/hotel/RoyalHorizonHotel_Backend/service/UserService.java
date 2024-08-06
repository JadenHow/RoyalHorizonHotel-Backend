package com.hotel.RoyalHorizonHotel_Backend.service;

import com.hotel.RoyalHorizonHotel_Backend.model.User;
import com.hotel.RoyalHorizonHotel_Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MongoTemplate mongoTemplate;

    @Override
    public User registerUser(User user) {
        List<String> existingEmails = mongoTemplate.query(User.class)
                .distinct("email")
                .as(String.class)
                .all();

        if (existingEmails.contains(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, user.getEmail() + " already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User deleteUser(String email) {
        Query userQuery = new Query();
        userQuery.addCriteria(Criteria.where("email").is(email));

        User user = mongoTemplate.findOne(userQuery, User.class);

        if (user != null) {
            mongoTemplate.remove(userQuery, User.class);
            return user;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    @Override
    public User getUser(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));

        User user = mongoTemplate.findOne(query, User.class);
        if (user != null) {
            return user;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }
}
