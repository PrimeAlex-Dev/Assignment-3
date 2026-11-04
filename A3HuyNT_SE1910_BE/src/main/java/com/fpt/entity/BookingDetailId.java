package com.fpt.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailId implements Serializable {

    private Integer bookingReservationID;
    private Integer roomID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingDetailId that = (BookingDetailId) o;
        return Objects.equals(bookingReservationID, that.bookingReservationID) &&
               Objects.equals(roomID, that.roomID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingReservationID, roomID);
    }
}
