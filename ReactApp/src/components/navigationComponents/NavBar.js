//author: Jonas Semmler
import React from "react"
import { Nav, Navbar, Toast } from "react-bootstrap"
import { Link } from 'react-router-dom'
import LoginForm from "./LoginForm"
import { observer } from "mobx-react"
import { hasRight, hasRole } from "../../auth"
import { managementRights, ownAppointmentRights, appointmentRights, adminRole } from "../Rights"
import "./toastStyles.css"



function NavBar({ userStore }) {
    function getToastStyle(type) {
        switch (type) {
            case 'success': return { background: "#def2d6" };
            case 'error': return { background: "#ecc8c5" };
            case 'warning': return { background: "#f8f3d6" };
            case 'info': return { background: "#cde8f5" };
        }
    }
    function getToastTextStyle(type) {
        switch (type) {
            case 'success': return { color: "#5a7052" };
            case 'error': return { color: "#b32f2d" };
            case 'warning': return { color: "#967132" };
            case 'info': return { color: "#4480ae" };
        }
    }

    return (
        <React.Fragment>
                <Navbar variant="dark" bg="dark" expand="lg" justify-content="end" fixed="top">
                    <Navbar.Brand as={Link} to="/" >betabook.me</Navbar.Brand>
                    <Navbar.Toggle aria-controls="basic-navbar-nav" />
                    <Navbar.Collapse id="basic-navbar-nav" className="mr-auto">
                        <Nav className="mr-auto">
                            {(hasRight(userStore, managementRights()) || hasRole(userStore, [adminRole])) &&
                                <Nav.Item className="navbarItem"><Nav.Link as={Link} to="/admin">Verwaltung</Nav.Link></Nav.Item>
                            }
                            {hasRight(userStore, ownAppointmentRights.concat(appointmentRights)) &&
                                <Nav.Item className="navbarItem"><Nav.Link as={Link} to="/warning">Konfliktansicht</Nav.Link></Nav.Item>
                            }
                            {hasRight(userStore, ownAppointmentRights.concat(appointmentRights)) &&
                                <Nav.Item className="navbarItem"><Nav.Link as={Link} to="/appointment">Terminansicht</Nav.Link></Nav.Item>
                            }
                            <Nav.Item className="navbarItem"><Nav.Link as={Link} to="/booking">Buchen</Nav.Link></Nav.Item>
                        </Nav>
                    </Navbar.Collapse >
                    <Nav.Item className="navbarItem" style={{ color: "#bbb", marginRight: "20px", fontSize: "14pt" }}>
                        {userStore.loggedIn ? "Benutzer: " + userStore.username : null}
                    </Nav.Item>
                    <LoginForm userStore={userStore} />
                </Navbar>
            <Toast show={userStore.message} onClose={() => userStore.setMessage(null)} style={{ height: "60px", border: "none", position: "fixed", zIndex: "100003", top: "30px", left: "40%" }} delay={5000} autohide>
                <Toast.Header className="justify-content-md-center" style={userStore.message && Object.assign({ height: "60px", textAlign: "center" }, getToastStyle(userStore.message.type))}>
                    <h5 style={userStore.message && Object.assign({ margin: "0px", padding: "0px", color: "black" },getToastTextStyle(userStore.message.type))}>{userStore.message && userStore.message.message}</h5>
                </Toast.Header>
            </Toast>
        </React.Fragment>
    )
}

export default observer(NavBar)