/*
Namn: Tomas Ekholm
ltu-id: tomekh-2
 */
package tomekh2.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Klass för bankens kunder. Innehåller variabler för personnummer, förnamn, efternamn och en lista med alla konton som denna kund äger.
 * Konstruktorn tar ett personnumer, förnamn och efternamn som input.
 * Det finns getters och setters för alla variabler nämnda ovan samt även två metoder för att lägga till och ta bort konton från användaren.
 * @author Tomas Ekholm, tomekh-2
 */
public class Customer implements Serializable {
    private final String pNo;
    private String name;
    private String surName;
    private List<Account> accounts = new ArrayList<>();


    public Customer(String pNo, String firstName, String surName) {
        this.pNo = pNo;
        this.name = firstName;
        this.surName = surName;
    }


    public String getName() {
        return this.name;
    }
    public String getSurName() {
        return this.surName;
    }
    public List<Account> getAccounts() {
        return this.accounts;
    }
    public void setName(String newFirst) {
        this.name = newFirst;
    }
    public void setSurName(String newSur) { this.surName = newSur; }
    public String getpNo() { return pNo; }


    /**
     * Lägger till ett konto till en kunds lista med konton.
     * @param account innehåller kontot som skall läggas till.
     */
    public void addAccount(Account account){
        accounts.add(account);
    }
    /**
     * Tar bort ett konto från en kunds lista med konton.
     * @param account innehåller kontot som ska tas bort
     */
    public void rmAccount(List<Account> account){
        accounts.removeAll(account);
    }


}
