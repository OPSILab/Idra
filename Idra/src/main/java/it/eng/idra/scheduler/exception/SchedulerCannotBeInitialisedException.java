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

package it.eng.idra.scheduler.exception;

// TODO: Auto-generated Javadoc
/**
 * The Class SchedulerCannotBeInitialisedException.
 */
public class SchedulerCannotBeInitialisedException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5194733344990785894L;

  /**
   * Instantiates a new scheduler cannot be initialised exception.
   *
   * @param message the message
   */
  public SchedulerCannotBeInitialisedException(String message) {
    super(message);
  }

  /**
   * Instantiates a new scheduler cannot be initialised exception.
   *
   * @param message the message
   * @param t       the t
   */
  public SchedulerCannotBeInitialisedException(String message, Throwable t) {
    super(message, t);
  }
}
