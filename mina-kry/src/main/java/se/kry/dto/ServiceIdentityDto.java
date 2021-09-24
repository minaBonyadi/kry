package se.kry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.kry.enumeration.Status;

import javax.validation.constraints.NotNull;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceIdentityDto {

    @NotNull(message = "Id should be fill out")
    long id;

    @NotNull(message = "URL should be fill out")
    String url;

    Status status;
}
