package com.example.demo.event;

import com.example.demo.model.User;
import org.springframework.context.ApplicationEvent;

/**
 * Observer Pattern — domain event fired when a new user is registered.
 * Published by UserServiceImpl, consumed by DomainEventListener.
 */
public class UserRegisteredEvent extends ApplicationEvent {

    private final User user;

    public UserRegisteredEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
