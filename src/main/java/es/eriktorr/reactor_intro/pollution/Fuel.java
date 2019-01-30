package es.eriktorr.reactor_intro.pollution;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

enum Fuel {

    GASOLINE("Gasoline"),
    DIESEL("Diesel"),
    HYBRID("Hybrid electric"),
    ELECTRIC("Electric"),
    GAS("Gas");

    @Getter
    private final String literal;

    private static Map<String, Fuel> fuels = new HashMap<>();

    Fuel(String literal) {
        this.literal = literal;
    }

    static {
        Stream.of(Fuel.values()).forEach(fuel -> fuels.put(fuel.literal, fuel));
    }

    static Fuel fuelFrom(String literal) {
        return fuels.get(literal);
    }

}