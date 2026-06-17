package com.cl.mdd.server.contract.pdf;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PdfTemplateIT {

    Template practice;

    Template pro;

    byte[] signature;

    @Before
    public void setUp() throws IOException {
        practice = new Template(new ClassPathResource("MDD_PRACTICE.pdf").getURL().toString());
        pro = new Template(new ClassPathResource("MDD_PRO.pdf").getURL().toString());
        signature = IOUtils.toByteArray(new ClassPathResource("signature.png").getInputStream());
    }

    @Test
    public void testFields() {
        Assert.assertFalse(CollectionUtils.isEmpty(practice.fields()));
        Assert.assertTrue(practice.fields().contains("signature"));
        Assert.assertFalse(CollectionUtils.isEmpty(pro.fields()));
        Assert.assertTrue(pro.fields().contains("signature"));
    }

    private void render(Template template, Map<String, String> data, String file) throws IOException {
        FileOutputStream output = new FileOutputStream(file);
        output.write(template.render(data, signature));
        output.close();
        Assert.assertTrue(new File(file).exists());
    }

    @Test
    public void testPractice() throws IOException {
        Map<String, String> data = new HashMap<>();
        data.put("practice_name", "Southfield Family Dental Center");
        data.put("practice_address", "18800 W 10 Mile Rd, Southfield, MI 48075");
        data.put("practice_signer_name", "Dr. Levi");
//        data.put("practice_signer_title", "Big boss");
        data.put("unused", "parameter");
        render(practice, data, "./target/contract_signed_doc.pdf");
    }

    @Test
    public void testPro() throws IOException {
        Map<String, String> data = new HashMap<>();
        data.put("pro_print_name", "John Smith");
        data.put("pro_title", "Super RDH");
        render(pro, data, "./target/contract_signed_pro.pdf");
    }
}
