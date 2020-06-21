/*
import { useHistory } from "react-router-dom";

let history = useHistory();
 */


import React, { useState, useEffect } from "react"
import { Container, Form, Col, Row, Button } from "react-bootstrap"
import {Link} from 'react-router-dom';


var moment = require('moment'); 


function AppointmentWarningForm({onCancel, edit, selected}) {

    useEffect(() => { 
        //loadAppointmentGroup()
    }, [])


    const handleSubmit = async () => {
        
    }

    const getWarningsAsString = () =>{
        var warningString
        if(selected.warnings.length > 0){
            selected.warnings.map((singleWarning, index )=> {
                if(index == 0) {
                    warningString += singleWarning
                }
                warningString += "; " + singleWarning
            })
        }
        return warningString
    }

    const rendertest = () => {
        console.log("---------Render-FORM------")
    }
   
    return (
        <div className="page">
            <Container>
                {rendertest()}
                <Form>   
                    <Form.Row>
                       <h4>Wie sollen die Konflikte behandelt werden?</h4>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group>
                            <Form.Label>Konflikte:</Form.Label>
                                <Form.Control
                                    readOnly
                                    style={{background: "white", color: "red", fontWeight: "bold"}}
                                    name="warnings"
                                    type="text"
                                    value={getWarningsAsString()} 
                                />
                        </Form.Group>
                    </Form.Row>
                    <hr/>
                    <div style={{ textAlign: "right" }}>
                        <Button variant="secondary" onClick={handleSubmit} style={{ marginLeft: "10px" }}>Beibehalten</Button>
                        <Button variant="success" onClick={onCancel} style={{ marginLeft: "10px" }}>Terminumbuchung</Button>
                    </div>
                </Form>
            </Container>
        </div >
    )
}

export default AppointmentWarningForm