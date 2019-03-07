package es.eriktorr.reactor_intro.pollution;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import javax.validation.Validation;
import javax.validation.Validator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static es.eriktorr.reactor_intro.pollution.Fuel.fuelFrom;

@Tag("unit")
@DisplayName("Pollution radar")
@ExtendWith(MockitoExtension.class)
class PollutionRadarTest {

    private static final String CARS_CSV_RESOURCE_FILENAME = "cars.csv";

    private SimulatedTraffic simulatedTraffic;
    private TrafficScanner trafficScanner;

    @BeforeEach
    void setUp() {
        simulatedTraffic = loadCarsFromCsv();
        trafficScanner = new TrafficScannerSimulator(simulatedTraffic.allCars);
    }

    @DisplayName("Polluting cars")
    @Test void
    detect_polluting_cars_in_traffic() {
        val pollutionRadar = new PollutionRadar(trafficScanner);

        StepVerifier.create(pollutionRadar.pollutingCars())
                .expectNextCount(344L)
                .as("Polluting cars")
                .expectComplete()
                .verifyThenAssertThat(Duration.ofSeconds(4L))
                .hasDiscardedExactly(Stream.concat(
                        simulatedTraffic.cleanCars.stream(),
                        simulatedTraffic.invalidCars.stream()
                ).toArray());
    }

    @Getter
    @AllArgsConstructor
    private static class SimulatedTraffic {
        private final List<Car> allCars;
        private final List<Car> cleanCars;
        private final List<Car> invalidCars;
    }

    private static class TrafficScannerSimulator implements TrafficScanner {
        private final AtomicBoolean isScanning = new AtomicBoolean(false);

        private final List<Car> cars;
        private CarScannedEventListener eventListener;

        private TrafficScannerSimulator(List<Car> cars) {
            this.cars = cars;
        }

        @Override
        public void register(CarScannedEventListener eventListener) {
            this.eventListener = eventListener;
            if (!isScanning.getAndSet(true)) {
                scan();
            }
        }

        @Override
        public void scan() {
            cars.parallelStream().forEach(car -> eventListener.onScanned(car));
            eventListener.onStopped();
        }
    }

    private SimulatedTraffic loadCarsFromCsv() {
        val allCars = new ArrayList<Car>();
        val cleanCars = new ArrayList<Car>();
        val invalidCars = new ArrayList<Car>();
        val validator = Validation.buildDefaultValidatorFactory().getValidator();
        try (
                val inputStream = getClass().getClassLoader().getResourceAsStream(CARS_CSV_RESOURCE_FILENAME);
                val bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))
        ) {
            val csvParser = CSVFormat.DEFAULT.withHeader().parse(bufferedReader);
            for (final CSVRecord record : csvParser) {
                val carPlateNumber = record.get(0);
                val carManufacturer = record.get(1);
                val carModel = record.get(2);
                val carModelYear = yearFromEmpty(record.get(3));
                val fuelType = fuelFrom(record.get(4));
                val car = new Car(carPlateNumber, carManufacturer, carModel, carModelYear, fuelType);
                allCars.add(car);
                if (!addInvalid(car, validator, invalidCars)) {
                    addClean(car, cleanCars);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("failed to load cars from CSV resource file: " + CARS_CSV_RESOURCE_FILENAME, e);
        }
        return new SimulatedTraffic(allCars, cleanCars, invalidCars);
    }

    private int yearFromEmpty(String value) {
        return !"".equals(value) ? Integer.parseInt(value) : -1;
    }

    private boolean addInvalid(Car car, Validator validator, List<Car> invalidCars) {
        val constraintViolations = validator.validate(car);
        if (!constraintViolations.isEmpty()) {
            invalidCars.add(car);
            return true;
        }
        return false;
    }

    private void addClean(Car car, List<Car> cleanCars) {
        if (!(car.getYear() < 1994 || (Fuel.GASOLINE.equals(car.getFuelType()) && car.getYear() < 2000)
                || (Fuel.DIESEL.equals(car.getFuelType()) && car.getYear() < 2006))) {
            cleanCars.add(car);
        }
    }

}