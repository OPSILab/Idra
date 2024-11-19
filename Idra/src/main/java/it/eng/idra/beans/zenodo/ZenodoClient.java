package it.eng.idra.beans.zenodo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import it.eng.idra.beans.odms.OdmsCatalogueForbiddenException;
import it.eng.idra.beans.odms.OdmsCatalogueNotFoundException;
import it.eng.idra.beans.odms.OdmsCatalogueOfflineException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ckan.DatasetAdditionalDeserializer;

public final class ZenodoClient {

    /** Connection class */
    private ZenodoConnection _connection = null;
    /** Logger */
    private static Logger logger = LogManager.getLogger(ZenodoClient.class);
    /**
     * Gson library that can be used to convert Java Objects into their JSON
     * representation
     */
    public static Gson _gson = new Gson();
    /** Gson extras */
    private Gson gsonExtras = (new GsonBuilder())
            .registerTypeAdapter(ZenodoDataset.class, new DatasetAdditionalDeserializer()).create();

    public ZenodoClient(ZenodoConnection c, String apikey) {
        this._connection = c;
        this._connection.setApiKey(apikey);
    }

    /**
     * List all open access records with query arguments
     *
     * @param query        Search query
     * @param status       Filter result based on the deposit status (either draft
     *                     or published)
     * @param sort         Sort order (bestmatch or mostrecent). Prefix with minus
     *                     to change form ascending to descending (e.g. -mostrecent)
     * @param page         Page number for pagination
     * @param size         Number of results to return per page
     * @param all_versions Show (true or 1) or hide (false or 0) all versions of
     *                     records.
     * @param communities  Return records that are part of the specified communities
     * @param type         Return records of the specified type
     * @param subtype      Return records of the specified subtype
     * @param bounds       Return records filtered by a geolocation bounding box
     * @param custom       Return records containing the specified custom keywords
     * @return List<ZenodoDataset>
     * @throws OdmsCatalogueOfflineException   the odms catalogue offline
     *                                         exception
     * @throws OdmsCatalogueForbiddenException the odms catalogue forbidden
     *                                         exception
     * @throws OdmsCatalogueNotFoundException  the odms catalogue not found
     *                                         exception
     * @throws ZenodoException                 Zenodo Exception
     * @throws Exception                       Exception
     */
    public List<ZenodoDataset.Hit> findRecords(String query, String status, String sort, Integer page, Integer size,
            String all_versions,
            String communities, String type, String subtype, String bounds, String custom)
            throws OdmsCatalogueOfflineException, OdmsCatalogueForbiddenException, OdmsCatalogueNotFoundException,
            ZenodoException, Exception {

        StringBuilder urlStringBuilder = new StringBuilder();

        if (query != null && !query.trim().isEmpty()) {
            urlStringBuilder.append("q=").append(query).append("&");
        }

        // Default to 'road-steamer' community if communities are null or empty
        if (communities == null || communities.trim().isEmpty()) {
            communities = "road-steamer";
        }

        if (communities != null && !communities.trim().isEmpty()) {
            urlStringBuilder.append("communities=").append(communities).append("&");
        }

        if (status != null && !status.trim().isEmpty()) {
            urlStringBuilder.append("status=").append(status).append("&");
        }

        if (sort != null && !sort.trim().isEmpty()) {
            urlStringBuilder.append("sort=").append(sort).append("&");
        }

        if (page != null) {
            urlStringBuilder.append("page=").append(page).append("&");
        }

        if (size != null) {
            urlStringBuilder.append("size=").append(size).append("&");
        }

        if (all_versions != null && !all_versions.trim().isEmpty()) {
            urlStringBuilder.append("all_versions=").append(all_versions).append("&");
        }

        if (type != null && !type.trim().isEmpty()) {
            urlStringBuilder.append("type=").append(type).append("&");
        }

        if (subtype != null && !subtype.trim().isEmpty()) {
            urlStringBuilder.append("subtype=").append(subtype).append("&");
        }

        if (bounds != null && !bounds.trim().isEmpty()) {
            urlStringBuilder.append("bounds=").append(bounds).append("&");
        }

        if (custom != null && !custom.trim().isEmpty()) {
            urlStringBuilder.append("custom=").append(custom).append("&");
        }

        // Remove the last "&" if present
        String urlStringBuilderFinal = urlStringBuilder.toString();
        if (urlStringBuilderFinal.endsWith("&")) {
            urlStringBuilderFinal = urlStringBuilderFinal.substring(0, urlStringBuilderFinal.length() - 1);
        }

        logger.info("ZenodoClient - findRecords - urlStringBuilderFinal: " + urlStringBuilderFinal);

        StringBuilder returned_json = new StringBuilder();
        boolean stop = true;
        int attempts = 0;

        do {
            try {

                returned_json.append(_connection.sendGetRequest("records?" + urlStringBuilderFinal));

                logger.info("ZenodoClient - findRecords - returned_json: " + returned_json.toString());

            } catch (UnknownHostException uhe) {
                if (!"404NotFound".equals(uhe.getMessage())) {
                    throw new ZenodoException(" The Zenodo host does not exist");
                }
            } catch (SocketTimeoutException ste) {
                throw new ZenodoException(" The Zenodo node is currently unreachable");
            } catch (IOException ioe) {
                throw new ZenodoException(ioe.getMessage());
            } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
                e.printStackTrace();
                throw new ZenodoException(e.getMessage());
            }

            if (returned_json != null && returned_json.toString().startsWith("{")) {
                stop = false;
            } else {
                attempts++;
                if (attempts == 5) {
                    stop = false;
                }
            }
        } while (stop);

