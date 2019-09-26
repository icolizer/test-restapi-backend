package kz.app.repositories;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;
import kz.app.entities.Transfer;

import java.util.List;
import java.util.UUID;

@JdbcRepository
public interface TransfersRepository extends CrudRepository<Transfer, UUID> {
    List<Transfer> listOrderByDate(Pageable pageable);
    List<Transfer> listOrderByDateDesc(Pageable pageable);
}
