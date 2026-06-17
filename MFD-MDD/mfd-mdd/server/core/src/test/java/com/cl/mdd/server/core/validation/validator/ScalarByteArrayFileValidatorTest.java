package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.validation.constraint.File;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tika.Tika;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.imageio.ImageIO;
import javax.validation.ConstraintValidatorContext;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.cl.mdd.server.core.validation.constraint.File.FileType;
import static com.cl.mdd.server.core.validation.constraint.File.FileType.ANY;
import static com.cl.mdd.server.core.validation.constraint.File.FileType.IMAGE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScalarByteArrayFileValidatorTest {

    @InjectMocks
    @Spy
    private ScalarByteArrayFileValidator scalarByteArrayFileValidator;

    @Spy
    private Tika tika;

    @Mock
    private File file;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Before
    public void setUp() {
        when(file.allowedTypes()).thenReturn(new FileType[]{IMAGE});
        when(file.maxSize()).thenReturn(Long.MAX_VALUE);
        scalarByteArrayFileValidator.initialize(file);
    }

    @Test
    public void test() {
        assertTrue(scalarByteArrayFileValidator.isValid(randomImage(), constraintValidatorContext));
    }

    @Test
    public void testAny() {
        when(file.allowedTypes()).thenReturn(new FileType[]{ANY});
        scalarByteArrayFileValidator.initialize(file);
        assertTrue(scalarByteArrayFileValidator.isValid("test".getBytes(), constraintValidatorContext));
    }

    @Test
    public void testSize() {
        when(file.allowedTypes()).thenReturn(new FileType[]{ANY});
        when(file.maxSize()).thenReturn(1L);
        scalarByteArrayFileValidator.initialize(file);
        assertFalse(scalarByteArrayFileValidator.isValid(RandomStringUtils.randomAlphanumeric(2444).getBytes(), constraintValidatorContext));
    }

    @Test
    public void testNull() {
        assertTrue(scalarByteArrayFileValidator.isValid(null, constraintValidatorContext));
    }

    private byte[] randomImage() {
        BufferedImage image = new BufferedImage(128, 128, IndexColorModel.BITMASK);
        for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 128; x++) {
                int a = (int) (Math.random() * 256); //alpha
                int r = (int) (Math.random() * 256); //red
                int g = (int) (Math.random() * 256); //green
                int b = (int) (Math.random() * 256); //blue

                int p = (a << 24) | (r << 16) | (g << 8) | b; //pixel

                image.setRGB(x, y, p);
            }
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toByteArray();
    }

}