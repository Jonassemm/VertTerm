import React from 'react';
import './App.css';

import Welcome from './components/Welcome';
import Footer from './components/Footer';


import {Container, Row, Col} from 'react-bootstrap';
import {BrowserRouter as Router, Switch, Route} from 'react-router-dom';
import NavigationBar from './components/NavigationBar';
import CustomerAdd from './components/user/CustomerAdd';
import EmployeeAdd from './components/user/EmployeeAdd';
import UserList from './components/user/UserList';


export default function App() {

  const heading = "Willkommen bei dem verteilten Termin- und Ressourcenplaner";
  const quote = "Hier könnte Ihre Werbung stehen";
  const footer = "Im Auftrag von Herrn Frenz";

  return (
    <Router>
        <NavigationBar/>
        <Container>
            <Row>
                <Col lg={12} className={"margin-top"}>
                    <Switch>
                        <Route path="/employee/add" exact component={EmployeeAdd}/>
                        <Route path="/customer/add" exact component={CustomerAdd}/>
                        <Route path="/customer/list" exact component={() => <UserList heading={"Kundenliste"}/>}/>
                        <Route path="/employee/list" exact component={() => <UserList heading={"Mitarbeiterliste"}/>}/>
                        <Route path="/" exact component={() => <Welcome heading={heading} quote={quote} footer={footer}/>}/>
                    </Switch>
                </Col>
            </Row>
        </Container>
        <Footer/>
    </Router>
  );
}
