//author: Jonas Semmler
import React from "react";
import { Container} from "react-bootstrap";
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
import OpeningHours from "../administrationComponents/openingHoursComponents/openingHoursPage"
import { hasRight, hasRole } from "../../auth";
import {managementRights, ownUserRights, userRights, positionRights, procedureRights, resourceRights, resourceTypeRights, roleRights, adminRole} from "../Rights"


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
            {hasRight(userStore, managementRights()) &&
               <Sidebar closeFunction={closeMenu} userStore={userStore} /> 
            }
            {hasRight(userStore, managementRights()) &&
                <div id="arrow" alt="Right arrow" className="arrow" onMouseOver={openMenu}>
                    <Arrow />
                </div>
            }
            <div id="page-content-wrapper">
                {hasRight(userStore, procedureRights) &&
                    <Route exact path="/admin/procedure" component={() => <ProcedurePage userStore={userStore}/>} />
                }
                {hasRole(userStore, [adminRole]) &&
                     <Route exact path="/admin/restriction" component={() => <RestrictionPage userStore={userStore}/>} />
                }
                {hasRight(userStore, ownUserRights.concat(userRights)) &&
                    <Route exact path="/admin" component={() => <UserPage userStore={userStore} userType={"customer"} />} />
                }
                {hasRight(userStore, ownUserRights.concat(userRights)) &&
                    <Route exact path="/admin/customer" component={() => <UserPage userStore={userStore} userType={"customer"} />} />
                }
                {hasRight(userStore, ownUserRights.concat(userRights)) &&
                    <Route exact path="/admin/employee" component={() => <UserPage userStore={userStore} userType={"employee"} />} />
                }
                {hasRole(userStore, [adminRole]) &&
                    <Route exact path="/admin/optionalAttributes" component={() => <OptionalAttributesPage userStore={userStore}/>} />
                }
                {hasRight(userStore, resourceRights) &&
                    <Route exact path="/admin/resource" component={() => <ResourcePage userStore={userStore}/>} />
                }
                {hasRight(userStore, roleRights) &&
                    <Route exact path="/admin/role" component={() => <RolePage userStore={userStore} />} />
                }
                {hasRight(userStore, positionRights) &&
                    <Route exact path="/admin/position" component={() => <PositionPage userStore={userStore}/>} />
                }
                {hasRight(userStore, resourceTypeRights) &&
                    <Route exact path="/admin/resourceType"component={() => <ResourceTypePage userStore={userStore}/>} />
                }
                {hasRole(userStore, [adminRole]) &&
                    <Route exact path="/admin/openingHours"component={() => <OpeningHours userStore={userStore}/>} />
                }
               
            </div>

        </Container>
    )
}

export default AdminPage