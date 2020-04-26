import React from 'react';

import {Navbar, Nav, NavDropdown} from 'react-bootstrap';

function NavigationBar() {
    return(
        <Navbar collapseOnSelect expand="lg" bg="dark" variant="dark">
            <Navbar.Brand href="home">React-Bootstrap</Navbar.Brand>
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
                </Nav>
            </Navbar.Collapse>
        </Navbar>
    )

}

export default NavigationBar;