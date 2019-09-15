package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipsRepository;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;



@Service
@Data
public class ShipServiceImpl implements ShipService {

    ShipsRepository shipsRepository;

    @Autowired
    public ShipServiceImpl(ShipsRepository shipsRepository) {
        this.shipsRepository = shipsRepository;
    }

    private Specification<Ship> findByName(String name) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return name == null ? null :
                        criteriaBuilder.like(root.get("name"), "%" + name + "%");
            }
        };
    }

    private Specification<Ship> findByPlanet(String planet) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return planet == null ? null : criteriaBuilder.like(root.get("planet"), "%" + planet + "%");
            }
        };
    }

    private Specification<Ship> findByShipType(ShipType shipType) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return shipType == null ? null : criteriaBuilder.equal(root.get("shipType"), shipType);
            }
        };
    }

    private Specification<Ship> findByProdDate(Long after, Long before) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (after == null && before == null) {
                    return null;
                } else if (after == null) {
                    Date beforeDate = new Date(before);
                    return criteriaBuilder.lessThanOrEqualTo(root.get("prodDate"), beforeDate);
                } else if (before == null) {
                    Date afterDate = new Date(after);
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("prodDate"), afterDate);
                } else {
                    Date afterDate = new Date(after);
                    Date beforeDate = new Date(before);
                    return criteriaBuilder.between(root.get("prodDate"), afterDate, beforeDate);
                }
            }
        };
    }

    private Specification<Ship> findByIsUsed(Boolean isUsed) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return isUsed == null ? null :
                        isUsed ? criteriaBuilder.isTrue(root.get("isUsed")) : criteriaBuilder.isFalse(root.get("isUsed"));
            }
        };
    }

    private Specification<Ship> findBySpeed(Double minSpeed, Double maxSpeed) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (minSpeed == null && maxSpeed == null) {
                    return null;
                } else if (minSpeed == null) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("speed"), maxSpeed);
                } else if (maxSpeed == null) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("speed"), minSpeed);
                } else {
                    return criteriaBuilder.between(root.get("speed"), minSpeed, maxSpeed);
                }
            }
        };
    }

    private Specification<Ship> findByCrewSize(Integer minCrewSize, Integer maxCrewSize) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (minCrewSize == null && maxCrewSize == null) {
                    return null;
                } else if (minCrewSize == null) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("crewSize"), maxCrewSize);
                } else if (maxCrewSize == null) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("crewSize"), minCrewSize);
                } else {
                    return criteriaBuilder.between(root.get("crewSize"), minCrewSize, maxCrewSize);
                }
            }
        };
    }

    private Specification<Ship> findByRating(Double minRating, Double maxRating) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (minRating == null && maxRating == null) {
                    return null;
                } else if (minRating == null) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("rating"), maxRating);
                } else if (maxRating == null) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), minRating);
                } else {
                    return criteriaBuilder.between(root.get("rating"), minRating, maxRating);
                }
            }
        };
    }

    private boolean isTrueParam(Ship ship) {
        if (ship.getName() == null || ship.getName().equals("")
                || ship.getName().length() > 50
                || ship.getProdDate().getYear() < 2800
                || ship.getProdDate().getYear() > 3019
                || ship.getProdDate() == null
                || ship.getProdDate().getTime() < 0
                || ship.getSpeed() == null
                || ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99
                || ship.getPlanet() == null || ship.getPlanet().equals("")
                || ship.getPlanet().length() > 50
                || ship.getCrewSize() == null
                || ship.getCrewSize() < 1 || ship.getCrewSize() > 9999) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return true;
    }

    private Double getRating(Ship ship) {
        Double k;
        if (ship.getIsUsed()) k = 0.5;
        else k = 1.0;
        if(ship.getProdDate().getYear()>3019)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        else {
            Double rating = 100 * (80 * ship.getSpeed() * k) / (3019 - ship.getProdDate().getYear() + 1);
            rating = (double) Math.round(rating);
            rating = rating / 100;
            return rating;
        }
    }


    @Transactional
    @Override
    public List<Ship> getShipsList(String name,
                                   String planet,
                                   ShipType shipType,
                                   Long after, Long before,
                                   Boolean isUsed,
                                   Double minSpeed, Double maxSpeed,
                                   Integer minCrewSize, Integer maxCrewSize,
                                   Double minRating, Double maxRating,
                                   ShipOrder order,
                                   Integer pageNumber,
                                   Integer pageSize) {
        Specification<Ship> specification = findByName(name)
                .and(findByPlanet(planet))
                .and(findByShipType(shipType))
                .and(findByProdDate(after,before))
                .and(findByIsUsed(isUsed))
                .and(findBySpeed(minSpeed,maxSpeed))
                .and(findByCrewSize(minCrewSize,maxCrewSize))
                .and(findByRating(minRating,maxRating));
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        return shipsRepository.findAll(specification,pageable).getContent();
    }
    @Transactional
    @Override
    public Long getShipsCount(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating) {
        return null;
    }
    @Transactional
    @Override
    public Ship createShip(Ship ship) {
        return null;
    }
    @Transactional
    @Override
    public Ship getShip(Long id) {
        return null;
    }
    @Transactional
    @Override
    public Ship updateShip(Long id, Ship ship) {
        return null;
    }
    @Transactional
    @Override
    public void deleteShip(Long id) {

    }
}
