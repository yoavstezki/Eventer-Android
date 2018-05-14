package com.yoavs.eventer.service;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;

/**
 * @author yoavs
 */

public class FacebookFriendsFinderService {

    public static void find(GraphRequest.GraphJSONArrayCallback callback) {
        GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), callback);

        request.executeAsync();

    }
}
