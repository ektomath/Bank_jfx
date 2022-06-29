package tomekh2.model;

import java.math.BigDecimal;
/**
 * Klassen för kreditkonto och ska innehålla variabler för typ, kreditgräns, räntesats och ränta på lånade pengar.
 * Klassen kommer ärva ett par metoder ifrån den abstrakta klassen Account, nämligen -> logTransaction, getTransactionLog,
 * och diverse andra getters och setters
 * Sedan har den en egen metod för withdraw eftersom det är viss skillnad på hur sparkonton och kreditkonton hanterar detta.
 * Samma för getInterest då kreditkonton har två olika räntor.
 * Konstruktor fås även den från superklassen.
 * @author Tomas Ekholm, tomekh-2
 */
public class CreditAccount extends Account {
    private String type = "Kreditkonto";
    private BigDecimal creditlimit = new BigDecimal("5000");
    private BigDecimal interest = new BigDecimal("0.5");
    private BigDecimal debtInterest = new BigDecimal("7.0");


    public CreditAccount(String owner) {
        super(owner);
    }
    public String getType(){ return this.type; }


    public BigDecimal getInterest() {
        // kollar om saldot ligger på + eller -
        if (getSaldo().compareTo(BigDecimal.ZERO) >= 0) {
            return this.interest;
        } else { return this.debtInterest;
        }
    }


    /**
     * Tar ut pengar från kontot
     *
     * @param amount innehåller summan vi tar ut
     * @return returnerar false om det saknades täckning och true om pengarna togs ut
     */
    public Boolean withdraw(BigDecimal amount){
        int result = saldo.add(creditlimit).compareTo(amount);
        // kollar så att vi inte tar ut mer pengar än som finns på kontot
        if(result < 0){return false;}
        // om vi har täckning tar vi ut pengarna
        saldo = saldo.subtract(amount);
        logTransaction(amount.negate());
        return true;

    }
}
