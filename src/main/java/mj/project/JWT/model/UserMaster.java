package mj.project.JWT.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;
import mj.project.JWT.model.dto.AuthProvider;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="USER_MASTER")
@Builder
public class UserMaster {
	@Id
	@Column(name = "USER_UUID")
	private String userUuid;

	@Column(name = "PROVIDER_ID")
	private String providerId;

	@Enumerated(EnumType.STRING)
	private AuthProvider provider;

	@Column(nullable = false)
	private String email;

	@Enumerated(EnumType.STRING)
	private Role role;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthday;

	@Column(name = "CREATE_DATE")
	private LocalDateTime createDate;

	@PrePersist // 디비에 INSERT 되기 직전에 실행
	public void createDate() {
		this.createDate = LocalDateTime.now();
	}

}
