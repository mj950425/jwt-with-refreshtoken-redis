package mj.project.JWT.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CMRespDto<T> {
    private int code; // 200 성공 500 실패
    private String message;
    private T data;
}
