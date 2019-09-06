package com.space.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

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

    @Column(name = "shipType")
    private ShipType shipType;          // Тип корабля

    @Column(name = "prodDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
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
    private Double k;


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPlanet() {
        return planet;
    }

    public ShipType getShipType() {
        return shipType;
    }

    public Date getProdDate() {
        return prodDate;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public Double getSpeed() {
        return speed;
    }

    public Integer getCrewSize() {
        return crewSize;
    }

    public Double getRating() {
        if(isUsed) k =0.5;
        else k= 1.0;
        rating =100*(80*speed*k)/(3019-prodDate.getYear()+1);
        rating = (double)Math.round (rating);
        rating = rating/100;
        return rating;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlanet(String planet) {
        this.planet = planet;
    }

    public void setShipType(ShipType shipType) {
        this.shipType = shipType;
    }

    public void setProdDate(Date prodDate) {
        this.prodDate = prodDate;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public void setCrewSize(Integer crewSize) {
        this.crewSize = crewSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ship ship = (Ship) o;
        return Objects.equals(id, ship.id) &&
                Objects.equals(name, ship.name) &&
                Objects.equals(planet, ship.planet) &&
                shipType == ship.shipType &&
                Objects.equals(prodDate, ship.prodDate) &&
                Objects.equals(isUsed, ship.isUsed) &&
                Objects.equals(speed, ship.speed) &&
                Objects.equals(crewSize, ship.crewSize) &&
                Objects.equals(rating, ship.rating);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, planet, shipType, prodDate, isUsed, speed, crewSize, rating);
    }

    @Override
    public String toString() {
        return "Ship{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", planet='" + planet + '\'' +
                ", shipType=" + shipType +
                ", prodDate=" + prodDate +
                ", isUsed=" + isUsed +
                ", speed=" + speed +
                ", crewSize=" + crewSize +
                ", rating=" + rating +
                '}';
    }
}
