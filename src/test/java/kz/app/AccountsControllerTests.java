package kz.app;

import io.micronaut.http.*;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.annotation.MicronautTest;
import kz.app.data.AccountModel;
import kz.app.entities.Account;
import kz.app.helpers.AccountModelHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static kz.app.helpers.AccountsRequestHelper.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountsControllerTests {
    @Inject EmbeddedServer server;

    @Inject
    @Client(value = "/")
    private RxHttpClient client;

    @Test
    void postNewEntitySuccessfully() throws URISyntaxException {
        var accountModel = AccountModelHelper.create(BigDecimal.TEN, "RUB", 999999999999L);
        var request = createPostV1Account(accountModel);
        var httpResponse = client.exchange(request, Account.class).blockingFirst();
        var createdAccount = httpResponse.body();
        assertEquals(HttpStatus.CREATED, httpResponse.getStatus());
        assertEquals("RUB", createdAccount.getCurrency());
        assertEquals(BigDecimal.TEN, createdAccount.getBalance());
        assertNotNull(createdAccount.getId());
    }

    @Test
    void postDuplicatedEntity() throws URISyntaxException {
        var accountModel = AccountModelHelper.create(BigDecimal.TEN, "USD", 123L);
        var request = createPostV1Account(accountModel);
        client.exchange(request, Account.class).blockingSingle();
        var duplicationResponse = client.exchange(request, HttpMethod.class);
        assertThrows(HttpClientResponseException.class,
                () -> { duplicationResponse.blockingSingle(); },
                "Method Not Allowed");
    }

    @Test
    void postModelValidationError() throws URISyntaxException {
        var accountModel = AccountModelHelper.create(null, "", 123L);
        var request = createPostV1Account(accountModel);
        var validErrorResp = client.exchange(request, HttpMethod.class);
        assertThrows(HttpClientResponseException.class,
                () -> { validErrorResp.blockingSingle(); },
                "Field currency is NULL");
    }

    @Test
    void postNewEntityAndGetFromLocation() throws URISyntaxException {
        var accountModel = AccountModelHelper.create(BigDecimal.ZERO, "ZZZ", 55L);
        var request = createPostV1Account(accountModel);
        var httpResponse = client.exchange(request, Account.class).blockingFirst();
        var createdAccount = httpResponse.body();
        var location = httpResponse.getHeaders().findFirst("Location").orElseThrow(IllegalArgumentException::new);
        var getReq = HttpRequest.GET(new URI(location));
        var checkAccount = client.toBlocking().retrieve(getReq, Account.class);
        assertEquals(HttpStatus.CREATED, httpResponse.getStatus());
        assertEquals(accountModel.getCurrency(), createdAccount.getCurrency());
        assertEquals(accountModel.getBalance(), createdAccount.getBalance());
        assertEquals(accountModel.getUserId(), createdAccount.getUserId());
        assertEquals(createdAccount.getCurrency(), checkAccount.getCurrency());
        assertEquals(createdAccount.getBalance(), checkAccount.getBalance());
        assertEquals(createdAccount.getUserId(), checkAccount.getUserId());
        assertEquals(createdAccount.getId(), checkAccount.getId());
    }

    @Test
    void putAccountsChangeCurrency() throws URISyntaxException {
        var accountModel = AccountModelHelper.create(BigDecimal.TEN, "AAA", 11111L);
        var request = createPostV1Account(accountModel);
        var newAccount = client.exchange(request, Account.class).blockingFirst();
        accountModel.setBalance(BigDecimal.ONE);
        accountModel.setCurrency("BBB");
        accountModel.setId(newAccount.body().getId());
        var putRequest = HttpRequest.PUT(new URI("/v1/accounts"), accountModel);
        var httpResponse = client.exchange(putRequest, Account.class).blockingFirst();
        assertEquals(HttpStatus.OK, httpResponse.getStatus());
        assertEquals("BBB", httpResponse.body().getCurrency());
        assertEquals(accountModel.getUserId(), httpResponse.body().getUserId());
        assertEquals(accountModel.getId(), httpResponse.body().getId());
    }

    @Test
    void putNotFoundAccount() throws URISyntaxException {
        var accountModel = AccountModelHelper.create(BigDecimal.TEN, "AAA", 11111L);
        accountModel.setId(Long.MAX_VALUE);
        var request = HttpRequest.PUT(new URI("/v1/accounts"), accountModel);
        var badRequest = client.exchange(request, HttpMethod.class);
        assertThrows(HttpClientResponseException.class,
                () -> { badRequest.blockingSingle(); },
                "Not found by id");
    }

    @Test
    void listAllByOrder() {
        var accountModel1 = AccountModelHelper.create(BigDecimal.TEN, "AAA", 100L);
        var accountModel2 = AccountModelHelper.create(BigDecimal.TEN, "BBB", 100L);
        var accountModel3 = AccountModelHelper.create(BigDecimal.TEN, "CCC", 100L);
        var models = List.of(accountModel1, accountModel2, accountModel3);
        createGroup(models);
        Account[] all = client.toBlocking().retrieve(HttpRequest.GET("/v1/accounts"), Account[].class);
        Account[] first2 = client.toBlocking().retrieve(HttpRequest.GET("/v1/accounts?offset=0&max=2"), Account[].class);
        Account[] last2 = client.toBlocking().retrieve(HttpRequest.GET("/v1/accounts?offset=0&max=2&order=desc"), Account[].class);
        assertEquals(all[0].getId(), first2[0].getId());
        assertEquals(all[1].getId(), first2[1].getId());
        assertEquals(all[all.length - 1].getId(), last2[0].getId());
        assertEquals(all[all.length - 2].getId(), last2[1].getId());
    }

    @Test
    void deleteSuccess() throws URISyntaxException {
        var accountModel = AccountModelHelper.create(BigDecimal.TEN, "AAA", 555L);
        var request = createPostV1Account(accountModel);
        var newAccount = client.exchange(request, Account.class).blockingFirst();
        accountModel.setId(newAccount.body().getId());
        var deleteReq = HttpRequest.DELETE(new URI("/v1/accounts"), accountModel);
        var httpResponse = client.exchange(deleteReq, Account.class).blockingFirst();
        assertEquals(HttpStatus.OK, httpResponse.getStatus());
    }

    private List<Account> createGroup(List<AccountModel> models) {
        return models.stream()
                .map((m) -> {
                    try {
                        HttpRequest<AccountModel> request1 = createPostV1Account(m);
                        return client.exchange(request1, Account.class).blockingFirst().body();
                    } catch (Exception e) {
                        throw new RuntimeException("Error creating group");
                    }
                })
                .collect(Collectors.toList());
    }
}
