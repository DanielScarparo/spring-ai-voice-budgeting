package dio.budgeting.infrastructure.persistence.repository;

import dio.budgeting.domain.Category;
import dio.budgeting.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TransactionEntityRepository extends CrudRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findAllByCategory(Category category);

    // novo metodo de soma o valor (em centavos) de uma categoria específica
    @Query("SELECT COALESCE(SUM(t.amount), 0L) FROM TransactionEntity t WHERE t.category = :category")
    Long sumAmountByCategory(@Param("category") Category category);
}
