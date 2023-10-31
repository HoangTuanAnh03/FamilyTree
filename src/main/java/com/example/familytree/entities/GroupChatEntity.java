package com.example.familytree.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
@Table(name = "group_chat", schema = "dbo", catalog = "web")
public class GroupChatEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "group_chat_id", nullable = false)
    private int groupChatId;
    @Basic
    @Column(name = "user_id", nullable = true)
    private Integer userId;
    @Basic
    @Column(name = "family_tree_id", nullable = true)
    private Integer familyTreeId;


}
