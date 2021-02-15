import React from 'react'
import {Navbar, Nav} from 'react-bootstrap'
import {Link, NavLink} from 'react-router-dom'
import './AppHeader.css'

export const AppHeader = (props) => {
    const {authenticated, onLogout} = props

    const links = authenticated ? (
        <Nav className='justify-content-end'>
            <Nav.Link as={Link} to="/profile">Profile</Nav.Link>
            <Nav.Link onClick={onLogout}>Logout</Nav.Link>
        </Nav>
    ) : (
        <Nav className='justify-content-end'>
            <Nav.Link as={Link} to="/login">Login</Nav.Link>
            <Nav.Link as={Link} to="/signup">Signup</Nav.Link>
        </Nav>)

    return (
        <Navbar bg="light" expand="lg" className='app-header'>
            <Navbar.Brand href="/">Tube</Navbar.Brand>
            <Navbar.Collapse>
                {links}
            </Navbar.Collapse>
        </Navbar>
    )


}
export default AppHeader