import React from "react";
import {Row, Container, Button} from "react-bootstrap";
import CommentComponent from "./Comment";

import './CommentSection.css'
import CommentFormComponent from "./CommentForm";

const CommentSectionComponent = (props) => {

    const {comments, commentsTotalCount ,currentUser, leaveComment, authenticated, withLoadMore, onLoadMore} = props

    return (
        <Container className='comment-section'>
            <Row className = 'comment-section-total-count'>
                {commentsTotalCount} comments
            </Row>
            <Row>
                <CommentFormComponent
                    leaveComment={leaveComment}
                    currentUser={currentUser}
                    authenticated={authenticated}
                />
                <Container>
                    {comments.map((comment) =>
                        <CommentComponent comment = {comment} {...props} />)}
                </Container>
            </Row>
            {withLoadMore &&
                <Row className='justify-content-center mb-2'>
                    <Button onClick = {onLoadMore}>Load More</Button>
                </Row>
            }
        </Container>
    )

}

export default CommentSectionComponent