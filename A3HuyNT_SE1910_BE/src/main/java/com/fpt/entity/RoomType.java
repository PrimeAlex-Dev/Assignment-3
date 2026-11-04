package com.fpt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "RoomType")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoomTypeID")
    private Integer roomTypeID;

    @Column(name = "RoomTypeName", nullable = false, length = 100)
    private String roomTypeName;

    @Column(name = "TypeDescription", length = 500)
    private String typeDescription;

    @Column(name = "TypeNote", length = 500)
    private String typeNote;
}
