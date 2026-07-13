package dio.budgeting.domain;

import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction transaction);

    List<Transaction> findAllByCategory(Category category);

    // novo metodo Contrato para obter o saldo total por categoria
    long getTotalAmountByCategory(Category category);
}
