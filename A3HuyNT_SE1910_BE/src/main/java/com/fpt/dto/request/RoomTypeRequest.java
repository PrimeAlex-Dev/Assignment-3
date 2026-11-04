package com.fpt.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomTypeRequest {

    @NotBlank(message = "Tên loại phòng không được để trống")
    @Size(max = 100, message = "Tên loại phòng tối đa 100 ký tự")
    private String roomTypeName;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String typeDescription;

    @Size(max = 500, message = "Ghi chú tối đa 500 ký tự")
    private String typeNote;
}
