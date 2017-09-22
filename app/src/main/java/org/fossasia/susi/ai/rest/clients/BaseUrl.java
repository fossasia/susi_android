package org.fossasia.susi.ai.rest.clients;

import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.responses.susi.SusiBaseUrls;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

/**
 * <h1>Class to get Base URLs of susi/custom server.</h1>
 *
 * Created by Rajan Maurya on 12/10/16.
 */
public class BaseUrl {

    public static final String PROTOCOL_HTTP = "https://";
    public static final String SUSI_DEFAULT_BASE_URL = "https://api.susi.ai";
    public static final String SUSI_SERVICES_URL = "https://config.asksusi.com";

    /**
     * This Method for updating the Susi Base url. first checking the index of current base url
     * from the list of baseUrls and if index != size() of list, it means that current base url
     * lies between the 0 to size() of base urls and just pick the next one base url and set in
     * SharedPreferences and if current base url is the last url in BaseUrl list then just pick
     * the first one base url and set in SharedPreferences.
     *
     * @param t the throwable
     */
    public static void updateBaseUrl(Throwable t) {
        SusiBaseUrls baseUrls = PrefManager.getBaseUrls();
        if(baseUrls!=null){
            int indexOfUrl = baseUrls.getSusiServices().indexOf(PrefManager.getSusiRunningBaseUrl());
            if (indexOfUrl != baseUrls.getSusiServices().size()) {
                PrefManager.setSusiRunningBaseUrl(BaseUrl.PROTOCOL_HTTP + baseUrls.getSusiServices().get(indexOfUrl + 1));
            } else {
                PrefManager.setSusiRunningBaseUrl(BaseUrl.PROTOCOL_HTTP + baseUrls.getSusiServices().get(0));
            }
            if (t instanceof SocketTimeoutException || t instanceof ConnectException) {
                ClientBuilder.createSusiService();
            }
        }
    }
}
