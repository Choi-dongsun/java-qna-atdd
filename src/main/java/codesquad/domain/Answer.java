package codesquad.domain;

import codesquad.exception.CannotDeleteException;
import codesquad.exception.UnAuthorizedException;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Answer extends AbstractEntity implements UrlGeneratable {
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_writer"))
    private User writer;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_to_question"))
    private Question question;

    @Size(min = 5)
    @Lob
    private String contents;

    private boolean deleted = false;

    public Answer() {
    }

    public Answer(User writer, String contents) {
        this.writer = writer;
        this.contents = contents;
    }

    public Answer(Long id, User writer, Question question, String contents) {
        super(id);
        this.writer = writer;
        this.question = question;
        this.contents = contents;
        this.deleted = false;
    }

    public Answer(Long id, User writer, Question question, String contents, boolean delete) {
        this(id, writer, question, contents);
        this.deleted = delete;
    }

    public User getWriter() {
        return writer;
    }

    public Question getQuestion() {
        return question;
    }

    public String getContents() {
        return contents;
    }

    public Answer setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public void toQuestion(Question question) {
        if (question.isDeleted()) throw new IllegalStateException();
        this.question = question;
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public DeleteHistory delete(User loginUser) throws RuntimeException {
        if (!isOwner(loginUser)) throw new UnAuthorizedException();
        if (question.isDeleted() || isDeleted()) throw new IllegalStateException();

        this.deleted = true;

        return new DeleteHistory(ContentType.ANSWER, getId(), loginUser);
    }

    public static List<DeleteHistory> deleteAll(List<Answer> answers, User writer) throws CannotDeleteException {
        List<DeleteHistory> deletedAnswers = new ArrayList<>();

        for (Answer answer : answers) {
            if(!answer.isOwner(writer)) if(!answer.isDeleted()) throw new CannotDeleteException();
            answer.deleted = true;
            deletedAnswers.add(new DeleteHistory(ContentType.ANSWER, answer.getId(), answer.writer));
        }

        return deletedAnswers;
    }

    @Override
    public String generateUrl() {
        return String.format("%s/answers/%d", question.generateUrl(), getId());
    }

    @Override
    public String toString() {
        return "Answer [id=" + getId() + ", writer=" + writer + ", contents=" + contents + ", deleted=" + deleted + "]";
    }
}
