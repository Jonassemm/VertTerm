import React, { useState, useEffect } from "react"
import { Nav, Navbar, NavItem, NavDropdown } from "react-bootstrap"
import Styled from "styled-components"
import { Link } from 'react-router-dom'
import LoginForm from "./LoginForm"
import { observer } from "mobx-react"
import { hasRole } from "../../auth"


const Styles = Styled.div`
.nav-item {
    margin-left: 5px;
}
`


function NavBar({ userStore }) {
    var x = document.cookie
    console.log(x)
    return (
        <Styles>
            <Navbar variant="dark" bg="dark" expand="lg" justify-content="end" fixed="top">
                <Navbar.Brand as={Link} to="/" >betabook.me</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav" className="mr-auto">
                    <Nav className="mr-auto">
                        <Nav.Item><Nav.Link as={Link} to="/admin">Verwaltung</Nav.Link></Nav.Item>
                        <Nav.Item><Nav.Link as={Link} to="/warning">Konfliktansicht</Nav.Link></Nav.Item>
                        <Nav.Item><Nav.Link as={Link} to="/appointment">Terminansicht</Nav.Link></Nav.Item>
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