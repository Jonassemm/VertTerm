//author: Jonas Semmler
import React from "react"
import ReactDom from "react-dom"
import App from "./App"
import {CalendarStore} from "./components/calendarComponents/Store"
import 'mobx-react-lite/batchingForReactDom'
import {UserStore} from "./UserStore"

const userStore = new UserStore()
const calendarStore = new CalendarStore()

ReactDom.render(<App userStore={userStore} calendarStore={calendarStore}/>, document.getElementById("root"))