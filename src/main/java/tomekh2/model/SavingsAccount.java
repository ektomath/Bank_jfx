/*
Namn: Tomas Ekholm
ltu-id: tomekh-2
 */
package tomekh2.model;
import java.math.BigDecimal;

/**
 * Klassen för sparkonto innehåller variabler för typ, räntesats, ränta per uttag och om man har rätt till ett fritt uttag.
 * Klassen kommer ärva ett par metoder ifrån den abstrakta klassen Account, nämligen -> logTransaction, getTransactionLog
 * samt även diverse andra getters och setters.
 * Sedan har den en egen metod för withdraw eftersom det är viss skillnad på hur sparkonton och kreditkonton hanterar detta.
 * Samma för getInterest då kreditkonton har två olika räntor och sparkonton bara en.
 * Konstruktor fås även den från superklassen.
 * @author Tomas Ekholm, tomekh-2
 */
public class SavingsAccount extends Account {
    private BigDecimal interest = new BigDecimal("1.2");
    private BigDecimal withdrawFee = new BigDecimal("1.02");
    private String type = "Sparkonto";
    private boolean freeWithdrawLeft = true; //Tänker mig att denna variabeln resettas varje år.


    public SavingsAccount(String owner) {
        super(owner);
    }
    public BigDecimal getInterest() {
        return this.interest;
    }
    public String getType(){
        return this.type;
    }


    /**
     * Tar ut pengar från kontot
     *
     * @param amount innehåller summan vi tar ut
     * @return returnerar false om det saknades täckning och true om pengarna togs ut
     */
    public Boolean withdraw(BigDecimal amount){
        BigDecimal newamount = amount;
        //Kollar om vi har några gratis withdrawals left
        if (!freeWithdrawLeft) {
            newamount = newamount.multiply(withdrawFee);
        }
        int result = saldo.compareTo(newamount);
        // kollar så att vi inte tar ut mer pengar än som finns på kontot
        if(result < 0){return false;}
        // om vi har täckning tar vi ut pengarna
        saldo = saldo.subtract(newamount);
        //loggar uttaget
        logTransaction(newamount.negate());
        freeWithdrawLeft = false;
        return true;

    }


}
