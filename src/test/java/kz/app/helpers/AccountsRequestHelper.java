package kz.app.helpers;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import kz.app.data.AccountModel;

import java.net.URI;
import java.net.URISyntaxException;

public class AccountsRequestHelper {
    public static HttpRequest<AccountModel> createPostV1Account(AccountModel accountModel) throws URISyntaxException {
        return HttpRequest
                .POST(new URI("/v1/accounts"), accountModel)
                .contentType(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_STREAM_TYPE);
    }
}
