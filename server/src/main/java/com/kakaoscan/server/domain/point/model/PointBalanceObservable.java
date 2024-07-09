package com.kakaoscan.server.domain.point.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PointBalanceObservable {
    private final List<PointBalanceObserver> observers = new ArrayList<>();

    public void addObserver(PointBalanceObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(PointBalanceObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String userId, int points) {
        for (PointBalanceObserver observer : observers) {
            observer.update(userId, points);
        }
    }
}
