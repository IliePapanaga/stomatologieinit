package com.cl.mdd.server.contract.pdf;

import com.cl.mdd.server.contract.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import org.apache.commons.io.output.NullOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Template implements Document {

    public static final String PREFIX_SIGNATURE = "signature";
    public static final String PREFIX_CURRENT_DATE = "current_date";
    public static final String DATE_FORMAT = "MMM dd, yyyy";

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private String location;

    // TODO: use input stream constructor
    public Template(String location) throws IOException {
        this.location = location;
        new PdfReader(location).close();
    }

    @Override
    public String type() {
        return "PDF";
    }

    Collection<String> clientFields(Collection<String> fields) {
        return fields.stream()
                .filter(f -> !fieldMatches(f, PREFIX_CURRENT_DATE))
                .map(f -> fieldMatches(f, PREFIX_SIGNATURE) ? "signature" : f)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<String> fields() {
        Set<String> fieldNames;
        try {
            // TODO: cache the fields
            PdfReader reader = new PdfReader(location);
            PdfStamper stamper = new PdfStamper(reader, NullOutputStream.NULL_OUTPUT_STREAM);
            fieldNames = stamper.getAcroFields().getFields().keySet();
            stamper.close();
            reader.close();
        } catch (Exception e) {
            logger.error("Cannot parse PDF template", e);
            throw new IllegalStateException(e);
        }
        return clientFields(fieldNames);
    }

    boolean fieldMatches(String candidate, String field) {
        return candidate.startsWith(field);
    }

    String fieldName(String name) {
        // maybe trim tailing _X
        return name;
    }

    void insertImage(PdfStamper stamper, AcroFields.Item formItem, byte [] signature) throws IOException, DocumentException {
        PdfArray array = formItem.getWidget(0).getAsArray(PdfName.RECT);
        Rectangle rectangle = new Rectangle(
                array.getAsNumber(0).intValue(), array.getAsNumber(1).intValue(),
                array.getAsNumber(2).intValue(), array.getAsNumber(3).intValue());
        Image image = Image.getInstance(signature);
        //image.scaleAbsolute(rectangle);
        image.scaleToFit(rectangle);
        image.setAbsolutePosition(rectangle.getLeft(), rectangle.getBottom());
        stamper.getOverContent(formItem.getPage(0)).addImage(image);
    }

    @Override
    public byte[] render(Map<String, String> parameters, byte[] signature) {
        parameters = new HashMap<>(parameters);
        ByteArrayOutputStream outputStream;
        try {
            PdfReader reader = new PdfReader(location);
            outputStream = new ByteArrayOutputStream();
            PdfStamper stamper = new PdfStamper(reader, outputStream);

            AcroFields form = stamper.getAcroFields();
            for(Map.Entry<String, AcroFields.Item> entry : form.getFields().entrySet()) {
                String originalFieldName = entry.getKey();
                String narrowFieldName = fieldName(originalFieldName);
                if(fieldMatches(originalFieldName, PREFIX_SIGNATURE)) {
                    insertImage(stamper, form.getFields().get(originalFieldName), signature);
                }
                else if(fieldMatches(originalFieldName, PREFIX_CURRENT_DATE)) {
                    form.setField(originalFieldName, new SimpleDateFormat(DATE_FORMAT).format(new Date()));
                }
                else if(parameters.containsKey(narrowFieldName)){
                    form.setField(originalFieldName, parameters.get(narrowFieldName));
                    parameters.remove(narrowFieldName);
                }
                else {
                    logger.debug("form field '{}' not filled in", originalFieldName);
                }
            }

            if(! parameters.isEmpty()) {
                logger.debug("parameters unused during rendering are: {}", parameters.keySet());
            }

            stamper.close();
            reader.close();
        } catch (Exception e) {
            logger.error("Cannot render PDF template", e);
            throw new IllegalStateException(e);
        }
        return outputStream.toByteArray();
    }
}
