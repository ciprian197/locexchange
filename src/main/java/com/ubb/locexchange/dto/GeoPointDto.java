package com.ubb.locexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoPointDto implements Serializable {

    private static final long serialVersionUID = -5223740492908559431L;

    private double x;
    private double y;

}
