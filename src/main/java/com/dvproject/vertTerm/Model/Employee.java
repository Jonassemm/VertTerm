package com.dvproject.vertTerm.Model;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("user")
public class Employee extends User implements Serializable
{
    private static final long serialVersionUID = -4432631544443788288L;
    
    private boolean isAvailable;
    
    public boolean isAvailable() {
        return isAvailable;
    }
    
    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

}
