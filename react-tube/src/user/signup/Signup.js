import React, {useState} from 'react'
import {Redirect, Link} from 'react-router-dom'
import {ACCESS_TOKEN, GOOGLE_AUTH_URL} from '../../constants/holder'
import {Alert, Button, Form, Container} from 'react-bootstrap'
import { signup } from '../../utils/ApiUtils'
import {GoogleLoginButton} from "react-social-login-buttons";

import './Signup.css'

const SocialSignup = (props) => {
    return (
        <div className="social-signup">
            <a href = {GOOGLE_AUTH_URL}>
                <GoogleLoginButton text = 'Signup with Google' />
            </a>
        </div>
    )
}

const SignupForm = (props) => {

    const [state, setState] = useState({
        username: '',
        email: '',
        password: ''
    })

    const [alertState, setAlertState] = useState({
        show: false,
        message: ''
    })


    const setShowAlert = (visible) =>
        setAlertState(prev => {
            return {
                ...prev,
                show: visible
            }
        })

    const handleInputChange = (e) => {
        const {name, value} = e.target

        setState(prev => ({
            ...prev,
            [name] : value
        }))
    }

    const handleSubmit = (e) => {
        e.preventDefault()

        const signupRequest = {
            email: state.email,
            password: state.password,
            username: state.username
        }

        signup(signupRequest).then(data => {
            props.history.push('/login')
        }).catch(error => {
            setAlertState({
                show: true,
                message: error.message
            })
        })
    }

    return (
        <Form onSubmit = {handleSubmit}>
            <Form.Group controlId='username'>
                <Form.Control
                    type = 'text'
                    placeholder = 'Username'
                    name = 'username'
                    onChange={handleInputChange}
                />
            </Form.Group>
            <Form.Group controlId='email'>
                <Form.Control
                    type = 'email'
                    placeholder = 'Email'
                    name = 'email'
                    onChange={handleInputChange}
                />
            </Form.Group>

            <Form.Group controlId='password'>
                <Form.Control
                    type = 'password'
                    placeholder = 'Password'
                    name = 'password'
                    onChange = {handleInputChange}
                />
            </Form.Group>

            {alertState.show &&
            <Alert dismissible variant='danger' onClose = {() => setShowAlert(false)}>
                <Alert.Heading>Error!</Alert.Heading>
                <p>Message from server: {alertState.message}</p>
            </Alert>}

            <Button block className = 'btn btn-primary' type = 'submit'>
                Sign Up
            </Button>
        </Form>
    )
}

export const Signup = (props) => {

    const {authenticated} = props

    if (authenticated){
        return <Redirect 
            to = {{
                pathname: '/',
                state: {from: props.locaiton}
            }} 
        />
    }

    return (
        <Container className='signup-container'>
            <Container className='signup-content'>
                <h2 className = 'signup-title'> Signup to Tube</h2>
                <SocialSignup />
                <hr />
                <SignupForm {...props}/>
                <span className = 'login-link'>Already have an account? <Link to = '/login'>Login!</Link></span>
            </Container>
        </Container>
    )
}

export default Signup