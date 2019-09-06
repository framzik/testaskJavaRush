package com.space.model;

import java.io.IOException;
import java.util.Date;

public class ConsoleTest {
    public  static void main(String args[]) throws IOException {
        Ship ship = new Ship("Vasya","Earth",ShipType.MILITARY,0.55,12);
        ship.setProdDate(new Date());
        System.out.println(ship.getK());
        System.out.println(ship.getRating());
    }
}
