package com.cl.mdd.server.core.service.notification.definition.impl;

import com.cl.mdd.server.core.data.model.notification.NotificationTypeDescriptorModel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Optional;

import static org.junit.Assert.*;

public class NotificationTypeDescriptorRegistryTest {

    private NotificationTypeDescriptorRegistry registry;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        registry = new NotificationTypeDescriptorRegistry();
    }

    @Test
    public void descriptors_whenNew_descriptorShouldBeEmpty() {
        assertNotNull(registry.descriptors());
        assertEquals(0, registry.descriptors().size());
    }

    @Test
    public void put_whenAdded_shouldBeAvailableFromDescriptors() {
        NotificationTypeDescriptorModel model = new NotificationTypeDescriptorModel();
        model.setName("NAME1");

        registry.put(model);

        assertEquals(1, registry.descriptors().size());
        assertEquals(model, registry.descriptors().get(0));
    }

    @Test
    public void put_whenAddedSameType_shouldNotAddDuplicate() {
        NotificationTypeDescriptorModel model = new NotificationTypeDescriptorModel();
        model.setType("NAME1");

        NotificationTypeDescriptorModel sameName = new NotificationTypeDescriptorModel();
        sameName.setType("NAME1");

        registry.put(model);
        registry.put(sameName);

        assertEquals(1, registry.descriptors().size());
    }

    @Test
    public void put_whenAddWithSameTypeButDifferentContent_shouldThrowException() {
        NotificationTypeDescriptorModel model = new NotificationTypeDescriptorModel();
        model.setType("NAME1");
        model.setDescription("Description");

        NotificationTypeDescriptorModel sameNameOtherContent = new NotificationTypeDescriptorModel();
        sameNameOtherContent.setType("NAME1");
        sameNameOtherContent.setDescription("Other DESCRIPTION");

        registry.put(model);

        expectedException.expect(IllegalArgumentException.class);

        registry.put(sameNameOtherContent);
    }

    @Test
    public void byType_whenDescriptorIsPresentByType_shouldReturnOptionalWithIt() {
        NotificationTypeDescriptorModel model = new NotificationTypeDescriptorModel();
        model.setType("TYPE1");

        registry.put(model);

        Optional<NotificationTypeDescriptorModel> result = registry.byType("TYPE1");

        assertTrue(result.isPresent());
        assertSame(model, result.get());
    }

    @Test
    public void byType_whenDescriptionIsNotPresentByType_shouldReturnEmptyOptional() {
        Optional<NotificationTypeDescriptorModel> result = registry.byType("Not_EXISTING_TYPE");

        assertFalse(result.isPresent());
    }
}