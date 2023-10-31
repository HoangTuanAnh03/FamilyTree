package com.example.familytree.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
@Table(name = "tree_node", schema = "dbo", catalog = "web")
public class TreeNodeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "tree_node_id", nullable = false)
    private int treeNodeId;
    @Basic
    @Column(name = "family_tree_id", nullable = true)
    private Integer familyTreeId;
    @Basic
    @Column(name = "PersonId", nullable = true)
    private Integer personId;
    @Basic
    @Column(name = "left_node", nullable = true)
    private Integer leftNode;
    @Basic
    @Column(name = "right_node", nullable = true)
    private Integer rightNode;
    @Basic
    @Column(name = "person_id_source", nullable = true)
    private Integer personIdSource;
    @Basic
    @Column(name = "person_id_destination", nullable = true)
    private Integer personIdDestination;
    @Basic
    @Column(name = "person_id_center", nullable = true)
    private Integer personIdCenter;
    @Basic
    @Column(name = "person_id_relate", nullable = true)
    private Integer personIdRelate;

   }
