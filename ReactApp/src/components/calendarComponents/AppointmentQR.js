import React from "react"
import { APIURL } from "../../APIConfig"
import QRCode from "qrcode.react"
import { Container, Card } from "react-bootstrap"

export default function AppointmentQR({cred}) {
    const credString = cred
    //const link = APIURL + `/apts/${credString}`
    const link = `${APIURL}/api/${credString}`

    return (
        <div className="page">
            <Container style={{ display: "flex", alignItems: "center", padding:"5px",flexDirection:"column" }}>
                    <QRCode value={link} onClick={() => window.print()} size="200"/>
                    <div style={{marginTop:"10px"}}>{link}</div>
            </Container>
        </div>
    )
}