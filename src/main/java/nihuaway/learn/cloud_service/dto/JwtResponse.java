package nihuaway.learn.cloud_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
	private String accessToken;
	private String refreshToken;
}
