package com.example.demo.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice(basePackages = "com.example.demo.controller.mvc")
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException ex) {
        return errorView(404, ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ModelAndView handleBusiness(BusinessException ex) {
        return errorView(400, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ModelAndView handleIllegalState(IllegalStateException ex) {
        return errorView(400, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDenied(AccessDeniedException ex) {
        return errorView(403, "Доступ запрещён. Недостаточно прав.");
    }

    @ExceptionHandler(BindException.class)
    public ModelAndView handleBind(BindException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .findFirst().orElse("Ошибка валидации");
        return errorView(400, msg);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ModelAndView handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .findFirst().orElse("Ошибка валидации");
        return errorView(400, msg);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneric(Exception ex) {
        return errorView(500, "Внутренняя ошибка сервера: " + ex.getMessage());
    }

    private ModelAndView errorView(int status, String message) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", status);
        mav.addObject("message", message);
        return mav;
    }
}
