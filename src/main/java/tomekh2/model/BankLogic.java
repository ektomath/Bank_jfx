/*
Namn: Tomas Ekholm
ltu-id: tomekh-2
 */
//TODO glöm inte kommentarer till allt sen
package tomekh2.model;
import java.io.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Banklogic innehåller mer avancerade metoder som behöver interagera med flera klasser/objekt "samtidigt".
 * Det är "logiken" för att åstadkomma mer komplexa saker med flera beståndsdelar såsom att hämta en lista med alla kunder,
 * föra över pengar, stänga alla konton för en kund, etc.
 * Variablerna i klassen är en lista med kunder och en lista med konton.
 * Metoderna som finns är enligt specifikation
 * getAllCustomers
 * createCustomer
 * getCustomer
 * changeCustomerName
 * createSavingsAccount
 * createCreditAccount
 * getAccount
 * deposit
 * withdraw
 * closeAccount
 * deleteCustomer
 * getTransactions
 * @author Tomas Ekholm, tomekh-2
 */
public class BankLogic {
    private List<Customer> customerList = new ArrayList<>();
    private List<Account> accountList = new ArrayList<>();
    static String localDir = System.getProperty("user.dir");
    private static final String filepath = localDir+"\\tomekh2_files\\"; // alla filer sparas i detta directory
    String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());



    /**
     * Skriver alla Customers, Accounts samt viktiga classvariabler till fil
     * @param file är filen vi vill spara till
     */
    public void saveBank(File file) {
        ArrayList<Object> list = new ArrayList<>();
        //Sparar klassvariabeln lastAssignedNumber
        list.add(Account.getLastAssignedNumber());
        // sparar kunder och konton till list
        list.add(customerList);
        list.add(accountList);
        //Skriver list till fil med namn fname
        writeObjToFile(list, file);
    }
    /**
     * Laddar bank från fil och skapar lista som innehåller LastAssignedNumber(index 0), customerlist(index 1), accountlist(index 2)
     * @param file är filen vi vill spara till
     */
    public void loadBank(File file) throws NullPointerException, IOException {
        //clear old customers and accounts just to be sure
        customerList = new ArrayList<>();
        accountList = new ArrayList<>();
        // loads list from file, pos
        ArrayList<Object> tmplist = loadObjFromFile(file);
        //transfers accounts and customers to appropriate list.
        customerList = (List<Customer>) tmplist.get(1);
        accountList = (List<Account>) tmplist.get(2);
        //load LastAssignedNumber
        Account.setLastAssignedNumber((Integer) tmplist.get(0));
    }

    /**
     * Skriver Transaktionslog till fil i klartext
     * @param pNo är personnumret till kontots ägare
     * @param accountId är kontot vars logg vi vill spara
     * @param fname är filnamnet vi vill spara till
     */
    public void writeLogFile(String pNo, int accountId, String fname) throws FileNotFoundException {
        ArrayList<String> logg = new ArrayList<>();
        for (Customer c : customerList) {
            // kollar om vi hittar kunden
            if (c.getpNo().equals(pNo)) {
                // om vi har hittat kunden loopar vi igenom listan med kundens konton
                for (Account a : c.getAccounts()) {
                    // kollar om kontot existerar
                    if (a.getAccNr() == accountId) {
                        logg = a.getTransactionLog();
                        try {
                            BufferedWriter writer = new BufferedWriter(new FileWriter(filepath+fname));
                            String headlineStr = "Account owner: "+c.getName()+" "+c.getSurName()+ ", Account Id: "+a.getAccNr()+", Current saldo: "+a.getSaldo()+", Todays date is: "+date;
                            writer.write(headlineStr);
                            for (String str : logg) {
                                writer.append(System.lineSeparator());
                                writer.write(str);
                            }
                            writer.close();
                        } catch (FileNotFoundException ex) {
                            throw new FileNotFoundException(ex.getMessage());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                    }
                }
            }
        }

    }
    /**
     * Skriver objekt till fil
     * @param serObjs är en lista med serializerbara objekt som vi skriver till fil
     * @param file är filen vi skriver till
     */
    public void writeObjToFile(ArrayList<Object> serObjs, File file) { // takes a list of objects to write to file as a single object
        OutputStream fos;
        try {
                fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos); // need to add multiple objects to the output stream
                oos.writeObject(serObjs);
                System.out.println("success");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        /**
         * Tar en fil som input, konverterar till filestream -> ObjectInputStream -> lista med objects
        * @param file är filen med object som vi ska läsa
        * @return result vilket är en lista med objekt som har lästs in
        */
        public ArrayList<Object> loadObjFromFile(File file) throws NullPointerException, IOException { // loads object from file, returns a list
            ArrayList<Object> result = new ArrayList<>();
            FileInputStream fis = null;
            try { fis = new FileInputStream(file); }
            catch (FileNotFoundException e) {
                System.out.println("1"+e.getMessage());
            }
            ObjectInputStream ois = null;
            try { ois = new ObjectInputStream(fis); } catch (IOException e) {
                throw new IOException(e.getMessage());
            }
            try{
                result = (ArrayList<Object>) ois.readObject();
                System.out.println("List of Objects "+result+" loaded");
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("3"+ex.getMessage()); // TODO utveckla felhantering här med
            }
            return result;
        }
        /**
         * Hämtar en lista med alla bankens kunder
         *
         * @return en Lista av strings med "personnummer, förnamn, efternamn" för varje kund
         */
        public ArrayList<String> getAllCustomers () {
        ArrayList<String> allCustomers = new ArrayList<>();
        // loopar igenom alla customers i customerlist och hämtar personnummer och namn för varje
        for (Customer c : customerList) {
            String customer = c.getpNo() + " " + c.getName() + " " + c.getSurName();
            allCustomers.add(customer);
        }
        return allCustomers;
        }

        /**
         * Skapar en ny kund
         *
         * @param name    innehåller kundens förnamn
         * @param surname innehåller kundens efternamn
         * @param pNo     innehåller kundens personnummer
         * @return returnerar false om det redan existerar en kund med det personnumret och true om vi skapade en ny kund
         */
        public Boolean createCustomer (String name, String surname, String pNo) {
            if(!checkIfString(name)||!checkIfString(surname)||!checkIfIdnr(pNo)) {return false;} // kollar om så att all input är i lämpligt format
            // loopar igenom customerlist och kollar om det redan existerar en kund med samma personnummer
            for (Customer c : customerList) {
                if (c.getpNo().equals(pNo)) {
                    return false;
                }
            }
            Customer customer = new Customer(pNo, name, surname);
            customerList.add(customer);
            return true;
        }


        /**
         * Hämtar information om en kund och dess konton.
         *
         * @param pNo innehåller kundens personnummer som vi vill hämta information om
         * @return returnerar null om ingen kund hittades, annars returneras en lista med en kund och information om alla konton kopplade till kunden.
         */
        public ArrayList<String> getCustomer (String pNo){
        ArrayList<String> customerInfo = new ArrayList<>();
        // loopar igenom listan med kunder
        for (Customer c : customerList) {
            // jämför om det är rätt personnummer, om det är rätt skapar vi index0 som innehåller personnummer och namn, vi lägger till i listan customerInfo
            if (c.getpNo().equals(pNo)) {
                String index0 = pNo + " " + c.getName() + " " + c.getSurName();
                customerInfo.add(index0);
                // hämtar lista med varje konto som tillhör kunden, loopar igenom listan och hämtar kontoinformationen som vi sen lägger till CustomerInfo
                for (Account a : c.getAccounts()) {
                    String accInfo = getAccount(c.getpNo(), a.getAccNr());
                    customerInfo.add(accInfo);
                }
                return customerInfo;
            }
        }
        return null;
        }

        /**
         * Jag har valt att överlagra metoden ovan för att kunna ha ett alternativ där jag returnerar samma information men
         * istället för varje kontos räntesats kommer räntan räknas ut i kronor.
         * Detta för att det krävdes i andra metoder, t.ex när ett konto avslutas.
         *
         * @param option parametern gör i sig inget men om stringen skickas med så kommer den här versionen av getCustomer användas istället för den andra.
         * @return returnerar false om ingen kund hittas, annars en lista med en kund och dess konton
         */
        public ArrayList<String> getCustomer (String pNo, String option){
        ArrayList<String> customerInfo = new ArrayList<>();
        // loopar igenom listan med kunder
        for (Customer c : customerList) {
            // jämför om det är rätt personnummer, om det är rätt skapar vi index0 som innehåller personnummer och namn, vi lägger till i listan customerInfo
            if (c.getpNo().equals(pNo)) {
                String index0 = pNo + " " + c.getName() + " " + c.getSurName();
                customerInfo.add(index0);
                // hämtar lista med varje konto som tillhör kunden, loopar igenom listan och hämtar kontoinformationen som vi sen lägger i en string och lägger till i CustomerInfo
                for (Account a : c.getAccounts()) {
                    // hämtar kontonummer
                    String accInfo = a.getAccNr() + " "
                            // formaterar informationen som vi får från kontot så att den skriver ut kronor efter saldot
                            + NumberFormat.getCurrencyInstance(new Locale("sv", "SE")).format(a.getSaldo()) + " "
                            // hämtar kontotyp
                            + a.getType() + " "
                            //hämtar räntesats, multiplicerar med saldo, dividerar med 100, formaterar till kr
                            + NumberFormat.getCurrencyInstance(new Locale("sv", "SE")).format(a.getSaldo().multiply(a.getInterest()).divide(new BigDecimal(100)));
                    customerInfo.add(accInfo);
                }
                return customerInfo;
            }
        }
        return null;
    }
    /**
     * Hjälpfunktion för att hämta ett specifikt Customer objekt
     * @param pNo identifierar kunden
     * @return c objektet vi returnerar
     */
    public Customer getCustomerObj(String pNo) {
            for (Customer c : customerList) {
                // jämför om det är rätt personnummer, om det är rätt skapar vi index0 som innehåller personnummer och namn, vi lägger till i listan customerInfo
                if (c.getpNo().equals(pNo)) {
                    return c;
                }
            }
            return null;
    }
    /**
     * Hjälpfunktion för att hämta ett specifikt Account objekt
     * @param c kunden vi skall hämta ett konto ifrån
     * @param accountId för kontot vi ska hämta
     * @return c
     */
    public Account getAccObj(Customer c, int accountId) {

            for (Account a : c.getAccounts()) {
                // kollar om kontot existerar
                if (a.getAccNr() == accountId) {
                return a;
                }
        }
        return null;
    }
        /**
         * Byter namn på en kund
         *
         * @param name    innehåller kundens nya förnamn, om tom kommer inte förnamnet ändras
         * @param surname innehåller kundens nya efternamn, om tom kommer inte efternamnet ändras
         * @param pNo     innehåller kundens personnummer, identifierar vilken kund vi ska byta namn på
         * @return returnerar false om inget händer, true om vi har ändrat namn.
         */
        public boolean changeCustomerName (String name, String surname, String pNo){
        if ((name.isBlank() && surname.isBlank())|| !checkIfString(name) || !checkIfString(surname)) {
            return false;
        }
        // loopar igenom kundlistan
        for (Customer c : customerList) {
            // kollar om personnumret stämmer
            if (c.getpNo().equals(pNo)) {
                // om personnumret stämmer kolla om något av fälten name/surname är null eller blank. Då ska namnet inte ändras, annars ändrar vi till ett nytt namn
                if (!name.isBlank() && !name.isEmpty()) {
                    c.setName(name);
                }
                if (surname != null && !surname.isBlank()) {
                    c.setSurName(surname);
                }
                return true;
            }
        }
        return false;
    }


        /**
         * Skapar ett sparkonto till en kund
         *
         * @param pNo innehåller kunden som vi skapa ett konto till's personnummer
         * @return returnerar -1 om inget konto skapades, returnerar kontonumret på kontot som vi skapade annars
         */
        public int createSavingsAccount (String pNo){
        // loopar igenom customerlistan
        for (Customer c : customerList)
            // kollar om kunden existerar, om den gör det skapar vi ett nytt konto och lägger till det till den allmäna kontolistan och kundens egna kontolista
            if (c.getpNo().equals(pNo)) {
                Account account = new SavingsAccount(pNo);
                accountList.add(account);
                c.addAccount(account);
                return account.getAccNr();
            }
        return -1;
    }


        /**
         * Skapar kreditkonto till en kund
         *
         * @param pNo innehåller kunden som vi skapa ett konto till's personnummer
         * @return returnerar -1 om inget konto skapades, returnerar kontonumret på kontot som vi skapade annars
         */
        public int createCreditAccount (String pNo){
        // loopar igenom customerlistan
        for (Customer c : customerList)
            // kollar om kunden existerar, om den gör det skapar vi ett nytt konto och lägger till det till den allmäna kontolistan och kundens egna kontolista
            if (c.getpNo().equals(pNo)) {
                Account account = new CreditAccount(pNo);
                accountList.add(account);
                c.addAccount(account);
                return account.getAccNr();
            }
        return -1;
    }


        /**
         * hämtar kontoinformation för ett specifikt konto tillhörandes en specifik kund
         *
         * @param pNo       innehåller kundens personnummer
         * @param accountId innehåller kontonummret som vi ska hämta information om
         * @return returnerar null om vi inte hittar något, returnerar string med saldo, kontotyp och räntesats om allt går bra
         */
        public String getAccount (String pNo,int accountId){
        // loopar igenom kundlistan
        for (Customer c : customerList) {
            // kollar om vi hittar kunden
            if (c.getpNo().equals(pNo)) {
                // om vi har hittat kunden loopar vi igenom listan med kundens konton
                for (Account a : c.getAccounts()) {
                    // kollar om kontot existerar
                    if (a.getAccNr() == accountId) {
                        // om kontot existerar hämtar vi kontonummer, saldo typ och räntesats, saldot formateras
                        String accInfo = a.getAccNr() + " "
                                + NumberFormat.getCurrencyInstance(new Locale("sv", "SE")).format(a.getSaldo()) + " "
                                + a.getType() + " "
                                + a.getInterest() + " %";
                        return accInfo;
                    }
                }
            }
        }
        return null;
    }


        /**
         * Sätter in en summa pengar på ett konto
         *
         * @param pNo       innehåller personnummer till kunden som äger kontot
         * @param accountId innehåller kontonummret till kontot
         * @param amount    innehåller summan som skall sättas in
         * @return returnerar false om det inte fungerade, returnerar true om allt gick bra.
         */
        public boolean deposit (String pNo,int accountId, int amount){
        // kollar om det är en positiv summa vi vill sätta in
        if (amount < 0) {
            return false;
        }
        // loopar igenom kundlistan
        for (Customer c : customerList) {
            // kollar om vi hittar rätt kund
            if (c.getpNo().equals(pNo)) {
                // loopar igenom kundens konton
                for (Account a : c.getAccounts()) {
                    // kollar om vi hittar rätt konto
                    if (a.getAccNr() == accountId) {
                        // om vi hittar rätt konto sätter vi in pengarna och avslutar loopen
                        a.setSaldo(a.getSaldo().add(new BigDecimal(amount)));
                        a.logTransaction(new BigDecimal(amount));
                        return true;
                    }
                }
            }
        }
        return false;
    }


        /**
         * Tar ut en summa pengar från ett konto
         *
         * @param pNo       innehåller personnummer till kunden som äger kontot
         * @param accountId innehåller kontonummret till kontot
         * @param amount    innehåller summan som skall tas ut
         * @return returnerar false om det inte fungerade, returnerar true om pengarna togs ut.
         */
        public boolean withdraw (String pNo,int accountId, int amount){
        if (amount < 0) {
            return false;
        }
        for (Customer c : customerList) {
            if (c.getpNo().equals(pNo)) {
                for (Account a : c.getAccounts()) {
                    if (a.getAccNr() == accountId) {
                        BigDecimal amnt = new BigDecimal(amount);
                        return a.withdraw(amnt);
                    }
                }
            }
        }
        return false;
    }


        /**
         * Avslutar en kunds konto.
         *
         * @param pNr       innehåller personnummer till kunden som äger kontot
         * @param accountId innehåller kontonummret till kontot
         * @return returnerar null om det inte fungerade, returnerar en string med information om kontot som vi stängde "kontonummer, saldo, ackumulerad ränta i kr"
         */
        public String closeAccount (String pNr,int accountId){
        List<Account> toRemove = new ArrayList<>();
        String returnstring = "";
        for (Customer c : customerList) {
            if (c.getpNo().equals(pNr)) {
                // när vi har hittat kunden så loopar vi igenom alla dess konton
                for (Account a : c.getAccounts()) {
                    if (a.getAccNr() == accountId) {
                        // får vi en match på kontonummer så sparar vi kontonmr, saldo , ackumulerad ränta och typ i en string
                        // vi tar sedan bort kontot från den "globala" listan med konton
                        // men eftersom vi inte kan ta bort ett konto från en lista som vi för tillfället loopar igenom så sparar vi objektet i en ny lista "toRemove"
                        returnstring = a.getAccNr() + " "
                                + NumberFormat.getCurrencyInstance(new Locale("sv", "SE")).format(a.getSaldo()) + " "
                                + a.getType() + " "
                                + NumberFormat.getCurrencyInstance(new Locale("sv", "SE")).format(a.getSaldo().abs().multiply(a.getInterest()).divide(new BigDecimal(100)));
                        accountList.remove(a);
                        toRemove.add(a);
                    }
                }
                // Nu när vi inte sitter fast i en loop kan vi ta bort kontot som vi sparat i toRemove från vår kunds egna lista med konton
                c.rmAccount(toRemove);
            }
        }
        if (toRemove.isEmpty()) {
            return null;
        } else {
            return returnstring;
        }
    }


        /**
         * Tar bort en kund och stänger alla dess konton
         *
         * @param pNo innehåller kundens personnumer
         * @return returnerar null om det inte fungerade, returnerar lista med strings innehållande kundinformation och kontoinformation för varje konto
         */
        public ArrayList<String> deleteCustomer (String pNo){
        List<Customer> CusToRemove = new ArrayList<>();
        List<Account> AccToRemove = new ArrayList<>();
        ArrayList<String> returnString = new ArrayList<>();
        // loopar igenom kundlistan
        for (Customer c : customerList) {
            // kollar om vi hittar rätt kund
            if (c.getpNo().equals(pNo)) {
                // om vi hittar kunden lägger vi till kundobjektet i en lista med objekt som vi ska ta bort senare
                CusToRemove.add(c);
                returnString = getCustomer(c.getpNo(), "o");
                // loopa igenom kontona
                for (Account a : c.getAccounts()) {
                    // lägg till kontoobjektet i en lista med objekt som vi tar bort senare
                    AccToRemove.add(a);
                    // ta bort konto från den globala listan
                    accountList.remove(a);
                }
                // tar bort alla konton från kundens lokala lista
            }
            c.rmAccount(AccToRemove);// removes link between customer and accounts
        }
        if (CusToRemove.isEmpty()) {
            return null;
        } else {
            // tar bort kunden från customerlist
            customerList.removeAll(CusToRemove);
            return returnString;
        }
    }

        /**
         * Hämtar transaktionsloggen
         *
         * @param pNo identifierar vilken kund vi vill hämta transaktionshistoriken ifrån
         * @param accountId är kontoid som vi vill hämta historiken ifrån
         * @return returnerar null om det inte fungerade, returnerar lista med strings innehållande transaktionshistoriken annars
         */
        public ArrayList<String> getTransactions (String pNo,int accountId){
        for (Customer c : customerList) {
            if (c.getpNo().equals(pNo)) {
                // när vi har hittat kunden så loopar vi igenom alla dess konton
                for (Account a : c.getAccounts()) {
                    if (a.getAccNr() == accountId) {
                        // hämtar transaktionslog
                        return a.getTransactionLog();
                    }
                }
            }
        }
        return null;
    }
    /** Hjälpfunktion
     * Kollar om en string är godkänd för input till lämplig metod, dvs inga specialtecken, inte tom, inga siffror
     * @param input strängen vi skall kontrollera
     * @return returnerar true om det är en godkänd string, false annars
     */
    public boolean checkIfString(String input) {
            if(input.isBlank() || input.matches("^.*[^a-zA-Z].*$")){
                return false;
            } else {
            return true; }
    }
    /** Hjälpfunktion
     * Kollar om en string har rätt format för att vara ett idnr, dvs bara siffror och max 12 karaktärer
     * @param input strängen vi skall kontrollera
     * @return returnerar true om det är en godkänd string, false annars
     */
    public boolean checkIfIdnr(String input) {
        if (input.isBlank() || input.matches("^.*[^0-9].*$") || input.length() > 12 ) {
            return false;
        } else {
            return true;
        }
    }
    /** Hjälpfunktion
     * Kollar om en string har rätt format för att vara ett nummer
     * @param input strängen vi skall kontrollera
     * @return returnerar true om det är en godkänd string, false annars
     */
    public boolean checkIfNr(String input) {
        if (input.isBlank() || input.matches("^.*[^0-9].*$") ){
            return false;
        } else {
            return true;
        }
    }

}