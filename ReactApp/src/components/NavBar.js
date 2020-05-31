import React, { useState, useEffect } from "react"
import { Nav, Navbar, NavItem, NavDropdown } from "react-bootstrap"
import Styled from "styled-components"
import { Link } from 'react-router-dom'
import LoginForm from "./LoginForm"
import { observer } from "mobx-react"
import { hasRole } from "../auth"


const Styles = Styled.div`
.navbar {
    background-color: #222;
    z-index: 1
}

.navbar-brand, .navbar-nav .nav-link {
    color: #bbb;

.nav-item {
    margin-left: 5px;
}

&:hover {
    color:white;
}

&:active{
    color: #bbb
}
}

&visited{
    color: #bbb
}
`


function NavBar({ userStore }) {
    return (
        <Styles>
            <Navbar expand="lg" justify-content="end" fixed="top">
                <Navbar.Brand as={Link} to="/" >VertTerm</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav" className="mr-auto">
                    <Nav className="mr-auto">
                        {//hasRole(userStore, ["ADMIN_ROLE"]) &&
                            <NavDropdown className="mr-auto" title="Benutzerverwaltung" id="user-nav-dropdown">
                                <NavDropdown.Item as={Link} to="/employee">Mitarbeiter</NavDropdown.Item>
                                <NavDropdown.Item as={Link} to="/customer">Kunden</NavDropdown.Item>
                                <NavDropdown.Divider />
                                <NavDropdown.Item as={Link} to="/position">Positionen</NavDropdown.Item>
                                <NavDropdown.Item as={Link} to="/role">Rollen</NavDropdown.Item>
                            </NavDropdown>}
                            <NavDropdown className="mr-auto" title="Ressourcen" id="resource-nav-dropdown">
                                <NavDropdown.Item as={Link} to="/resource">Ressourcen</NavDropdown.Item>
                                <NavDropdown.Divider />
                                <NavDropdown.Item as={Link} to="/resourceType">Ressourcen-Typen</NavDropdown.Item>
                            </NavDropdown>
                        <Nav.Item><Nav.Link as={Link} to="/procedure">Prozeduren</Nav.Link></Nav.Item>
                        <Nav.Item><Nav.Link as={Link} to="/restriction">Einschränkungen</Nav.Link></Nav.Item>
                        <Nav.Item><Nav.Link as={Link} to="/calendar">Kalender</Nav.Link></Nav.Item>    
                        <Nav.Item><Nav.Link as={Link} to="/booking">Buchen</Nav.Link></Nav.Item>

                    </Nav>
                </Navbar.Collapse >
                <Nav.Item style={{ color: "#bbb", marginRight: "10px" }}>
                    {userStore.loggedIn ? "" + userStore.userID : null}
                </Nav.Item>
                <LoginForm userStore={userStore} />
            </Navbar>
        </Styles>
    )
}

export default observer(NavBar)