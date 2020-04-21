'use strict'

const React = require('react')
const useEffect = require('react')
const useState = require('react')
const ReactDOM = require('react-dom')
const axios = require('axios')

function UserList() {
	
	
	return(
		<div>
			<h1>All User: </h1>
			<ul>
			</ul>
		</div>
	)
}

ReactDOM.render(
  <UserList />,
  document.getElementById('userList')
);
