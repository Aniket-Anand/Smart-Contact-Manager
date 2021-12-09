package com.smart.smartcontactmanager.entities;
import java.util.*;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size; 

@Entity
@Table(name="USER")
public class User {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    @NotBlank(message = "Name field is required !!")
    @Size(min = 2, max = 25, message = "Length of name should be in between 2 and 25 character.")
    private  String name;
    @Column(unique=true)
    private String email;
    private String password;
    private String role;
    private boolean enable;
    private String imageUrl;
    @Column(length=500)
    private String about;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private List<Contact> contacts =new ArrayList<>();

    public List<Contact> getContacts() {
        return contacts;
    }
    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public boolean isEnable() {
        return enable;
    }
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getAbout() {
        return about;
    }
    public void setAbout(String about) {
        this.about = about;
    }
    public User(int id, String name, String email, String password, String role, boolean enable, String imageUrl,
            String about) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enable = enable;
        this.imageUrl = imageUrl;
        this.about = about;
    }
    public User() {
    }

    
    @Override
    public String toString() {
        return "User [about=" + about + ", contacts=" + contacts + ", email=" + email + ", enable=" + enable + ", id="
                + id + ", imageUrl=" + imageUrl + ", name=" + name + ", password=" + password + ", role=" + role + "]";
    }

    
    

    

}
