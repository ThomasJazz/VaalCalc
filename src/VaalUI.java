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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.control.TextArea;

public class VaalUI extends Application implements EventHandler<ActionEvent> {
    private ComboBox<String> basetypes, implicit;
    private Button calculate, reset;
    private Label ilvlLabel, initInvLabel, corrValLabel;
    private TextField ilvl, initInv, corrVal;
    private int ilvlInt = 0;
    private Double initInvInt, corrValInt;
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
        initTool = new Tooltip("The value of your item with your selected implicit (in chaos).");
        corrTool = new Tooltip("The cost of purchasing an uncorrupted version of the item you are attempting to vaal (in chaos).");
        initTool.setWrapText(true);
        initTool.setPrefWidth(200);
        corrTool.setWrapText(true);
        corrTool.setPrefWidth(200);

        // ilvl label
        ilvlLabel = new Label("ilvl:");
        ilvlLabel.setLayoutX(232);
        ilvlLabel.setLayoutY(210);

        // initInv Label
        initInvLabel = new Label("Initial Cost of Item (chaos)");
        initInvLabel.setLayoutY(364);
        initInvLabel.setLayoutX(65);

        //corrVal label
        corrValLabel = new Label("Value w/ Desired Corruption");
        corrValLabel.setLayoutY(400);
        corrValLabel.setLayoutX(49);

