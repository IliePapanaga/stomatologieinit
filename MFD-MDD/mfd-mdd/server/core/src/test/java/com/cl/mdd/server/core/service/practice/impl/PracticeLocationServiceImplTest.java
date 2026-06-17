package com.cl.mdd.server.core.service.practice.impl;

import com.cl.mdd.server.core.data.model.AddPracticeLocation;
import com.cl.mdd.server.core.data.model.PracticeLocationModel;
import com.cl.mdd.server.core.data.model.PracticeModel;
import com.cl.mdd.server.core.data.model.UpdatePracticeLocation;
import com.cl.mdd.server.core.data.model.common.AddressModel;
import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.query.FindAllPracticeLocationsQuery;
import com.cl.mdd.server.core.data.model.query.Pagination;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.persistent.access.practice.PracticeLocationDao;
import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.common.embeddable.FullName;
import com.cl.mdd.server.core.data.persistent.model.contact.Address;
import com.cl.mdd.server.core.data.persistent.model.contact.Contact;
import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.manager.converter.CommonConverter;
import com.cl.mdd.server.core.manager.converter.QueryConverter;
import com.cl.mdd.server.core.manager.user.PracticeManager;
import com.cl.mdd.server.core.security.SecurityAccess;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PracticeLocationServiceImplTest {
    private static final String ID = "id";
    @Spy
    @InjectMocks
    private PracticeLocationServiceImpl service;
    @Mock
    private PracticeLocationDao practiceLocationDao;

    @Mock
    private PracticeManager practiceManager;
    @Spy
    private CommonConverter commonConverter = new CommonConverter();
    @Spy
    private QueryConverter queryConverter = new QueryConverter();
    @Mock
    private SecurityAccess securityAccess;
    @Mock
    private QueryResult queryResult;

    @Mock
    private UserDao userDao;

    private PracticeLocation practiceLocation;
    private Practice practice;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        practiceLocation = new PracticeLocation();
        practice = new Practice();

        doReturn(ID).when(securityAccess).currentUserId();
    }

    @Test
    public void addPracticeLocation() throws Exception {
        PracticeLocationModel expected = new PracticeLocationModel();
        ContactModel contactModel = new ContactModel();
        AddressModel addressModel = new AddressModel();
        contactModel.setAddress(addressModel);
        AddPracticeLocation addPracticeLocation = new AddPracticeLocation();
        addPracticeLocation.setContact(contactModel);

        doReturn(practiceLocation).when(commonConverter).toPracticeLocation(addPracticeLocation);
        doReturn(practice).when(practiceManager).get(ID);
        doReturn(practiceLocation).when(practiceLocationDao).save(practiceLocation);
        doReturn(expected).when(commonConverter).toPracticeLocationModel(practiceLocation);

        PracticeLocationModel actual = service.execute(addPracticeLocation);

        verify(commonConverter).toPracticeLocation(addPracticeLocation);
        verify(securityAccess).currentUserId();
        verify(practiceLocationDao).save(practiceLocation);
        verify(commonConverter).toPracticeLocationModel(practiceLocation);

        assertNotNull(actual);
        assertTrue(practice.getLocations().contains(practiceLocation));
        assertSame(practiceLocation.getPractice(), practice);
        assertSame(expected, actual);
    }

    @Test
    public void executeUpdate() throws Exception {
        PracticeLocationModel expected = new PracticeLocationModel();
        UpdatePracticeLocation updatePracticeLocation = new UpdatePracticeLocation();
        ContactModel contactModel = new ContactModel();
        AddressModel addressModel = new AddressModel();
        contactModel.setAddress(addressModel);
        updatePracticeLocation.setContact(contactModel);
        PracticeLocation update = new PracticeLocation();
        update.setId(ID);
        PracticeLocation db = new PracticeLocation();

        doReturn(update).when(commonConverter).toPracticeLocation(updatePracticeLocation);
        doReturn(db).when(practiceLocationDao).findOne(ID);
        doNothing().when(service).updateLocation(update, db);
        doReturn(expected).when(commonConverter).toPracticeLocationModel(update);

        PracticeLocationModel actual = service.execute(updatePracticeLocation);

        verify(commonConverter).toPracticeLocation(updatePracticeLocation);
        verify(practiceLocationDao).findOne(ID);
        verify(commonConverter).toPracticeLocationModel(update);
        assertNotNull(actual);
        assertSame(expected, actual);

    }

    @Test
    public void update() throws Exception {
        Practice fromPractice = new Practice();
        Practice toPractice = new Practice();
        FullName name = new FullName();
        name.setLast("last");
        name.setFirst("first");
        name.setMiddle("middle");
        name.setTitle("title");

        Address address = new Address();
        address.setCountry("country");
        address.setState("state");
        address.setCity("city");
        address.setStreet("street");
        address.setZipCode("zip");


        Contact contact = new Contact();
        contact.setName(name);
        contact.setFax("fax");
        contact.setEmail("email");
        contact.setPhone("phone");

        contact.setAddress(address);

        PracticeLocation from = new PracticeLocation();
        from.setContact(contact);
        from.setName("name");
        from.setPractice(fromPractice);

        PracticeLocation to = new PracticeLocation();
        Contact toContact = new Contact();
        toContact.setName(new FullName());
        toContact.setAddress(new Address());
        to.setContact(toContact);
        to.setPractice(toPractice);

        service.updateLocation(from, to);

        assertEquals(from.getName(), to.getName());
        assertEquals(from.getPractice(), fromPractice);
        assertEquals(to.getPractice(), toPractice);
        assertEquals(contact.getEmail(), to.getContact().getEmail());
        assertEquals(contact.getFax(), to.getContact().getFax());
        assertEquals(contact.getPhone(), to.getContact().getPhone());
        assertSame(contact.getName(), to.getContact().getName());
        assertEquals(address.getCountry(), to.getContact().getAddress().getCountry());
        assertEquals(address.getState(), to.getContact().getAddress().getState());
        assertEquals(address.getCity(), to.getContact().getAddress().getCity());
        assertEquals(address.getStreet(), to.getContact().getAddress().getStreet());
        assertEquals(address.getZipCode(), to.getContact().getAddress().getZipCode());
    }

    @Test
    public void delete() throws Exception {
        doNothing().when(practiceLocationDao).deleteById(ID);

        service.delete(ID);

        verify(practiceLocationDao).deleteById(ID);
    }

    @Test
    public void getPracticeSpecialities() throws Exception {
        PracticeModel practiceModel = new PracticeModel();
        practiceModel.setId(ID);
        PracticeLocation first = new PracticeLocation();
        PracticeLocation second = new PracticeLocation();
        PracticeLocationModel firstModel = new PracticeLocationModel();
        PracticeLocationModel secondModel = new PracticeLocationModel();
        doReturn(firstModel).when(commonConverter).toPracticeLocationModel(first);
        doReturn(secondModel).when(commonConverter).toPracticeLocationModel(second);
        doReturn(Arrays.asList(first, second)).when(practiceLocationDao).findByPracticeId(ID);

        List<PracticeLocationModel> actual = service.getPracticeLocations(practiceModel);

        verify(commonConverter).toPracticeLocationModel(first);
        verify(commonConverter).toPracticeLocationModel(second);
        assertNotNull(actual);
        assertTrue(actual.containsAll(Arrays.asList(firstModel, secondModel)));
    }

    @Test
    public void findAllPracticeLocations() throws Exception {
        Pageable pageable = new PageRequest(1, 2);
        Pagination pagination = new Pagination();
        FindAllPracticeLocationsQuery queryInfo = new FindAllPracticeLocationsQuery();
        FindAllPracticeLocationsQuery.FindAllPracticeLocationsFilter filters = queryInfo.getFilters();
        filters.setNameLike("nameLike");
        filters.setContactPhoneLike("phoneLike");
        filters.setContactFirstNameLike("firstLike");
        filters.setContactLastNameLike("lastLike");
        filters.setContactEmailLike("emailLike");
        PracticeLocation first = new PracticeLocation();
        PracticeLocation second = new PracticeLocation();
        List<PracticeLocation> locations = Arrays.asList(first, second);
        PracticeLocationModel firstModel = new PracticeLocationModel();
        PracticeLocationModel secondModel = new PracticeLocationModel();
        Page<PracticeLocation> practiceLocations = new PageImpl<PracticeLocation>(locations);

        doReturn(pageable).when(queryConverter).toPageable(queryInfo.getPagination());
        doReturn(firstModel).when(commonConverter).toPracticeLocationModel(first);
        doReturn(secondModel).when(commonConverter).toPracticeLocationModel(second);
        doReturn(practiceLocations).when(practiceLocationDao).findAllPracticeLocations(
                filters.getContactEmailLike(),
                filters.getNameLike(),
                filters.getContactPhoneLike(),
                filters.getContactFirstNameLike(),
                filters.getContactLastNameLike(), pageable);
        doReturn(pagination).when(queryConverter).convertToPagination(practiceLocations);

        QueryResult<PracticeLocationModel> result = service.findAllPracticeLocations(queryInfo);

        verify(commonConverter).toPracticeLocationModel(first);
        verify(commonConverter).toPracticeLocationModel(second);
        verify(practiceLocationDao).findAllPracticeLocations(
                filters.getContactEmailLike(),
                filters.getNameLike(),
                filters.getContactPhoneLike(),
                filters.getContactFirstNameLike(),
                filters.getContactLastNameLike(), pageable);
        assertNotNull(result);
        assertNotNull( result.getResult());
        assertEquals(2, result.getResult().size());
        assertTrue(result.getResult().containsAll(Arrays.asList(firstModel, secondModel)));

    }

    @Test
    public void get() throws Exception {
        PracticeLocationModel expected = new PracticeLocationModel();
        doReturn(practiceLocation).when(practiceLocationDao).findOne(ID);
        doReturn(expected).when(commonConverter).toPracticeLocationModel(practiceLocation);

        PracticeLocationModel actual = service.get(ID);

        verify(practiceLocationDao).findOne(ID);
        verify(commonConverter).toPracticeLocationModel(practiceLocation);
        assertNotNull(actual);
        assertSame(expected, actual);
    }
}