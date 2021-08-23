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

// TODO: Auto-generated Javadoc
/**
 * The Enum OdmsSynchLock.
 */
public enum OdmsSynchLock {

  /** The first. */
  FIRST("FIRST"),
  /** The periodic. */
  PERIODIC("PERIODIC"),
  /** The none. */
  NONE("NONE");

  /** The text. */
  private final String text;

  /**
   * Instantiates a new odms synch lock.
   *
   * @param text the text
   */
  private OdmsSynchLock(final String text) {
    this.text = text;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return text;
  }
}
