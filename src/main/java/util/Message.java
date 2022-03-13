package util;

import java.io.Serializable;

public class Message implements Serializable {

    private final MessageType type;
    private final String text;
    private String author;

    public Message(MessageType type, String text, String author) {
        this.type = type;
        this.author = author;
        this.text = text;
    }

    public Message(MessageType type, String text) {
        this(type, text, "New user");
    }

    public MessageType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String username) {
        author = username;
    }

    @Override
    public String toString() {
        return String.format(
                "Message {author: %s data: %s}",
                this.author,
                this.text);
    }

}
