import React from "react"
import { Nav, Navbar, Toast} from "react-bootstrap"
import Styled from "styled-components"
import { Link } from 'react-router-dom'
import LoginForm from "./LoginForm"
import { observer } from "mobx-react"
import { hasRight } from "../../auth"
import { managementRights, ownAppointmentRights, appointmentRights } from "../Rights"


const Styles = Styled.div`
.nav-item {
    margin-left: 5px;
}
`

function NavBar({ userStore }) {
    console.log(document.cookie)
    return (
        <React.Fragment>
            <Styles>
                <Navbar variant="dark" bg="dark" expand="lg" justify-content="end" fixed="top">
                    <Navbar.Brand as={Link} to="/" >betabook.me</Navbar.Brand>
                    <Navbar.Toggle aria-controls="basic-navbar-nav" />
                    <Navbar.Collapse id="basic-navbar-nav" className="mr-auto">
                        <Nav className="mr-auto">
                            {hasRight(userStore, managementRights()) &&
                                <Nav.Item><Nav.Link as={Link} to="/admin">Verwaltung</Nav.Link></Nav.Item>
                            }
                            {hasRight(userStore, ownAppointmentRights.concat(appointmentRights)) && 
                                <Nav.Item><Nav.Link as={Link} to="/warning">Konfliktansicht</Nav.Link></Nav.Item>
                            }
                            {hasRight(userStore, ownAppointmentRights.concat(appointmentRights)) &&
                                <Nav.Item><Nav.Link as={Link} to="/appointment">Terminansicht</Nav.Link></Nav.Item>
                            }       
                            <Nav.Item><Nav.Link as={Link} to="/booking">Buchen</Nav.Link></Nav.Item>
                        </Nav>
                    </Navbar.Collapse >
                    <Nav.Item style={{ color: "#bbb", marginRight: "20px", fontSize:"14pt"}}>
                        {userStore.loggedIn ? "Benutzer: " + userStore.username : null}
                    </Nav.Item>
                    <LoginForm userStore={userStore} />
                </Navbar>
            </Styles>
            <Toast show={userStore.message} onClose={() => userStore.setMessage(null)} style={{ width: "600px", height: "60px", border: "none", position: "fixed", zIndex: "100000", top: "30px", left: "40%" }} delay={5000} autohide>
                <Toast.Header className="justify-content-md-center" style={{ height: "60px", textAlign: "center", backgroundColor: "#def2d6" }}>
                    <h5 style={{margin:"0px", padding:"0px", color: "#5a7052" }}>{userStore.message}</h5>
                </Toast.Header>
            </Toast>
        </React.Fragment>
    )
}

export default observer(NavBar)