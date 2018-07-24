import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;
import javafx.scene.image.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.Group;
import java.text.DecimalFormat;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.control.TextArea;

public class VaalUI extends Application implements EventHandler<ActionEvent> {
    private ComboBox<String> basetypes, implicits;
    private Button calculate, reset;
    private Label initInvLabel, corrValLabel, resaleLabel;
    private TextField initInv, corrVal, resaleVal;
    private Double initInvDoub, corrValDoub, resaleValDoub;
    private Driver driver;
    private Image ui;
    private ImagePattern bg;
    private ObservableList<String> list;
    private Tooltip initTool, corrTool;

    @Override
    public void start(Stage primaryStage) throws Exception {
        driver = new Driver();
        // ideally these layout settings would not be hard coded like this. In the future I'd like to change to using
        // a GridPane so that we can create columns and align the elements accordingly.

        // calculate button
        calculate = new Button("Calculate");
        calculate.setOnAction(this);
        calculate.setLayoutX(164);
        calculate.setLayoutY(500);
        calculate.setId("calculate-button");

        // reset button
        reset = new Button("Reset Form");
        reset.setOnAction(this);
        reset.setLayoutX(167);
        reset.setLayoutY(565);
        reset.setId("reset-button");

        // creating tooltips to help the user understand what information is being asked for
        initTool = new Tooltip("The value of your item with your selected implicits (in chaos).");
        corrTool = new Tooltip("The cost of purchasing an uncorrupted version of the item you are attempting to vaal (in chaos).");
        initTool.setWrapText(true);
        initTool.setPrefWidth(200);
        corrTool.setWrapText(true);
        corrTool.setPrefWidth(200);

        // initInv Label
        initInvLabel = new Label("Initial Cost of Item (chaos)");
        initInvLabel.setLayoutY(365);
        initInvLabel.setLayoutX(65);

        //corrVal label
        corrValLabel = new Label("Value w/ Desired Corruption");
        corrValLabel.setLayoutY(399);
        corrValLabel.setLayoutX(48);

        // initInv textfield
        initInv = new TextField();
        initInv.setTooltip(initTool);
        initInv.setPrefWidth(55);
        initInv.setLayoutY(365);
        initInv.setLayoutX(270);

        //corrVal textfield
        corrVal = new TextField();
        corrVal.setPrefWidth(55);
        corrVal.setLayoutY(399);
        corrVal.setLayoutX(270);
        corrVal.setTooltip(corrTool);

        //resaleVal textfield
        resaleVal = new TextField();
        resaleVal.setPrefWidth(55);
        resaleVal.setLayoutY(433);
        resaleVal.setLayoutX(270);
        resaleVal.setTooltip(corrTool);

        //resale label
        resaleLabel = new Label("Corrupted resale value");
        resaleLabel.setLayoutY(433);
        resaleLabel.setLayoutX(89);

        // implicits combobox
        implicits = new ComboBox<>();
        implicits.setPrefWidth(230);
        implicits.setLayoutY(270);
        implicits.setLayoutX(85);
        implicits.getSelectionModel().select("-Insufficient Item Information-");
        implicits.setOnAction(this);

        ui = new Image("images/ui.png");
        bg = new ImagePattern(ui);

        // basetypes comboBox
        basetypes = new ComboBox<>(); // this is how we make the dropdown menu
        basetypes.setPrefWidth(140);
        basetypes.setLayoutX(130);
        basetypes.setLayoutY(208);
        basetypes.getSelectionModel().select("-Item Basetype-");
        basetypes.setOnAction(this);
        basetypes.getItems().addAll( // user can either search for or select one of the options below
                "Amulet",
                "Body Armour",
                "Belt",
                "Boots",
                "Bow",
                "Claw",
                "Dagger",
                "Fishing Rod",
                "Gloves",
                "Helmet",
                "One Hand Axe",
                "One Hand Mace",
                "One Hand Sword",
                "Quiver",
                "Ring",
                "Sceptre",
                "Shield",
                "Staff",
                "Two Hand Axe",
                "Two Hand Mace",
                "Two Hand Sword",
                "Wand"
        );
        // Group setup
        Group group = new Group();
        group.setId("pane");
        group.setLayoutY(0);
        group.getChildren().addAll(basetypes, implicits);
        group.getChildren().addAll(initInvLabel, initInv, corrVal, corrValLabel, resaleLabel, resaleVal);
        group.getChildren().addAll(calculate, reset);

        // Scene set-up and assignment
        Scene scene = new Scene(group, 400, 600);
        scene.setFill(bg);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(VaalUI.class.getResourceAsStream("images/chaosvaal.png")));
        primaryStage.setTitle("VaalCalc v1.1");
        primaryStage.setResizable(false); // cannot be resized
        scene.getStylesheets().addAll(this.getClass().getResource("/Stylesheet.css").toExternalForm());
        primaryStage.show();
    }

    @Override
    public void handle(ActionEvent event) {
        // shows when the user has failed to select an implicits stat for calculation
        Alert implicitAlert = new Alert(Alert.AlertType.ERROR);
        implicitAlert.setTitle("Missing Implicit Stat");
        implicitAlert.setHeaderText(null);
        implicitAlert.setContentText("Please select an implicits stat");

        // when the user is missing information about the item basetype or ilvl
        Alert infoError = new Alert(Alert.AlertType.ERROR);
        infoError.setTitle("Something Went Wrong");
        infoError.setHeaderText(null);

        // shows when user has entered illegal value in one of the currency information fields
        Alert inputAlert = new Alert(Alert.AlertType.ERROR);
        inputAlert.setTitle("Invalid input value(s)");
        inputAlert.setHeaderText(null);
        inputAlert.setContentText("All values entered must be a number greater than 0");

        // if there is an update to the basetype combobox
        if (event.getSource() == basetypes) {
            if (!basetypes.getValue().equals("-Item Basetype-")) {
                list = FXCollections.observableArrayList(driver.analyze(basetypes.getValue()));
                implicits.setItems(list); // set the list of implicits to display in the dropdown box
                implicits.getSelectionModel().select("--Select a desired implicits--");
            } else
                return;

        } else if (event.getSource() == implicits) {
            driver.setPrefImp(implicits.getValue());
        } else if (event.getSource() == calculate) { // if the user clicks the calculate button at the bottom
            String errorMsg = "";
            if (implicits.getValue().equals("--Select a desired implicits--")) {
                implicitAlert.showAndWait();
                return;
            } else if (implicits.getValue().equals("-Insufficient Item Information-")) {
                if (basetypes.getValue().equals("-Item Basetype-")) {
                    errorMsg += "-No item basetype selected\n";
                }

                infoError.setContentText(errorMsg);
                infoError.showAndWait();
                return;
            }

            try {
                Integer.parseInt(initInv.getText()); // check to make sure the field isn't empty
                Integer.parseInt(corrVal.getText());

                corrValDoub = Double.valueOf(corrVal.getText());
                initInvDoub = Double.valueOf(initInv.getText());

                if (corrValDoub > 0 && initInvDoub > 0) { // if both of the fields have a value greater than 0
                    driver.setEcon(initInvDoub, corrValDoub);
                }

                Integer.parseInt(resaleVal.getText());
                resaleValDoub = Double.valueOf(resaleVal.getText());
            } catch (NumberFormatException ex) {

            }

            showSummary();

        } else if (event.getSource() == reset) { // if the reset button is clicked
            basetypes.getSelectionModel().select("-Item Basetype-");

            list = FXCollections.observableArrayList(driver.analyze(basetypes.getValue()));
            implicits.getItems().clear(); // set the list of implicits to display in the dropdown box
            implicits.getSelectionModel().select("-Insufficient Item Information-");

            corrVal.clear();
            initInv.clear();
        }
    }

    /**
     * This method is called when the user has filled every field and wants a summary of the information
     * surrounding their item base and implicits.
     */
    public void showSummary(){

        Alert calcAlert = new Alert(Alert.AlertType.INFORMATION);
        calcAlert.setTitle("Calculation Results");
        calcAlert.setHeaderText(null);

        String alertMsg = "";

        // setting up decimal formatting
        DecimalFormat df = new DecimalFormat("##.###%"); // formatting of percent signs
        DecimalFormat currFormat = new DecimalFormat(".##"); // for formatting the profitability

        // temporary objects
        Item tempBase = driver.getBaseItem();
        Implicit tempImp = driver.getPrefImp();

        // get our best and worst case data
        double worstPct = (tempBase.getWorstChance(tempImp)*1/6);
        double bestPct = (tempBase.getBestChance(tempImp,tempImp.getIlvl())*1/6);
        double bestTries = ((double)1/bestPct);
        double worstTries = ((double)1/worstPct);

        alertMsg += ("Chance of corruption " +
                "\n\tBest case (ilvl " + tempImp.getIlvl() +"): " + df.format(bestPct) +
                "\n\tWorst case (ilvl " + tempBase.getMaxIlvl() + "): " + df.format(worstPct) +

                "\nAverage attempts to corrupt: " +
                "\n\tBest case (ilvl " + tempImp.getIlvl() +"): " + currFormat.format(bestTries) +
                "\n\tWorst case (ilvl " + tempBase.getMaxIlvl() + "): " + currFormat.format(worstTries));

        // if we have economy info
        if (hasNumber(initInv) && hasNumber(corrVal)) {
            double expRet = 0;

            if (hasNumber(resaleVal)) // if the user enters a resale value
                 expRet = (((double)100 / (1/bestPct)) * corrValDoub) / ((initInvDoub - resaleValDoub) *100);
             else
                expRet = (((double)100 / (1/bestPct)) * corrValDoub) / (initInvDoub  *100);

            if (Double.isInfinite(expRet)) {
                alertMsg += ("\nCongrats, you found a way to make infinite money (or you entered some incorrect information)" +
                " because your corrupted resale value is at least as much as you paid for the item.");
            } else {
                alertMsg += ("\nFor every chaos spent (best case), you can expect a return of " + currFormat.format(expRet) +
                        " chaos (above 1 means you should make money).");
            }

        }

        calcAlert.setContentText(alertMsg);

        String possibleImp = driver.getBaseItem().toString();
        Label label = new Label("Probabilities of all possible corruptions (worst case): ");

        TextArea potRolls = new TextArea(possibleImp); // setting up the stat summary area
        potRolls.setEditable(false);
        potRolls.setWrapText(true);

        potRolls.setMaxWidth(Double.MAX_VALUE);
        potRolls.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(potRolls, Priority.ALWAYS);
        GridPane.setHgrow(potRolls, Priority.ALWAYS);

        GridPane rollBox = new GridPane();
        rollBox.setMaxWidth(Double.MAX_VALUE);
        rollBox.add(label, 0, 0);
        rollBox.add(potRolls, 0, 1);

        // Set expandable Exception into the dialog pane.
        calcAlert.getDialogPane().setExpandableContent(rollBox);
        calcAlert.getDialogPane().setExpanded(true);
        calcAlert.showAndWait();

    }

    private boolean hasNumber(TextField field) {
        try {
            Integer.parseInt(field.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}