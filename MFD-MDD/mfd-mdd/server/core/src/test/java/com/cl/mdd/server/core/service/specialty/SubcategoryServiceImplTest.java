package com.cl.mdd.server.core.service.specialty;

import com.cl.mdd.server.core.data.model.common.SubcategoryModel;
import com.cl.mdd.server.core.data.persistent.access.specialty.SubCategoryDao;
import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import com.cl.mdd.server.core.manager.converter.CommonConverter;
import com.cl.mdd.server.core.manager.user.SubcategoryManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class SubcategoryServiceImplTest {
    @Spy
    @InjectMocks
    private SubcategoryServiceImpl testClass;

    @Mock
    private SubcategoryManager subcategoryManager;

    @Mock
    private CommonConverter commonConverter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void get() throws Exception {
        String id = "id";
        SubcategoryModel expected = new SubcategoryModel();
        SubCategory subCategory = new SubCategory();
        doReturn(subCategory).when(subcategoryManager).findOne(id);
        doReturn(expected).when(commonConverter).toSubcategoryModel(subCategory);

        SubcategoryModel actual = testClass.get(id);

        verify(subcategoryManager).findOne(id);
        verify(commonConverter).toSubcategoryModel(subCategory);
        assertNotNull(actual);
        assertSame(expected, actual);
    }

    @Test
    public void getAll() throws Exception {
        SubcategoryModel expectedFirst = new SubcategoryModel();
        SubcategoryModel expectedSecond = new SubcategoryModel();
        SubCategory first = new SubCategory();
        SubCategory second = new SubCategory();
        List<SubCategory> subCategories = Arrays.asList(first, second);
        doReturn(subCategories).when(subcategoryManager).findAll();
        doReturn(expectedFirst).when(commonConverter).toSubcategoryModel(first);
        doReturn(expectedSecond).when(commonConverter).toSubcategoryModel(second);

        List<SubcategoryModel> result = testClass.getAll();

        verify(subcategoryManager).findAll();
        verify(commonConverter).toSubcategoryModel(first);
        verify(commonConverter).toSubcategoryModel(second);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(expectedFirst, expectedSecond)));
    }

}