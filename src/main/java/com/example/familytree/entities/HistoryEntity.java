package com.example.familytree.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "History", schema = "dbo", catalog = "web")
public class HistoryEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "history_id", nullable = false)
    private int historyId;
    @Basic
    @Column(name = "user_id", nullable = false)
    private int userId;
    @Basic
    @Column(name = "history_action", nullable = false)
    private int historyAction;
    @Basic
    @Column(name = "family_tree_id", nullable = false)
    private int familyTreeId;
    @Basic
    @Column(name = "person_id", nullable = false)
    private int personId;
    @Basic
    @Column(name = "history_created_at", nullable = false)
    private Date historyCreatedAt;
    @Basic
    @Column(name = "history_deleted_at", nullable = true)
    private Date historyDeletedAt;
    @Basic
    @Column(name = "history_updated_at", nullable = false)
    private Date historyUpdatedAt;

   
}