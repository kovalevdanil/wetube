import React from "react";
import {Row, Container} from "react-bootstrap";
import CommentComponent from "./Comment";

import './CommentSection.css'
import CommentFormComponent from "./CommentForm";

const CommentSectionComponent = (props) => {

    const {comments,currentUser, leaveComment, authenticated} = props

    return (
        <Container className='comment-section'>
            <Row className = 'comment-section-total-count'>
                {comments.length} comments
            </Row>
            <Row>
                <CommentFormComponent
                    leaveComment={leaveComment}
                    currentUser={currentUser}
                    authenticated={authenticated}
                />
                <Container>
                    {comments.map((comment) =>
                        <CommentComponent comment = {comment} />)}
                </Container>
            </Row>
        </Container>
    )

}

export default CommentSectionComponent