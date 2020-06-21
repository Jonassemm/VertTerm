import React, { Suspense } from 'react';
import { HashRouter as Router, Switch, Route } from 'react-router-dom';
import { hasRole } from "./auth"
import { observer } from "mobx-react"
// required css for whole app
import './App.css';
import "react-datepicker/dist/react-datepicker.css"
import 'react-bootstrap-typeahead/css/Typeahead.css'
// navigation
import NavBar from './components/navigationComponents/NavBar'
import Footer from './components/navigationComponents/Footer';
// preloaded pages
import Home from "./components/Home"
import HomePage from './components/calendarComponents/HomePage'
import BookingForm from "./components/calendarComponents/BookingForm"
import AdminPage from "./components/navigationComponents/AdminPage"
import AppointmentPage from "./components/appointmentComponents/AppointmentPage"
import { TestComponent } from './components/TestComponent';

export default observer(function App({ userStore, calendarStore }) {
  console.log(document.cookie)
  return (
    <Router>
      <NavBar userStore={userStore} />
      <Suspense fallback={<div>loading...</div>}>
        <div style={{ margin: "55px 0px 50px 0px" }}>
          <Switch>
            <Route path="/" exact component={() => <Home />} />
            <Route path="/admin" component={() => <AdminPage userStore={userStore}/>} />
            <Route exact path="/calendar" component={() => (<HomePage calendarStore={calendarStore} />)} />
            <Route path="/appointment" component={() => <AppointmentPage calendarStore={calendarStore} userStore={userStore} />} />
            <Route exact path="/booking" component={() => (<BookingForm/>)}/>
            <Route exact path="/booking/:appointmentID/:appointmentGroupID" component={BookingForm}/>
            <Route exact path="/test" component={TestComponent}/>
          </Switch>
        </div>
      </Suspense>
      <Footer />
    </Router>
  )
})