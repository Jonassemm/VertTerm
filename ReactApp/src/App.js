import React from 'react';
import './App.css';
import Footer from './components/Footer';
import {Container, Row, Col} from 'react-bootstrap';
import {BrowserRouter as Router, Switch, Route} from 'react-router-dom';
import NavigationBar from './components/NavigationBar';
import CustomerAdd from './components/user/CustomerAdd';
import EmployeeForm from './components/user/EmployeeForm';
import UserList from './components/user/UserList';
import {Home} from './components/Home'
import HomePage from './components/calendarComponents/HomePage'
import Header from './components/Header' 
import "react-big-calendar/lib/css/react-big-calendar.css"
import "react-datepicker/dist/react-datepicker.css"
import "react-loader-spinner/dist/loader/css/react-spinner-loader.css"

export default function App({calendarStore}) {

  return (
       <Router>
       <Header />
           <Switch>
               <Route path="/" exact component={Home}/>
               <Route path="/employee/add" exact component={() => <EmployeeForm type={"add"}/>}/>
               <Route path="/customer/add" exact component={() => <CustomerAdd type={"add"}/>}/>
               <Route path="/employee/edit/:userId" exact component={() => <EmployeeForm type={"edit"}/>}/>
               <Route path="/customer/list" exact component={() => <UserList heading={"Kundenliste"}/>}/>
               <Route path="/employee/list" exact component={() => <UserList heading={"Mitarbeiterliste"}/>}/>
               <Route exact path="/Calendar" component={() => (<HomePage calendarStore={calendarStore}/>)}/>
           </Switch>
           <Footer/>
           </Router>
  )
}
