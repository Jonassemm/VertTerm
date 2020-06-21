import React from "react";
import { Container, Row, Col, Card, Form, Button } from "react-bootstrap";
import { withRouter, Switch } from "react-router";
import Sidebar from "./Sidebar"
import { Route } from "react-router"
import "./adminPage_style.css"
import Arrow from "./arrow.jsx"

import ProcedurePage from "../administrationComponents/procedureComponents/ProcedurePage"
import RestrictionPage from "../administrationComponents/restrictionComponents/RestrictionPage"
import UserPage from "../administrationComponents/userComponents/UserPage"
import OptionalAttributesPage from "../administrationComponents/optionalAttributesComponents/optionalAttributesPage"
import RolePage from "../administrationComponents/roleComponents/RolePage"
import PositionPage from "../administrationComponents/positionComponents/PositionPage"
import ResourceTypePage from "../administrationComponents/resourceTypeComponents/ResourceTypePage"
import ResourcePage from "../administrationComponents/resourceComponents/ResourcePage"


function openMenu() {
    document.getElementById('sidebar-wrapper').style.marginLeft = "0";
    document.getElementById('page-content-wrapper').style.marginLeft = "250px"
    document.body.style.backgroundColor = 'rgba(0,0,0,0.4)'
}

function closeMenu() {
    document.getElementById('sidebar-wrapper').style.marginLeft = "-250px";
    document.getElementById('page-content-wrapper').style.marginLeft = "0"
    document.body.style.backgroundColor = '#fff'
}

function AdminPage({userStore}) {
    return (
        <Container fluid>
            <Sidebar closeFunction={closeMenu} />
            <div id="arrow" alt="Right arrow" className="arrow" onMouseOver={openMenu}>
                <Arrow />
            </div>
            <div id="page-content-wrapper">
                <Route exact path="/admin/procedure" component={ProcedurePage} />
                <Route exact path="/admin/restriction" component={RestrictionPage} />
                <Route exact path="/admin" component={() => <UserPage userType={"customer"} heading={"Kunden"} />} />
                <Route exact path="/admin/customer" component={() => <UserPage userType={"customer"} heading={"Kunden"} />} />
                <Route exact path="/admin/employee" component={() => <UserPage userType={"employee"} heading={"Mitarbeiter"} />} />
                <Route exact path="/admin/optionalAttributes" component={OptionalAttributesPage} />
                <Route exact path="/admin/resource" component={ResourcePage} />
                <Route exact path="/admin/role" component={() => (<RolePage userStore={userStore} />)} />
                <Route exact path="/admin/position" component={PositionPage} />
                <Route exact path="/admin/resourceType"component={ResourceTypePage} />
            </div>

        </Container>
    )
}

export default AdminPage