import React from "react"
import Jumbotron from "react-bootstrap/Jumbotron"
import Styled from "styled-components"
import image from "../assets/backgroundWood.jpg"
import Container from "react-bootstrap/Container"


const Style = Styled.div`
.jumbo {
    background: url(${image});
    background-size: cover;
    color: #efefef;
    height: 200px;
    position: relative;
    z-index: -2;

    .overlay {
        background-color: #000;
        opacity: 0.6;
        position: absolute;
        top:0;
        left:0;
        bottom:0;
        right:0;
        z-index: -1;
    }
}
`

export const Jumbo = () => {
    return ( 
        <Style>
        <Jumbotron fluid className="jumbo">
            <div className="overlay"></div>
                <Container>
                    <h1>Welcome</h1>
                    <p>VertTerm Test Page here!</p>
                </Container>
        </Jumbotron>
    </Style>
)
}