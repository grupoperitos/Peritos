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
    private String user;
    private String pass;
    private String name;
    private String email;
    private String tipo;

    public Usuarios() {}

    public String getRegId() {
        return regId;
    }

    public String getRegFc() {
        return regFc;
    }

    public String getUser(){
        return user;
    }

    public String getPass(){
        return pass;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getTipo(){
        return tipo;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public void setRegFc(String regFc) {
        this.regFc = regFc;
    }

    public void setUser(String user){
        this.user=user;
    }

    public void setPass(String pass){
        this.pass=pass;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setEmail(String email){
        this.email=email;
    }

    public void setTipo(String tipo){
        this.tipo=tipo;
    }
}