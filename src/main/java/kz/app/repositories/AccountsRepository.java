package kz.app.repositories;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;
import kz.app.entities.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@JdbcRepository
public interface AccountsRepository extends CrudRepository<Account, Long> {
    Optional<Account> findByUserIdAndCurrency(Long userId, String currency);
    void update(@Id Long id, BigDecimal balance, String currency, Long userId);
    void update(@Id Long id, BigDecimal balance);
    List<Account> listOrderById(Pageable pageable);
    List<Account> listOrderByIdDesc(Pageable pageable);
}
