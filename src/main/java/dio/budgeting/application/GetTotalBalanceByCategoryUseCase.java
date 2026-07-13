package dio.budgeting.application;

import dio.budgeting.application.output.BalanceOutput;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class GetTotalBalanceByCategoryUseCase {
    private final TransactionRepository transactionRepository;

    public GetTotalBalanceByCategoryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(
            name = "get-total-balance-by-category",
            description = "Calcula o saldo total, soma ou total acumulado de gastos financeiros em uma categoria específica (GROCERIES, PHARMA ou AUTO)"
    )
    public BalanceOutput execute(
            @ToolParam(description = "Categoria financeira para consultar o saldo (GROCERIES, PHARMA, AUTO)") Category category) {
        long totalInCents = transactionRepository.getTotalAmountByCategory(category);
        return BalanceOutput.from(category, totalInCents);
    }
}