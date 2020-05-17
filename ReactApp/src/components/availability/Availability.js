import React ,{useState} from 'react'
import {Form, Table, Col, Container, Button} from 'react-bootstrap';

import DatePicker from "react-datepicker"
import "react-datepicker/dist/react-datepicker.css"


/* ---------------------------------------------------------FOR USAGE---------------------------------------------------------
-> Usage in your Form:
    <AvailabilityForm  availability={availability} addAvailability={addAvailability} editedAvailability={editedAvailability}/>

-> Make sure you have a state for your availabilities as an array like this: 
    const [availability, setAvailability] = useState([])

-> Make sure you have a state for representing an edit:
    const [edited, setEdited] = useState(false)

-> Create the following two functions in your Form

    const addAvailability = (newAvailability) => {
        setAvailability(availability => [...availability, newAvailability]);
    }

    const editedAvailability = (isEdited) => {
        setEdited(isEdited)
    } 
----------------------------------------------------------------------------------------------------------------------------*/



function setDate() {
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
    return date;
}



const Availability = (props) => {
    //props.addAvailability
    //props.availability
    //props.editedAvailability

    const availabilityRhythm ={
        once: "Einmalig",
        dayly: "Täglich",
        weekly: "Wöchtenlich",
        monthly: "Monatlich",
        yearly: "Jährlich"
    }

    //Availability
    const [startDate, setStartDate] = useState(setDate)
    const [endDate, setEndDate] = useState(setDate)
    const [rhythm, setRhythm] = useState(availabilityRhythm.once)
    const [frequency, setFrequency] = useState(null)
    const [endOfSeries, setEndOfSeries] = useState(null)

    const [withSeriesEnd, setWithSeriesEnd] = useState(false)
    
    /* const [availabilities, setAvailabilities] = useState([]) */
 
    const handleStartDateChange = date => {setStartDate(date); setEndDate(date); props.editedAvailability(true)}
    const handleEndDateChange = date => {setEndDate(date); props.editedAvailability(true)}
    const handleEndOfSeriesChange = date => {setEndOfSeries(date); props.editedAvailability(true)}
    const handleRhythmChange = data => {
        if(data.target.value != availabilityRhythm.once) {
            setFrequency(1)
        }else {
            setFrequency(null)
        }
        setRhythm(data.target.value)
        props.editedAvailability(true)
    }
    const handleFrequencyChange = data =>  {    
        if(rhythm == availabilityRhythm.once) {
            setFrequency(null)
        }else {
            setFrequency(data.target.value)
        }
        props.editedAvailability(true)
    }
    const handleWithSeriesEndChange = data =>  {    
        if(data.target.value == "false"){
            setWithSeriesEnd(false);
            setEndOfSeries(null)
        } else{
            setWithSeriesEnd(true);
            setEndOfSeries(endDate)
        }
        props.editedAvailability(true)
    }

    //---------------------------------Availability---------------------------------
    //ADD
    const handleAdd = () => {
        const newAvailability = {startDate, endDate, rhythm, frequency, endOfSeries}
        props.addAvailability(newAvailability)
        //props.setAvailabilities(props.availability => [...props.availabilityavailability, newAvailability]);
    };


    //---------------------------------RENDERING---------------------------------
    function renderAvailabilityTable() {
        const options = { weekday: 'short', day: 'numeric', month: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit'};
        //const options = {year: 'numeric', weekday: 'long', month: '2-digit',day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit'}
        if(props.availability.length > 0)
        {
          return ( 
            props.availability.map((SingleAvailability, index) =>(
              <tr key={index}>
                <td><Form.Control readOnly type="text" name={"availability"+ index} style={{width: "170px"}}
                    value={new Intl.DateTimeFormat('de-DE', options).format(SingleAvailability.startDate)}/></td>
                <td><Form.Control readOnly type="text" name={"availability"+ index} style={{width: "170px"}}
                    value={new Intl.DateTimeFormat('de-DE', options).format(SingleAvailability.endDate)}/></td>
                <td><Form.Control readOnly type="text" name={"availability"+ index} 
                    value={SingleAvailability.rhythm}/></td>
                <td><Form.Control readOnly type="text" name={"availability"+ index} style={{width: "120px"}}
                    value={SingleAvailability.rhythm == availabilityRhythm.once ? "-" : SingleAvailability.frequency}/></td>
                <td><Form.Control readOnly type="text" name={"availability"+ index} style={{width: "170px"}}
                    value={ SingleAvailability.rhythm == availabilityRhythm.once ? "-" :
                            SingleAvailability.endOfSeries != null ? new Intl.DateTimeFormat('de-DE', options).format(SingleAvailability.endOfSeries) :
                            "Ohne Ende"}/></td>
                <td><Button>Beenden</Button></td>
              </tr>
            ))
          );
        }
      };

    return (
        <React.Fragment>
            <Container>
                <Form id="availabilityAdd" onSubmit={(e) => handleSubmit(e)}>
                    <Form.Row>
                        <Form.Label><h5>Erster verfügbarer Zeitraum</h5></Form.Label>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group style={{display: "flex", flexWrap: "nowrap"}} as={Col} md="6">
                            <Form.Label style={{marginRight: "20px"}}>Start</Form.Label>
                            <DatePicker
                            required
                            selected={startDate}
                            onChange={handleStartDateChange}
                            showTimeSelect
                            timeFormat="HH:mm"
                            timeIntervals={5}
                            timeCaption="Uhrzeit"
                            dateFormat="d.M.yyyy / HH:mm"
                            />
                        </Form.Group>
                        <Form.Group style={{display: "flex", flexWrap: "nowrap"}} as={Col} md="6">
                            <Form.Label style={{marginRight: "20px"}}>Ende</Form.Label>
                            <DatePicker 
                            required
                            selected={endDate}
                            onChange={handleEndDateChange}
                            showTimeSelect
                            timeFormat="HH:mm"
                            timeIntervals={5}
                            timeCaption="Uhrzeit"
                            dateFormat="d.M.yyyy / HH:mm"
                            />
                        </Form.Group>
                    </Form.Row>
                        <hr style={{ border: "0,5px dashed #999999" }}/>
                    <Form.Row>
                        <Form.Label><h5>Serienoptionen</h5> <h6>(aktuell: {rhythm == availabilityRhythm.once ? "OHNE": "MIT"} Serie)</h6></Form.Label>
                    </Form.Row>
                    <Form.Row >
                        <Form.Group style={{display: "flex", flexWrap: "nowrap"}} as={Col} md="7">
                            <div style={{borderStyle: "solid"}} key={`inline-radio-rhythm`} className="mb-3">
                                <Form.Label style={{margin: "0px 10px 0px 10px"}}>Intervall:</Form.Label>
                                <Form.Check inline 
                                    defaultChecked
                                    name="rhythm" 
                                    label="Einmalig"
                                    type='radio' 
                                    value={availabilityRhythm.once}
                                    onChange={handleRhythmChange} 
                                    id={`rhythm-non`} />
                                <Form.Check inline 
                                    name="rhythm" 
                                    label="Täglich" 
                                    type='radio' 
                                    value={availabilityRhythm.dayly} 
                                    onChange={handleRhythmChange} 
                                    id={`rhythm-dayly`} />
                                <Form.Check inline 
                                    name="rhythm" 
                                    label="Wöchentlich" 
                                    type='radio' 
                                    value={availabilityRhythm.weekly} 
                                    onChange={handleRhythmChange} 
                                    id={`rhythm-weekly`} />
                                <Form.Check inline 
                                    name="rhythm" 
                                    label="Monatlich" 
                                    type='radio'
                                    value={availabilityRhythm.monthly} 
                                    onChange={handleRhythmChange} 
                                    id={`rhythm-monthly`} />
                                <Form.Check inline 
                                    name="rhythm" 
                                    label="Jährlich" 
                                    type='radio' 
                                    value={availabilityRhythm.yearly} 
                                    onChange={handleRhythmChange} 
                                    id={`rhythm-yearly`} />
                            </div>
                        </Form.Group>
                        <Form.Group style={{display: "flex", flexWrap: "nowrap"}} as={Col} md="5">
                            <Form.Label style={{margin: "3px 20px 0px 0px"}}>Wiederholungsintervall:</Form.Label>
                            <Form.Control
                                disabled={rhythm == availabilityRhythm.once}
                                style={{width: "70px", height: "30px"}}
                                name="frequency"
                                type="text"
                                placeholder="1"
                                value={frequency || ""}
                                onChange={handleFrequencyChange}
                            />
                        </Form.Group>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group as={Col} md="4">
                            <Form.Check
                                disabled={rhythm == availabilityRhythm.once}
                                style={{marginRight: "20px"}}
                                name="endOfSeries" 
                                label="Mit Ende" 
                                type='radio' 
                                onChange={handleWithSeriesEndChange}
                                value={true} 
                                checked={withSeriesEnd}
                                id={`withSeriesEnd`} />
                            <DatePicker
                                disabled={!withSeriesEnd || rhythm == availabilityRhythm.once}
                                selected={withSeriesEnd ? endOfSeries : null}
                                onChange={handleEndOfSeriesChange}
                                showTimeSelect
                                timeFormat="HH:mm"
                                timeIntervals={5}
                                timeCaption="Uhrzeit"   
                                dateFormat="d.M.yyyy / HH:mm"
                            />
                        </Form.Group>
                        <Form.Group as={Col} md="3">
                            <Form.Check
                                disabled={rhythm == availabilityRhythm.once}
                                name="endOfSeries" 
                                label="Ohne Ende" 
                                type='radio' 
                                onChange={handleWithSeriesEndChange}
                                value={false} 
                                checked={!withSeriesEnd}
                                id={`noSeriesEnd`} />
                        </Form.Group>
                    </Form.Row>
                        <hr style={{ border: "0,5px solid #999999" }}/>
                    <Form.Row>
                        <Button onClick={handleAdd} style={{marginBottom: "20px"}}>Verfügbarkeit hinzufügen</Button>
                    </Form.Row>
                    <Form.Row>
                    <Table striped hover variant="light">
                            <thead>
                                <tr>
                                    <th>Verfügbarkeiten</th>
                                </tr>
                                <tr>
                                    <th>Start</th>
                                    <th>Ende</th>
                                    <th>Intervall</th>
                                    <th>Wdh-Intervall</th>
                                    <th>Serienende</th>
                                </tr>
                            </thead>
                            <tbody>
                                {renderAvailabilityTable()}
                            </tbody>
                        </Table> 
                    </Form.Row>
                </Form>
        </Container>
    </React.Fragment>
    )
}
export default Availability