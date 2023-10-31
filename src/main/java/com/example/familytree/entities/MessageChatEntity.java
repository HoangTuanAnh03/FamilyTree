package com.example.familytree.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
@Table(name = "message_chat", schema = "dbo", catalog = "web")
public class MessageChatEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "message_id", nullable = false)
    private int messageId;
    @Basic
    @Column(name = "group_chat_id", nullable = true)
    private Integer groupChatId;
    @Basic
    @Column(name = "user_id", nullable = true)
    private Integer userId;
    @Basic
    @Column(name = "message_content", nullable = true, length = 400)
    private String messageContent;
    @Basic
    @Column(name = "messenge_date", nullable = true)
    private Date messengeDate;


}
