package it.eda.users.write.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.eda.users.write.model.RestResponse;
import it.eda.users.write.model.Utente;
import it.eda.users.write.service.UserService;

@RestController
@RequestMapping("/v1/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		super();
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<RestResponse<Utente>> saveUser(@RequestBody Utente user) {
		return userService.register(user);
	}

	@PostMapping("/login")
	public ResponseEntity<RestResponse<Utente>> findUserByEmail(@RequestBody Utente user) {
		return userService.login(user);
	}
}
