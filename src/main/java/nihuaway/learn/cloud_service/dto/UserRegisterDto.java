package nihuaway.learn.cloud_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDto {
	@NotEmpty
	@Email(message = "Некорректный формат почты")
	private String username;

	@NotEmpty
	@Pattern(
		regexp = "^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{6,16}$",
		message = "Пароль должен быть от 6 до 16 символов, содержать хотя бы одну цифру и одну заглавную букву, и состоять только из латинских букв и цифр"
	)
	private String password;
}
