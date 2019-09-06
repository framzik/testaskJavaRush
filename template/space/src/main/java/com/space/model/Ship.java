package com.space.model;

import java.util.Date;

public class Ship {
    private Long id;                // ID корабля
    private String name;            // Название корабля (до 50 знаков включительно)
    private String planet;          // Планета пребывания (до 50 знаков включительно)
    private ShipType shipType;      // Тип корабля
    private Date prodDate;          // Дата выпуска.    Диапазон значений года 2800..3019 включительно
    private Boolean isUsed = false;         // Использованный / новый
    private Double speed;           //Максимальная скорость корабля. Диапазон значений 0,01..0,99 включительно. Используй математическое округление до сотых.
    private Integer crewSize;       // Количество членов экипажа. Диапазон значений1..9999 включительно.

    private Double rating = (double) Math.round( (getSpeed() * 80 * getK() ) /
            (3019 - getProdDate().getYear()+1))/10;          //Рейтинг корабля. Используй математическое    округление до сотых

    public Ship(String name, String planet, ShipType shipType, Double speed, Integer crewSize) {
        this.name = name;
        this.planet = planet;
        this.shipType = shipType;
        this.prodDate = prodDate;
        this.speed = speed;
        this.crewSize = crewSize;

    }

    private Double k;

    public Double getK(){
        if(getUsed()) k= 0.5;
        k= 1.0;
        return k;
    }

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
        return rating;
    }

    public void setId(Long id) {
        this.id = id;
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
}
