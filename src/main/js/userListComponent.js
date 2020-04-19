'use strict'

const React = require('react')
const ReactDOM = require('react-dom')

function UserList() {
	return(
		<h1>This is the User List Component</h1>	
	)
}

ReactDOM.render(
  <UserList />,
  document.getElementById('userList')
);
