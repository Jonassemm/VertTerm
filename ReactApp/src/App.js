'use strict'
import './App.css'
import React from "react"
import HomePage from "./components/calendarComponents/HomePage"
import {BrowserRouter as Router, Route, Switch} from "react-router-dom"
import {Header} from "./components/Header"
import {Home} from "./components/Home"
import {NoMatch} from "./components/NoMatch"
import "react-big-calendar/lib/css/react-big-calendar.css"
import "react-datepicker/dist/react-datepicker.css"
import "react-loader-spinner/dist/loader/css/react-spinner-loader.css"

function App({calendarStore}) {
	return (
		<React.Fragment>
			<Router>
			<Header/>
				<Switch>
					<Route exact path="/" component={Home}/>
					<Route exact path="/Calendar" component={() => (<HomePage calendarStore={calendarStore}/>)}/>
					<Route exact path="/NoMatch" component={NoMatch}/>
				</Switch>
			</Router> 
		</React.Fragment>
		  )
}

export default App


