package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@AllArgsConstructor
@Service
public class ShipServiceImpl implements ShipService {
    @Autowired
    private ShipRepository shipRepository;



    private Specification<Ship> filterByName(String name) {
        return (root, query, criteriaBuilder) -> name == null ? null :
                criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    private Specification<Ship> filterByPlanet(String planet) {
        return (root, query, criteriaBuilder) -> planet == null ? null :
                criteriaBuilder.like(root.get("planet"), "%" + planet + "%");
    }

    private Specification<Ship> filterByShipType(ShipType shipType) {
        return (root, query, criteriaBuilder) -> shipType == null ? null :
                criteriaBuilder.equal(root.get("shipType"), shipType);
    }

    private Specification<Ship> filterByProdDate(Long after, Long before) {
        return (root, query, criteriaBuilder) -> {
            if(after == null && before == null) {
                return null;
            }
            if (after == null) {
                Date beforeDate = new Date(before);
                return criteriaBuilder.lessThanOrEqualTo(root.get("prodDate"), beforeDate);
            }
            if (before == null) {
                Date afterDate = new Date(after);
                return criteriaBuilder.greaterThanOrEqualTo(root.get("prodDate"), afterDate);
            }
            Date afterDate = new Date(after);
            Date beforeDate = new Date(before);
            return criteriaBuilder.between(root.get("prodDate"), afterDate, beforeDate);
        };
    }

    private Specification<Ship> filterByUsing(Boolean isUsed) {
        return (root, query, criteriaBuilder) -> isUsed == null ? null :
                isUsed ? criteriaBuilder.isTrue(root.get("isUsed")) : criteriaBuilder.isFalse(root.get("isUsed"));
    }

    private Specification<Ship> filterBySpeed(Double minSpeed, Double maxSpeed) {
        return (root, query, criteriaBuilder) -> {
            if (minSpeed == null && maxSpeed == null) {
                return null;
            }
            if (minSpeed == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("speed"), maxSpeed);
            }
            if (maxSpeed == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("speed"), minSpeed);
            }
            return criteriaBuilder.between(root.get("speed"), minSpeed, maxSpeed);
        };
    }

    private Specification<Ship> filterByCrewSize(Integer minCrewSize, Integer maxCrewSize) {
        return (root, query, criteriaBuilder) -> {
            if (minCrewSize == null && maxCrewSize == null) {
                return null;
            }
            if (minCrewSize == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("crewSize"), maxCrewSize);
            }
            if (maxCrewSize == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("crewSize"), minCrewSize);
            }
            return criteriaBuilder.between(root.get("crewSize"), minCrewSize, maxCrewSize);
        };
    }

    private Specification<Ship> filterByRating(Double minRating, Double maxRating) {
        return (root, query, criteriaBuilder) -> {
            if (minRating == null && maxRating == null) {
                return null;
            }
            if (minRating == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("rating"), maxRating);
            }
            if (maxRating == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), minRating);
            }
            return criteriaBuilder.between(root.get("rating"), minRating, maxRating);
        };
    }

    private boolean isParamTrue(Ship ship) {
        if (ship.getName() == null || ship.getName().equals("")
                || ship.getName().length() > 50 || ship.getPlanet() == null
                || ship.getPlanet().equals("")|| ship.getPlanet().length() > 50
                || ship.getProdDate() == null
                || ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() < 2800
                || ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() > 3019
                || ship.getProdDate().getTime() < 0 || ship. getSpeed() == null
                || ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99
                || ship.getCrewSize() == null
                || ship.getCrewSize() < 1 || ship.getCrewSize() > 9999) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return true;
    }

    private boolean checkProdDate(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return  calendar.get(Calendar.YEAR) >= 2800 && calendar.get(Calendar.YEAR) <= 3019;
    }

    private Double getRating(Ship ship) {
        double k = ship.isUsed() ? 0.5 : 1;
        LocalDate localDate = ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (localDate.getYear() > 3019) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        double rating = (80 * ship.getSpeed() * k / (3019 - localDate.getYear()+1));
        return Math.round(rating*100.0)/100.0;
    }

    @Transactional
    @Override
    public List<Ship> getShipsList(String name,
                                   String planet,
                                   ShipType shipType,
                                   Long after,
                                   Long before,
                                   Boolean isUsed,
                                   Double minSpeed,
                                   Double maxSpeed,
                                   Integer minCrewSize,
                                   Integer maxCrewSize,
                                   Double minRating,
                                   Double maxRating,
                                   ShipOrder order,
                                   Integer pageNumber,
                                   Integer pageSize) {
        Specification<Ship> specification = filterByName(name).and(filterByPlanet(planet)).and(filterByShipType(shipType)).
                and(filterByProdDate(after, before)).and(filterByUsing(isUsed)).and(filterBySpeed(minSpeed, maxSpeed)).
                and(filterByCrewSize(minCrewSize, maxCrewSize)).and(filterByRating(minRating, maxRating));
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        return shipRepository.findAll(specification,pageable).getContent();
    }

    @Transactional
    @Override
    public Integer getShipsCount(String name,
                                 String planet,
                                 ShipType shipType,
                                 Long after,
                                 Long before,
                                 Boolean isUsed,
                                 Double minSpeed,
                                 Double maxSpeed,
                                 Integer minCrewSize,
                                 Integer maxCrewSize,
                                 Double minRating,
                                 Double maxRating) {
        Specification<Ship> specification = filterByName(name).and(filterByPlanet(planet)).and(filterByShipType(shipType)).
                and(filterByProdDate(after, before)).and(filterByUsing(isUsed)).and(filterBySpeed(minSpeed, maxSpeed)).
                and(filterByCrewSize(minCrewSize, maxCrewSize)).and(filterByRating(minRating, maxRating));
        return shipRepository.findAll(specification).size();
    }

    @Transactional
    @Override
    public Ship createShip(Ship ship) {
        if (ship == null) {
            return null;
        }
        Ship newShip = new Ship();
        if (isParamTrue(ship)) {
            newShip.setName(ship.getName());
            newShip.setPlanet(ship.getPlanet());
            if (ship.isUsed() == null) {
                newShip.setUsed(false);
            } else {
                newShip.setUsed(ship.isUsed());
            }
            newShip.setProdDate(ship.getProdDate());
            newShip.setShipType(ship.getShipType());
            newShip.setSpeed(ship.getSpeed());
            newShip.setCrewSize(ship.getCrewSize());
        }
        Double rating = getRating(newShip);
        newShip.setRating(rating);
        shipRepository.save(newShip);
        return newShip;
    }

    @Transactional
    @Override
    public Ship getShip(Long id) {
        if (id <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (!shipRepository.findById(id).isPresent()) {
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return shipRepository.findById(id).get();
    }

    @Transactional
    @Override

    public Ship updateShip(Long id, Ship ship) {
        Ship newShip = getShip(id);

        if (ship.getName() == null &&
                ship.getPlanet() == null &&
                ship.getShipType() == null &&
                ship.getProdDate() == null &&
                ship.getSpeed() == null &&
                ship.getCrewSize() == null) return newShip;

        if (ship.getName() != null)
            if (!ship.getName().isEmpty()
                    && ship.getName().length() <= 50)
                newShip.setName(ship.getName());
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        if (ship.getPlanet() != null)
            if (ship.getPlanet().length() <= 50)
                newShip.setPlanet(ship.getPlanet());
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        if (ship.getShipType() != null)
            newShip.setShipType(ship.getShipType());

        if (ship.getProdDate() != null) {
            if (ship.getProdDate().getTime() > 0
                    && checkProdDate(ship.getProdDate()))
                newShip.setProdDate(ship.getProdDate());
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (ship.isUsed() != null)
            newShip.setUsed(ship.isUsed());

        if (ship.getSpeed() != null)
            if (ship.getSpeed() >= 0.01 || ship.getSpeed() <= 0.99)
                newShip.setSpeed(ship.getSpeed());
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        if (ship.getCrewSize() != null)
            if (ship.getCrewSize() >= 1 && ship.getCrewSize() <= 9999)
                newShip.setCrewSize(ship.getCrewSize());
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Double rating = getRating(newShip);
        newShip.setRating(rating);
        shipRepository.saveAndFlush(newShip);

        return newShip;
    }

    @Transactional
    @Override
    public void deleteShip(Long id) {
        if (id <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (!shipRepository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        shipRepository.deleteById(id);
    }


}
