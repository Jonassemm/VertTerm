import React ,{useState} from 'react'
import {Form, Table, Card, Col, Container, Button, InputGroup} from 'react-bootstrap';

import DatePicker from "react-datepicker"
import "react-datepicker/dist/react-datepicker.css"


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



export default function AvaiabilityForm() {

    const availabilityRhythm ={
        dayly: "Täglich",
        weekly: "Wöchtenlich",
        monthly: "Monatlich",
        yearly: "Jährlich"
    }

    const [start, setStart] = useState(setDate)
    const [end, setEnd] = useState(setDate)
    const [rhythm, setRhythm] = useState("")
    const [frequency, setFrequency] = useState(1)
    const [endOfSeries, setEndOfSeries] = useState(setDate)

    const [withSeriesEnd, setWithSeriesEnd] = useState(false)
    
    const [availabilities, setAvailabilities] = useState([])
 
    const handleStartChange = date => setStart(date)
    const handleEndChange = date => {setEnd(date), setEndOfSeries(date)}
    const handleRhythmChange = data => setRhythm(data.target.value)
    const handleFrequencyChange = data => setFrequency(data.target.value)
    const handleEndOfSeriesChange = date => setEndOfSeries(date)

    const handleWithSeriesEndChange = data => {if(data.target.value == "false")
                                                {
                                                    setWithSeriesEnd(false);
                                                    setEndOfSeries(null)
                                                } else{
                                                    setWithSeriesEnd(true);
                                                    setEndOfSeries(end)
                                                }}

    //---------------------------------Availability---------------------------------
    //ADD
    const addAvailability = () => {
        console.log("withSerienende")
        console.log(withSeriesEnd)

        const newAvailability = {start, end, rhythm, frequency, endOfSeries}
        console.log(newAvailability)
        setAvailabilities(availabilities => [...availabilities, newAvailability]);
    };


    //---------------------------------RENDERING---------------------------------
    function renderAvailabilityTable() {
        const options = { weekday: 'short', day: 'numeric', month: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit'};
        //const options = {year: 'numeric', weekday: 'long', month: '2-digit',day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit'}
        if(availabilities.length > 0)
        {
          return ( 
            availabilities.map((availability, index) =>(
              <tr key={index}>
                <td><Form.Control readOnly type="text" name={"availability"+ index} style={{width: "170px"}}
                    value={new Intl.DateTimeFormat('de-DE', options).format(availability.start)}/></td>
                <td><Form.Control readOnly type="text" name={"availability"+ index} style={{width: "170px"}}
                    value={new Intl.DateTimeFormat('de-DE', options).format(availability.end)}/></td>
                <td><Form.Control readOnly type="text" name={"availability"+ index} 
                    value={availability.rhythm}/></td>
                <td><Form.Control readOnly type="text" name={"availability"+ index} style={{width: "120px"}}
                    value={availability.frequency}/></td>
                <td><Form.Control readOnly type="text" name={"availability"+ index} style={{width: "170px"}}
                    value={availability.endOfSeries != null ? new Intl.DateTimeFormat('de-DE', options).format(availability.endOfSeries) : "Ohne Ende"}/></td>
                <td><Button>Beenden</Button></td>
              </tr>
            ))
          );
        }
      };

    return (
        <Container>
            <h4 style={{fontWeight:"bold", margin: "20px 0px 20px 0px"}} >Verfügbarkeit</h4>
            <hr style={{ border: "2px solid white" }}/>
            <Form.Row>
                    <Form.Label><h5>Erster verfügbarer Zeitraum</h5></Form.Label>
            </Form.Row>
            <Form.Row>
                    <Form.Group style={{display: "flex", flexWrap: "nowrap"}} as={Col} md="6">
                        <Form.Label style={{marginRight: "20px"}}>Start</Form.Label>
                        <DatePicker
                        required
                        selected={start}
                        onChange={handleStartChange}
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
                        selected={end}
                        onChange={handleEndChange}
                        showTimeSelect
                        timeFormat="HH:mm"
                        timeIntervals={5}
                        timeCaption="Uhrzeit"
                        dateFormat="d.M.yyyy / HH:mm"
                        />
                    </Form.Group>
            </Form.Row>
            <hr style={{ border: "1px dashed white" }}/>
            <Form.Row>
                <Form.Label><h5>Als Serie anlegen</h5></Form.Label>
            </Form.Row>
            <Form.Row >
                <Form.Group style={{display: "flex", flexWrap: "nowrap"}} as={Col} md="7">
                    <div style={{borderStyle: "solid"}} key={`inline-radio-rhythm`} className="mb-3">
                        <Form.Label style={{margin: "0px 10px 0px 10px"}}>Intervall:</Form.Label>
                        <Form.Check inline 
                            defaultChecked
                            name="rhythm" 
                            label="Ohne" 
                            type='radio' 
                            value={""} 
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
                        style={{marginRight: "20px"}}
                        name="endOfSeries" 
                        label="Mit Ende" 
                        type='radio' 
                        onChange={handleWithSeriesEndChange}
                        value={true} 
                        checked={withSeriesEnd}
                        id={`sithSeriesEnd`} />
                    <DatePicker
                        disabled={!withSeriesEnd}
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
                        name="endOfSeries" 
                        label="Ohne Ende" 
                        type='radio' 
                        onChange={handleWithSeriesEndChange}
                        value={false} 
                        checked={!withSeriesEnd}
                        id={`noSeriesEnd`} />
                </Form.Group>
            </Form.Row>
            <hr style={{ border: "2px solid white" }}/>
            <Form.Row>
            <Button onClick={addAvailability} style={{marginBottom: "20px"}}>Verfügbarkeit hinzufügen</Button>
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
        </Container>
    )
}
