package ir.carpino.tracker.controller;

import ir.carpino.tracker.controller.exception.CarCategoryNotFoundException;
import ir.carpino.tracker.controller.exception.DriverNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class DriverControllerAdvice {

    @ResponseBody
    @ExceptionHandler(CarCategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String carCategoryNotFoundHandler(CarCategoryNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(DriverNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String driverNotFoundHandler(DriverNotFoundException ex) {
        return ex.getMessage();
    }
}
