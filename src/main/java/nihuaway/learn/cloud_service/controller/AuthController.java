package nihuaway.learn.cloud_service.controller;

import jakarta.validation.Valid;
import nihuaway.learn.cloud_service.dto.UserLoginDto;
import nihuaway.learn.cloud_service.dto.UserRegisterDto;
import nihuaway.learn.cloud_service.entity.User;
import nihuaway.learn.cloud_service.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
	private UserRepository userRepository;

	@Autowired
	public AuthController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody @Valid UserRegisterDto dto) {
		try {
			if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
				throw new BadRequestException("Такой пользователь уже зарегестрирован");
			}

			User user = new User();
			user.setUsername(dto.getUsername());
			user.setPassword(dto.getPassword());
			userRepository.save(user);
			return ResponseEntity.ok().build();
		} catch (BadRequestException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserLoginDto dto) {
		Optional<User> user = userRepository.findByUsername(dto.getUsername());
		if(user.isEmpty()) {
			return new ResponseEntity<>("Такой пользователь не существует", HttpStatus.BAD_REQUEST);
		}
		if(!user.get().getPassword().equals(dto.getPassword())){
			return new ResponseEntity<>("Неверный пароль", HttpStatus.BAD_REQUEST);
		}

		return ResponseEntity.ok().build();
	}

	@PostMapping("/logout")
	public void logout() {

	}
}
