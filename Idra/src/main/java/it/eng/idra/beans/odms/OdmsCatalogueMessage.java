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

package it.eng.idra.beans.odms;

import com.google.gson.annotations.SerializedName;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

// TODO: Auto-generated Javadoc
/**
 * The Class OdmsCatalogueMessage.
 */
@Entity
@Table(name = "node_messages")
public class OdmsCatalogueMessage {

  /** The id. */
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  /** The node id. */
  @Column(name = "nodeID", unique = false, nullable = false)
  @SerializedName(value = "nodeID")
  private int nodeId;

  /** The message. */
  @Column(name = "message", unique = false, nullable = false, columnDefinition = "LONGTEXT")
  private String message;

  /** The date. */
  @Column(name = "date")
  // @Type(type="date")
  private ZonedDateTime date;

  /**
   * Instantiates a new odms catalogue message.
   */
  public OdmsCatalogueMessage() {

  }

  /**
   * Instantiates a new odms catalogue message.
   *
   * @param id      the id
   * @param nodeId  the node ID
   * @param message the message
   * @param date    the date
   */
  public OdmsCatalogueMessage(int id, int nodeId, String message, ZonedDateTime date) {
    super();
    this.id = id;
    this.nodeId = nodeId;
    this.message = message;
    this.date = date;
  }

  /**
   * Instantiates a new odms catalogue message.
   *
   * @param nodeId  the node ID
   * @param message the message
   * @param date    the date
   */
  public OdmsCatalogueMessage(int nodeId, String message, ZonedDateTime date) {
    super();
    this.nodeId = nodeId;
    this.message = message;
    this.date = date;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  public int getId() {
    return id;
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
   * Gets the node id.
   *
   * @return the node id
   */
  public int getNodeId() {
    return nodeId;
  }

  /**
   * Sets the node id.
   *
   * @param nodeId the new node id
   */
  public void setNodeId(int nodeId) {
    this.nodeId = nodeId;
  }

  /**
   * Gets the message.
   *
   * @return the message
   */
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
   * Gets the date.
   *
   * @return the date
   */
  public ZonedDateTime getDate() {
    return date;
  }

  /**
   * Sets the date.
   *
   * @param date the new date
   */
  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Message [id=" + id + ", message=" + message + ", date=" + date + "]";
  }

}
