import React from "react"
import Navbar from "react-bootstrap/Navbar"
import Nav from "react-bootstrap/Nav"
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

export const Header = () => {
    return (
        <Styles>
            <Navbar expand="lg" justify-content="end">
                <Navbar.Brand href="/">Calendar App</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav"/>
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="ml-auto">
                        <Nav.Item><Link to="/">Home</Link></Nav.Item>
                        <Nav.Item><Link to="/Calendar">Calendar</Link></Nav.Item>
                    </Nav>
                </Navbar.Collapse>
            </Navbar>
        </Styles>
    )
   
}