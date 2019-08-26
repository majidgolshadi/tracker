package ir.carpino.tracker.controller.exception;

public class CarCategoryNotFoundException extends RuntimeException {

    public CarCategoryNotFoundException(String message) {
        super(message);
    }
}
