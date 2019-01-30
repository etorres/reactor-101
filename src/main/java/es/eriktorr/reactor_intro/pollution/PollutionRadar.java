package es.eriktorr.reactor_intro.pollution;

import reactor.core.publisher.ParallelFlux;

class PollutionRadar {

    ParallelFlux<Car> pollutingCars() {
        throw new IllegalStateException("feed me!");
    }

}