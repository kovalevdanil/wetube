import React, {useState} from "react";
import {Container, Col, Row, Image} from "react-bootstrap";

import './Comment.css'
import CommentFormComponent from "./CommentForm";

const CommentComponent = (props) => {
    const [expanded, setExpanded] = useState(false)
    const [showReplyForm, setShowReplyForm] = useState(false)

    const {comment} = props
    const user = comment.user

    const expandable = comment.children !== undefined && comment.children !== null &&
            comment.children.length > 0

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
                    <Row>
                        {showReplyForm ? <CommentFormComponent currentUser = {props.currentUser} {...props} onCancel = {() => setShowReplyForm(false)}/> :
                            <span className = 'comment-form-reply-btn' onClick = {() => {setShowReplyForm(true)}}>Reply</span>}
                    </Row>
                    {expandable &&
                    <Row className = 'comment-children'>
                        {expanded ?
                            <span>
                            <span className = 'comment-more-btn' onClick = {() => setExpanded(false)}>Hide</span>
                                {comment.children.map(c => <CommentComponent comment = {c} key = {c.id} />)}
                        </span>
                            :
                            <span className = 'comment-more-btn' onClick = {() => setExpanded(true)}>More...</span>

                        }
                    </Row>
                    }
                </Col>
            </Row>
        </Container>
    )
}

export default CommentComponent