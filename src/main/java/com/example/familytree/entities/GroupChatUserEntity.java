package com.example.familytree.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
@Table(name = "group_chat_user", schema = "dbo", catalog = "web")
public class GroupChatUserEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private int id;
    @Basic
    @Column(name = "group_chat_id", nullable = true)
    private Integer groupChatId;
    @Basic
    @Column(name = "user_id", nullable = true)
    private Integer userId;


}
