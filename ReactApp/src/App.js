'use strict'

const React = require('react');
const ReactDOM = require('react-dom');

class App extends React.Component {
	constructor(props){
		super(props)
	}
	
	render() {
		return (
			<div>
				<h2>successful inject v3.3</h2>
				<h1>Endlich funktioniert alles</h1>
				<h3>test</h3>
				<h4>test nr 2</h4>
			</div>	
		)
	}
}

const wrapper = document.getElementById("root");
wrapper ? ReactDOM.render(<App />, wrapper) : false;
