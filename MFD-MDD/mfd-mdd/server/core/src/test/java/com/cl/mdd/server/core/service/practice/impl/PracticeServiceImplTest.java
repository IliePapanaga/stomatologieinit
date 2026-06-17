package com.cl.mdd.server.core.service.practice.impl;

import com.cl.mdd.server.core.data.model.PracticeModel;
import com.cl.mdd.server.core.data.model.RegisterPractice;
import com.cl.mdd.server.core.data.model.common.SpecialityModel;
import com.cl.mdd.server.core.data.persistent.model.common.Speciality;
import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.manager.converter.CommonConverter;
import com.cl.mdd.server.core.manager.user.PracticeManager;
import com.cl.mdd.server.core.manager.user.PracticeOwnerManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class PracticeServiceImplTest {
    private final static String ID = "id";
    @Spy
    @InjectMocks
    private PracticeServiceImpl service;
    @Mock
    private PracticeManager practiceManager;
    @Mock
    private PracticeOwnerManager practiceOwnerManager;
    @Mock
    private CommonConverter commonConverter;
    private PracticeOwner practiceOwner;
    private Practice practice;

    @Captor
    private ArgumentCaptor<Practice> practiceArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        practiceOwner = new PracticeOwner();
        practice = new Practice();
    }

    @Test
    public void register() throws Exception {
        PracticeModel expected = new PracticeModel();
        RegisterPractice registerPractice = new RegisterPractice();
        doReturn(practiceOwner).when(practiceOwnerManager).get(ID);
        doReturn(practice).when(commonConverter).toPractice(registerPractice);
        doReturn(practice).when(practiceManager).save(practice);
        doReturn(expected).when(commonConverter).toPracticeModel(practice);

        PracticeModel actual = service.register(registerPractice, ID);

        verify(practiceOwnerManager).get(ID);
        verify(commonConverter).toPractice(registerPractice);
        verify(practiceManager).save(practiceArgumentCaptor.capture());
        Practice savedPractice = practiceArgumentCaptor.getValue();
        assertNotNull(savedPractice);
        assertSame(practice, savedPractice);
        assertSame(practiceOwner, practice.getOwner());
        assertNotNull(actual);
        assertSame(expected, actual);
    }
    @Test
    public void getById() throws Exception {
        PracticeModel expected = new PracticeModel();
        doReturn(practice).when(practiceManager).get(ID);
        doReturn(expected).when(commonConverter).toPracticeModel(practice);

        PracticeModel actual = service.getById(ID);

        verify(practiceManager).get(ID);
        verify(commonConverter).toPracticeModel(practice);
        assertNotNull(actual);
        assertSame(expected, actual);
    }
    @Test
    public void getByOwnerUsername() throws Exception {
        String username = "username";
        PracticeModel expected = new PracticeModel();
        doReturn(practice).when(practiceManager).findOneByOwnerUsername(username);
        doReturn(expected).when(commonConverter).toPracticeModel(practice);

        PracticeModel actual = service.getByOwnerUsername(username);

        verify(practiceManager).findOneByOwnerUsername(username);
        verify(commonConverter).toPracticeModel(practice);
        assertNotNull(actual);
        assertSame(expected, actual);
    }

    @Test
    public void getPracticeSpecialities() throws Exception {
        PracticeModel practiceModel = new PracticeModel();
        practiceModel.setId(ID);
        Speciality first = new Speciality();
        Speciality second = new Speciality();
        SpecialityModel firstModel = new SpecialityModel();
        SpecialityModel secondModel = new SpecialityModel();
        doReturn(firstModel).when(commonConverter).toSpecialityModel(first);
        doReturn(secondModel).when(commonConverter).toSpecialityModel(second);
        doReturn(Arrays.asList(first, second)).when(practiceManager).getPracticeSpecialities(ID);

        List<SpecialityModel> actual = service.getPracticeSpecialities(practiceModel);

        verify(commonConverter).toSpecialityModel(first);
        verify(commonConverter).toSpecialityModel(second);
        assertNotNull(actual);
        assertTrue(actual.containsAll(Arrays.asList(firstModel, secondModel)));
    }
}