package ru.minipay.service;

import ru.minipay.dao.AccountDao;
import ru.minipay.model.Account;
import ru.minipay.model.Currency;

import java.math.BigDecimal;

public class FundTransferServiceImpl implements FundTransferService{
    private final AccountDao dao;
    private final FundExchangeService exchangeService;

    public FundTransferServiceImpl(AccountDao dao, FundExchangeService exchangeService) {
        this.dao = dao;
        this.exchangeService = exchangeService;
    }

    @Override
    public void makeTransfer(Account from, Account to, Currency currency, BigDecimal amount) {
        //TODO: what if dao returns null?
        from = dao.getById(from.getId());
        to = dao.getById(to.getId());
        BigDecimal amountInCurrency = exchangeService.exchange(amount, currency, from.getCurrency());
        from.setBalance(from.getBalance().subtract(amountInCurrency));
        dao.insert(from);

        amountInCurrency = exchangeService.exchange(amount, currency, to.getCurrency());
        to.setBalance(to.getBalance().add(amountInCurrency));
        dao.insert(to);
    }
}
