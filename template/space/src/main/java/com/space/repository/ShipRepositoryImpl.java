package com.space.repository;

import com.space.model.Ship;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ShipRepositoryImpl implements ShipRepository {
    @Override
    public List<Ship> getShipsList() {
        return null;
    }

    @Override
    public void createShip(Ship ship) {

    }

    @Override
    public void updateShip(int id) {

    }

    @Override
    public void deleteShip(int id) {

    }

    @Override
    public Ship getShip(int id) {
        return null;
    }

    @Override
    public List<Ship> getShipsFilters() {
        return null;
    }

    @Override
    public int getShipsCount(List<Ship> shipsFilters) {
        return 0;
    }
}
