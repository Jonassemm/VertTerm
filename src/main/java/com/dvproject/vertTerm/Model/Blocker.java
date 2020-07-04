package com.dvproject.vertTerm.Model;
import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

//author Amar Alkhankan
public class Blocker extends Appointment{

    @Indexed(unique = true)
    private String name;
    
    public Blocker () {
    }
    
    public Blocker (String name) {
    	this.setName(name);
    }
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    

}
