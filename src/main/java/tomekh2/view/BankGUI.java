package tomekh2.view;


import tomekh2.model.Customer;
import tomekh2.model.Account;
import tomekh2.model.BankLogic;

import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;


/** @author Tomas Ekholm, tomekh-2
 * Den här klassen bygger upp vår GUI och hanterar all input från användaren samt kommunicerar denna till BankLogic.
 * */
public class BankGUI extends Application {
    private static BorderPane layout;
    private Pane cNamePane;
    private Pane delCustPane;
    private Pane creCustPane;
    private Pane getCustIPane;
    private Pane getAccIPane;
    private Pane getTransHistPane;
    private Pane createAccPane;
    private Pane closeAccPane;
    private static Pane emptyGridPane;
    private final BankLogic BANK = new BankLogic();
    private ObservableList<String> clist = FXCollections.observableArrayList(BANK.getAllCustomers());
    private ObservableList<String> alist;



    /**
     * Hämtar alla kunder från BANK och konverterar från ArrayList till ObservableList
     * @return en ObservableList
     */
    public ObservableList<String> updateClist(){

        return FXCollections.observableArrayList(BANK.getAllCustomers());
    }
    /**
     * Hämtar alla konton för en specifik kund från BANK och konverterar från ArrayList till ObservableList
     * @param idnr identifierar vilken kunde det gäller
     * @return en ObservableList
     */
    public ObservableList<String> updateAlist(String idnr){
        ObservableList<String> alist = FXCollections.observableArrayList(BANK.getCustomer(idnr)); // updaterar bara listan på konton, därav getCustomer och inte get Account
        alist.remove(0);
        return alist;
    }

