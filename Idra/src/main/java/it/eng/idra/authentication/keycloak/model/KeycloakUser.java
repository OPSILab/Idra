package it.eng.idra.authentication.keycloak.model;

import java.util.Set;

public class KeycloakUser {
   	
    private String sub;
	private boolean email_verified;
	private Set<String> roles;
	private String name;
	private String preferred_username;
	private String given_name;
	private String family_name;
	private String email;
	
	
	public KeycloakUser() {
		// TODO Auto-generated constructor stub
	}


	public KeycloakUser(String sub, boolean email_verified, Set<String> roles, String name, String preferred_username,
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


	public String getSub() {
		return sub;
	}


	public void setSub(String sub) {
		this.sub = sub;
	}


	public boolean isEmail_verified() {
		return email_verified;
	}


	public void setEmail_verified(boolean email_verified) {
		this.email_verified = email_verified;
	}


	public Set<String> getRoles() {
		return roles;
	}


	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPreferred_username() {
		return preferred_username;
	}


	public void setPreferred_username(String preferred_username) {
		this.preferred_username = preferred_username;
	}


	public String getGiven_name() {
		return given_name;
	}


	public void setGiven_name(String given_name) {
		this.given_name = given_name;
	}


	public String getFamily_name() {
		return family_name;
	}


	public void setFamily_name(String family_name) {
		this.family_name = family_name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	@Override
	public String toString() {
		return "KeycloakUser [sub=" + sub + ", email_verified=" + email_verified + ", roles=" + roles + ", name=" + name
				+ ", preferred_username=" + preferred_username + ", given_name=" + given_name + ", family_name="
				+ family_name + ", email=" + email + "]";
	}
	

}
