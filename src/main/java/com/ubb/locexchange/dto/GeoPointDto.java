package com.ubb.locexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoPointDto implements Serializable {

    private static final long serialVersionUID = -5223740492908559431L;

    @Min(value = -180, message = "{longitude.invalid}")
    @Max(value = 180, message = "{longitude.invalid}")
    private double x;

    @Min(value = -90, message = "{latitude.invalid}")
    @Max(value = 90, message = "{latitude.invalid}")
    private double y;

}
