import React from "react"
import "./home.css"
import {Button} from "react-bootstrap"
import { useHistory } from "react-router-dom"

export default function Home() {
    const history = useHistory()

    return (
            <div className="backgroundImage">
                <div className="text">
                    <h1 style={{fontSize:"50px"}}>Willkommen bei betabook.me</h1>
                    <h3>Jetzt Termin vereinbaren</h3>
                    <Button variant="primary" style={{marginTop:"20px"}} onClick={() => history.push("/booking")}>Buchen</Button>
                </div>
                
            </div>
    )
}
