import React from "react";
import { Nav } from "react-bootstrap";
import { withRouter } from "react-router";
import { Link } from 'react-router-dom'

const Side = ({closeFunction}) => {
    return (
        <div id="sidebar-wrapper" onMouseLeave={closeFunction}>
            <Nav className="sidebar"
                activeKey="/home"
                onClick={closeFunction}
                onSelect={selectedKey => alert(`selected ${selectedKey}`)}
            >
                <Nav.Item>
                    <Nav.Link className="sidebarLink" as={Link} to="/admin/customer">Kunden</Nav.Link>
                </Nav.Item>
                <Nav.Item>
                    <Nav.Link className="sidebarLink" as={Link} to="/admin/employee">Mitarbeiter</Nav.Link>
                </Nav.Item>
                <hr />
                <Nav.Item>
                    <Nav.Link className="sidebarLink" as={Link} to="/admin/openingHours">Öffnungszeiten</Nav.Link>
                </Nav.Item>
                <Nav.Item>
                    <Nav.Link className="sidebarLink" as={Link} to="/admin/restriction">Einschränkungen</Nav.Link>
                </Nav.Item>
                <Nav.Item>
                    <Nav.Link className="sidebarLink" as={Link} to="/admin/optionalAttributes">Optionale Attribute</Nav.Link>
                </Nav.Item>
                <Nav.Item>
                    <Nav.Link className="sidebarLink" as={Link} to="/admin/position">Positionen</Nav.Link>
                </Nav.Item>
                <Nav.Item>
                    <Nav.Link className="sidebarLink" as={Link} to="/admin/role">Rollen</Nav.Link>
                </Nav.Item>
                <hr />
                <Nav.Item>
                    <Nav.Link className="sidebarLink" as={Link} to="/admin/procedure">Prozeduren</Nav.Link>
                </Nav.Item>
                <hr />
                <Nav.Item>
                    <Nav.Link className="sidebarLink" as={Link} to="/admin/resourceType">Ressourcen-Typen</Nav.Link>
                </Nav.Item>
                <Nav.Item>
                    <Nav.Link className="sidebarLink" as={Link} to="/admin/resource">Ressourcen</Nav.Link>
                </Nav.Item>
             
            </Nav>
        </div>

    );
};
const Sidebar = withRouter(Side);
export default Sidebar