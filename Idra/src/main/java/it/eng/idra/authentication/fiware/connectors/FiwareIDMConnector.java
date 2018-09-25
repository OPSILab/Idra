package it.eng.idra.authentication.fiware.connectors;

import it.eng.idra.authentication.fiware.configuration.IDMProperty;
import it.eng.idra.authentication.fiware.model.Token;
import it.eng.idra.authentication.fiware.model.UserInfo;
import it.eng.idra.utils.PropertyManager;

public abstract class FiwareIDMConnector {

	protected String clientId;
	protected String clientSecret;
	protected String redirectUri;
	protected String protocol;
	protected String host;
	protected int port;

	protected String baseUrl;
	protected static final String path_token = PropertyManager.getProperty(IDMProperty.IDM_PATH_TOKEN);
	protected static final String path_user = PropertyManager.getProperty(IDMProperty.IDM_PATH_USER);

	public FiwareIDMConnector(String protocol, String host, int port, String client_id, String client_secret,
			String redirectUri) {
		super();
		this.clientId = client_id;
		this.clientSecret = client_secret;
		this.redirectUri = redirectUri;

		this.protocol = protocol;
		this.host = host;
		this.port = port;
		String _host = null;
		
		boolean needsPort = !(("http".equalsIgnoreCase(this.protocol) && port == 80)
				|| ("https".equalsIgnoreCase(this.protocol) && port == 443));
		
		if (this.port != -1)
			_host = this.host.concat(needsPort ? ":".concat(String.valueOf(this.port)) : "");
		else
			_host = this.host;
		
		this.baseUrl = this.protocol.concat("://").concat(_host);
	}

	/**
	 * Get the Token associated to the input Authorization Code (OAuth2 Code grant)
	 * 
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public abstract Token getToken(String code) throws Exception;

	/**
	 * Get the User Info associated to the input token
	 * 
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public abstract UserInfo getUserInfo(String token) throws Exception;

}
