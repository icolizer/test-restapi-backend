package kz.app;

import io.micronaut.http.*;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Flowable;
import kz.app.data.TransferModel;
import kz.app.entities.Account;
import kz.app.entities.Transfer;
import kz.app.helpers.AccountModelHelper;
import kz.app.helpers.TransferModelHelper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static kz.app.helpers.AccountsRequestHelper.*;
import static org.hamcrest.MatcherAssert.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransfersControllerTests {
    private AtomicLong atomicLong = new AtomicLong(500L);
    @Inject EmbeddedServer server;

    @Inject
    @Client(value = "/")
    private RxHttpClient client;

    @Test
    void postTransferCheckRatingSuccessfullyCheckLocationHeader() throws URISyntaxException {
        System.out.println("postTransferCheckRatingSuccessfullyCheckLocationHeader");
        var accountModelFrom = AccountModelHelper.create(new BigDecimal(100), "RUB", atomicLong.incrementAndGet());
        var accountModelTo = AccountModelHelper.create(BigDecimal.ZERO, "RUB", atomicLong.incrementAndGet());
        var accountFrom = client.exchange(createPostV1Account(accountModelFrom), Account.class).blockingFirst().body();
        var accountTo = client.exchange(createPostV1Account(accountModelTo), Account.class).blockingFirst().body();
        var transferModel = TransferModelHelper.create(accountFrom.getId(), accountTo.getId(), BigDecimal.TEN, BigDecimal.ONE);
        var transferResponse = createPostTransfer(transferModel).blockingFirst();
        var transfer = transferResponse.body();
        var location = transferResponse.getHeaders().findFirst("Location").get();
        var transferFromGetRequest = client.exchange(HttpRequest.GET(location), Transfer.class).blockingFirst().body();
        assertEquals(HttpStatus.CREATED, transferResponse.getStatus());
        assertEquals(transferFromGetRequest.getId(), transfer.getId());
        assertNotNull(transferFromGetRequest.getAccountTo());
        assertNotNull(transferFromGetRequest.getAccountFrom());
        assertThat(new BigDecimal(90), Matchers.comparesEqualTo(transfer.getAccountFrom().getBalance()));
        assertThat(BigDecimal.TEN, Matchers.comparesEqualTo(transfer.getAccountTo().getBalance()));
    }

    @Test
    void postTransferNotEnoughBalance() throws URISyntaxException {
        System.out.println("postTransferNotEnoughBalance");
        var amountToTransfer = new BigDecimal(100);
        var doubleAmount = amountToTransfer.add(amountToTransfer);
        var accountModelFrom = AccountModelHelper.create(amountToTransfer, "RUB", atomicLong.incrementAndGet());
        var accountFrom = client.exchange(createPostV1Account(accountModelFrom), Account.class).blockingFirst().body();
        var transferModel = TransferModelHelper.create(accountFrom.getId(), 1L, doubleAmount, BigDecimal.ONE);
        var transferRequest = createPostTransfer(transferModel);
        assertThrows(HttpClientResponseException.class,
                () -> { transferRequest.blockingSingle(); },
                String.format("Balance not enough: actual %s transfer amount %s",
                        amountToTransfer, doubleAmount));
    }

    @Test
    void postTransferAccountToNotFound() throws URISyntaxException {
        System.out.println("postTransferAccountToNotFound");
        var amountToTransfer = new BigDecimal(100);
        var doubleAmount = amountToTransfer.add(amountToTransfer);
        var accountModelFrom = AccountModelHelper.create(amountToTransfer, "RUB", atomicLong.incrementAndGet());
        var accountFrom = client.exchange(createPostV1Account(accountModelFrom), Account.class).blockingFirst().body();
        var transferModel = TransferModelHelper.create(accountFrom.getId(), -1L, doubleAmount, BigDecimal.ONE);
        var transferRequest = createPostTransfer(transferModel);
        assertThrows(HttpClientResponseException.class, () -> { transferRequest.blockingSingle(); });
    }

    @Test
    void postTransferValidationError() throws URISyntaxException {
        System.out.println("postTransferValidationError");
        var transferModel = TransferModelHelper.create(-1L, -1L, BigDecimal.ZERO, BigDecimal.ZERO);
        var transferRequest = createPostTransfer(transferModel);
        assertThrows(HttpClientResponseException.class,
                () -> { transferRequest.blockingSingle(); },
                "Field amount is less than 0");
    }

    @Test
    void listAllByOrder() throws URISyntaxException {
        System.out.println("listAllByOrder");
        var accountModel1 = AccountModelHelper.create(new BigDecimal(1000), "USD", atomicLong.incrementAndGet());
        var accountModel2 = AccountModelHelper.create(new BigDecimal(1000), "USD", atomicLong.incrementAndGet());
        var accountModel3 = AccountModelHelper.create(new BigDecimal(1000), "USD", atomicLong.incrementAndGet());
        var account1 = client.exchange(createPostV1Account(accountModel1), Account.class).blockingFirst().body();
        var account2 = client.exchange(createPostV1Account(accountModel2), Account.class).blockingFirst().body();
        var account3 = client.exchange(createPostV1Account(accountModel3), Account.class).blockingFirst().body();
        var transferA1A2Model = TransferModelHelper.create(account1.getId(), account2.getId(), BigDecimal.TEN, BigDecimal.ONE);
        var transferA2A1Model = TransferModelHelper.create(account2.getId(), account1.getId(), BigDecimal.TEN, BigDecimal.ONE);
        var transferA2A3Model = TransferModelHelper.create(account2.getId(), account3.getId(), BigDecimal.TEN, BigDecimal.ONE);
        var transferA3A2Model = TransferModelHelper.create(account3.getId(), account2.getId(), BigDecimal.TEN, BigDecimal.ONE);
        var transferA1A3Model = TransferModelHelper.create(account1.getId(), account3.getId(), BigDecimal.TEN, BigDecimal.ONE);
        var transferA3A1Model = TransferModelHelper.create(account3.getId(), account2.getId(), BigDecimal.TEN, BigDecimal.ONE);
        var transferA3A1_2Model = TransferModelHelper.create(account3.getId(), account2.getId(), BigDecimal.TEN, BigDecimal.ONE);
        sendGroupTransfers(Arrays.asList(transferA1A2Model, transferA2A1Model, transferA2A3Model, transferA3A2Model,
                transferA1A3Model, transferA3A1Model, transferA3A1_2Model));
        Transfer[] all = client.toBlocking().retrieve(HttpRequest.GET("/v1/transfers"), Transfer[].class);
        Transfer[] first2 = client.toBlocking().retrieve(HttpRequest.GET("/v1/transfers?max=2&offset=0"), Transfer[].class);
        Transfer[] last3 = client.toBlocking().retrieve(HttpRequest.GET("/v1/transfers?max=3&order=desc"), Transfer[].class);
        assertThat(all.length, Matchers.greaterThanOrEqualTo(7));
        assertEquals(2, first2.length);
        assertEquals(3, last3.length);
        assertThat(first2[0].getDate(), Matchers.lessThan(first2[1].getDate()));
        assertThat(last3[0].getDate(), Matchers.greaterThan(last3[2].getDate()));
    }

    @Test
    void deleteSuccess() throws URISyntaxException {
        System.out.println("deleteSuccess");
        var accountModelFrom = AccountModelHelper.create(new BigDecimal(100), "RUB", atomicLong.incrementAndGet());
        var accountModelTo = AccountModelHelper.create(BigDecimal.ZERO, "RUB", atomicLong.incrementAndGet());
        var accountFrom = client.exchange(createPostV1Account(accountModelFrom), Account.class).blockingFirst().body();
        var accountTo = client.exchange(createPostV1Account(accountModelTo), Account.class).blockingFirst().body();
        var transferModel = TransferModelHelper.create(accountFrom.getId(), accountTo.getId(), BigDecimal.TEN, BigDecimal.ONE);
        var transferResponse = client.exchange(
                HttpRequest.POST(
                        new URI("/v1/transfers"), transferModel).contentType(MediaType.APPLICATION_JSON_TYPE),
                Transfer.class)
                .blockingFirst();
        transferModel.setId(transferResponse.body().getId().toString());
        var deleteReq = HttpRequest.DELETE(new URI("/v1/transfers"), transferModel);
        HttpResponse<Account> httpResponse = client.exchange(deleteReq, Account.class).blockingFirst();
        assertEquals(HttpStatus.OK, httpResponse.getStatus());
    }

    private void sendGroupTransfers(List<TransferModel> transferModels) {
        transferModels.forEach((tm) -> {
            try {
                createPostTransfer(tm).blockingFirst();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Flowable<HttpResponse<Transfer>> createPostTransfer(TransferModel transferModel) throws URISyntaxException {
        return client.exchange(
                    HttpRequest.POST(new URI("/v1/transfers"), transferModel)
                        .contentType(MediaType.APPLICATION_JSON_TYPE),
                    Transfer.class);
    }
}
