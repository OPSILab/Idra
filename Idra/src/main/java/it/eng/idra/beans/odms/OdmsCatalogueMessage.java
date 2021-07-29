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

package it.eng.idra.beans.odms;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "node_messages")
public class OdmsCatalogueMessage {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "nodeID", unique = false, nullable = false)
  private int nodeID;

  @Column(name = "message", unique = false, nullable = false, columnDefinition = "LONGTEXT")
  private String message;

  @Column(name = "date")
  //@Type(type="date")
  private ZonedDateTime date;

  public OdmsCatalogueMessage() {

  }

  /**
   * Instantiates a new odms catalogue message.
   *
   * @param id the id
   * @param nodeID the node ID
   * @param message the message
   * @param date the date
   */
  public OdmsCatalogueMessage(int id, int nodeID, String message, ZonedDateTime date) {
    super();
    this.id = id;
    this.nodeID = nodeID;
    this.message = message;
    this.date = date;
  }

  /**
   * Instantiates a new odms catalogue message.
   *
   * @param nodeID the node ID
   * @param message the message
   * @param date the date
   */
  public OdmsCatalogueMessage(int nodeID, String message, ZonedDateTime date) {
    super();
    this.nodeID = nodeID;
    this.message = message;
    this.date = date;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getNodeID() {
    return nodeID;
  }

  public void setNodeID(int nodeID) {
    this.nodeID = nodeID;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ZonedDateTime getDate() {
    return date;
  }

  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  @Override
  public String toString() {
    return "Message [id=" + id + ", message=" + message + ", date=" + date + "]";
  }

}
