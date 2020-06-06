import React, {Component} from 'react';

import {Navbar, Container, Col} from 'react-bootstrap';

export default function Footer() {
    return (
        <div>
        <Navbar fixed="bottom" bg="dark" variant="dark">
            <Container>
                <Col lg={12} className="text-center text-muted">
                    <div> All Rights Reserved</div>
                </Col>
            </Container>
        </Navbar>
        </div>
    )
}