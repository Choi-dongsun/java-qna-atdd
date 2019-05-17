INSERT INTO user (id, user_id, password, name, email, created_at) values (1, 'movingline', '123456', '무빙라인', 'movlinglinecheck@gmail.com', CURRENT_TIMESTAMP());
INSERT INTO user (id, user_id, password, name, email, created_at) values (2, 'zingoworks', '123456', '징고', 'zingworks@gmail.com', CURRENT_TIMESTAMP());

INSERT INTO question (id, writer_id, title, contents, created_at, deleted) VALUES (1, 1, '질문1', '질문1의 내용', CURRENT_TIMESTAMP(), false);
INSERT INTO question (id, writer_id, title, contents, created_at, deleted) VALUES (2, 2, '질문2', '질문2의 내용', CURRENT_TIMESTAMP(), false);
INSERT INTO question (id, writer_id, title, contents, created_at, deleted) VALUES (3, 1, '삭제된 질문3', '질문3의 내용', CURRENT_TIMESTAMP(), true);
INSERT INTO question (id, writer_id, title, contents, created_at, deleted) VALUES (4, 2, '질문4', '질문4의 내용', CURRENT_TIMESTAMP(), false);

INSERT INTO answer (writer_id, contents, created_at, question_id, deleted) VALUES (1, '질문1의 댓글1', CURRENT_TIMESTAMP(), 1, false);
INSERT INTO answer (writer_id, contents, created_at, question_id, deleted) VALUES (2, '질문2의 댓글2', CURRENT_TIMESTAMP(), 2, false);
INSERT INTO answer (writer_id, contents, created_at, question_id, deleted) VALUES (2, '질문4의 삭제된 댓글3', CURRENT_TIMESTAMP(), 2, true);