/*
Namn: Tomas Ekholm
ltu-id: tomekh-2
 */
package tomekh2.model;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Abstrakt klass superklass till kreditkonto och sparkonto.
 * Klassen innehåller variabler för kontonummer, ägare, ett objekt för saldo och en static int lastAssignedNumber som ser till att vi kan ge alla nya konton unika kontonummer.
 * Efter Lab2 har vi lagt till objekt för datumformatering och en sträng innehållande formateringsreglerna. Även en ArrayList för gjorda transaktioner har skapats.
 * Det finns en konstruktor som tar ett personnumer som input och skapar ett unikt konto med personnumret som ägare.
 * Vi har även getters för saldo,kontonummer och type samt en setter för saldo vilket vi behöver när vi för över eller tar ut pengar.
 * Finns en funktion för att spara transaktions historik nu efter Lab2
 * @author Tomas Ekholm, tomekh-2
 */
public abstract class Account implements Serializable {
    private final int accNr;
    protected BigDecimal saldo = new BigDecimal("0");
    private String owner;
    private static int lastAssignedNumber = 1000;
    private final String pattern = "yyyy-MM-dd HH:mm:ss";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    private ArrayList<String> transactionLog = new ArrayList<>();


    public Account(String pNo){
        lastAssignedNumber++;
        this.accNr = lastAssignedNumber;
        this.owner = pNo;
    }
    abstract BigDecimal getInterest();
    abstract String getType();
    abstract Boolean withdraw(BigDecimal amount);

    public static void setLastAssignedNumber(int nr) { lastAssignedNumber = nr; }
    public static int getLastAssignedNumber() { return lastAssignedNumber; }
    public int getAccNr(){
        return this.accNr;
    }
    public BigDecimal getSaldo() { return this.saldo; }
    public void setSaldo(BigDecimal newsaldo) { this.saldo = newsaldo; }
    public ArrayList<String> getTransactionLog() { return this.transactionLog; }


    /**
     * Loggar en gjord transaktion
     *
     * @param amount innehåller summan som transfererades
     */
    public void logTransaction(BigDecimal amount){
        // skapar en sträng med datum + transaktionsbelopp + saldo och adderar till transactionLog
        String event = simpleDateFormat.format(new Date())+" "
                +NumberFormat.getCurrencyInstance(new Locale("sv","SE")).format(amount)
                +" Saldo: " +NumberFormat.getCurrencyInstance(new Locale("sv","SE")).format(saldo);
        transactionLog.add(event);

    }


}
