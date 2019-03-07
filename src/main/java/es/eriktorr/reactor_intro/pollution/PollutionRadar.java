package es.eriktorr.reactor_intro.pollution;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

@Slf4j
class PollutionRadar {

    private final TrafficScanner trafficScanner;

    PollutionRadar(TrafficScanner trafficScanner) {
        this.trafficScanner = trafficScanner;
    }

    ParallelFlux<Car> pollutingCars() {
        val validatorFactory = Validation.buildDefaultValidatorFactory();
        val validator = validatorFactory.getValidator();
        return registerWithTrafficScanner()
                .parallel()
                .runOn(Schedulers.parallel())
                .filter(car -> isValid(car, validator))
                .filter(car -> olderThan1994(car) || gasolineMadeBefore2000(car) || dieselMadeBefore2006(car));

    }

    private Flux<Car> registerWithTrafficScanner() {
        return Flux.create(emitter -> trafficScanner.register(new CarScannedEventListener() {
            @Override
            public void onScanned(Car car) {
                emitter.next(car);
            }
            @Override
            public void onStopped() {
                emitter.complete();
            }
        }));
    }

    private boolean isValid(Car car, Validator validator) {
        val constraintViolations = validator.validate(car);
        if (!constraintViolations.isEmpty()) {
            log.error("Invalid car found", new ConstraintViolationException(constraintViolations));
            return false;
        }
        return true;
    }

    private boolean olderThan1994(Car car) {
        return car.getYear() < 1994;
    }

    private boolean gasolineMadeBefore2000(Car car) {
        return Fuel.GASOLINE.equals(car.getFuelType()) && car.getYear() < 2000;
    }

    private boolean dieselMadeBefore2006(Car car) {
        return Fuel.DIESEL.equals(car.getFuelType()) && car.getYear() < 2006;
    }

}