import React from "react"
import {Nav, Navbar, NavItem, NavDropdown} from "react-bootstrap"
import Styled from "styled-components"
import {Link} from 'react-router-dom'

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
}
`

function Header() {
    return (
        <Styles>
            <Navbar expand="lg" justify-content="end">
                <Navbar.Brand as={Link} to="/" >VertTerm</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav"/>
                <Nav className="ml-auto">
                    <NavDropdown className="mr-auto" title="Benutzer" id="collasible-nav-dropdown">
                                <NavDropdown.Item href="/employee/add">Mitarbeiter anlegen</NavDropdown.Item>
                                <NavDropdown.Item href="/employee/list">Mitarbeiter verwalten</NavDropdown.Item>
                                <NavDropdown.Divider />
                                <NavDropdown.Item href="/customer/add">Kunden anlegen</NavDropdown.Item>
                                <NavDropdown.Item href="/customer/list">Kunden verwalten</NavDropdown.Item>
                    </NavDropdown>
                    <Nav.Item><Nav.Link as={Link} to="/">Home</Nav.Link></Nav.Item>
                    <Nav.Item><Nav.Link as={Link} to="/Calendar">Calendar</Nav.Link></Nav.Item>
                </Nav>
            </Navbar>
        </Styles>
    )
}

export default Header