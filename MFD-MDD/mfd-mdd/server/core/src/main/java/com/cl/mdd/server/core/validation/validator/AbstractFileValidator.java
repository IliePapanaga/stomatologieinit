package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.validation.constraint.File;
import com.google.common.collect.Lists;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.cl.mdd.server.core.validation.constraint.File.FileType;
import static com.cl.mdd.server.core.validation.constraint.File.FileType.ANY;
import static com.cl.mdd.server.core.validation.constraint.File.FileType.IMAGE;
import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Objects.isNull;

public abstract class AbstractFileValidator<T> implements ConstraintValidator<File, T> {

    private static final String IMAGE_DETECTION_HINT = "image/*";

    private static final String IMAGE_PNG = "image/png";

    private static final String IMAGE_JPEG = "image/jpeg";

    private static final Map<FileType, Metadata> FILE_TYPE_DETECTION_HINTS = of(IMAGE, new Metadata() {
        {
            set(TikaCoreProperties.FORMAT, IMAGE_DETECTION_HINT);
        }
    });

    private static final Map<FileType, Set<String>> FILE_TYPE_ALLOWED_SUBTYPES = of(IMAGE, newHashSet(IMAGE_PNG, IMAGE_JPEG));

    public static final int BYTES_IN_KILOBYTE = 1024;

    private List<FileType> allowedTypes;

    private long maxSize;

    @Autowired
    private Tika tika;


    @Override
    public void initialize(File constraintAnnotation) {
        allowedTypes = Lists.newArrayList(constraintAnnotation.allowedTypes());
        maxSize = constraintAnnotation.maxSize();
    }

    @Override
    final public boolean isValid(T file, ConstraintValidatorContext context) {
        return isNull(file) || isValid(file);
    }

    private boolean isValid(T file) {
        byte[] byteArray = content(file);
        return byteArray.length / BYTES_IN_KILOBYTE < maxSize && isAnyOfAllowedTypes(byteArray, allowedTypes);
    }

    protected abstract byte[] content(T file);

    private boolean isAnyOfAllowedTypes(byte[] byteArray, List<FileType> allowedTypes) {
        return allowedTypes.contains(ANY) || allowedTypes.stream().anyMatch(fileType -> checkType(byteArray, fileType));
    }

    private boolean checkType(byte[] byteArray, FileType fileType) {
        try {
            return FILE_TYPE_ALLOWED_SUBTYPES.get(fileType).contains(tika.detect(new ByteArrayInputStream(byteArray), FILE_TYPE_DETECTION_HINTS.get(fileType)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
