import React from 'react';
import {Navbar, Nav, NavDropdown} from 'react-bootstrap'
import {Link} from "react-router-dom"
import Styled from "styled-components"

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

function NavigationBar() {
    return(
        <Styles>
            <Navbar collapseOnSelect expand="lg" bg="dark" variant="dark">
                <Navbar.Brand href="home">VertTerm App</Navbar.Brand>
                <Navbar.Toggle aria-controls="responsive-navbar-nav" />
                <Navbar.Collapse id="responsive-navbar-nav">
                    <Nav className="mr-auto">
                        <NavDropdown className="mr-auto" title="Benutzer" id="collasible-nav-dropdown">
                            <NavDropdown.Item href="/employee/add">Mitarbeiter anlegen</NavDropdown.Item>
                            <NavDropdown.Item href="/employee/list">Mitarbeiter verwalten</NavDropdown.Item>
                            <NavDropdown.Divider />
                            <NavDropdown.Item href="/customer/add">Kunden anlegen</NavDropdown.Item>
                            <NavDropdown.Item href="/customer/list">Kunden verwalten</NavDropdown.Item>
                        </NavDropdown>
                        <Nav.Item><Link to="/">Home</Link></Nav.Item>
                        <Nav.Item><Link to="/Calendar">Calendar</Link></Nav.Item>
                    </Nav>
                </Navbar.Collapse>
            </Navbar>
        </Styles>
    )
}

export default NavigationBar;