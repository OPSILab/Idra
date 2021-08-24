/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.beans;

import it.eng.idra.utils.JsonRequired;

// TODO: Auto-generated Javadoc
/**
 * The Class PasswordChange.
 */
public class PasswordChange {

  /** The old password. */
  @JsonRequired
  private String oldPassword;

  /** The new password. */
  @JsonRequired
  private String newPassword;

  /** The new password confirm. */
  @JsonRequired
  private String newPasswordConfirm;

  /** The username. */
  @JsonRequired
  private String username;

  /**
   * Instantiates a new password change.
   *
   * @param oldPassword        the old password
   * @param newPassword        the new password
   * @param newPasswordConfirm the new password confirm
   * @param username           the username
   */
  public PasswordChange(String oldPassword, String newPassword, String newPasswordConfirm,
      String username) {
    super();
    this.oldPassword = oldPassword;
    this.newPassword = newPassword;
    this.newPasswordConfirm = newPasswordConfirm;
    this.username = username;
  }

  /**
   * Gets the old password.
   *
   * @return the old password
   */
  public String getOldPassword() {
    return oldPassword;
  }

  /**
   * Sets the old password.
   *
   * @param oldPassword the new old password
   */
  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

  /**
   * Gets the new password.
   *
   * @return the new password
   */
  public String getNewPassword() {
    return newPassword;
  }

  /**
   * Sets the new password.
   *
   * @param newPassword the new new password
   */
  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  /**
   * Gets the new password confirm.
   *
   * @return the new password confirm
   */
  public String getNewPasswordConfirm() {
    return newPasswordConfirm;
  }

  /**
   * Sets the new password confirm.
   *
   * @param newPasswordConfirm the new new password confirm
   */
  public void setNewPasswordConfirm(String newPasswordConfirm) {
    this.newPasswordConfirm = newPasswordConfirm;
  }

  /**
   * Gets the username.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username.
   *
   * @param username the new username
   */
  public void setUsername(String username) {
    this.username = username;
  }

}
