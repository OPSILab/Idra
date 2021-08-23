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

package it.eng.idra.beans.orion;

import com.google.gson.annotations.SerializedName;
import it.eng.idra.beans.odms.OdmsCatalogueAdditionalConfiguration;
import javax.persistence.Entity;
import javax.persistence.Transient;

// TODO: Auto-generated Javadoc
/**
 * The Class OrionCatalogueConfiguration.
 */
@Entity
public class OrionCatalogueConfiguration extends OdmsCatalogueAdditionalConfiguration {

  /** The is authenticated. */
  private boolean isAuthenticated;

  /** The auth token. */
  private String authToken;

  /** The oauth 2 endpoint. */
  private String oauth2Endpoint;

  /** The client id. */
  @SerializedName(value = "clientID")
  private String clientId;

  /** The client secret. */
  private String clientSecret;

  /** The orion dataset dump string. */
  private String orionDatasetDumpString;

  /** The orion dataset file path. */
  private String orionDatasetFilePath;

  /** The ngsild. */
  private boolean ngsild = false;

  /**
   * Instantiates a new orion catalogue configuration.
   */
  public OrionCatalogueConfiguration() {
    this.setType("ORION");
  }

  /**
   * Instantiates a new orion catalogue configuration.
   *
   * @param isAuthenticated the is authenticated
   * @param authToken       the auth token
   * @param oauth2Endpoint  the oauth 2 endpoint
   * @param clientId        the client id
   * @param clientSecret    the client secret
   * @param datasets        the datasets
   * @param ngsild          the ngsild
   */
  public OrionCatalogueConfiguration(boolean isAuthenticated, String authToken,
      String oauth2Endpoint, String clientId, String clientSecret, String datasets,
      boolean ngsild) {
    super();
    this.isAuthenticated = isAuthenticated;
    this.authToken = authToken;
    this.oauth2Endpoint = oauth2Endpoint;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.orionDatasetDumpString = datasets;
    this.ngsild = ngsild;
    this.setType("ORION");
  }

  /**
   * Instantiates a new orion catalogue configuration.
   *
   * @param isAuthenticated the is authenticated
   * @param authToken       the auth token
   * @param oauth2Endpoint  the oauth 2 endpoint
   * @param clientId        the client id
   * @param clientSecret    the client secret
   * @param datasets        the datasets
   * @param dumpPath        the dump path
   * @param ngsild          the ngsild
   */
  public OrionCatalogueConfiguration(boolean isAuthenticated, String authToken,
      String oauth2Endpoint, String clientId, String clientSecret, String datasets, String dumpPath,
      boolean ngsild) {
    this(isAuthenticated, authToken, oauth2Endpoint, clientId, clientSecret, datasets, ngsild);
    this.orionDatasetFilePath = dumpPath;
  }

  /**
   * Checks if is authenticated.
   *
   * @return true, if is authenticated
   */
  public boolean isAuthenticated() {
    return isAuthenticated;
  }

  /**
   * Sets the authenticated.
   *
   * @param isAuthenticated the new authenticated
   */
  public void setAuthenticated(boolean isAuthenticated) {
    this.isAuthenticated = isAuthenticated;
  }

  /**
   * Gets the auth token.
   *
   * @return the auth token
   */
  public String getAuthToken() {
    return authToken;
  }

  /**
   * Sets the auth token.
   *
   * @param authToken the new auth token
   */
  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }

  /**
   * Gets the orion dataset dump string.
   *
   * @return the orion dataset dump string
   */
  @Transient
  public String getOrionDatasetDumpString() {
    return orionDatasetDumpString;
  }

  /**
   * Sets the orion dataset dump string.
   *
   * @param orionDatasetDump the new orion dataset dump string
   */
  public void setOrionDatasetDumpString(String orionDatasetDump) {
    this.orionDatasetDumpString = orionDatasetDump;
  }

  /**
   * Gets the orion dataset file path.
   *
   * @return the orion dataset file path
   */
  public String getOrionDatasetFilePath() {
    return orionDatasetFilePath;
  }

  /**
   * Sets the orion dataset file path.
   *
   * @param orionDatasetFilePath the new orion dataset file path
   */
  public void setOrionDatasetFilePath(String orionDatasetFilePath) {
    this.orionDatasetFilePath = orionDatasetFilePath;
  }

  /**
   * Gets the oauth 2 endpoint.
   *
   * @return the oauth 2 endpoint
   */
  public String getOauth2Endpoint() {
    return oauth2Endpoint;
  }

  /**
   * Sets the oauth 2 endpoint.
   *
   * @param oauth2Endpoint the new oauth 2 endpoint
   */
  public void setOauth2Endpoint(String oauth2Endpoint) {
    this.oauth2Endpoint = oauth2Endpoint;
  }

  /**
   * Gets the client id.
   *
   * @return the client id
   */
  public String getClientId() {
    return clientId;
  }

  /**
   * Sets the client id.
   *
   * @param clientId the new client id
   */
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  /**
   * Gets the client secret.
   *
   * @return the client secret
   */
  public String getClientSecret() {
    return clientSecret;
  }

  /**
   * Sets the client secret.
   *
   * @param clientSecret the new client secret
   */
  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  /**
   * Checks if is ngsild.
   *
   * @return true, if is ngsild
   */
  public boolean isNgsild() {
    return ngsild;
  }

  /**
   * Sets the ngsild.
   *
   * @param ngsild the new ngsild
   */
  public void setNgsild(boolean ngsild) {
    this.ngsild = ngsild;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((authToken == null) ? 0 : authToken.hashCode());
    result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
    result = prime * result + ((clientSecret == null) ? 0 : clientSecret.hashCode());
    result = prime * result + (isAuthenticated ? 1231 : 1237);
    result = prime * result + ((oauth2Endpoint == null) ? 0 : oauth2Endpoint.hashCode());
    result = prime * result
        + ((orionDatasetDumpString == null) ? 0 : orionDatasetDumpString.hashCode());
    result = prime * result
        + ((orionDatasetFilePath == null) ? 0 : orionDatasetFilePath.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    OrionCatalogueConfiguration other = (OrionCatalogueConfiguration) obj;
    if (authToken == null) {
      if (other.authToken != null) {
        return false;
      }
    } else if (!authToken.equals(other.authToken)) {
      return false;
    }
    if (clientId == null) {
      if (other.clientId != null) {
        return false;
      }
    } else if (!clientId.equals(other.clientId)) {
      return false;
    }
    if (clientSecret == null) {
      if (other.clientSecret != null) {
        return false;
      }
    } else if (!clientSecret.equals(other.clientSecret)) {
      return false;
    }
    if (isAuthenticated != other.isAuthenticated) {
      return false;
    }
    if (oauth2Endpoint == null) {
      if (other.oauth2Endpoint != null) {
        return false;
      }
    } else if (!oauth2Endpoint.equals(other.oauth2Endpoint)) {
      return false;
    }
    if (orionDatasetDumpString == null) {
      if (other.orionDatasetDumpString != null) {
        return false;
      }
    } else if (!orionDatasetDumpString.equals(other.orionDatasetDumpString)) {
      return false;
    }
    if (orionDatasetFilePath == null) {
      if (other.orionDatasetFilePath != null) {
        return false;
      }
    } else if (!orionDatasetFilePath.equals(other.orionDatasetFilePath)) {
      return false;
    }
    return true;
  }

}
