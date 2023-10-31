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
@Table(name = "blog", schema = "dbo", catalog = "web")
public class BlogEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "blog_id", nullable = false)
    private int blogId;
    @Basic
    @Column(name = "family_tree_id", nullable = true)
    private Integer familyTreeId;
    @Basic
    @Column(name = "user_id", nullable = true)
    private Integer userId;
    @Basic
    @Column(name = "blog_posting_date", nullable = true)
    private Date blogPostingDate;
    @Basic
    @Column(name = "blog_content", nullable = true, length = 500)
    private String blogContent;

    public int getBlogId() {
        return blogId;
    }

    public void setBlogId(int blogId) {
        this.blogId = blogId;
    }

    public Integer getFamilyTreeId() {
        return familyTreeId;
    }

    public void setFamilyTreeId(Integer familyTreeId) {
        this.familyTreeId = familyTreeId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public java.util.Date getBlogPostingDate() {
        return blogPostingDate;
    }

    public void setBlogPostingDate(java.util.Date blogPostingDate) {
        this.blogPostingDate = blogPostingDate;
    }

    public String getBlogContent() {
        return blogContent;
    }

    public void setBlogContent(String blogContent) {
        this.blogContent = blogContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlogEntity that = (BlogEntity) o;

        if (blogId != that.blogId) return false;
        if (familyTreeId != null ? !familyTreeId.equals(that.familyTreeId) : that.familyTreeId != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (blogPostingDate != null ? !blogPostingDate.equals(that.blogPostingDate) : that.blogPostingDate != null)
            return false;
        if (blogContent != null ? !blogContent.equals(that.blogContent) : that.blogContent != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = blogId;
        result = 31 * result + (familyTreeId != null ? familyTreeId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (blogPostingDate != null ? blogPostingDate.hashCode() : 0);
        result = 31 * result + (blogContent != null ? blogContent.hashCode() : 0);
        return result;
    }
}
