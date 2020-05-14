import React from 'react';
import './App.css';
import Footer from './components/Footer';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';
import CustomerForm from './components/user/CustomerForm';
import EmployeeForm from './components/user/EmployeeForm';
import Availability from './components/availability/AvailabilityForm'
import UserList from './components/user/UserList';
import { Home } from './components/Home'
import HomePage from './components/calendarComponents/HomePage'
import NavBar from './components/NavBar'
import "react-big-calendar/lib/css/react-big-calendar.css"
import "react-datepicker/dist/react-datepicker.css"
import "react-loader-spinner/dist/loader/css/react-spinner-loader.css"
import { hasRole } from "./auth"
import { observer } from "mobx-react"
import RolePage from "./components/roleComponents/RolePage"
import PositionPage from "./components/position/PositionPage"

export default observer(function App({ userStore, calendarStore }) {

  return (
    <Router>
      <NavBar userStore={userStore} />
      <div style={{ margin: "55px 0px 50px 0px" }}>
        <Switch>
          <Route path="/" exact component={Home} />
          {hasRole(userStore, ["ADMIN_ROLE"]) && <Route path="/position" exact component={() => <PositionPage/>} />}
          {hasRole(userStore, ["ADMIN_ROLE"]) && <Route path="/employee/edit/availability/:userId" exact component={() => <Availability/>} />}
          {hasRole(userStore, ["ADMIN_ROLE"]) && <Route path="/employee/add" exact component={() => <EmployeeForm type={"add"} />} />}
          {hasRole(userStore, ["ADMIN_ROLE"]) && <Route path="/customer/add" exact component={() => <CustomerForm type={"add"} />} />}
          {hasRole(userStore, ["ADMIN_ROLE"]) && <Route path="/employee/edit/:userId" exact component={() => <EmployeeForm type={"edit"} />} />}
          {hasRole(userStore, ["ADMIN_ROLE"]) && <Route path="/customer/edit/:userId" exact component={() => <CustomerForm type={"edit"} />} />}
          {hasRole(userStore, ["ADMIN_ROLE"]) && <Route path="/customer/list" exact component={() => <UserList userType={"customer"} heading={"Kundenliste"} />} />}
          {hasRole(userStore, ["ADMIN_ROLE"]) && <Route path="/employee/list" exact component={() => <UserList userType={"employee"} heading={"Mitarbeiterliste"} />} />}
          <Route exact path="/calendar" component={() => (<HomePage calendarStore={calendarStore} />)} />
          <Route exact path="/role" component={() => (<RolePage userStore={userStore} />)} />
        </Switch>
      </div>
      <Footer />
    </Router>
  )
})
