//author: Jonas Semmler
import React from "react"
import { APIURL } from "../../APIConfig"
import ReactQRCode from "qrcode.react"
import { Container, Card } from "react-bootstrap"

export default function QRCode({cred,userStore}) {
    const credString = cred
    const link = `${APIURL}/api/login/${credString}`

    function handleQRClick() {
        //userStore.setMessage(null)
        window.print()
    }
    return (
        <div className="page">
            <Container style={{ display: "flex", alignItems: "center", padding:"5px",flexDirection:"column" }}>
                    <ReactQRCode value={link} onClick={handleQRClick} size="200"/>
                    <div style={{marginTop:"10px"}}>{link}</div>
            </Container>
        </div>
    )
}