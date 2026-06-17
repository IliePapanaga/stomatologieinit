package com.cl.mdd.server.core.data.model.questionnaire;

import com.cl.mdd.server.core.data.model.MDDModel;

public class SpecialtyComfortLevelModel extends MDDModel {

    private Integer pedo;

    private Integer prostho;

    private Integer perio;

    private Integer endo;

    private Integer general;

    private Integer cosmetic;

    private Integer implants;

    private Integer oralSurgery;

    public Integer getPedo() {
        return pedo;
    }

    public SpecialtyComfortLevelModel setPedo(Integer pedo) {
        this.pedo = pedo;
        return this;
    }

    public Integer getProstho() {
        return prostho;
    }

    public SpecialtyComfortLevelModel setProstho(Integer prostho) {
        this.prostho = prostho;
        return this;
    }

    public Integer getPerio() {
        return perio;
    }

    public SpecialtyComfortLevelModel setPerio(Integer perio) {
        this.perio = perio;
        return this;
    }

    public Integer getEndo() {
        return endo;
    }

    public SpecialtyComfortLevelModel setEndo(Integer endo) {
        this.endo = endo;
        return this;
    }

    public Integer getGeneral() {
        return general;
    }

    public SpecialtyComfortLevelModel setGeneral(Integer general) {
        this.general = general;
        return this;
    }

    public Integer getCosmetic() {
        return cosmetic;
    }

    public SpecialtyComfortLevelModel setCosmetic(Integer cosmetic) {
        this.cosmetic = cosmetic;
        return this;
    }

    public Integer getImplants() {
        return implants;
    }

    public SpecialtyComfortLevelModel setImplants(Integer implants) {
        this.implants = implants;
        return this;
    }

    public Integer getOralSurgery() {
        return oralSurgery;
    }

    public SpecialtyComfortLevelModel setOralSurgery(Integer oralSurgery) {
        this.oralSurgery = oralSurgery;
        return this;
    }
}
