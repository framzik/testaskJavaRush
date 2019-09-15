package com.space.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ship")
public class Ship {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    //ID корабля

    @Column(name = "name")
    private String name;                // Название корабля (до 50 знаков включительно)

    @Column(name = "planet")
    private String planet;              // Планета пребывания (до 50 знаков включительно)

    @Enumerated(EnumType.STRING)
    private ShipType shipType;          // Тип корабля

    @Column(name = "prodDate")
    private Date prodDate;              // Дата выпуска.  Диапазон значений года 2800..3019 включительно

    @Column(name = "isUsed")
    private Boolean isUsed = false;     // Использованный / новый

    @Column(name = "speed")
    private Double speed;               // Максимальная скорость корабля. Диапазон значений 0,01..0,99 включительно. Используй математическое
    // округление до сотых

    @Column(name = "crewSize")
    private Integer crewSize;           // Количество членов экипажа. Диапазон значений
    // 1..9999 включительно.

    @Column(name = "rating")
    private Double rating;              // Рейтинг корабля. Используй математическое
    //    округление до сотых.

}
