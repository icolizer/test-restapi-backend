package kz.app.controllers.v1;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import kz.app.data.AccountModel;
import kz.app.entities.Account;
import kz.app.exceptions.BadRequestException;
import kz.app.exceptions.NotFoundException;
import kz.app.services.AccountsService;
import kz.app.validators.AccountsModelValidator;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@Controller("/v1/accounts")
public class AccountsController {
    private final AccountsService accountsService;
    private final AccountsModelValidator accountsModelValidator;

    @Inject
    public AccountsController(final AccountsService accountsService,
                              final AccountsModelValidator accountsModelValidator) {
        this.accountsService = accountsService;
        this.accountsModelValidator = accountsModelValidator;
    }

    @Post
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<Account> postAccounts(@Body AccountModel accountModel) {
        accountsModelValidator.checkRulesWithoutId(accountModel)
                .ifPresent( (br) -> { throw new BadRequestException(br); } );
        Account account = accountsService.create(accountModel);
        return HttpResponse.created(account)
                .header("Location", String.format("/v1/accounts/%d", account.getId()));
    }

    @Put
    @Produces(MediaType.APPLICATION_JSON)
    public Account putAccounts(@Body AccountModel accountModel) {
        accountsModelValidator.checkRules(accountModel)
                .ifPresent( (br) -> { throw new BadRequestException(br); } );
        return accountsService.update(accountModel);
    }

    @Delete
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse deleteAccounts(@Body AccountModel accountModel) {
        if (accountModel.getId() == null)
            throw new BadRequestException("Id is empty");
        accountsService.delete(accountModel);
        return HttpResponse.ok();
    }

    @Get("/{?max,offset,order}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Account> accounts(@Nullable Integer max,
                                  @Nullable Integer offset,
                                  @Nullable String order) {
        return accountsService.list(
                max == null ? Integer.MAX_VALUE : max,
                offset == null ? 0 : offset,
                order == null ? "ASC" : order);
    }

    @Get(uri = "/{id}", processes = MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccountById(Long id) {
        return accountsService.getById(id).orElseThrow(NotFoundException::new);
    }
}
