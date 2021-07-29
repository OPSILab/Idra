/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *  
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package it.eng.idra.beans;

import it.eng.idra.beans.exception.InvalidPasswordException;
import it.eng.idra.utils.JsonRequired;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "user")
public class User {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @JsonRequired
  @Column(name = "username")
  private String username;

  @Column(name = "password")
  private String password;

  @Column(name = "firstname")
  private String firstname;

  @Column(name = "lastname")
  private String lastname;

  @Column(name = "email")
  private String email;

  @Column(name = "registerDate")
  @Type(type = "date")
  private Date registerDate;

  @Column(name = "lastAccess")
  @Type(type = "date")
  private Date lastAccess;

  public User() {
  }

  /**
   * Instantiates a new user.
   *
   * @param id the id
   * @param username the username
   * @param password the password
   * @param firstname the firstname
   * @param lastname the lastname
   * @param email the email
   * @param registerDate the register date
   * @param lastAccess the last access
   */
  public User(int id, String username, 
      String password, String firstname, String lastname, String email,
      Date registerDate, Date lastAccess) {

    try {
      setUsername(username);
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }

    try {
      setPassword(password);
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }

    setId(id);
    setFirstname(firstname);
    setLastname(lastname);
    setEmail(email);
    setRegisterDate(registerDate);
    setLastAccess(lastAccess);

  }

  private void setUsername(String param) throws Exception {
    if (param.length() > 2 && param.length() < 50) {
      this.username = param;
    } else {
      throw new Exception("Username" + param + "not valid");
    }
  }

  /**
   * Sets the password.
   *
   * @param param the new password
   * @throws InvalidPasswordException the invalid password exception
   */
  public void setPassword(String param) throws InvalidPasswordException {
    if (param.length() >= 5 && param.length() < 50) {
      this.password = param;
    } else {
      throw new InvalidPasswordException("Provided new password not valid");
    }
  }

  private void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  private void setLastname(String param) {
    this.lastname = param;
  }

  private void setEmail(String param) {
    this.email = param;
  }

  private void setRegisterDate(Date param) {
    this.registerDate = param;
  }

  private void setLastAccess(Date param) {
    this.lastAccess = param;
  }

  public String getUsername() {
    return this.username;
  }

  public String getPassword() {
    return this.password;
  }

  public String getLastname() {
    return this.lastname;
  }

  public String getEmail() {
    return this.email;
  }

  public Date getRegisterDate() {
    return this.registerDate;
  }

  public Date getLastAccess() {
    return this.lastAccess;
  }

  public String toString() {
    return this.username + " " + this.password + " " + this.email + " " + this.registerDate;
  }

  public String getFirstname() {
    return firstname;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

}
