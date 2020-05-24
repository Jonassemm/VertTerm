import React from 'react'
import {Form, Button} from 'react-bootstrap';
var moment = require('moment'); 

export function setDate() {
    const date = new Date();
    var changeHours = false;

    date.setSeconds(0);

    var minutes = date.getMinutes();
    if (minutes < 15) {
        date.setMinutes(15);
    } else if (minutes < 30) {
        date.setMinutes(30);
    } else if (minutes < 45) {
        date.setMinutes(45);
    } else if (minutes <= 59) {
        date.setMinutes(0);
        changeHours = true;
    }

    var hours = date.getHours();
    if (changeHours) {
        hours = hours + 1;
    } 
    if (hours > 23) {
        hours = 0;
    }
    date.setHours(hours);

   /*  TESTS
    var ds = "13.01.2003 13:45";
    var date = moment(ds, "DD.MM.yyyy HH:mm").toDate();
    var dateString = moment(d).format("DD.MM.YYYY HH:mm").toString();
    console.log("PAT:")
    console.log(d)
    console.log(dss)
    console.log(ds) */

    const resultDate = moment(date).format("DD.MM.YYYY HH:mm").toString()
    return resultDate;
}

export function setValidEndDate(startDateString) {
    var validEndDate = moment(startDateString, "DD.MM.yyyy HH:mm").toDate()
    var startEndDifference = 5
    validEndDate.setMinutes(validEndDate.getMinutes() + startEndDifference)
    return moment(validEndDate).format("DD.MM.YYYY HH:mm").toString()
}

export function validateDates(startDateString, endDateString, endOfSeriesString) {
    var validation = false
    var startDate = moment(startDateString, "DD.MM.yyyy HH:mm").toDate();
    var endDate = moment(endDateString, "DD.MM.yyyy HH:mm").toDate();

    console.log(startDate + " vs " + endDate)
    console.log(startDate > endDate)
    console.log(startDate.getTime() == endDate.getTime())

    if(startDate > endDate) {
        alert("Ungültiges Ende! Das Ende darf nicht vor dem Start sein!")
        return false
    }else if(startDate.getTime() == endDate.getTime()) {
        alert("Ungültiges Ende! Das Ende darf nicht gleich dem Start sein!")
        return false
    } else {
        validation = true
    }

    if(endOfSeriesString != null) {
        var endOfSeries = moment(endOfSeriesString, "DD.MM.yyyy HH:mm").toDate();
        if(endOfSeries <= startDate || endOfSeries < endOfSeries) {
            alert("Ungültiges Serienende! Das Serienende darf nicht vor dem Star und vor dem Ende einer Verfügbarkeit sein!")
            return false
        }
    } else {
        validation = true
    }
    return validation;
}

function availabilityRhythmGE(rhythm) {
    var translation
    switch(rhythm) {
        case "oneTime": translation = "Einmalig"
        break;
        case "daily": translation = "Täglich"
        break;
        case "weekly": translation = "Wöchentlich"
        break;
        case "monthly": translation = "Monatlich"
        break;
        case "yearly": translation = "Jährlich"
        break;
        default: translation = "Translation-Error"
    }
    return translation;
}

//---------------------------------RENDERING---------------------------------
export function renderAvailabilityTable(allAvailabilities, availabilityRhythm, handleCancleAvailability) {
    if(allAvailabilities.length > 0)
    {
      return ( 
        allAvailabilities.map((SingleAvailability, index) => {
            if(moment(SingleAvailability.endOfSeries, "DD.MM.yyyy HH:mm").toDate() < new Date ){
                
            } else {
                return (
                    <tr key={index}>
                      <td><Form.Control readOnly type="text" name={"availability"+ index} style={{width: "180px"}}
                          value={SingleAvailability.startDate}/></td>
                      <td><Form.Control readOnly type="text" name={"availability"+ index} style={{width: "180px"}}
                          value={SingleAvailability.endDate}/></td>
                      <td><Form.Control readOnly type="text" name={"availability"+ index} 
                          value={availabilityRhythmGE(SingleAvailability.rhythm)}/></td>
                      <td><Form.Control readOnly type="text" name={"availability"+ index} style={{width: "120px"}}
                          value={SingleAvailability.rhythm == availabilityRhythm.oneTime ? "-" : SingleAvailability.frequency}/></td>
                      <td><Form.Control readOnly type="text" name={"availability"+ index} style={{width: "18s0px"}}
                          value={ SingleAvailability.endOfSeries != null ? SingleAvailability.endOfSeries : 
                                  SingleAvailability.rhythm == availabilityRhythm.oneTime ? "-" :
                                  "Ohne Ende"}/></td>
      
                      <td><Button variant="danger" value={index} onClick={handleCancleAvailability}>Deaktivieren</Button></td>
                    </tr>
                )
            }
        })
      );
    }
  };
