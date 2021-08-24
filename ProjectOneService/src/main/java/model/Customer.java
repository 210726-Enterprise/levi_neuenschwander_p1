package model;

import orm.annotations.Column;
import orm.annotations.Entity;
import orm.annotations.Id;

/**
 * User class
 */
@Entity(tableName = "customer")
public class Customer {
    /**
     * Instance variables
     */
    @Id(columnName = "id")
    private int id;
    @Column(columnName = "fname")
    private String fName;
    @Column(columnName = "lname")
    private String lName;
    @Column(columnName = "username")
    private String username;
    @Column(columnName = "password")
    private String password;

    public Customer(){}

    /**
     * Constructor for a user
     * @param id database id of the user
     * @param fName first name of the user
     * @param lName last name of the user
     * @param username username of the user
     * @param password password of the user
     */
    public Customer(String fName, String lName, String username, String password){
        this.fName = fName;
        this.lName = lName;
        this.username = username;
        this.password = password;
    }

    public Customer(int id, String fName, String lName, String username, String password) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.username = username;
        this.password = password;
    }

    public void setId(int id){this.id = id;}

    /**
     * Getter method for the database id of the user
     * @return database id of the user
     */
    public int getId(){
        return this.id;
    }

    /**
     * Getter method for first name of the user
     * @return first name of the user
     */
    public String getFName() {
        return fName;
    }

    /**
     * Setter method for first name of the user
     * @param fName first name of the user
     */
    public void setFName(String fName) {
        this.fName = fName;
    }

    /**
     * Getter method for the last name of the user
     * @return last name of the user
     */
    public String getLName() {
        return lName;
    }

    /**
     * Setter method for the last name of the user
     * @param lName last name of the user
     */
    public void setLName(String lName) {this.lName = lName;}

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", fName='" + fName + '\'' +
                ", lName='" + lName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    /**
     * Getter method for username
     * @return username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter method for the username
     * @param username username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter method for the password
     * @return password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter method for the password
     * @param password password for the user
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
