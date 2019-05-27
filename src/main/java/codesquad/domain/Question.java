package codesquad.domain;

import codesquad.exception.CannotDeleteException;
import codesquad.exception.UnAuthorizedException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.Buildable;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {
    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String title;

    @Size(min = 3)
    @Lob
    private String contents;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public Question(Builder builder) {
        super(builder.id);
        this.writer = builder.writer;
        this.title = builder.title;
        this.contents = builder.contents;
        this.deleted = builder.deleted;
    }

    public String getTitle() {
        return title;
    }

    public Question setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public Question setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public User getWriter() {
        return writer;
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public Question addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);

        return this;
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Question update(User loginUser, String updateTitle, String updateContents) throws UnAuthorizedException {
        if(!isOwner(loginUser)) throw new UnAuthorizedException();

        this.title = updateTitle;
        this.contents = updateContents;
        return this;
    }

    public List<DeleteHistory> delete(User loginUser) throws RuntimeException {
        if(!isOwner(loginUser)) throw new UnAuthorizedException();
        if(isDeleted()) throw new IllegalStateException();

        return processDeletion();
    }

    public List<DeleteHistory> processDeletion() {
        DeleteHistory deletedQuestion = new DeleteHistory(ContentType.QUESTION, getId(), writer);
        this.deleted = true;
        List<DeleteHistory> deletedAnswers = Answer.deleteAll(answers, writer);

        List<DeleteHistory> deleteHistories = new ArrayList<>();
        deleteHistories.add(deletedQuestion);
        deleteHistories.addAll(deletedAnswers);
        return deleteHistories;
    }

    public static class Builder implements Buildable {
        private long id;
        private String title;
        private String contents;
        private User writer;
        private List<Answer> answers = new ArrayList<>();
        private boolean deleted = false;

        public Builder(long id, User writer, String title, String contents) {
            this.id = id;
            this.writer = writer;
            this.title = title;
            this.contents = contents;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder contents(String contents) {
            this.contents = contents;
            return this;
        }

        public Builder writer(User writer) {
            this.writer = writer;
            return this;
        }

        public Buildable answers(List<Answer> answers) {
            this.answers = answers;
            return this;
        }

        public Builder deleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        @Override
        public Question build() {
            return new Question(this);
        }
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + ", deleted=" + deleted + "]";
    }
}
