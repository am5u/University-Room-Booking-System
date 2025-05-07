package com.univeristy.userService.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Room {
    private Long id;
    private String name;
    private Integer capacity;
    private String location;
    private String roomType;
    private String description;
    private String availableEquipment;
    private String imageUrl;
}
