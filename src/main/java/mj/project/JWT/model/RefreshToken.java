package mj.project.JWT.model;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@RedisHash
public class RefreshToken implements Serializable {
    private static final long serialVersionUID = -7353484588260422449L;
    private String email;
    private String refreshToken;
}
