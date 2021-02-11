import React from 'react'
import { Link, NavLink } from 'react-router-dom'
import './AppHeader.css'

export const AppHeader = (props) => {
    const {authenticated, onLogout} = props

    const links = authenticated ? (
    <ul>
        <li>
            <NavLink to="/profile">Profile</NavLink>
        </li>
        <li>
            <a onClick={onLogout}>Logout</a>
        </li>
    </ul>
    ) : (
    <ul>
        <li>
            <NavLink to="/login">Login</NavLink>        
        </li>
        <li>
            <NavLink to="/signup">Signup</NavLink>        
        </li>
    </ul>)

    return (
        <header className = "app-header">
            <div className = "container">
                 <div className = "app-branding">
                     <Link to = "/" className = "app-title">Tube</Link>
                 </div>
                 <div className = "app-options">
                     <nav className = "app-nav">
                         {links}
                     </nav>
                 </div>
            </div>
        </header>
    )


}

export default AppHeader