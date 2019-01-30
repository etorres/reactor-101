package es.eriktorr.reactor_intro.pollution;

public interface TrafficScanner {

    void register(CarScannedEventListener eventListener);

    void scan();

}