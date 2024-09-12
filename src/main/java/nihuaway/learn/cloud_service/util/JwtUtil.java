package nihuaway.learn.cloud_service.util;

import io.jsonwebtoken.Jwts;
import lombok.Getter;
import nihuaway.learn.cloud_service.entity.User;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
	@Getter
	private static final SecretKey secretKey = initKey();
	@Getter
	private static final Date refreshTokenExpiration = Date.from(Instant.now().plusSeconds(6000L));
	@Getter
	private static final Date accessTokenExpiration = Date.from(Instant.now().plusSeconds(600L));

	public static SecretKey initKey() {
		return 	Jwts.SIG.HS256.key().build();
	}

	public static String createToken(Map<String, ?> claims, Date expiration) {
		return Jwts.builder()
				.claims(claims)
				.expiration(expiration)
				.signWith(secretKey)
				.compact();
	}

}
