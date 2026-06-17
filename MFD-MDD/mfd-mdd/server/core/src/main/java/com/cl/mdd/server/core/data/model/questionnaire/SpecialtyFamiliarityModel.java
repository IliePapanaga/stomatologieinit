package com.cl.mdd.server.core.data.model.questionnaire;

import com.cl.mdd.server.core.data.model.MDDModel;

public class SpecialtyFamiliarityModel extends MDDModel {

    private Boolean pedo;

    private Boolean prostho;

    private Boolean perio;

    private Boolean endo;

    private Boolean general;

    private Boolean cosmetic;

    private Boolean implants;

    private Boolean oralSurgery;

    public Boolean getPedo() {
        return pedo;
    }

    public SpecialtyFamiliarityModel setPedo(Boolean pedo) {
        this.pedo = pedo;
        return this;
    }

    public Boolean getProstho() {
        return prostho;
    }

    public SpecialtyFamiliarityModel setProstho(Boolean prostho) {
        this.prostho = prostho;
        return this;
    }

    public Boolean getPerio() {
        return perio;
    }

    public SpecialtyFamiliarityModel setPerio(Boolean perio) {
        this.perio = perio;
        return this;
    }

    public Boolean getEndo() {
        return endo;
    }

    public SpecialtyFamiliarityModel setEndo(Boolean endo) {
        this.endo = endo;
        return this;
    }

    public Boolean getGeneral() {
        return general;
    }

    public SpecialtyFamiliarityModel setGeneral(Boolean general) {
        this.general = general;
        return this;
    }

    public Boolean getCosmetic() {
        return cosmetic;
    }

    public SpecialtyFamiliarityModel setCosmetic(Boolean cosmetic) {
        this.cosmetic = cosmetic;
        return this;
    }

    public Boolean getImplants() {
        return implants;
    }

    public SpecialtyFamiliarityModel setImplants(Boolean implants) {
        this.implants = implants;
        return this;
    }

    public Boolean getOralSurgery() {
        return oralSurgery;
    }

    public SpecialtyFamiliarityModel setOralSurgery(Boolean oralSurgery) {
        this.oralSurgery = oralSurgery;
        return this;
    }
}
