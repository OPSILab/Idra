package it.eng.idra.authentication.keycloak.model;

import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Class KeycloakUser.
 */
public class KeycloakUser {

  /** The sub. */
  private String sub;
  
  /** The email verified. */
  private boolean email_verified;
  
  /** The roles. */
  private Set<String> roles;
  
  /** The name. */
  private String name;
  
  /** The preferred username. */
  private String preferred_username;
  
  /** The given name. */
  private String given_name;
  
  /** The family name. */
  private String family_name;
  
  /** The email. */
  private String email;

  /**
   * Instantiates a new keycloak user.
   */
  public KeycloakUser() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new keycloak user.
   *
   * @param sub the sub
   * @param email_verified the email verified
   * @param roles the roles
   * @param name the name
   * @param preferred_username the preferred username
   * @param given_name the given name
   * @param family_name the family name
   * @param email the email
   */
  public KeycloakUser(String sub, boolean email_verified, 
      Set<String> roles, String name, String preferred_username,
      String given_name, String family_name, String email) {
    super();
    this.sub = sub;
    this.email_verified = email_verified;
    this.roles = roles;
    this.name = name;
    this.preferred_username = preferred_username;
    this.given_name = given_name;
    this.family_name = family_name;
    this.email = email;
  }

  /**
   * Gets the sub.
   *
   * @return the sub
   */
  public String getSub() {
    return sub;
  }

  /**
   * Sets the sub.
   *
   * @param sub the new sub
   */
  public void setSub(String sub) {
    this.sub = sub;
  }

  /**
   * Checks if is email verified.
   *
   * @return true, if is email verified
   */
  public boolean isEmail_verified() {
    return email_verified;
  }

  /**
   * Sets the email verified.
   *
   * @param email_verified the new email verified
   */
  public void setEmail_verified(boolean email_verified) {
    this.email_verified = email_verified;
  }

  /**
   * Gets the roles.
   *
   * @return the roles
   */
  public Set<String> getRoles() {
    return roles;
  }

  /**
   * Sets the roles.
   *
   * @param roles the new roles
   */
  public void setRoles(Set<String> roles) {
    this.roles = roles;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the preferred username.
   *
   * @return the preferred username
   */
  public String getPreferred_username() {
    return preferred_username;
  }

  /**
   * Sets the preferred username.
   *
   * @param preferred_username the new preferred username
   */
  public void setPreferred_username(String preferred_username) {
    this.preferred_username = preferred_username;
  }

  /**
   * Gets the given name.
   *
   * @return the given name
   */
  public String getGiven_name() {
    return given_name;
  }

  /**
   * Sets the given name.
   *
   * @param given_name the new given name
   */
  public void setGiven_name(String given_name) {
    this.given_name = given_name;
  }

  /**
   * Gets the family name.
   *
   * @return the family name
   */
  public String getFamily_name() {
    return family_name;
  }

  /**
   * Sets the family name.
   *
   * @param family_name the new family name
   */
  public void setFamily_name(String family_name) {
    this.family_name = family_name;
  }

  /**
   * Gets the email.
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email.
   *
   * @param email the new email
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "KeycloakUser [sub=" + sub + ", "
        + "email_verified=" + email_verified + ", roles=" + roles + ", name=" + name
        + ", preferred_username=" + preferred_username + ", "
            + "given_name=" + given_name + ", family_name=" + family_name
        + ", email=" + email + "]";
  }

}
