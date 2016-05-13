package com.epsl.peritos.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Usuarios {

    @Id
    Long id;

    @Index
    private String regId;
    private String regFc;

    public Usuarios() {}

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getRegFc() {
        return regFc;
    }

    public void setRegFc(String regFc) {
        this.regFc = regFc;
    }
}