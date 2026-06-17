package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.validation.constraint.File;
import com.cl.mdd.server.core.validation.constraint.File.FileType;
import com.google.gdata.util.common.util.Base64;
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

import static com.cl.mdd.server.core.validation.constraint.File.FileType.ANY;
import static com.cl.mdd.server.core.validation.constraint.File.FileType.IMAGE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class Base64FileValidatorTest {

    @InjectMocks
    @Spy
    private Base64FileValidator base64FileValidator;

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
        base64FileValidator.initialize(file);
    }

    @Test
    public void test() {
        assertTrue(base64FileValidator.isValid(randomImage(), constraintValidatorContext));
    }

    @Test
    public void testAny() {
        when(file.allowedTypes()).thenReturn(new FileType[]{ANY});
        base64FileValidator.initialize(file);
        assertTrue(base64FileValidator.isValid("test", constraintValidatorContext));
    }

    @Test
    public void testSize() {
        when(file.allowedTypes()).thenReturn(new FileType[]{ANY});
        when(file.maxSize()).thenReturn(1L);
        base64FileValidator.initialize(file);
        assertFalse(base64FileValidator.isValid(RandomStringUtils.randomAlphanumeric(2444), constraintValidatorContext));
    }

    @Test
    public void testNull() {
        assertTrue(base64FileValidator.isValid(null, constraintValidatorContext));
    }

    private String randomImage() {
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
        return Base64.encode(output.toByteArray());
    }
}