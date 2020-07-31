package com.example.rumi.models;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Date;

public class Chat {
    public static final String KEY_CHATS = "chats";
    public static final String KEY_LAST_MESSAGE_DATE = "lastMessageDate";
    public static final String KEY_MEMBERS = "members";

    private String lastMessage;
    private ArrayList<String> members;
    private Date lastMessageDate;
    private boolean memberOneRead;
    private boolean memberTwoRead;

    @Exclude
    private String chatId;

    public Chat() {
    }

    public Chat(String lastMessage, ArrayList<String> members, Date lastMessageDate,
                boolean memberOneRead, boolean memberTwoRead, String chatId) {
        this.lastMessage = lastMessage;
        this.members = members;
        this.lastMessageDate = lastMessageDate;
        this.memberOneRead = memberOneRead;
        this.memberTwoRead = memberTwoRead;
        this.chatId = chatId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public boolean isMemberOneRead() {
        return memberOneRead;
    }

    public void setMemberOneRead(boolean memberOneRead) {
        this.memberOneRead = memberOneRead;
    }

    public boolean isMemberTwoRead() {
        return memberTwoRead;
    }

    public void setMemberTwoRead(boolean memberTwoRead) {
        this.memberTwoRead = memberTwoRead;
    }
}