        // ilvl textField
        ilvl = new TextField();
        ilvl.setPrefWidth(60);
        ilvl.setLayoutX(265);
        ilvl.setLayoutY(210);
        // auto update the list of possible implicits whenever a key is pressed
        ilvl.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (!basetypes.getValue().equals("-Item Basetype-")) {
                    try {
                        ilvlInt = Integer.parseInt(ilvl.getText()); // set to integer representation of ilvl field

                        list = FXCollections.observableArrayList(driver.analyze(basetypes.getValue(), ilvlInt));
                        implicit.setItems(list); // set the list of implicits to display in the dropdown box
                        implicit.getSelectionModel().select("--Select a desired implicit--");
                    } catch (NumberFormatException e){
                        implicit.getSelectionModel().select("-Insufficient Item Information-");
                        implicit.getItems().clear();
                    }
                }
            }
        });

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

        // implicit combobox
        implicit = new ComboBox<>();
        implicit.setPrefWidth(230);
        implicit.setLayoutY(270);
        implicit.setLayoutX(93);
        implicit.getSelectionModel().select("-Insufficient Item Information-");

        ui = new Image("images/ui.png");
        bg = new ImagePattern(ui);

        // basetypes comboBox
        basetypes = new ComboBox<>(); // this is how we make the dropdown menu
        basetypes.setPrefWidth(140);
        basetypes.setLayoutX(70);
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
        group.getChildren().addAll(basetypes, ilvlLabel, ilvl, implicit);
        group.getChildren().addAll(initInvLabel, initInv, corrVal, corrValLabel);
        group.getChildren().addAll(calculate, reset);

        // Scene set-up and assignment
        Scene scene = new Scene(group, 400, 600);
        scene.setFill(bg);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(VaalUI.class.getResourceAsStream("images/chaosvaal.png")));
        primaryStage.setTitle("VaalCalc v1.0");
        primaryStage.setResizable(false); // cannot be resized
        scene.getStylesheets().addAll(this.getClass().getResource("/Stylesheet.css").toExternalForm());
        primaryStage.show();
    }

    @Override
    public void handle(ActionEvent event) {
        // shows when the user has failed to select an implicit stat for calculation
        Alert implicitAlert = new Alert(Alert.AlertType.ERROR);
        implicitAlert.setTitle("Missing Implicit Stat");
        implicitAlert.setHeaderText(null);
        implicitAlert.setContentText("Please select an implicit stat");

        // when the user is missing information about the item basetype or ilvl
        Alert infoError = new Alert(Alert.AlertType.ERROR);
        infoError.setTitle("Something Went Wrong");
        infoError.setHeaderText(null);

        // shows when user has entered illegal value in one of the currency information fields
        Alert inputAlert = new Alert(Alert.AlertType.ERROR);
        inputAlert.setTitle("Invalid input value(s)");
        inputAlert.setHeaderText(null);
        inputAlert.setContentText("All values entered must be a number greater than 0");

        // if there is an update to either the basetype or ilvl, this will auto update the selection options accordingly
        if (event.getSource() == basetypes || event.getSource() == ilvl) {
            if (!basetypes.getValue().equals("-Item Basetype-")) {
                try {
                    ilvlInt = Integer.parseInt(ilvl.getText()); // set to integer representation of ilvl field

                    if (ilvlInt != 0) {
                        list = FXCollections.observableArrayList(driver.analyze(basetypes.getValue(), ilvlInt));
                        implicit.setItems(list); // set the list of implicits to display in the dropdown box
                        implicit.getSelectionModel().select("--Select a desired implicit--");
                    }
                } catch (NumberFormatException e){

                }
            } else
                return;

        } else if (event.getSource() == calculate) { // if the user clicks the calculate button at the bottom
            String errorMsg = "";
            if (implicit.getValue().equals("--Select a desired implicit--")) {
                implicitAlert.showAndWait();
                return;
            } else if (implicit.getValue().equals("-Insufficient Item Information-")) {
                if (basetypes.getValue().equals("-Item Basetype-")) {
                    errorMsg += "-No item basetype selected\n";
                }

                try {
                    ilvlInt = Integer.parseInt(ilvl.getText());
                } catch (NumberFormatException e) {
                    errorMsg += "-No item level entered";
                }
                infoError.setContentText(errorMsg);
                infoError.showAndWait();
                return;
            }

            try {
                Integer.parseInt(initInv.getText()); // check to make sure the field isn't empty
                Integer.parseInt(corrVal.getText());

                corrValInt = Double.valueOf(corrVal.getText());
                initInvInt = Double.valueOf(initInv.getText());

                if (corrValInt > 0 && initInvInt > 0) { // if both of the fields have a value greater than 0
                    driver.setPrefImp (implicit.getValue()); // send the implicit we are looking for to the driver
                    driver.setEcon(initInvInt, corrValInt);
                }
            } catch (NumberFormatException ex) {

            }

            showSummary();
        } else if (event.getSource() == reset) { // if the reset button is clicked
            basetypes.getSelectionModel().select("-Item Basetype-");

            list = FXCollections.observableArrayList(driver.analyze(basetypes.getValue(), ilvlInt));
            implicit.getItems().clear(); // set the list of implicits to display in the dropdown box
            implicit.getSelectionModel().select("-Insufficient Item Information-");

            ilvl.clear();
            corrVal.clear();
            initInv.clear();
        }
    }

    /**
     * This method is called when the user has filled every field and wants a summary of the information
     * surrounding their item base and implicit.
     */
    public void showSummary(){
        Alert calcAlert = new Alert(Alert.AlertType.INFORMATION);
        calcAlert.setTitle("Calculation Results");
        calcAlert.setHeaderText(null);

        try {
            // if either field is empty we wont display the information below, just the overall summary
            Integer.parseInt(initInv.getText());
            Integer.parseInt(corrVal.getText());

            // economy information
            DecimalFormat df = new DecimalFormat("##.###%"); // formatting of percent signs
            DecimalFormat currFormat = new DecimalFormat(".##"); // for formatting the profitability
            double percent = (driver.getUserItem().getChance(driver.getPrefImp())*1/6);
            double ret = (((double)100 / (1/percent)) * corrValInt) / (initInvInt*100); // expected returns per corruption

            calcAlert.setContentText("Your selected implicit stat has a {" + df.format(percent) + "} chance of rolling.\n"
                    + "For every chaos spent, you can expect a return of {" + currFormat.format(ret) +
                    "} chaos (above 1 means you should make money). \n" +
                    "It will take an average of " + (double)1/percent + " attempts to roll your desired implicit.");
        } catch (NumberFormatException e) {

        } finally {
            String possibleImp = driver.getUserItem().toString();
            Label label = new Label("Probabilities of all possible corruptions: ");

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
    }
}