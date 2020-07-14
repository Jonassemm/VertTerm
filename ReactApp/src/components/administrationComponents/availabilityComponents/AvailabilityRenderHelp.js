//author: Patrick Venturini
import React from 'react'
import {Form, Button} from 'react-bootstrap';
var moment = require('moment');


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


//-----------------------------------------------------------RENDERING---------------------------------------------------------------------
export function renderAvailabilityTable(allAvailabilities, 
    addedAvailabilitiesLength, 
    availabilityRhythm, 
    handleCancleAvailability, 
    changeAvailabilityAllowed
    ) {
    var startDate = null
    var endDate = null
    var endOfSeries = null
    if(allAvailabilities.length > 0)
    {
      return ( 
        allAvailabilities.map((SingleAvailability, index) => {
            startDate =  moment(moment(SingleAvailability.startDate, "DD.MM.yyyy HH:mm").toDate()).format("dd DD.MM.YYYY HH:mm").toString();
            endDate =  moment(moment(SingleAvailability.endDate, "DD.MM.yyyy HH:mm").toDate()).format("dd DD.MM.YYYY HH:mm").toString();
            endOfSeries = moment(moment(SingleAvailability.endOfSeries, "DD.MM.yyyy HH:mm").toDate()).format("dd DD.MM.YYYY HH:mm").toString();
            if(moment(SingleAvailability.endOfSeries, "DD.MM.yyyy HH:mm").toDate() < new Date ){
                
            } else {
                return (
                    <tr key={index}>
                      <td ><Form.Control readOnly type="text" name={"availability"+ index} 
                          value={startDate}/></td>
                      <td><Form.Control readOnly type="text" name={"availability"+ index} 
                          value={endDate}/></td>
                      <td><Form.Control readOnly type="text" name={"availability"+ index} 
                          value={availabilityRhythmGE(SingleAvailability.rhythm)}/></td>
                      <td><Form.Control readOnly type="text" name={"availability"+ index}
                          value={SingleAvailability.rhythm == availabilityRhythm.oneTime ? "-" : SingleAvailability.frequency}/></td>
                      <td><Form.Control readOnly type="text" name={"availability"+ index} 
                          value={ SingleAvailability.endOfSeries != null ? endOfSeries : 
                                  SingleAvailability.rhythm == availabilityRhythm.oneTime ? "-" :
                                  "Ohne Ende"}/></td>
                      <td>{((allAvailabilities.length - addedAvailabilitiesLength) > index) ? 
                            changeAvailabilityAllowed &&
                            <Button style={{marginTop:"0px"}} variant="danger" value={index} onClick={handleCancleAvailability}>Deaktivieren</Button>:
                            changeAvailabilityAllowed &&
                            <Button style={{marginTop:"0px"}} variant="secondary" value={index} onClick={handleCancleAvailability}>Entfernen</Button>
                        }</td>
                    </tr>
                )
            }
        })
      );
    }
};

