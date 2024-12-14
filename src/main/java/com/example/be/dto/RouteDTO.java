package com.example.be.dto;

import java.math.BigDecimal;
import com.example.be.model.Routes.RouteStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteDTO {
    private Integer routeId;
    private String routeName;
    private BigDecimal ticketPrice;
    private BigDecimal distance;
    private Integer estimatedDuration;
    private RouteStatus routeStatus;
}