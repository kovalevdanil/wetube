import React from "react";
import {Container, Col, Row, Image} from "react-bootstrap";

import './Comment.css'

const CommentComponent = (props) => {
    const {comment} = props
    const user = comment.user

    return (
        <Container className = 'comment'>
            <Row>
                <Col md = 'auto'>
                    <Image className = "user-avatar" src = {user.imageUrl} roundedCircle/>
                </Col>
                <Col>
                    <Row>
                        <span className = 'comment-username'>{user.username}</span>
                        <span className='comment-date'>{comment.date}</span>
                    </Row>
                    <Row>
                        <p>{comment.content}</p>
                    </Row>
                </Col>
            </Row>
        </Container>
    )
}

export default CommentComponent