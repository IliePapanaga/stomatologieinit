package com.cl.mdd.server.core.data.model.payment;

import com.cl.mdd.server.core.data.model.MDDModel;
import com.cl.mdd.server.core.validation.constraint.payment.PreferredMethod;
import com.cl.mdd.server.core.validation.group.Save;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

@PreferredMethod(groups = Save.class, message = "{payment.instrument.preferred.none}")
public abstract class PaymentInstrumentBase extends MDDModel {

    @NotEmpty(groups = Save.class, message = "{payment.instrument.id.not.empty}")
    private String id;

    @NotEmpty(groups = Save.class, message = "{payment.instrument.label.not.empty}")
    @Size(groups = Save.class, max = 255, message = "{payment.instrument.label.too.long}")
    private String label;

    private boolean preferred;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public PaymentInstrumentBase withId(String id) {
        setId(id);
        return this;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    public PaymentInstrumentBase withLabel(String label) {
        setLabel(label);
        return this;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }
    public PaymentInstrumentBase withPreferred(boolean preferred) {
        setPreferred(preferred);
        return this;
    }
}
