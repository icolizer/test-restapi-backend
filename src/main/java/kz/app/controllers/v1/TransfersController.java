package kz.app.controllers.v1;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import kz.app.data.TransferModel;
import kz.app.entities.Transfer;
import kz.app.exceptions.BadRequestException;
import kz.app.exceptions.NotFoundException;
import kz.app.services.TransfersService;
import kz.app.validators.TransfersModelValidator;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@Controller("/v1/transfers")
public class TransfersController {
    private final TransfersService transfersService;
    private final TransfersModelValidator transfersModelValidator;

    @Inject
    public TransfersController(final TransfersService transfersService,
                               final TransfersModelValidator transfersModelValidator) {
        this.transfersService = transfersService;
        this.transfersModelValidator = transfersModelValidator;
    }

    @Post
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<Transfer> postTransfer(@Body TransferModel transferModel) {
        transfersModelValidator.checkRulesWithoutId(transferModel)
                .ifPresent( (br) -> { throw new BadRequestException(br); } );
        Transfer transfer = transfersService.create(transferModel);
        return HttpResponse.created(transfer)
                .header("Location", String.format("/v1/transfers/%s", transfer.getId().toString()));
    }

    @Delete
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse deleteTransfer(@Body TransferModel transferModel) {
        if (transferModel.getId() == null)
            throw new BadRequestException("Id is empty");
        transfersService.delete(transferModel);
        return HttpResponse.ok();
    }

    @Get(uri = "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Transfer getTransferById(String id) {
        return transfersService.getById(id)
                .orElseThrow(NotFoundException::new);
    }

    @Get("/{?max,offset,order}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Transfer> transfers(@Nullable Integer max,
                                  @Nullable Integer offset,
                                  @Nullable String order) {
        return transfersService.list(
                max == null ? Integer.MAX_VALUE : max,
                offset == null ? 0 : offset,
                order == null ? "ASC" : order);
    }
}
