package io.kamzy.breezebill.models;

import java.util.Date;

import io.kamzy.breezebill.enums.GroupType;

public class Groupss {
    private int group_id;
    private String group_name;
    private int created_by;
    private String description;
    private GroupType group_type;
    private String parent_group;
    private int member_count;
    private Date created_at;

    public Groupss(int group_id, String group_name, int created_by, String description, GroupType group_type, String parent_group, int member_count, Date created_at) {
        this.group_id = group_id;
        this.group_name = group_name;
        this.created_by = created_by;
        this.description = description;
        this.group_type = group_type;
        this.parent_group = parent_group;
        this.member_count = member_count;
        this.created_at = created_at;
    }

    public Groupss() {
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GroupType getGroup_type() {
        return group_type;
    }

    public void setGroup_type(GroupType group_type) {
        this.group_type = group_type;
    }

    public String getParent_group() {
        return parent_group;
    }

    public void setParent_group(String parent_group) {
        this.parent_group = parent_group;
    }

    public int getMember_count() {
        return member_count;
    }

    public void setMember_count(int member_count) {
        this.member_count = member_count;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
