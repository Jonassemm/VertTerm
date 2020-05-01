import React from "react"
import {Container} from "react-bootstrap"

const Layout = (props) => {
    return(
        <Container style={{marginTop: "10px"}}>
            {props.children}
        </Container>
    )
}

export default Layout