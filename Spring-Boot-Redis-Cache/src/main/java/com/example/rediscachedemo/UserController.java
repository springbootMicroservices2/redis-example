package com.example.rediscachedemo;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private final UserRepository userRepository;

	@Autowired
	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Cacheable(value = "users", key = "#userId", unless = "#result.followers < 12000")
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	public User getUser(@PathVariable Long userId) {
		LOG.info("Getting user with ID {}.", userId);
		Optional<User> user = userRepository.findById(userId);

		if (user.isPresent()) {
			return user.get();
		} else {
			System.out.println("No employee record exist for given id");
			return null;
		}

	}

	@CachePut(value = "users", key = "#user.id")
	@PutMapping("/update")
	public User updatePersonByID(@RequestBody User user) {
		userRepository.save(user);
		return user;
	}

	@CacheEvict(value = "users", allEntries = true)
	@DeleteMapping("/{userId}")
	public void deleteUserByID(@PathVariable Long userId) {
		LOG.info("deleting person with id {}", userId);
		userRepository.deleteById(userId);
	}
}
