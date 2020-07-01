import React ,{useState, useEffect, useRef} from 'react'
import Availability from "../availabilityComponents/Availability"
import { Form } from 'react-bootstrap'
import styled from "styled-components"
import {Link} from 'react-router-dom';
import {Style} from "../../OverviewPage"
import { Container, Row, Col, Button, Table, Modal } from "react-bootstrap"
import {getOpeningHours, updateOpeningHours} from "./OpeningHoursRequests"



export default function OpeningHoursPage () {

    //Editing
    const [edited, setEdited] = useState(false)
    //Availabilities
    const [availabilities, setAvailabilities] = useState([])
    const [openingHours, setOpeningHours] = useState(null)
    const availabilityRef = useRef();

    useEffect( () => {
        loadOpeningHours()
    },[])

    //---------------------------------Availability---------------------------------
    const addAvailability = (newAvailability) => {
        setAvailabilities(availabilities => [...availabilities, newAvailability]);
    }

    const updateAvailabilities = (newAvailabilities) => {
        setAvailabilities([])
        newAvailabilities.map((SingleAvailability)=> {
        setAvailabilities(availabilities => [...availabilities, SingleAvailability]);
        })
    }

    const loadOpeningHours = async () => {
        var data = {}
        try {
            const respone = await getOpeningHours()
            data = respone.data
        }catch (error) {
            console.log(Object.keys(error), error.message)
        }
        setOpeningHours(data)
        if(data.availabilities != null) {
            setAvailabilities(data.availabilities)
        }
    }

    const handleSubmit = async () => {
        console.log(openingHours)
        try {
            openingHours.availabilities = availabilities
            console.log(openingHours)
            await updateOpeningHours(openingHours)
        }catch (error) {
            console.log(Object.keys(error), error.message)
        }
        setEdited(false)
        resetAvailabilitySettings()
    }

    const render = () => {
        console.log("render")
    }

    const resetAvailabilitySettings = () =>{
        availabilityRef.current.submitted()
    }
    
    return (
        <Style>
            {render}
             <Container>
                <div className="topRow"></div>
                <Row>
                    <h1>Öffnungszeiten</h1>
                </Row>
                <hr/>
                <Row>
                    <Availability
                        ref = {availabilityRef}
                        availabilities={availabilities} 
                        addAvailability={addAvailability}
                        updateAvailabilities={updateAvailabilities} 
                        editedAvailabilities={setEdited}
                        withOpeningHours={false}
                    />
                </Row>
                <hr/>
                <div style={{textAlign: "right"}}>
                    {edited ? 
                        <Button variant="success" onClick={handleSubmit} type="submit" >Übernehmen</Button>:
                        null
                    }
                </div>
            </Container>
        </Style>
    )
}

{/* <Link to={`/`}>
    <Button variant="success" onClick={handleSubmit} type="submit" >Übernehmen</Button>
</Link>: */}