import React, { useEffect } from "react"
import "./home.css"
import {Button} from "react-bootstrap"

export default function Home() {
    return (
            <div className="backgroundImage">
                <div className="text">
                    <h1 style={{fontSize:"50px"}}>Willkommen bei betabook.me</h1>
                    <h3>Jetzt Termin vereinbaren</h3>
                    <Button variant="primary" style={{marginTop:"20px"}}>Buchen</Button>
                </div>
                
            </div>
    )
}
