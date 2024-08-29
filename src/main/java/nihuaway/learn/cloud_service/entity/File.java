package nihuaway.learn.cloud_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class File {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private User author;
	@Column(nullable = false)
	private String filename;
	@Column(nullable = false)
	private String filetype;
	@Column(nullable = false)
	private String path;
	@Column(nullable = false)
	private Date createdAt;
}
