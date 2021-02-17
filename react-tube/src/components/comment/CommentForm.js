import React, {useState} from 'react'
import {Button,Container, Form, Image, Col, Row} from "react-bootstrap";

import './CommentForm.css'

const emptyState = {
    content: ''
}

const CommentFormComponent = ({leaveComment, currentUser, authenticated, cancelText = 'Cancel', commentText = 'Comment', onCancel}) => {
    const [state, setState] = useState({
        content: ''
    })

    const handleInput = (e) => {
        e.preventDefault()

        const {name, value} = e.target

        setState(prev => ({
            ...prev,
            [name] : value
        }))
    }

    const handleSubmit = (e) => {
        e.preventDefault()

        const payload = {
            content: state.content
        }

        leaveComment(payload)
        setState({content: ''})
    }

    const handleCancel = (e) => {
        e.preventDefault()
        if (onCancel){
            onCancel()
        } else {
            setState({...emptyState})
        }
    }

    return (
        <Container>
            <Row className = 'comment-form'>
                <Col md = 'auto'>
                    <Image className = 'comment-form-user-avatar' src = {currentUser.imageUrl} roundedCircle/>
                </Col>
                <Col>
                    <Form onSubmit={handleSubmit}>
                        <Form.Group controlId='content'>
                            <Form.Control
                                type = 'text'
                                onChange={handleInput}
                                name = 'content'
                                placeholder = 'Leave a comment...'
                                required
                                autoComplete = 'off'
                                value = {state.content}
                            />
                        </Form.Group>
                        <Button variant='primary' className = 'align-self-end' type = 'submit'>
                            {commentText}
                        </Button>
                        <Button variant = 'outline-secondary' className = 'comment-form-cancel-btn' onClick = {handleCancel}>
                            {cancelText}
                        </Button>
                    </Form>
                </Col>
            </Row>
        </Container>
    )
}

export default CommentFormComponent