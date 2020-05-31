import React, { Suspense } from 'react';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';
import { hasRole } from "./auth"
import { observer } from "mobx-react"
// required css for whole app
import './App.css';
import "react-datepicker/dist/react-datepicker.css"
import 'react-bootstrap-typeahead/css/Typeahead.css'
// navigation
import NavBar from './components/NavBar'
import Footer from './components/Footer';
// preloaded pages
import Home from "./components/Home"
import BookingForm from "./components/calendarComponents/BookingForm"
//lazy loaded pages
/*
const UserList = React.lazy(() => import('./components/userComponents/UserList'))
const RolePage = React.lazy(() => import("./components/roleComponents/RolePage"))
const PositionPage = React.lazy(() => import("./components/positionComponents/PositionPage"))
const ResourceTypePage = React.lazy(() => import("./components/resourceTypeComponents/ResourceTypePage"))
const ResourcePage = React.lazy(() => import("./components/resourceComponents/ResourcePage"))
const HomePage = React.lazy(() => import('./components/calendarComponents/HomePage'))
const ProcedurePage = React.lazy(() => import("./components/procedureComponents/ProcedurePage")) */

import UserList from './components/userComponents/UserList'
import RolePage from "./components/roleComponents/RolePage"
import PositionPage from "./components/positionComponents/PositionPage"
import HomePage from './components/calendarComponents/HomePage'
import ProcedurePage from "./components/procedureComponents/ProcedurePage"




export default observer(function App({ userStore, calendarStore }) {

  return (

    <Router>
      <NavBar userStore={userStore} />
      <Suspense fallback={<div>loading...</div>}>
        <div style={{ margin: "55px 0px 50px 0px" }}>
          <Switch>
            <Route path="/" exact component={() => <Home />} />
            {hasRole(userStore, ["ADMIN_ROLE"]) && <Route exact path="/customer/list" component={() => <UserList userType={"customer"} heading={"Kundenliste"} />} />}
            {hasRole(userStore, ["ADMIN_ROLE"]) && <Route exact path="/employee/list" component={() => <UserList userType={"employee"} heading={"Mitarbeiterliste"} />} />}
            <Route exact path="/calendar" component={() => (<HomePage calendarStore={calendarStore} />)} />
            <Route exact path="/role" component={() => (<RolePage userStore={userStore} />)} />
            <Route path="/position" exact component={() => <PositionPage />} />
            <Route path="/resourceType" exact component={() => <ResourceTypePage />} />
            <Route path="/resource" exact component={() => <ResourcePage />} />
            <Route exact path="/procedure" component={() => (<ProcedurePage />)} />
            <Route exact path="/booking" component={() => (<BookingForm/>)}/>
          </Switch>
        </div>
      </Suspense>
      <Footer />
    </Router>
  )
})
