package es.eriktorr.reactor_intro.pollution;

interface CarScannedEventListener {

    void onScanned(Car car);
    void onStopped();

}