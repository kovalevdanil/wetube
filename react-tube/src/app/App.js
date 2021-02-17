import React, {useState, useEffect} from 'react'
import { Route, Switch } from 'react-router-dom';
import PrivateRoute from '../common/PrivateRoute';
import { ACCESS_TOKEN } from '../constants/holder';
import Home from '../pages/Home';
import Login from '../user/login/Login';
import Profile from '../user/profile/Profile';
import { getCurrentUser } from '../utils/ApiUtils';
import {AppHeader} from '../common/AppHeader'
import {Signup} from '../user/signup/Signup'
import {NotFound} from '../common/NotFound'
import Video from '../pages/Video'

import Alert from 'react-s-alert'
import OAuth2RedirectHandler from '../user/oauth2/OAuth2RedirectHandler'
import './App.css';


const App = (props) => {
  const [authenticated, setAuthenticated] = useState(false)
  const [currentUser, setCurrentUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
      setLoading(true)

      getCurrentUser().then(data => {
        setCurrentUser(data)
        setAuthenticated(true)
        setLoading(false)
      }).catch(error => {
        setLoading(false)
      })
  }, [])

  const handleLogout = () => {
    localStorage.removeItem(ACCESS_TOKEN)
    setAuthenticated(false)
    setCurrentUser(null)
  }

  if (loading)
    return <div>Loading...</div>

  return (
    <div className = "app">
      <div className = "app-top-box">
        <AppHeader authenticated = {authenticated} onLogout = {handleLogout}/>
      </div>

      <div className = "app-body">
        <Switch>
          <PrivateRoute path = "/" exact
            authenticated={authenticated}
            currentUser={currentUser}
            component = {Home}/>

          <PrivateRoute path = "/profile"
            authenticated = {authenticated}
            currentUser = {currentUser}
            component = {Profile}/>

          <PrivateRoute path = '/video/:slug'
            authenticated={authenticated}
            currentUser = {currentUser}
            component={Video}/>

          <Route path = "/login" 
             render = {props => 
               <Login authenticated = {authenticated} {...props}/>
             }>
          </Route>

          <Route path = "/signup"
                 render = {props =>
                     <Signup authenticated={authenticated} {...props}/>
                 }/>

          <Route path = "/oauth2/redirect"  component = {OAuth2RedirectHandler}/>

          <Route component = {NotFound} />
        </Switch>
      </div>

      <Alert stack = {{limit: 3}}
        timeout = {3000}
        position = 'top-right' effect = 'slide' offset = {65}/>
    </div>
  )
}

export default App;
