package com.cl.mdd.server.core.service.notification.definition.impl;

import com.cl.mdd.server.core.data.model.notification.NotificationTypeDescriptorModel;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Registry of all supported notification types in application
 */
@Component
public class NotificationTypeDescriptorRegistry {

    private final List<NotificationTypeDescriptorModel> descriptors;

    public NotificationTypeDescriptorRegistry() {
        this.descriptors = new ArrayList<>();
    }

    public Optional<NotificationTypeDescriptorModel> byType(String type) {
        return descriptors.stream()
                .filter(descriptor -> descriptor.getType().equals(type))
                .findAny();
    }

    public List<NotificationTypeDescriptorModel> descriptors() {
        return Collections.unmodifiableList(descriptors);
    }

    public void put(NotificationTypeDescriptorModel model) {
        Optional<NotificationTypeDescriptorModel> existing = descriptors.stream()
                .filter(descriptor -> descriptor.getType().equals(model.getType()))
                .findFirst();

        if (existing.isPresent()) {
            if (!existing.get().equals(model)) {
                throw new IllegalArgumentException("Found same notification types with different variables or description");
            }
        } else {
            descriptors.add(model);
        }
    }
}