    @Override
    public void start(Stage stage) throws IOException {

        // Skapar några testkunder för att kunna testa funktionaliteten medan vi kodar
        /*
        BANK.createCustomer("tomas", "ekholm","871105");
        BANK.createCustomer("tom", "jones","111111");
        BANK.createCustomer("mona", "moon","222222");
        BANK.createCreditAccount("871105");
        BANK.createSavingsAccount("222222");
        BANK.createCreditAccount("222222");
        BANK.deposit("871105",1001,500);
        BANK.withdraw("871105",1001,5);
        BANK.deposit("871105",1001,1);
        BANK.deposit("222222",1002,111);
        clist = updateClist();
        */




        // Skapar de olika menyerna
        Menu mainMenu = new Menu("_Main");
        Menu cAdminMenu = new Menu("_Customer administration");
        Menu aAdminMenu = new Menu("_Account administration");
        Menu getInfoMenu = new Menu("_Information");
        Menu transactionMenu = new Menu("_Transactions");
        //Skapar menyvalen för Main-menyn
        MenuItem load = new MenuItem("Load data from file");
        MenuItem save = new MenuItem("Save data to file");
        // skapar en filechooser som behövs för de två menyvalen ovan
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("tomekh2_files"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        // Kopplar ihop en händelse med vad som händer om man gör dessa menyval
        load.setOnAction(e-> {
            File selectedFile = fileChooser.showOpenDialog(stage);
            try {BANK.loadBank(selectedFile);
                clist = updateClist();
                createAllpanes();
            } catch ( NullPointerException ex ) {
                switchPanesBottom(errorPaneCreator("File not chosen"));
                System.out.println(ex.getMessage());
            } catch (IOException ex) {
                switchPanesBottom(errorPaneCreator("File could not be loaded: "+ex.getMessage()));
                System.out.println(ex.getMessage());
            }
        }); // kopplar setOnACtion till filechooser till att spara en fil
        save.setOnAction(e-> {
            File savefile = fileChooser.showSaveDialog(stage);
            try {BANK.saveBank(savefile); } catch ( NullPointerException ex ) {
                switchPanesBottom(errorPaneCreator("File not chosen"));
            }
        });

        //kopplar ihop våra menyval med menyn
        mainMenu.getItems().add(load);
        mainMenu.getItems().add(save);

        //Menyval för customer-adm menyn
        MenuItem createCust = new MenuItem("Create customer");
        MenuItem deleteCust = new MenuItem("Delete customer");
        MenuItem changeNameCust = new MenuItem("Change customer name");

        createCust.setOnAction(e-> {clearPanes();
        switchPanesCenter(creCustPane);});
        deleteCust.setOnAction(e-> {clearPanes();
            switchPanesCenter(delCustPane);});
        changeNameCust.setOnAction(e-> {clearPanes();
            switchPanesCenter(cNamePane);});

        cAdminMenu.getItems().add(createCust);
        cAdminMenu.getItems().add(deleteCust);
        cAdminMenu.getItems().add(changeNameCust);

        //Menyval under Account menyn
        MenuItem createAcc = new MenuItem("Create account");
        MenuItem closeAcc = new MenuItem("Close account");
        // creates event for switching panes
        createAcc.setOnAction(e-> {clearPanes();
            switchPanesCenter(createAccPane);});
        closeAcc.setOnAction(e-> {clearPanes();
            switchPanesCenter(closeAccPane);});

        aAdminMenu.getItems().add(createAcc);
        aAdminMenu.getItems().add(closeAcc);

        //Menyval under informationsmenyn
        MenuItem getCustI = new MenuItem("Get customer info");
        MenuItem getAccI = new MenuItem("Get account info");
        getCustI.setOnAction(e-> {clearPanes();
            switchPanesCenter(getCustIPane);});
        getAccI.setOnAction(e-> {clearPanes();
            switchPanesCenter(getAccIPane);});
        getInfoMenu.getItems().add(getCustI);
        getInfoMenu.getItems().add(getAccI);

        //Menyval under transactionsmenyn
        MenuItem transfer = new MenuItem("Transfer money");
        transfer.setOnAction(e-> {clearPanes();
            switchPanesCenter(transactionPane());});
        transactionMenu.getItems().add(transfer);

        // Skapa menybar och lägg till menyerna till den
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(mainMenu);
        menuBar.getMenus().addAll(cAdminMenu);
        menuBar.getMenus().addAll(aAdminMenu);
        menuBar.getMenus().addAll(getInfoMenu);
        menuBar.getMenus().addAll(transactionMenu);


        // Skapar alla panes som används
        createAllpanes();
        // Sätter upp en stage en scene och en pane som fungerar som vår layout som alla andra komponenter kommer visas i.
        stage.setTitle("Main window");
        layout = new BorderPane();
        layout.setTop(menuBar);
        HBox innerlayout = new HBox();
        layout.setCenter(innerlayout);
        Scene startscene = new Scene(layout, 800, 400, Color.LAVENDER);
        stage.setScene(startscene);
        stage.show();
    }

    // Helper metoder, självförklarande
    public void switchPanesCenter(Pane pane) { layout.setCenter(pane); }
    public void switchPanesLeft(Pane pane) { layout.setLeft(pane); }
    public void switchPanesRight(Pane pane) { layout.setRight(pane); }
    public void switchPanesBottom(Pane pane) { layout.setBottom(pane); }
    public static void clearPanes() {
        layout.setCenter(emptyGridPane);
        layout.setLeft(emptyGridPane);
        layout.setRight(emptyGridPane);
        layout.setBottom(emptyGridPane);
    }
    /**
     * Skapar alla panes som vi kommer att använda i vår GUI genom att anropa metoder som skapar varje pane
     * kan också användas för att uppdatera panes om det behövs.
     */
    private void createAllpanes() {
        cNamePane = cNamePane();
        delCustPane = deleteCustPane();
        creCustPane = createCustPane();
        getCustIPane = getCustIPane();
        getAccIPane = getAccIPane();
        createAccPane = createAccPane();
        closeAccPane = closeAccPane();
    }

    /**
     * Det här är en helpermetod som används när vi skapar panes, just denna koden är samma för alla panes
     * @param headline är den rubriktext som vi vill att vår pane ska ha
     * @return en GridPane
     */
    public GridPane gridPaneCreator(String headline) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);
        // Define headline
        final Label Headline = new Label(headline);
        Font font = Font.font("Serif", FontWeight.BOLD, FontPosture.REGULAR, 25);
        Headline.setFont(font);
        Headline.setTextFill(Color.DARKSLATEBLUE);
        GridPane.setConstraints(Headline, 0, 0);
        grid.getChildren().add(Headline);
        return grid;
    }
    /**
     * Det här är en metod som skapar en pane med en label som innehåller ett felmeddelande
     * @param errormsg är den rubriktext som vi vill att vår pane ska ha
     * @return en GridPane
     */
    public GridPane errorPaneCreator(String errormsg) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        // Define headline
        final Label Headline = new Label(errormsg);
        Font font = Font.font("Serif", FontWeight.BOLD, FontPosture.REGULAR, 16);
        Headline.setFont(font);
        Headline.setTextFill(Color.BROWN);
        GridPane.setConstraints(Headline, 0, 0);
        grid.getChildren().add(Headline);
        return grid;
    }
    /**
     * Den här metoden skapar den pane som visas när vi vill byta namn på en kund
     * @return Pane är den pane som skall visas
     */
    private Pane cNamePane() throws NullPointerException {
        //Creating a GridPane container
        GridPane grid = gridPaneCreator("Change name of customer");
        //Defining the Name text field
        final TextField name = new TextField();
        name.setPromptText("Enter new first name.");
        name.getText();
        GridPane.setConstraints(name, 0, 2);
        grid.getChildren().add(name);

        //Defining the Last Name text field
        final TextField surname = new TextField();
        surname.setPromptText("Enter new last name.");
        GridPane.setConstraints(surname, 1, 2);
        grid.getChildren().add(surname);

        // Defines listview with all existing customers
        ListView<String> lview = new ListView<>();
        lview.setItems(clist);
        GridPane.setConstraints(lview, 0, 1);
        grid.getChildren().add(lview);

        //Defining the Submit button
        Button submit = new Button("Change name");
        GridPane.setConstraints(submit, 2, 2);
        grid.getChildren().add(submit);
        submit.setOnAction(e->{ try {// will break if no customer is selected (NPE)
            Object result = BANK.changeCustomerName(name.getText(), surname.getText(), lview.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""));
            clist = updateClist();
            lview.setItems(clist);
            createAllpanes();
            switchPanesBottom(outputPane(result));
            switchPanesCenter(cNamePane);
            } catch(NullPointerException npe) {
            switchPanesBottom(errorPaneCreator("You did not select a customer"));
            }
        });
        return grid;
    }
    /**
     * Den här metoden skapar den pane som visas när vi vill skapa en ny kund
     * @return Pane är pane som skall visas
     */
    private Pane createCustPane() {
        //Creating a GridPane container
        GridPane grid = gridPaneCreator("Create Customer");
        //Defining the Name text field
        final TextField name = new TextField();
        name.setPromptText("First name.");
        name.setPrefColumnCount(10);
        name.getText();
        GridPane.setConstraints(name, 0, 2);
        grid.getChildren().add(name);
        //Defining the Last Name text field
        final TextField surname = new TextField();
        surname.setPromptText("Last name.");
        GridPane.setConstraints(surname, 0, 3);
        grid.getChildren().add(surname);
        //Defining the IDnr text field
        final TextField idnr = new TextField();
        idnr.setPrefColumnCount(15);
        idnr.setPromptText("Personal number.");
        GridPane.setConstraints(idnr, 0, 1);
        grid.getChildren().add(idnr);
        //Defining the Submit button
        Button create = new Button("Create customer");
        GridPane.setConstraints(create, 1, 1);
        grid.getChildren().add(create);
        create.setOnAction(e->{
            Object result = BANK.createCustomer(name.getText(), surname.getText(), idnr.getText());
            clist = updateClist();
            createAllpanes();
            switchPanesBottom(outputPane(result));
            switchPanesCenter(creCustPane);
        });
        return grid;
    }
    /**
     * Den här metoden skapar den pane som visas när vi vill ta bort en kund
     * @return Pane är pane som skall visas
     */
    private Pane deleteCustPane() {
        //Creating a GridPane container
        GridPane grid = gridPaneCreator("Delete Customer");
        // creates listview that displays all customers
        ListView<String> lview = new ListView<>();
        lview.setItems(clist);
        GridPane.setConstraints(lview, 0, 1);
        grid.getChildren().add(lview);

        //Defining the delete button
        Button delete = new Button("Delete customer");
        GridPane.setConstraints(delete, 1, 1);
        grid.getChildren().add(delete);
        delete.setOnAction(e->{ try { // gets selected item from clist, converts to string, extracts only digits for input to deletecustomer
            Object result = BANK.deleteCustomer(lview.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""));
            clist = updateClist();
            lview.setItems(clist);
            createAllpanes();
            switchPanesBottom(outputPane(result));
            switchPanesCenter(delCustPane);
            } catch(NullPointerException npe) {
            switchPanesBottom(errorPaneCreator("You did not select a customer")); //
            }
        });
        return grid;
    }
    /**
     * Den här metoden skapar den pane som visas när vi vill hämta customer information
     * @return Pane är pane som skall visas
     */
    private Pane getCustIPane() {
        //Creating a GridPane container
        GridPane grid = gridPaneCreator("Get Customer information");
        // creates listview that displays all customers
        ListView<String> lview = new ListView<>();
        lview.setItems(clist);
        GridPane.setConstraints(lview, 0, 1);
        grid.getChildren().add(lview);
        //Defining the delete button
        Button getInfo = new Button("Get information");
        GridPane.setConstraints(getInfo, 1, 1);
        grid.getChildren().add(getInfo);
        getInfo.setOnAction(e->{ try {
            Object result = BANK.getCustomer(lview.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""));
            switchPanesCenter(getCustIPane);
            switchPanesBottom(outputPane(result));
            } catch(NullPointerException npe) {
            switchPanesBottom(errorPaneCreator("You did not select a customer")); //
            }
            });
        return grid;
    }
    /**
     * Den här metoden skapar den pane som visas när vi vill hämta kontoinformation
     * @return Pane är pane som skall visas
     */
    private Pane getAccIPane() {
        //Creating a GridPane container
        GridPane grid = gridPaneCreator("Account information");
        // creates listview that displays all customers
        ListView<String> lview = new ListView<>();
        lview.setItems(clist);
        GridPane.setConstraints(lview, 0, 1);
        grid.getChildren().add(lview);

        // creates listview that displays a customers accounts on selection
        ListView<String> lview2 = new ListView<>();
        lview2.setItems(alist);
        // håller koll på vilken kund som är selected och displayar appropriate accounts
        MultipleSelectionModel<String> lvSelModel = lview.getSelectionModel();
        lvSelModel.selectedItemProperty().addListener((changed, oldVal, newVal) -> {
            alist = updateAlist(lview.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""));
            lview2.setItems(alist);
        });
        GridPane.setConstraints(lview2, 1, 1);
        grid.getChildren().add(lview2);
        // Håller koll på selection av konto och displayar transaktionshistorik i en pane till höger
        MultipleSelectionModel<String> lvSelModel2 = lview2.getSelectionModel(); // kollar vilket konto som är selected
        lvSelModel2.selectedItemProperty().addListener((changed, oldVal, newVal) -> { try {
            Customer c = BANK.getCustomerObj(lview.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", "")); // buggar pga not selected i början
            Account a = BANK.getAccObj(c, Integer.parseInt(lview2.getSelectionModel().getSelectedItem().split(" ", 2)[0]));
            switchPanesRight(createTextBox(a));
             } catch (Exception e) {
        }
        });

        final TextField filename = new TextField();
        filename.setPromptText("save file as");
        filename.setPrefColumnCount(10);
        filename.getText();
        GridPane.setConstraints(filename, 0, 2);
        grid.getChildren().add(filename);
        //Defining the button // öppna popup och låt användaren bestämma filnamn
        Button save = new Button("Save history to file");
        GridPane.setConstraints(save, 1, 2); // 1 2
        grid.getChildren().add(save);
        save.setOnAction(e->{
            try {
                if((filename.getText().isBlank())|| !Pattern.matches("[a-zA-Z0-9. ]*",filename.getText())) { // dont want special chars in filename
                    switchPanesBottom(outputPane("Invalid name"));
                } else {
                String accId = lview2.getSelectionModel().getSelectedItem().split(" ", 2)[0];
                String cid = lview.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", "");
                BANK.writeLogFile(cid, Integer.parseInt(accId), filename.getText());
                switchPanesBottom(outputPane("file saved"));
                }
            } catch(NullPointerException npe) {
                switchPanesBottom(errorPaneCreator("Select customer AND an account"));
        } catch (FileNotFoundException fnf) {
                switchPanesBottom(errorPaneCreator("FileNotFoundException: "+fnf.getMessage()));
            }

        });
        return grid;
    }

    // skapar ny textflow pane för transaktionshistoriken
    private  VBox createTextBox(Account a) {
        Label headline = new Label("Transaction history");
        Text history = new Text();
        // for each item in thist add string to text object and a newline
        for (String str : a.getTransactionLog()) {
            history.setText(history.getText() + str+"\n");
        }
        VBox textbox = new VBox();
        textbox.getChildren().add(headline);
        textbox.getChildren().addAll(history);
        textbox.setPadding(new Insets(20, 20, 30, 5));
        Font font = Font.font("Serif", FontWeight.BOLD, FontPosture.REGULAR, 18);
        headline.setFont(font);
        headline.setTextFill(Color.BROWN);
        return textbox;
    }

    /**
     * Den här metoden skapar den pane som visas när vi vill hämta transaktionsinformation och sedan kunna spara den
     * placeholder inför framtiden
     * @return Null returnerar null för tillfället
     */
    private Pane createAccPane() {
        //Creating a GridPane container
        GridPane grid = gridPaneCreator("Create account");
        // create radio buttons for choosing between credit or savings account
        ToggleGroup tg = new ToggleGroup();
        RadioButton credit = new RadioButton("Credit account");
        RadioButton savings = new RadioButton("Savings account");
        credit.setToggleGroup(tg);
        savings.setToggleGroup(tg);
        GridPane.setConstraints(credit, 0, 2);
        grid.getChildren().add(credit);
        GridPane.setConstraints(savings, 1, 2);
        grid.getChildren().add(savings);

        // creates listview that displays all customers
        ListView<String> lview = new ListView<>();
        lview.setItems(clist);
        GridPane.setConstraints(lview, 0, 1);
        grid.getChildren().add(lview);

        // creates listview that displays a customers accounts on selection
        ListView<String> lview2 = new ListView<>();
        lview2.setItems(alist);
        // håller koll på vilken kund som är selected och displayar appropriate accounts
        MultipleSelectionModel<String> lvSelModel = lview.getSelectionModel();
        lvSelModel.selectedItemProperty().addListener((changed, oldVal, newVal) -> {
            alist = updateAlist(lview.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""));
            lview2.setItems(alist);
        });
        GridPane.setConstraints(lview2, 1, 1);
        grid.getChildren().add(lview2);

        //Defining the button
        Button create = new Button("Create account");
        GridPane.setConstraints(create, 2, 2);
        grid.getChildren().add(create);

        create.setOnAction(e->{ // fires on click of button
            try {
                if (tg.getSelectedToggle() == credit) { // checks which radiobutton is checked
                    Object result = BANK.createCreditAccount(lview.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""));
                    switchPanesBottom(outputPane(result));
                }
                else if (tg.getSelectedToggle() == savings) {
                    Object result = BANK.createSavingsAccount(lview.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""));
                    switchPanesBottom(outputPane(result));
                } else {
                    switchPanesBottom(errorPaneCreator("account type not selected")); // hur gör jag när jag vill ha två felmeddelandenn?
                }
                alist = updateAlist(lview.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""));
                lview2.setItems(alist);
                createAllpanes();
                switchPanesCenter(createAccPane);
            } catch(NullPointerException npe) {
                switchPanesBottom(errorPaneCreator("You did not select a customer"));
            }
        });
        return grid;
    }
    /**
     * Den här metoden skapar den pane som visas när vi vill avsluta ett konto
     * @return Pane är pane som skall visas
     */
    private Pane closeAccPane() {
        //Creating a GridPane container
        GridPane grid = gridPaneCreator("Close account");
        // creates listview that displays all customers
        ListView<String> lview = new ListView<>();
        lview.setItems(clist);
        GridPane.setConstraints(lview, 0, 1);
        grid.getChildren().add(lview);

        // creates listview that displays a customers accounts on selection
        ListView<String> lview2 = new ListView<>();
        lview2.setItems(alist);
        // håller koll på vilken kund som är selected och displayar appropriate accounts
        MultipleSelectionModel<String> lvSelModel = lview.getSelectionModel();
        lvSelModel.selectedItemProperty().addListener((changed, oldVal, newVal) -> {
            alist = updateAlist(lview.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""));
            lview2.setItems(alist);
        });

        GridPane.setConstraints(lview2, 1, 1);
        grid.getChildren().add(lview2);

        //Defining the button
        Button close = new Button("Close account");
        GridPane.setConstraints(close, 2, 2);
        grid.getChildren().add(close);

        close.setOnAction(e->{ try {
            String accId = lview2.getSelectionModel().getSelectedItem().split(" ", 2)[0]; // saves the part containing the accountid
            Object result = BANK.closeAccount(lview.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""), Integer.parseInt(accId));
            switchPanesBottom(outputPane(result));
            alist = updateAlist(lview.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""));
            lview2.setItems(alist);
            createAllpanes();
            switchPanesCenter(closeAccPane);
            } catch(NullPointerException npe) {
            switchPanesBottom(errorPaneCreator("Account to be closed not selected"));
            }
            });
        return grid;
    }
    /**
     * Den här metoden skapar den pane som visas när vi vill ta ut eller sätta in pengar.
     * @return Pane är pane som skall visas
     */
    private Pane transactionPane() { //
        //Creating a GridPane container
        GridPane grid = gridPaneCreator("Transfer money");
        // create radio buttons for choosing between credit or savings account
        ToggleGroup tg = new ToggleGroup();
        RadioButton deposit = new RadioButton("Deposit");
        RadioButton withdraw = new RadioButton("Withdraw");
        deposit.setToggleGroup(tg);
        withdraw.setToggleGroup(tg);
        GridPane.setConstraints(deposit, 0, 2);
        grid.getChildren().add(deposit);
        GridPane.setConstraints(withdraw, 1, 2);
        grid.getChildren().add(withdraw);


        // creates listview that displays all customers
        ListView<String> lview = new ListView<>();
        lview.setItems(clist);
        GridPane.setConstraints(lview, 0, 1);
        grid.getChildren().add(lview);

        // creates listview that displays a customers accounts on selection
        ListView<String> lview2 = new ListView<>();
        lview2.setItems(alist);

        // håller koll på vilken kund som är selected och displayar appropriate accounts
        MultipleSelectionModel<String> lvSelModel = lview.getSelectionModel();
        lvSelModel.selectedItemProperty().addListener((changed, oldVal, newVal) -> {
            alist = updateAlist(newVal.replaceAll("[^0-9]", ""));
            lview2.setItems(alist);
        });
        GridPane.setConstraints(lview2, 1, 1);
        grid.getChildren().add(lview2);

        //Defining the Amount text field
        final TextField amount = new TextField();
        amount.setPrefColumnCount(15);
        amount.setPromptText("Amount");
        GridPane.setConstraints(amount, 0, 3);
        grid.getChildren().add(amount);

        //Defining the transaction button
        Button exec = new Button("Execute transaction");
        GridPane.setConstraints(exec, 1, 3);
        grid.getChildren().add(exec);

        exec.setOnAction(e->{ try {
            String selectedCustomer = lview.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", "");
            if (tg.getSelectedToggle() == deposit) {
                String accId = lview2.getSelectionModel().getSelectedItem().split(" ", 2)[0]; // saves the part containing the accountid //
                Object result = BANK.deposit(selectedCustomer, Integer.parseInt(accId), Integer.parseInt(amount.getText()));
                switchPanesBottom(outputPane(result));
            } else if (tg.getSelectedToggle() == withdraw) {
                String accId = lview2.getSelectionModel().getSelectedItem().split(" ", 2)[0]; // saves the part containing the accountid //
                Object result = BANK.withdraw(lview.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""), Integer.parseInt(accId), Integer.parseInt(amount.getText()));
                switchPanesBottom(outputPane(result));
            } else {
                switchPanesBottom(errorPaneCreator("Transaction type not selected"));
            }
            alist = updateAlist(lview.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""));
            lview2.setItems(alist);
            createAllpanes();
            switchPanesCenter(transactionPane());
            } catch(NullPointerException npe) {
            switchPanesBottom(errorPaneCreator("Account not selected"));
            } catch(NumberFormatException nfe) {
            switchPanesBottom(errorPaneCreator("Input field data is invalid"));
        }
        });
        return grid;
    }
    /**
     * Den här metoden skapar den pane som ska visas när vi får output från BankLogic. Beroende på vad denna metoden får för input så formaterar den Panelen lite olika.
     * @param o är det objekt som BankLogic ger som output, det kan vara string, boolean, null, int, osv.
     * @return Pane är pane som skall visas
     */
    public static Pane outputPane(Object o) { // TODO would be nice to have individual output text for success/fail depending on which event fired, but we dont

        if(o instanceof Integer) {
            VBox grid = new VBox();
            Label text = new Label();
            text.setText("An int was returned");
            Font font = Font.font("Serif", FontWeight.BOLD, FontPosture.REGULAR, 25);
            text.setFont(font);
            grid.getChildren().add(text);
            return grid;
        }
        if(o instanceof Boolean) {
            VBox grid = new VBox();
            Label text = new Label();
            if((Boolean) o) {
                text.setText("Success!");
                text.setTextFill(Color.GREEN);
            }
            else {
                text.setText("Failure!");
                text.setTextFill(Color.RED);
            }
            Font font = Font.font("Serif", FontWeight.BOLD, FontPosture.REGULAR, 25);
            text.setFont(font);
            grid.getChildren().add(text);
            return grid;
        } if(o instanceof String) {
            VBox grid = new VBox();
            Label text = new Label();
            text.setText((String) o);
            text.setTextFill(Color.GREEN);
            grid.getChildren().add(text);
            return grid;
        } if(o instanceof ArrayList) {
            ObservableList<String> oList = FXCollections.observableArrayList((ArrayList<String>) o);
            ListView<String> listView = new ListView<>(oList);
            VBox grid = new VBox();
            Label text = new Label();
            text.setText("Success!");
            text.setTextFill(Color.GREEN);
            Font font = Font.font("Serif", FontWeight.BOLD, FontPosture.REGULAR, 25);
            text.setFont(font);
            grid.getChildren().add(text);
            grid.getChildren().add(listView);
            return grid;

        } else { // this is the case if we get null back, which we do often
            VBox grid = new VBox();
            Label text = new Label();
            text.setText("Failure!");
            text.setTextFill(Color.RED);
            Font font = Font.font("Serif", FontWeight.BOLD, FontPosture.REGULAR, 25);
            text.setFont(font);
            grid.getChildren().add(text);
            return grid;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}