package es.eriktorr.reactor_intro.pollution;

import lombok.Value;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Value
class Car {

    @NotEmpty(message = "Plate number expected") private final
    String plateNumber;

    private final
    String manufacturer;

    private final
    String model;

    @Min(value = 1769, message = "No cars were built before 1769") private final
    int year;

    @NotNull(message = "Fuel must be something you can actually pay") private final
    Fuel fuelType;

}