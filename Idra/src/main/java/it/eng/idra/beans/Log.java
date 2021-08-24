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

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

// TODO: Auto-generated Javadoc
/**
 * The Class Log.
 */
@Entity
@Table(name = "logs")
public class Log {

  /** The id. */
  private int id = 0;

  /** The logger. */
  private String logger;

  /** The message. */
  private String message;

  /** The level. */
  private String level;

  /** The timestamp. */
  private ZonedDateTime timestamp;

  /**
   * Instantiates a new log.
   */
  public Log() {

  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  public int getId() {
    return this.id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Gets the logger.
   *
   * @return the logger
   */
  @Column(name = "logger")
  public String getLogger() {
    return logger;
  }

  /**
   * Sets the logger.
   *
   * @param logger the new logger
   */
  public void setLogger(String logger) {
    this.logger = logger;
  }

  /**
   * Gets the message.
   *
   * @return the message
   */
  @Column(name = "message", unique = false, nullable = false, columnDefinition = "LONGTEXT")
  public String getMessage() {
    return message;
  }

  /**
   * Sets the message.
   *
   * @param message the new message
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Gets the level.
   *
   * @return the level
   */
  @Column(name = "level")
  public String getLevel() {
    return level;
  }

  /**
   * Sets the level.
   *
   * @param level the new level
   */
  public void setLevel(String level) {
    this.level = level;
  }

  /**
   * Gets the dated.
   *
   * @return the dated
   */
  @Column(name = "dated")
  // @Type(type="date")
  public ZonedDateTime getDated() {
    return timestamp;
  }

  /**
   * Sets the dated.
   *
   * @param dated the new dated
   */
  public void setDated(ZonedDateTime dated) {
    this.timestamp = dated;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Logs [id=" + id + ", logger=" + logger + ", message=" + message + ", level=" + level
        + ", dated=" + timestamp + "]";
  }

}