        if (returned_json == null || !returned_json.toString().startsWith("{")) {
            if (returned_json.toString().matches(".*The requested URL could not be retrieved.*")
                    || returned_json.toString().matches(".*does not exist.*")) {
                throw new OdmsCatalogueNotFoundException(" The ODMS host does not exist");
            } else if (returned_json.toString().contains("403")) {
                throw new OdmsCatalogueForbiddenException(" The ODMS node is forbidden");
            } else {
                throw new OdmsCatalogueOfflineException(" The ODMS node is currently unreachable");
            }
        } else {
            ZenodoDataset.Response zenodoResponse = (ZenodoDataset.Response) this.LoadClass(
                    ZenodoDataset.Response.class,
                    returned_json.toString());
            List<ZenodoDataset.Hit> hits = null;
            if (zenodoResponse != null && zenodoResponse.getHits() != null) {
                hits = zenodoResponse.getHits().getHits();
                logger.info("ZenodoClient - findRecords - total: " + zenodoResponse.getHits().getTotal());
            }
            return hits;
        }
    }

    /**
     * Retrieve a single record.
     *
     * @param id id of record
     * @return ZenodoDataset
     * @throws OdmsCatalogueOfflineException   the odms catalogue offline
     *                                         exception
     * @throws OdmsCatalogueForbiddenException the odms catalogue forbidden
     *                                         exception
     * @throws OdmsCatalogueNotFoundException  the odms catalogue not found
     *                                         exception
     * @throws ZenodoException                 Zenodo Exception
     * @throws Exception                       Exception
     */
    public ZenodoDataset.Hit getRecord(Integer id)
            throws OdmsCatalogueOfflineException, OdmsCatalogueForbiddenException, OdmsCatalogueNotFoundException,
            ZenodoException, Exception {

        StringBuilder returned_json = new StringBuilder();

        try {

            returned_json.append(_connection.sendGetRequest("records/" + id));

            logger.info("ZenodoClient - getRecord - returned_json: " + returned_json.toString());

        } catch (UnknownHostException uhe) {
            if (uhe.getMessage() != "404NotFound") {
                throw new ZenodoException(" The ODMS host does not exist");
            }
        } catch (SocketTimeoutException ste) {
            throw new ZenodoException(" The ODMS node is currently unreachable");
        } catch (IOException ioe) {
            throw new ZenodoException(ioe.getMessage());
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
            throw new ZenodoException(e.getMessage());
        }

        if (returned_json == null || !returned_json.toString().startsWith("{")) {
            if (returned_json.toString().matches(".*The requested URL could not be retrieved.*")
                    || returned_json.toString().matches(".*does not exist.*")) {
                throw new OdmsCatalogueNotFoundException(" The ODMS host does not exist");
            } else if (returned_json.toString().contains("403")) {
                throw new OdmsCatalogueForbiddenException(" The ODMS node is forbidden");
            } else {
                throw new OdmsCatalogueOfflineException(" The ODMS node is currently unreachable");
            }
        } else {

            ZenodoDataset.Hit record = (ZenodoDataset.Hit) this.LoadClass(ZenodoDataset.Hit.class,
                    returned_json.toString());
            return record;
        }
    }

    // utils

    protected <T> T LoadClass(Class<T> cls, String data) {
        try {
            return this.gsonExtras.fromJson(data, cls);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

}