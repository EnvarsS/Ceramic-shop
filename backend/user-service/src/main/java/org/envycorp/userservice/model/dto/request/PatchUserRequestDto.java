package org.envycorp.userservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PatchUserRequestDto {
    private String address;
    private String birthDate;
}
