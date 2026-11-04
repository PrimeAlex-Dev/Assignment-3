package com.fpt.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomTypeResponse {

    private Integer roomTypeID;
    private String roomTypeName;
    private String typeDescription;
    private String typeNote;
}
