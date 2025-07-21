package it.eng.idra.beans.zenodo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import it.eng.idra.utils.restclient.RestClient;
import it.eng.idra.utils.restclient.RestClientImpl;

public class ZenodoConnection {

   /** Host */
   private String _host;
   /** Port */
   private int _port;
   /** Apikey */
   private String _apikey = null;
   /** Proxy */
   private static Properties proxyProps = new Properties();
   /** The logger */
   private static Logger logger = LogManager.getLogger(ZenodoConnection.class);

   public ZenodoConnection() {
   }

   public ZenodoConnection(String host) {
      if (host.contains("http") && !host.contains("https")) {
         this._host = "https" + host.split("http")[1];
      } else {
         this._host = host;
      }

   }

   public ZenodoConnection(String host, int port) {
      if (!Pattern.matches(".*:(\\d.*)", host)) {
         if (host.contains("http") && !host.contains("https")) {
            this._host = "https" + host.split("http")[1];
         } else {
            this._host = host;
         }
      } else {
         this._host = host.split(".*:(\\d.*)")[0];
      }

      if (host.contains("http") && !host.contains("https")) {
         this._host = "https" + host.split("http")[1];
      } else {
         this._host = host;
      }

      this._port = port;

      try {
         new URL(this._host + ":" + this._port + "/api/");
      } catch (MalformedURLException var4) {
         var4.printStackTrace();
         logger.info(var4);
      }

   }

   public void setApiKey(String key) {
      this._apikey = key;
   }

   /**
    * Send get request.
    *
    * @param urlString the url string
    * @return the string
    * @throws Exception the exception
    */
   protected String sendGetRequest(String urlString) throws Exception {
      try {
         // for testing
         logger.info("ZenodoConnection - sendGetRequest - urlString: " + urlString);
         logger.info("ZenodoConnection - sendGetRequest - host: " + this._host);
         logger.info("ZenodoConnection - sendGetRequest - port: " + this._port);
         logger.info("ZenodoConnection - sendGetRequest - apikey: " + this._apikey);
         //
         RestClient client = new RestClientImpl();

         HashMap<String, String> headers = new HashMap<>();
         headers.put("Authorization", "Bearer " + this._apikey);
         headers.put("Content-Type", "application/json; charset=UTF-8");

         HttpResponse response = client.sendGetRequest(this._host + urlString, headers);

         int status = client.getStatus(response);
         logger.info("ZenodoConnection - sendGetRequest - status: " + status);
         if (status == 200) {
            String responseBody = client.getHttpResponseBody(response);
            // logger.info("ZenodoConnection - sendGetRequest - getHttpResponseBody: " + responseBody);
            return responseBody;
         } else {
            return null;
         }

      } catch (Exception e) {
         throw e;
      }
   }

   /**
    * Send post request.
    *
    * @param path path
    * @param data data
    * @return the string
    * @throws UnknownHostException     the exception
    * @throws SocketTimeoutException   the exception
    * @throws IOException              the exception
    * @throws NoSuchAlgorithmException the exception
    * @throws KeyStoreException        the exception
    * @throws KeyManagementException   the exception
    */
   protected String Post(String path, String data) throws UnknownHostException, SocketTimeoutException, IOException,
         NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
      URL url = null;
      String body = "";
      logger.info("CONNECTION: OPEN");
      // System.out.println(this._host + path + "\t" + data);
      url = new URL(this._host + path);
      RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(300000).setSocketTimeout(900000).build();
      SSLContextBuilder builder = SSLContextBuilder.create();
      builder.loadTrustMaterial(new TrustSelfSignedStrategy());
      SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
      HttpHost proxy = null;
      CloseableHttpClient httpclient = null;
      if (Boolean.parseBoolean(getProperty("https.proxyEnabled").trim())
            && StringUtils.isNotBlank(getProperty("https.proxyHost").trim())) {
         int port = 80;
         if (StringUtils.isNotBlank(getProperty("https.proxyPort"))) {
            port = Integer.parseInt(getProperty("https.proxyPort"));
         }

         proxy = new HttpHost(getProperty("https.proxyHost"), port, "https");
         DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
         httpclient = HttpClients.custom().setRoutePlanner(routePlanner).setSSLSocketFactory(sslsf).build();
         if (StringUtils.isNotBlank(getProperty("https.proxyUser"))) {
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(getProperty("https.proxyHost"), port),
                  new UsernamePasswordCredentials(getProperty("https.proxyUser"), getProperty("https.proxyPassword")));
            HttpClients.custom().setRoutePlanner(routePlanner).setSSLSocketFactory(sslsf)
                  .setDefaultCredentialsProvider(credsProvider).build();
            httpclient = HttpClients.custom().setRoutePlanner(routePlanner).setSSLSocketFactory(sslsf)
                  .setDefaultCredentialsProvider(credsProvider).build();
         }
      } else {
         httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
      }

      try {
         HttpPost postRequest = new HttpPost(url.toString());
         postRequest.setHeader("X-ZENODO-API-KEY", this._apikey);// check about this
         StringEntity input = new StringEntity(data, "UTF-8");
         postRequest.setConfig(requestConfig);
         input.setContentType("application/json");
         input.setContentEncoding("UTF-8");
         postRequest.setEntity(input);
         HttpResponse response = httpclient.execute(postRequest);
         int statusCode = response.getStatusLine().getStatusCode();
         logger.info("Status code: " + statusCode);
         if (statusCode == 404) {
            throw new UnknownHostException("404NotFound");
         } else {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String result = "";

            for (String line = ""; (line = rd.readLine()) != null; result = result + line) {
            }

            body = result.toString();
            return body;
         }
      } catch (UnknownHostException | UnsupportedOperationException var17) {
         var17.printStackTrace();
         throw new UnknownHostException(var17.getMessage());
      } catch (IOException var18) {
         var18.printStackTrace();
         if (!var18.getClass().equals(SocketTimeoutException.class)
               && !var18.getClass().equals(ConnectException.class)) {
            throw new IOException(var18.getMessage());
         } else {
            throw new SocketTimeoutException(var18.getMessage());
         }
      }
   }

   public static String getProperty(String propName) {
      Optional<String> prop = Optional.ofNullable(System.getenv(propName.toString()));
      return (String) prop.orElseGet(() -> {
         return proxyProps.getProperty(propName.toString(), "");
      });
   }

   static {
      try {
         proxyProps.load(ZenodoConnection.class.getClassLoader().getResourceAsStream("configuration.properties"));
      } catch (IOException var1) {
         var1.printStackTrace();
      }

   }

}
