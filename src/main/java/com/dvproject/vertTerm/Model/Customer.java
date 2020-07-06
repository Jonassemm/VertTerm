package com.dvproject.vertTerm.Model;

import java.io.Serializable;
import java.time.Duration;
import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("user")
public class Customer extends User implements Serializable
{
    private static final long serialVersionUID = 7035726826167256599L;
    public Customer(){
        super();
        this.getAvailabilities().add(Availability.Always);
    }
}
