package common.message;

import java.io.Serializable;

public class Message implements Serializable {

    private final MessageType type;
    private String author;
    private final String text;

    public Message(MessageType type, String text) {
        this.type = type;
        this.text = text;
        this.author = "";
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
