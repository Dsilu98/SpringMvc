package com.example.demo.services.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.UserService;

@Service
public class UserServiceImpl implements UserService{
	private  UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	
	public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
		super();
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User saveUser(User user) {
    	String randomId = UUID.randomUUID().toString();
    	
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
    	user.setId(randomId);
        user.setRole("ROLE_USER");
		return repository.save(user);
	}

	

	@Override
	public Optional<User> findByEmail(String email) {
		return repository.findByEmail(email);
	}

	@Override
	public boolean isEmailAlreadyInUse(String email) {
		return repository.findByEmail(email).isPresent();
	}

}
