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
public class Folder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	private User author;
	@ManyToOne(fetch = FetchType.LAZY)
	private Folder parent;
	@Column(nullable = false)
	private Date createdAt;
	@Column(nullable = false)
	private Date foldername;
}
