package com.cl.mdd.server.contract;

import java.util.Collection;
import java.util.Map;

/**
 * A contract template.
 * <p />
 * Thread-safe.
 * <p />
 * Can be used for reading some meta-information about the template
 * and for filling in the template (rendering) with real values.
 */
public interface Document {

    /**
     * Document type, e.g. PDF.
     * @return type
     */
    String type();

    /**
     * List fields present in the PDF document template.
     * <p />
     * Fields with special handling (e.g. current date) will not be reported even if present in the template.
     * @return field names
     */
    Collection<String> fields();

    /**
     * Render a template using parameters.
     * @param parameters parameters to fill in the template
     * @param signature signature for the template as an image file (e.g. PNG or JPG)
     * @return rendered document
     */
    byte [] render(Map<String, String> parameters, byte [] signature);

}
