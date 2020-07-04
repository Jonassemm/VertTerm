import React from "react";
import { Nav } from "react-bootstrap";
import { withRouter } from "react-router";
import { Link } from 'react-router-dom'
import { hasRight, hasRole } from "../../auth"
import {ownUserRights, userRights, positionRights, procedureRights, resourceRights, resourceTypeRights, roleRights} from "../Rights"

const Side = ({closeFunction, userStore}) => {
    return (
        <div id="sidebar-wrapper" onMouseLeave={closeFunction}>
            <Nav className="sidebar"
                activeKey="/home"
                onClick={closeFunction}
                onSelect={selectedKey => alert(`selected ${selectedKey}`)}
            >
                {hasRight(userStore, ownUserRights.concat(userRights)) &&
                    <Nav.Item>
                        <Nav.Link className="sidebarLink" as={Link} to="/admin/customer">Kunden</Nav.Link>
                    </Nav.Item>
                }
                {hasRight(userStore, ownUserRights.concat(userRights)) &&
                    <Nav.Item>
                        <Nav.Link className="sidebarLink" as={Link} to="/admin/employee">Mitarbeiter</Nav.Link>
                    </Nav.Item>
                }
                {(hasRight(userStore, ownUserRights.concat(userRights)) &&
                    (hasRole(userStore, ["ADMIN_ROLE"]) || hasRight(userStore, positionRights) || hasRight(userStore, roleRights)) ||
                    hasRight(userStore, procedureRights) ||
                    (hasRight(userStore, resourceTypeRights) || hasRight(userStore, resourceRights))) &&
                    <hr />
                }
                {hasRole(userStore, ["ADMIN_ROLE"]) &&
                    <Nav.Item>
                        <Nav.Link className="sidebarLink" as={Link} to="/admin/openingHours">Öffnungszeiten</Nav.Link>
                    </Nav.Item>
                }
                {hasRole(userStore, ["ADMIN_ROLE"]) &&
                    <Nav.Item>
                        <Nav.Link className="sidebarLink" as={Link} to="/admin/restriction">Einschränkungen</Nav.Link>
                    </Nav.Item>
                }
                {hasRole(userStore, ["ADMIN_ROLE"]) &&
                    <Nav.Item>
                        <Nav.Link className="sidebarLink" as={Link} to="/admin/optionalAttributes">Optionale Attribute</Nav.Link>
                    </Nav.Item>
                }
                {hasRight(userStore, positionRights) &&
                    <Nav.Item>
                        <Nav.Link className="sidebarLink" as={Link} to="/admin/position">Positionen</Nav.Link>
                    </Nav.Item>
                }
                {hasRight(userStore, roleRights) &&
                    <Nav.Item>
                        <Nav.Link className="sidebarLink" as={Link} to="/admin/role">Rollen</Nav.Link>
                    </Nav.Item>
                }
                {(hasRole(userStore, ["ADMIN_ROLE"]) || hasRight(userStore, positionRights) || hasRight(userStore, roleRights)) &&
                     (hasRight(userStore, procedureRights) ||
                     (hasRight(userStore, resourceTypeRights) || hasRight(userStore, resourceRights))) &&
                    <hr />
                }
                {hasRight(userStore, procedureRights) &&
                    <Nav.Item>
                        <Nav.Link className="sidebarLink" as={Link} to="/admin/procedure">Prozeduren</Nav.Link>
                    </Nav.Item>
                }
                {hasRight(userStore, procedureRights) && 
                    (hasRight(userStore, resourceTypeRights) || hasRight(userStore, resourceRights)) &&
                  <hr />  
                }
                {hasRight(userStore, resourceTypeRights) &&
                    <Nav.Item>
                        <Nav.Link className="sidebarLink" as={Link} to="/admin/resourceType">Ressourcen-Typen</Nav.Link>
                    </Nav.Item>
                }
                {hasRight(userStore, resourceRights) &&
                    <Nav.Item>
                        <Nav.Link className="sidebarLink" as={Link} to="/admin/resource">Ressourcen</Nav.Link>
                    </Nav.Item>
                }
            </Nav>
        </div>

    );
};
const Sidebar = withRouter(Side);
export default Sidebar