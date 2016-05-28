package com.epsl.peritos.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Admin {

    @Id
    Long id;

    @Index
    private String user;
    private String pass;
    private String name;
    private String email;

    public Admin() {}


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

}