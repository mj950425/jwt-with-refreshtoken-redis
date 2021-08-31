package mj.project.JWT.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import mj.project.JWT.model.Role;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

@Data
public class SignUpRequestDto {

    private String email;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    private Role role;
    private String providerId;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;



}