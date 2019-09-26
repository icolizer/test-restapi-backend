package kz.app.services;

import kz.app.data.TransferModel;
import kz.app.entities.Transfer;

import java.util.List;
import java.util.Optional;

public interface TransfersService {
    Transfer create(TransferModel transferModel);
    void delete(TransferModel transferModel);
    Optional<Transfer> getById(String id);
    List<Transfer> list(int limit, int offset, String order);
}
