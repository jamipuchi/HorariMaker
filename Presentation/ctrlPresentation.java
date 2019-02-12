package Presentation;

import Domain.ctrlDomain;
import com.pixelduke.javafx.validation.ErrorLabel;
import com.pixelduke.javafx.validation.RequiredField;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

public class ctrlPresentation implements Initializable {

    @FXML
    public Button BotoHorari;

    private ctrlDomain CD;

    @FXML
    private RequiredField requiredUserName;

    @FXML
    private RequiredField requiredPassword;

    @FXML
    private TextField userName;

    @FXML
    private TextField password;

    @FXML
    private ChoiceBox<String> grupList;

    @FXML
    private ChoiceBox<String> aulaList;

    @FXML
    private ChoiceBox<String> assigList;

    @FXML
    public ErrorLabel registerMessage;

    @FXML
    public VBox side;

    @FXML
    public HBox bar;

    @FXML
    private ChoiceBox<String> instList;

    @FXML
    private ChoiceBox<String> plaList;

    private Button submit;

    private TextField nom;

    private String nomCosa;

    private String errorMessage;

    private int nclasse;

    private int horaIni = 8;

    private int horaFi = 16;

    private String sdies = "01234";

    private int asDia;
    private int asHora;
    private int asAula;
    private boolean grupSeleccionat=false;
    private boolean ferHorari = true;
    private String colorFons = "#ffffff";
    private String nomGrupSeleccionat;

    private boolean easterEggActivat = false;

    ChoiceBox llistaR;

    ArrayList<Button> buttons = new ArrayList<>();


    ArrayList<String> colorAssig = new ArrayList<>(FXCollections.observableArrayList("#ff6666","#ff8c66","#ffd966," +
            "#ffff66","#d9ff66","#b3ff66","#8cff66","#66ff66","#66ff8c","#66ffb3","#66ffd9","#66ffff","#66d9ff","#66b3ff"+"#668cff"+
            "#6666ff","#8c66ff","#b366ff","#d966ff","#ff66ff","#ff66d9","#ff66b3","#ff668c","#ff6666"));

    private double xOffset = 0 ;
    private double yOffset = 0 ;

    public ctrlPresentation() {
        CD = ctrlDomain.getInstance();
    }

    private void changeScene() throws Exception {
        nclasse = 0;
        Stage stage = (Stage) userName.getScene().getWindow();
        URL fxml = getClass().getClassLoader().getResource("Horari.fxml");
        assert fxml != null;
        Parent root = FXMLLoader.load(fxml);
        stage.setTitle("Horacio Maker");
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)){
                    if (event.getClickCount()==2)
                        stage.setMaximized(!stage.isMaximized());
                }
            }
        });

        //move around here
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });

        final Scene scene = new Scene(root);
        stage.setX(10);
        stage.setY(10);
        stage.setScene(scene);
        stage.show();


    }

    private void requiredFields() {
        requiredUserName.eval();
        requiredPassword.eval();
    }

    private boolean verifyFields() {
        registerMessage.setMessage("");
        boolean ret = false;
        if (userName.getText().isEmpty() && !password.getText().isEmpty()) requiredFields();
        else if (!userName.getText().isEmpty() && password.getText().isEmpty()) requiredFields();
        else if (userName.getText().isEmpty() && password.getText().isEmpty()) requiredUserName.eval();
        else ret = true;
        return ret;
    }

    public void submitPressedLogIn(ActionEvent actionEvent) throws Exception {
        if (verifyFields()) {
            if (CD.logIn(userName.getText(), password.getText())) changeScene();
            else
                registerMessage.setMessage("Username doesn't exist or" + System.lineSeparator() + "password is incorrect");
        }
    }

    public void submitPressedRegister(ActionEvent actionEvent) throws Exception {
        registerMessage.setMessage("");
        if (verifyFields()) {
            if (CD.registerUser(userName.getText(), password.getText()))
                registerMessage.setMessage("Registration successful!");
            else registerMessage.setMessage("User already exists.");

        }
    }

    public void closeApp(ActionEvent actionEvent) throws IOException {
        CD.saveBBDD();
        System.exit(0);
    }

    public void generateHorari(ActionEvent actionEvent) {
        if (ferHorari){
            Collections.shuffle(colorAssig);
            if (horaFi!=0){
                CD.makeHorari(horaIni, horaFi, sdies);
            }else{
                CD.makeHorari(8,20,"01234");
            }
            nclasse = 0;
            BotoHorari.setText("Veure Horari");
            ferHorari = false;
        }
        displayHorari(new ActionEvent());
    }

    public void displayHorari(ActionEvent actionEvent) {
        side.getChildren().clear();
        GridPane horariPane = new GridPane();
        horariPane.setAlignment(Pos.CENTER);

        HBox topmenu = new HBox();
        topmenu.setSpacing(10);
        if (nclasse != 0) {
            Button back = new Button("<");
            back.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    nclasse--;
                    displayHorari(new ActionEvent());
                }
            });
            topmenu.getChildren().add(back);
        }
        topmenu.getChildren().add(new Label(CD.getAules().get(nclasse)));
        if (nclasse + 1 < CD.getAules().size()) { //numero de classes
            Button forward = new Button(">");
            forward.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    nclasse++;
                    displayHorari(new ActionEvent());
                }
            });
            topmenu.getChildren().add(forward);
        }


        int k = 0;
        for (int i = 0; i < sdies.length(); i++) {
            Label dia = new Label(CD.getDies().get(Integer.parseInt(Character.toString(sdies.charAt(i)))));
            dia.setAlignment(Pos.CENTER);
            dia.setPadding(new Insets(10, 10, 10, 10));
            dia.setStyle("-fx-font-weight: bold");
            horariPane.add(dia, i+1, 0);
            horariPane.setHalignment(dia, HPos.CENTER);
            for (int j = 0; j < horaFi-horaIni; j++) {
                Label hora = new Label((j + horaIni) + ":00"); //sha de canviar la hora i iterar
                hora.setStyle("-fx-font-weight: bold");
                hora.setPadding(new Insets(10, 10, 10, 10));
                if (i==0)horariPane.add(hora,0,j+1);
                Button classe = new Button(); //sha de miarar la classe i grup que toca aquell dia
                if (CD.getSpotAssig(i,j,nclasse) != null) {
                    classe.setText(CD.getSpotAssig(i,j,nclasse) + System.lineSeparator() + CD.getSpotNumGrup(i,j,nclasse));
                    classe.setStyle("-fx-pref-width: 150; -fx-border-radius: 0;-fx-background-radius: 0;-fx-pref-height: 30;-fx-background-color:"+colorAssig.get(CD.getAssigs().indexOf(CD.getSpotAssig(i,j,nclasse)))+"; -fx-border-color: transparent; -fx-border-width: 0"); //SET COLOR BY SUBJECT
                } else {
                    classe.setStyle("-fx-pref-width: 150;-fx-pref-height: 30;-fx-background-color: " + "#ffffff" + "; -fx-border-color: transparent; -fx-border-width: 0; -fx-border-radius: 0"); //SET COLOR BY SUBJECT
                }
                classe.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (!grupSeleccionat){
                            Button sourceBtn = (Button) event.getSource();
                            if (sourceBtn.getText() == "") return;
                            classe.setStyle("-fx-pref-width: 150; -fx-border-radius: 0;-fx-background-radius: 0;-fx-pref-height: 30;-fx-background-color:#ffffff; -fx-border-color: #009aff; -fx-border-width: 2"); //SET COLOR BY SUBJECT
                            asDia = GridPane.getColumnIndex(sourceBtn).intValue()-1;
                            asHora = GridPane.getRowIndex(sourceBtn).intValue()-1;
                            asAula = nclasse;
                            nomGrupSeleccionat = sourceBtn.getText();
                            grupSeleccionat = true;
                        } else {
                            Button sourceBtn = (Button) event.getSource();
                            CD.moveGrup(asDia,GridPane.getColumnIndex(sourceBtn).intValue()-1,asHora,GridPane.getRowIndex(sourceBtn).intValue()-1,asAula,nclasse, nomGrupSeleccionat);
                            displayHorari(new ActionEvent());
                            grupSeleccionat = false;
                        }
                    }
                });
                buttons.add(classe);
                horariPane.add(buttons.get(k), i+1, j + 1, 1, 1);
                k++;
            }
        }

        buttons.clear();
        side.setAlignment(Pos.CENTER);
        topmenu.setAlignment(Pos.CENTER);
        side.getChildren().addAll(topmenu, horariPane);

    }

    public void afegirRestriccions(VBox aquiRestric){
        aquiRestric.getChildren().clear();
        aquiRestric.setSpacing(10);
        aquiRestric.setPadding(new Insets(10 , 10, 10, 10));
        ChoiceBox restric = new ChoiceBox();
        restric.setItems(FXCollections.observableArrayList("Dia","Hora","Dia i Hora", "Matins","Tardes"));
        restric.setMinWidth(250);
        VBox ficaRestric = new VBox();
        aquiRestric.getChildren().addAll(new Label("Tria la restricció que vols afegir"),restric, ficaRestric);
        restric.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switch (restric.getSelectionModel().getSelectedItem().toString()){
                    case "Dia": restriccioDia(aquiRestric);
                                break;
                    case "Hora": restriccioHora(aquiRestric);
                                break;
                    case "Matins": restriccioMatins(aquiRestric);
                                break;
                    case "Tardes": restriccioTardes(aquiRestric);
                                break;
                    case "Dia i Hora": restriccioDiaIHora(aquiRestric);
                                break;
                }
            }
        });
    }

    public void restriccioDia(VBox v){
        Label enunciat = new Label("Seleccioni els dies en els que vol"+System.lineSeparator()+"que no hi hagi classe: ");
        VBox r = new VBox();
        r.setSpacing(10);
        r.getChildren().add(enunciat);
        ArrayList<CheckBox> arrayDies = new ArrayList<CheckBox>();
        for (int i = 0; i < CD.getDies().size(); i++) {
            CheckBox nc = new CheckBox(CD.getDies().get(i));
            r.getChildren().add(nc);
            arrayDies.add(nc);
        }

        Button guardar = new Button("Guardar");
        r.getChildren().add(guardar);
        guardar.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                for (int i = 0; i < CD.getDies().size(); i++) {
                    if (arrayDies.get(i).isSelected()){
                        CD.addRestriccioDia(i);
                    }
                }
                llistaR.setItems(FXCollections.observableArrayList(CD.getLlistaRestriccions()));
                llistaR.getSelectionModel().select(CD.getLlistaRestriccions().size()-1);
                v.getChildren().clear();
            }
        });
        v.getChildren().clear();
        v.setPadding(new Insets(10,10,10,10));
        v.getChildren().add(r);

    }

    public void restriccioHora(VBox v){
        v.setSpacing(10);
        Label enunciat = new Label("Introdueixi les hores en les que vol"+System.lineSeparator()+"que no hi hagi classe: ");
        TextField de = new TextField();
        de.setPrefWidth(30);
        TextField fins = new TextField();
        fins.setPrefWidth(30);
        HBox selectHores = new HBox(new Label("De:"),de,new Label("Fins:"),fins);
        selectHores.setSpacing(10);
        Button guardar = new Button("Guardar");
        Label EM = new Label("Completa els camps correctament");
        EM.setTextFill(Paint.valueOf("red"));
        EM.setVisible(false);
        HBox completar = new HBox(guardar,EM);
        completar.setAlignment(Pos.BOTTOM_CENTER);
        completar.setSpacing(5);
        v.getChildren().clear();
        v.setPadding(new Insets(10,10,10,10));
        v.getChildren().addAll(enunciat,selectHores,completar);
        guardar.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (de.getText().matches("([0-9])|(1[0-9])|(2[0-4])") && fins.getText().matches("([0-9])|(1[0-9])|(2[0-4])")
                        && Integer.parseInt(de.getText())<Integer.parseInt(fins.getText())){
                    CD.addRestriccioHora(Integer.parseInt(de.getText()),Integer.parseInt(fins.getText()));
                    v.getChildren().clear();
                    llistaR.setItems(FXCollections.observableArrayList(CD.getLlistaRestriccions()));
                    llistaR.getSelectionModel().select(CD.getLlistaRestriccions().size()-1);

                }else{
                    EM.setVisible(true);
                }
            }
        });
    }

    public void restriccioDiaIHora(VBox v){
        Label enunciat = new Label("Seleccioni els dies i les hores en les"+System.lineSeparator()+"que vol que no hi hagi classe: ");
        VBox r = new VBox();
        r.setSpacing(10);
        r.getChildren().add(enunciat);
        ArrayList<CheckBox> arrayDies = new ArrayList<CheckBox>();
        for (int i = 0; i < CD.getDies().size(); i++) {
            CheckBox nc = new CheckBox(CD.getDies().get(i));
            r.getChildren().add(nc);
            arrayDies.add(nc);
        }

        Button guardar = new Button("Guardar");
        TextField de = new TextField();
        de.setPrefWidth(30);
        TextField fins = new TextField();
        fins.setPrefWidth(30);
        HBox selectHores = new HBox(new Label("De:"),de,new Label("Fins:"),fins);
        selectHores.setSpacing(10);
        r.getChildren().add(selectHores);

        r.getChildren().add(guardar);
        Label EM = new Label("Completa els camps correctament");
        EM.setTextFill(Paint.valueOf("red"));
        EM.setVisible(false);
        r.getChildren().add(EM);
        guardar.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (de.getText().matches("([0-9])|(1[0-9])|(2[0-4])") && fins.getText().matches("([0-9])|(1[0-9])|(2[0-4])")
                        && Integer.parseInt(de.getText())<Integer.parseInt(fins.getText())) {
                    for (int i = 0; i < CD.getDies().size(); i++) {
                        if (arrayDies.get(i).isSelected()){
                            CD.addRestriccioDiaHora(i, Integer.parseInt(de.getText()), Integer.parseInt(fins.getText()));
                        }
                    }
                    llistaR.setItems(FXCollections.observableArrayList(CD.getLlistaRestriccions()));
                    llistaR.getSelectionModel().select(CD.getLlistaRestriccions().size()-1);
                    v.getChildren().clear();
                }else {
                    EM.setVisible(true);
                }
            }
        });
        v.getChildren().clear();
        v.setPadding(new Insets(10,10,10,10));
        v.getChildren().add(r);

    }

    public void restriccioMatins(VBox v){
        v.setSpacing(10);
        Label enunciat = new Label("Cliqui guardar per guardar la restricció"+System.lineSeparator()+"que només hi hagi classe als matins: ");
        Button guardar = new Button("Guardar");
        v.getChildren().clear();
        v.setPadding(new Insets(10,10,10,10));
        v.getChildren().addAll(enunciat,guardar);
        guardar.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                CD.addRestriccioMatins();
                llistaR.setItems(FXCollections.observableArrayList(CD.getLlistaRestriccions()));
                llistaR.getSelectionModel().select(CD.getLlistaRestriccions().size()-1);

                v.getChildren().clear();
            }
        });
    }

    public void restriccioTardes(VBox v){
        v.setSpacing(10);
        Label enunciat = new Label("Cliqui guardar per guardar la restricció"+System.lineSeparator()+"que només hi hagi classe a les tardes: ");
        Button guardar = new Button("Guardar");
        v.getChildren().clear();
        v.setPadding(new Insets(10,10,10,10));
        v.getChildren().addAll(enunciat,guardar);
        guardar.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                CD.addRestriccioTardes();
                llistaR.setItems(FXCollections.observableArrayList(CD.getLlistaRestriccions()));
                llistaR.getSelectionModel().select(CD.getLlistaRestriccions().size()-1);
                v.getChildren().clear();
            }
        });
    }

    public void displayStuff(ActionEvent actionEvent) {
        side.getChildren().clear();
        side.setAlignment(Pos.CENTER);
        Label NomAules = new Label("Aules:");
        NomAules.setStyle("-fx-font-weight: bold; -fx-font-size: 22");
        VBox Aules = new VBox(NomAules);
        for (int i = 0; i < CD.getAules().size(); i++) {
            String nomAula =CD.getAules().get(i);
            CD.chooseAula(i);
            String symbolRes = "";
            for (int j = 0; j < CD.getRestriccionsAula().size(); j++) {
                if (CD.getRestriccionsAula().get(j).contains("Matins"))symbolRes+=" ◁";
                if (CD.getRestriccionsAula().get(j).contains("Tardes"))symbolRes+=" ▷";
                if (CD.getRestriccionsAula().get(j).contains("Hora"))symbolRes+=" ◑";
                if (CD.getRestriccionsAula().get(j).contains("Dia"))symbolRes+=" ☼";
            }
            Label nA = new Label(nomAula);
            nA.setStyle("-fx-font-size: 18; -fx-font-weight: bold");
            Label nR = new Label("Restriccions:" + symbolRes);
            nR.setStyle("-fx-font-size: 12");
            Aules.getChildren().addAll(nA,nR);

        }
        Label NomAssigs = new Label("Assignatures:");
        NomAssigs.setStyle("-fx-font-weight: bold; -fx-font-size: 22");
        VBox Assigs = new VBox(NomAssigs);
        for (int i = 0; i < CD.getAssigs().size(); i++) {
            String nomAssig =CD.getAssigs().get(i);
            CD.chooseAssig(CD.getAssigs().get(i));
            String symbolRes = "";
            for (int j = 0; j < CD.getRestriccionsAssig().size(); j++) {
                if (CD.getRestriccionsAssig().get(j).contains("Matins"))symbolRes+=" ◁";
                if (CD.getRestriccionsAssig().get(j).contains("Tardes"))symbolRes+=" ▷";
                if (CD.getRestriccionsAssig().get(j).contains("Hora"))symbolRes+=" ◑";
                if (CD.getRestriccionsAssig().get(j).contains("Dia"))symbolRes+=" ☼";
            }
            String corr = "";
            for (int j = 0; j < CD.getCorequisits().size(); j++) {
                corr += CD.getCorequisits().get(j);
            }
            Label nA = new Label(nomAssig);
            nA.setStyle("-fx-font-size: 18; -fx-font-weight: bold");
            Label nR = new Label("Restriccions:" + symbolRes);
            Label cor = new Label("Corequisits: "+ corr);
            Label nGrp = new Label("Num Grups: "+CD.getGroups().size());
            nR.setStyle("-fx-font-size: 12");
            cor.setStyle("-fx-font-size: 12");
            Assigs.getChildren().addAll(nA,nR,cor,nGrp);
        }

        HBox contingut = new HBox(Assigs, Aules);
        contingut.setAlignment(Pos.CENTER);
        contingut.setSpacing(100);
        side.getChildren().add(contingut);
        actualitzaPla(new ActionEvent());
        //Bàsicament s'ha de poder veure tot el que tenim a la base de dades (aules amb la info
        //corresponent i assignatures tb amb la info corresponent (grups) i tal i (inclou restriccions)
    }

    public void minimizeApp(ActionEvent actionEvent) {
        ((Stage)((Button)actionEvent.getSource()).getScene().getWindow()).setIconified(true);

    }

    public void addInstitucio(ActionEvent actionEvent) {
        createWithName("la institució");
        submit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (CD.createInstitucio(nom.getText())){
                    instList.setItems(FXCollections.observableArrayList(CD.getInstitucions()));
                    instList.getSelectionModel().select(nom.getText());
                    actualitzaInst(new ActionEvent());
                    guardar();
                } else {
                    side.getChildren().clear();
                    side.getChildren().add(new Label("Error! Introdueix un nom que no estigui utilitzat!"));
                }
            }
        });
        //actualitzar llista
    }

    public void addPlaEstudis(ActionEvent actionEvent){
        if (CD.getInstitucions().size()==0){
            side.getChildren().clear();
            side.setAlignment(Pos.CENTER);
            side.getChildren().add(new Label("Afegeix una Institució abans de crear un Pla d'estudis"));
        }else {
            createWithName("el pla d'estudis");
            submit.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (CD.createPla(nom.getText())){
                        plaList.setItems(FXCollections.observableArrayList(CD.getPlans()));
                        plaList.getSelectionModel().select(nom.getText());
                        guardar();
                    }else {
                        side.getChildren().add(new Label("Error"));
                        side.getChildren().clear();
                    }
                }
            });
        }

    }

    private void createWithName(String que) {
        side.setAlignment(Pos.CENTER);
        side.getChildren().clear();
        nom = new TextField();
        submit = new Button("Guardar");
        nom.setMaxWidth(300);
        submit.setMaxWidth(300);
        VBox content = new VBox(new Label("Introdueix el nom de "+que+":"), nom, submit);
        content.setPadding(new Insets(300,300,300,300));
        content.setSpacing(20);
        content.setAlignment(Pos.CENTER);
        side.getChildren().add(content);
    }

    public void creaAssignatura(ActionEvent actionEvent) {
        CD.clearRestriccions();
        if (CD.getPlans().size()!=0){
            side.setAlignment(Pos.CENTER);
            VBox container = new VBox();
            VBox rest = new VBox();
            HBox menuAssig = new HBox(container, rest);
            menuAssig.setPadding(new Insets(40 , 40, 40, 40));
            menuAssig.setSpacing(20);
            menuAssig.setAlignment(Pos.CENTER);
            container.setSpacing(10);
            Label nom = new Label("Nom Assignatura");
            TextField nom_aula = new TextField();
            nom_aula.setMaxWidth(235);
            Label capacitat = new Label("Hores setmanals:");
            TextField horesSetmana = new TextField();
            horesSetmana.setMaxWidth(235);
            Label restr = new Label("Restriccions");
            llistaR = new ChoiceBox();
            llistaR.setPrefWidth(200);
            Button add = new Button("Crear Nova Restricció");
            ChoiceBox<String> corequisits = new ChoiceBox();
            corequisits.setPrefWidth(200);
            Label corr = new Label("Corequisits");
            Button botocorequisit = new Button("Afegir correquisit");
            add.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    afegirRestriccions(rest);
                }
            });
            Button borrar = new Button("\uD83D\uDDD1");
            borrar.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    eliminarR();
                }
            });
            HBox HBR = new HBox(llistaR, borrar);
            HBR.setSpacing(10);
            Button borrarco = new Button("\uD83D\uDDD1");
            HBox HBR2 = new HBox(corequisits,borrarco);
            HBR2.setSpacing(10);

            Button complete = new Button("Guardar");
            Label EM = new Label("Emplena tots els camps correctament.");
            EM.setTextFill(javafx.scene.paint.Paint.valueOf("#ff0000"));
            EM.setVisible(false);
            container.getChildren().addAll(nom, nom_aula, capacitat, horesSetmana, restr, HBR, add,corr,HBR2,botocorequisit, complete,EM);
            side.getChildren().clear();
            side.getChildren().addAll(menuAssig);
            ArrayList<String> llistaCorrequisits = new ArrayList<>();
            borrarco.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    llistaCorrequisits.remove(corequisits.getSelectionModel().getSelectedItem());
                    CD.removeCorequisit(corequisits.getSelectionModel().getSelectedItem());
                    corequisits.setItems(FXCollections.observableArrayList(llistaCorrequisits));
                }
            });
            botocorequisit.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    rest.getChildren().clear();
                    ChoiceBox assignatures = new ChoiceBox(FXCollections.observableArrayList(CD.getAssigs()));
                    assignatures.getSelectionModel().select(0);
                    Button botoguardarcorequisit = new Button("Guardar");
                    VBox menucorrequisits = new VBox(assignatures, botoguardarcorequisit);
                    menucorrequisits.setSpacing(10);
                    rest.getChildren().add(menucorrequisits);
                    botoguardarcorequisit.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            llistaCorrequisits.add(CD.getAssigs().get(assignatures.getSelectionModel().getSelectedIndex()));
                            corequisits.setItems(FXCollections.observableArrayList(llistaCorrequisits));
                            corequisits.getSelectionModel().select(1-1);//igual
                            rest.getChildren().clear();
                        }
                    });
                }
            });
            complete.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (nom_aula.getText()!=""&& horesSetmana.getText().matches("-?\\d+") && !CD.getAssigs().contains(nom_aula.getText())) {
                        CD.createAssig(nom_aula.getText(),Integer.parseInt(horesSetmana.getText()));
                        CD.addRestriccionsAssig();
                        for (String c: llistaCorrequisits) {
                            CD.addCorequisit(c);
                        }
                        assigList.setItems(FXCollections.observableArrayList(CD.getAssigs()));
                        assigList.getSelectionModel().select(CD.getAssigs().indexOf(nom_aula.getText()));
                        grupList.setItems(FXCollections.emptyObservableList());
                        guardar();
                    } else {
                        EM.setVisible(true);
                        //handle wrong input
                    }
                }
            });
        }else{
            side.getChildren().clear();
            Label EM = new Label("Sisplau crea primer un Pla d'Estudis");
            EM.setStyle("-fx-text-fill: #ff0000");
            side.setAlignment(Pos.CENTER);
            side.getChildren().add(EM);
        }

    }

    public void editAssig(ActionEvent actionEvent) {
        if (CD.getAssigs().size()!=0){
            int ngrups = CD.getSuperGrups();
            int nsubgrups = CD.getSubGrups();
            CD.addRestriccionsAssigToTemp();
            String assigtriada = assigList.getSelectionModel().getSelectedItem();
            side.setAlignment(Pos.CENTER);
            VBox container = new VBox();
            VBox restricc = new VBox();
            HBox menuAssig = new HBox(container,restricc);
            menuAssig.setPadding(new Insets(40 , 40, 40, 40));
            menuAssig.setSpacing(20);
            menuAssig.setAlignment(Pos.CENTER);
            container.setSpacing(10);
            Label nom = new Label("Nom Assignatura");
            TextField nom_aula = new TextField();
            nom_aula.setText(assigList.getSelectionModel().getSelectedItem().toString());
            nom_aula.setMaxWidth(235);
            Label capacitat = new Label("Hores setmanals:");
            TextField horesSetmana = new TextField();
            horesSetmana.setText(Integer.toString(CD.getHoresAssig()));
            horesSetmana.setMaxWidth(235);
            ChoiceBox<String> corequisits = new ChoiceBox();
            corequisits.setPrefWidth(200);
            if (CD.getCorequisits().size()>0){
                corequisits.setItems(FXCollections.observableArrayList(CD.getCorequisits()));
                corequisits.getSelectionModel().select(0);
            }
            Label corr = new Label("Corequisits");
            Button botocorequisit = new Button("Afegir correquisit");
            Button borrarco = new Button("\uD83D\uDDD1");
            HBox HBR2 = new HBox(corequisits,borrarco);
            HBR2.setSpacing(10);
            llistaR = new ChoiceBox();
            llistaR.setPrefWidth(200);
            if (CD.getRestriccionsAssig().size()>0){
                llistaR.setItems(FXCollections.observableArrayList(CD.getRestriccionsAssig()));
                llistaR.getSelectionModel().select(0);
            }
            Button add = new Button("Crear Nova Restricció");
            add.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    afegirRestriccions(restricc);
                }
            });
            Button borrar = new Button("\uD83D\uDDD1");
            borrar.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    eliminarR();
                }
            });
            HBox HBR = new HBox(llistaR, borrar);
            HBR.setSpacing(10);
            Button complete = new Button("Guardar");
            Label EM = new Label("Emplena tots els camps correctament.");
            EM.setTextFill(javafx.scene.paint.Paint.valueOf("#ff0000"));
            EM.setVisible(false);
            container.getChildren().addAll(nom, nom_aula, capacitat, horesSetmana, new Label("Restriccions"), HBR, add,corr,HBR2,botocorequisit, complete,EM);
            side.getChildren().clear();
            side.getChildren().addAll(menuAssig);
            ArrayList<String> llistaCorrequisits = CD.getCorequisits();
            borrarco.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    llistaCorrequisits.remove(corequisits.getSelectionModel().getSelectedItem());
                    CD.removeCorequisit(corequisits.getSelectionModel().getSelectedItem());
                    corequisits.setItems(FXCollections.observableArrayList(llistaCorrequisits));
                }
            });
            botocorequisit.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    restricc.getChildren().clear();
                    ChoiceBox assignatures = new ChoiceBox(FXCollections.observableArrayList(CD.getAssigs()));
                    assignatures.getSelectionModel().select(0);
                    Button botoguardarcorequisit = new Button("Guardar");
                    VBox menucorrequisits = new VBox(assignatures, botoguardarcorequisit);
                    menucorrequisits.setSpacing(10);
                    restricc.getChildren().add(menucorrequisits);
                    botoguardarcorequisit.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            llistaCorrequisits.add(CD.getAssigs().get(assignatures.getSelectionModel().getSelectedIndex()));
                            corequisits.setItems(FXCollections.observableArrayList(llistaCorrequisits));
                            corequisits.getSelectionModel().select(1-1);//igual
                            restricc.getChildren().clear();
                        }
                    });
                }
            });
            complete.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (nom_aula.getText()!=""&& horesSetmana.getText().matches("-?\\d+")){
                        CD.chooseAssig(assigtriada);
                        CD.removeAssig();
                        CD.createAssig(nom_aula.getText(),Integer.parseInt(horesSetmana.getText()));
                        CD.addGroups(ngrups, nsubgrups);
                        for (String c: llistaCorrequisits) {
                            CD.addCorequisit(c);
                        }
                        CD.addRestriccionsAssig();
                        assigList.setItems(FXCollections.observableArrayList(CD.getAssigs()));
                        assigList.getSelectionModel().select(CD.getAssigs().indexOf(nom_aula.getText()));
                        grupList.setItems(FXCollections.emptyObservableList());
                        guardar();
                    } else {
                        EM.setVisible(true);
                        //handle wrong input
                    }
                }
            });
        }else{
            side.getChildren().clear();
            Label EM = new Label("Sisplau tingues una assignatura seleccionada");
            EM.setStyle("-fx-text-fill: #ff0000");
            side.setAlignment(Pos.CENTER);
            side.getChildren().add(EM);
        }

    }

    private void eliminarR(){
        CD.deleteRestriccio(llistaR.getSelectionModel().getSelectedIndex());
        llistaR.setItems(FXCollections.observableArrayList(CD.getLlistaRestriccions()));
        if (CD.getLlistaRestriccions().size()>0) llistaR.getSelectionModel().select(0);
    }

    public void creaAula(ActionEvent event){
        CD.clearRestriccions();
        if (CD.getInstitucions().size()!=0){
            side.setAlignment(Pos.CENTER);
            VBox container = new VBox();
            VBox espaiRestriccions = new VBox();
            HBox menuAula = new HBox(container,espaiRestriccions);
            menuAula.setPadding(new Insets(40 , 40, 40, 40));
            menuAula.setSpacing(20);
            menuAula.setAlignment(Pos.CENTER);
            container.setSpacing(10);
            Label nom = new Label("Nom Aula");
            TextField nom_aula = new TextField();
            nom_aula.setMaxWidth(235);
            Label tipus = new Label("Tipus d'Aula:");
            TextField tipusAula = new TextField();
            tipusAula.setMaxWidth(235);
            Label restr = new Label("Restriccions");
            llistaR = new ChoiceBox();
            llistaR.setPrefWidth(200);
            Button add = new Button("Crear Nova Restricció");
            add.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    afegirRestriccions(espaiRestriccions);
                }
            });
            Button borrar = new Button("\uD83D\uDDD1");
            borrar.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    eliminarR();
                }
            });
            borrar.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    eliminarR();
                }
            });
            HBox HBR = new HBox(llistaR, borrar);
            HBR.setSpacing(10);
            Button complete = new Button("Guardar");
            Label EM = new Label("Emplena tots els camps correctament.");
            EM.setTextFill(javafx.scene.paint.Paint.valueOf("#ff0000"));
            EM.setVisible(false);
            container.getChildren().addAll(nom, nom_aula, tipus ,tipusAula, restr, HBR, add, complete,EM);
            side.getChildren().clear();
            side.getChildren().addAll(menuAula);
            complete.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (nom_aula.getText()!=""&& tipusAula.getText()!="" && !CD.getAules().contains(nom_aula.getText())){
                        CD.createAula(nom_aula.getText(),tipusAula.getText());
                        CD.addRestriccionsAula();
                        aulaList.setItems(FXCollections.observableArrayList(CD.getAules()));
                        aulaList.getSelectionModel().select(CD.getAules().size()-1);
                        guardar();
                    } else {
                        EM.setVisible(true);
                        //handle wrong input
                    }
                }
            });
        }else{
            side.getChildren().clear();
            Label EM = new Label("Sisplau crea primer un Pla d'Estudis");
            EM.setStyle("-fx-text-fill: #ff0000");
            side.setAlignment(Pos.CENTER);
            side.getChildren().add(EM);
        }

    }

    public void editAula(ActionEvent event){
        CD.clearRestriccions();
        if (CD.getAules().size()!=0){
            int idAula = aulaList.getSelectionModel().getSelectedIndex();
            CD.chooseAula(idAula);
            CD.addRestriccionsAulaToTemp();
            side.setAlignment(Pos.CENTER);
            VBox container = new VBox();
            VBox espaiRestriccions = new VBox();
            HBox menuAula = new HBox(container,espaiRestriccions);
            menuAula.setPadding(new Insets(40 , 40, 40, 40));
            menuAula.setSpacing(20);
            menuAula.setAlignment(Pos.CENTER);
            container.setSpacing(10);
            Label nom = new Label("Nom Aula");
            TextField nom_aula = new TextField();
            nom_aula.setMaxWidth(235);
            nom_aula.setText(CD.getAules().get(aulaList.getSelectionModel().getSelectedIndex()));
            Label tipus = new Label("Tipus d'Aula:");
            TextField tipusAula = new TextField();
            tipusAula.setText(CD.getTipus());
            tipusAula.setMaxWidth(235);
            Label restr = new Label("Restriccions");
            llistaR = new ChoiceBox();
            llistaR.setPrefWidth(200);
            if (CD.getRestriccionsAula().size()>0){
                llistaR.setItems(FXCollections.observableArrayList(CD.getRestriccionsAula()));
                llistaR.getSelectionModel().select(0);
            }
            Button add = new Button("Crear Nova Restricció");
            add.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    afegirRestriccions(espaiRestriccions);
                }
            });
            Button borrar = new Button("\uD83D\uDDD1");
            borrar.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    eliminarR();
                }
            });
            HBox HBR = new HBox(llistaR, borrar);
            HBR.setSpacing(10);
            Button complete = new Button("Guardar");
            Label EM = new Label("Emplena tots els camps correctament.");
            EM.setTextFill(javafx.scene.paint.Paint.valueOf("#ff0000"));
            EM.setVisible(false);
            container.getChildren().addAll(nom, nom_aula, tipus ,tipusAula, restr, HBR, add, complete,EM);
            side.getChildren().clear();
            side.getChildren().addAll(menuAula);
            complete.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (nom_aula.getText()!=""&& tipusAula.getText()!=""){
                        CD.chooseAula(idAula);
                        CD.removeAula();
                        CD.createAula(nom_aula.getText(),tipusAula.getText());
                        CD.addRestriccionsAula();
                        aulaList.setItems(FXCollections.observableArrayList(CD.getAules()));
                        aulaList.getSelectionModel().select(nom_aula.getText());
                        guardar();
                    } else {
                        EM.setVisible(true);
                        //handle wrong input
                    }
                }
            });
        }else{
            side.getChildren().clear();
            Label EM = new Label("Sisplau crea primer un Pla d'Estudis");
            EM.setStyle("-fx-text-fill: #ff0000");
            side.setAlignment(Pos.CENTER);
            side.getChildren().add(EM);
        }

    }

    public void setUpStage(){
        if (CD.getInstitucions().size()>0){
            instList.setItems(FXCollections.observableArrayList(CD.getInstitucions()));
            instList.getSelectionModel().select(0);
            actualitzaInst(new ActionEvent());
        }
    }

    public void actualitzaInst (ActionEvent event){
        CD.chooseInstitucio(instList.getSelectionModel().getSelectedItem());
        if (CD.getPlans().size()>0){
            plaList.setItems(FXCollections.observableArrayList(CD.getPlans()));
            plaList.getSelectionModel().select(CD.getPlans().size()-1);
            actualitzaPla(new ActionEvent());
        } else {
            plaList.setItems(FXCollections.emptyObservableList());
            assigList.setItems(FXCollections.emptyObservableList());
            grupList.setItems(FXCollections.emptyObservableList());
            aulaList.setItems(FXCollections.emptyObservableList());
        }
    }

    public void actualitzaPla(ActionEvent event) {
        CD.choosePla(plaList.getSelectionModel().getSelectedItem());
        if (CD.getAssigs().size()>0){
            assigList.setItems(FXCollections.observableArrayList(CD.getAssigs()));
            assigList.getSelectionModel().select(0);
            actualitzaAssig(new ActionEvent());
        } else{
            assigList.setItems(FXCollections.emptyObservableList());
            grupList.setItems(FXCollections.emptyObservableList());
        }
        if (CD.getAules().size()>0){
            aulaList.setItems(FXCollections.observableArrayList(CD.getAules()));
            aulaList.getSelectionModel().select(0);
        } else{
            aulaList.setItems(FXCollections.emptyObservableList());
        }
        CD.hasHorari();
    }

    public void actualitzaAula(ActionEvent event) {
        if (CD.getAules().size()>0 && aulaList.getSelectionModel().getSelectedIndex()>0) CD.chooseAula(aulaList.getSelectionModel().getSelectedIndex());
    }

    public void actualitzaAssig(ActionEvent event) {
        CD.chooseAssig(assigList.getSelectionModel().getSelectedItem());
        if (CD.getGroups().size()>0){
            grupList.setItems(FXCollections.observableArrayList(CD.getGroups()));
            grupList.getSelectionModel().select(0);
            actualitzaGrup(new ActionEvent());
        }else{
            grupList.setItems(FXCollections.emptyObservableList());
        }
    }

    public void actualitzaGrup(ActionEvent event) {
        CD.chooseGrup(grupList.getSelectionModel().getSelectedIndex());
    }

    public void creaGrups(ActionEvent actionEvent) {
        CD.clearRestriccions();
        if (CD.getAssigs().size()!=0){
            side.setAlignment(Pos.CENTER);
            VBox container = new VBox();
            HBox menuGrup = new HBox(container);
            menuGrup.setPadding(new Insets(40 , 40, 40, 40));
            menuGrup.setSpacing(20);
            menuGrup.setAlignment(Pos.CENTER);
            container.setSpacing(10);
            Label nom = new Label("Número de grups:");
            TextField ngrups = new TextField();
            ngrups.setMaxWidth(235);
            Label tipus = new Label("Número de subgrups:");
            TextField nsubgrups = new TextField();
            nsubgrups.setMaxWidth(235);
            Button complete = new Button("Guardar");
            Label EM = new Label("Emplena tots els camps correctament.");
            EM.setTextFill(javafx.scene.paint.Paint.valueOf("#ff0000"));
            EM.setVisible(false);
            container.getChildren().addAll(nom, ngrups, tipus ,nsubgrups, complete,EM);
            side.getChildren().clear();
            side.getChildren().addAll(menuGrup);
            complete.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (ngrups.getText().matches("-?\\d+")&& nsubgrups.getText().matches("-?\\d+")){
                        CD.addGroups(Integer.parseInt(ngrups.getText()), Integer.parseInt(nsubgrups.getText()));
                        CD.addRestriccionsGrup();
                        grupList.setItems(FXCollections.observableArrayList(CD.getGroups()));
                        grupList.getSelectionModel().select(CD.getGroups().size()-1);
                        guardar();
                    } else {
                        EM.setVisible(true);
                        //handle wrong input
                    }
                }
            });
        }else{
            side.getChildren().clear();
            Label EM = new Label("Sisplau crea primer alguna Assignatura");
            EM.setStyle("-fx-text-fill: #ff0000");
            side.setAlignment(Pos.CENTER);
            side.getChildren().add(EM);
        }

    }

    public void menuHorari(ActionEvent event) {
        side.getChildren().clear();
        side.setAlignment(Pos.CENTER);
        HBox dies = new HBox();
        dies.setAlignment(Pos.CENTER);
        ArrayList<CheckBox> ac = new ArrayList<CheckBox>();
        for (int i = 0; i < CD.getDies().size(); i++) {
            CheckBox dia = new CheckBox(CD.getDies().get(i));
            if (sdies.contains(String.valueOf(i)))dia.setSelected(true);
            dies.getChildren().add(dia);
            ac.add(dia);
        }
        dies.setSpacing(10);
        TextField de = new TextField();
        de.setText(String.valueOf(horaIni));
        de.setPrefWidth(30);
        TextField fins = new TextField();
        fins.setText(String.valueOf(horaFi));
        fins.setPrefWidth(30);
        HBox hores = new HBox(new Label("De: "),de,new Label("  Fins: "), fins);
        hores.setAlignment(Pos.CENTER);
        Button guardar = new Button("Guardar");
        Button generar = new Button("Generar Horari");
        HBox butons = new HBox(guardar,generar);
        butons.setSpacing(10);
        butons.setAlignment(Pos.CENTER);
        Label EM = new Label("Emplena els camps correctament");
        EM.setTextFill(Paint.valueOf("red"));
        EM.setVisible(false);
        VBox content = new VBox(new Label("Seleccioni els dies que hi haurà classes:"),dies
                , new Label("Seleccioni les hores en les que hi haurà classes:"),
                hores, butons,EM);
        content.setAlignment(Pos.CENTER);
        content.setSpacing(15);
        side.getChildren().add(content);
        guardar.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (de.getText().matches("([0-9])|(1[0-9])|(2[0-4])") && fins.getText().matches("([0-9])|(1[0-9])|(2[0-4])")
                && Integer.parseInt(de.getText())<Integer.parseInt(fins.getText())){
                    horaIni = Integer.parseInt(de.getText());
                    horaFi = Integer.parseInt(fins.getText());
                    sdies = "";
                    for (int i = 0; i < ac.size(); i++) {
                        if (ac.get(i).isSelected()) sdies += i;
                    }
                    guardar();
                }else{
                    EM.setVisible(true);
                }
            }
        });
        generar.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (de.getText().matches("([0-9])|(1[0-9])|(2[0-4])") && fins.getText().matches("([0-9])|(1[0-9])|(2[0-4])")
                        && Integer.parseInt(de.getText())<Integer.parseInt(fins.getText())){
                    horaIni = Integer.parseInt(de.getText());
                    horaFi = Integer.parseInt(fins.getText());
                    sdies = "";
                    for (int i = 0; i < ac.size(); i++) {
                        if (ac.get(i).isSelected()) sdies += i;
                    }
                    guardar();
                    generateHorari(new ActionEvent());

                }else{
                    EM.setVisible(true);
                }
            }
        });
    }

    public void guardar(){
        horariGenerable();
        Hyperlink tut = new Hyperlink("Clica aquí per anar al manual");
        tut.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                displayManual();
            }
        });
        side.getChildren().addAll(new Label("GUARDAT!"),tut);
    }

    private void horariGenerable(){
        ferHorari = true;
        BotoHorari.setText("Generar Horari");
        side.getChildren().clear();
        side.setAlignment(Pos.CENTER);
    }

    private VBox getApartat(String nom, String des, String adr){
        Label registrarUsuari = new Label(nom);
        registrarUsuari.setStyle("-fx-font-weight: bold; -fx-font-size: 20");
        Label explicaregistrarUsuari = new Label(des);
        MediaPlayer player = new MediaPlayer( new Media(getClass().getResource(adr).toExternalForm()));
        player.setAutoPlay(true);
        player.setMute(true);
        player.setCycleCount(MediaPlayer.INDEFINITE);
        MediaView mediaView = new MediaView(player);
        mediaView.setFitHeight(200 * 2.4);
        VBox RU = new VBox(registrarUsuari, explicaregistrarUsuari, mediaView);
        RU.setSpacing(5);
        return RU;
    }

    @FXML
    private void displayManual() {
        side.getChildren().clear();
        side.setPrefHeight(700);
        side.setPrefWidth(1000);
        ScrollPane sp = new ScrollPane();
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: "+colorFons);

        Label titolManual = new Label("MANUAL D'USUARI");
        titolManual.setStyle("-fx-font-size: 25;-fx-font-weight: bold");
        VBox contingut = new VBox(titolManual);
        contingut.getChildren().add(getApartat("Registrar Usuari", "Omplir casella User Name i Password amb el nom d'usuari i contrassenya desitjats, i clicar a Register per donar d'alta", "res/V1.mp4"));
        contingut.getChildren().add(getApartat("Login Usuari", "Omplir casella User Name i Password amb els nom i contrassenya de l'usuari que ja estigui donat d'alta", "res/V2.mp4"));
        contingut.getChildren().add(getApartat("Crear Institució", "Clicar al botó de + a la dreta del desplegable d'institucions per poder accedir a la pantalla de creació. Un cop dintre afegir el nom desitjat per a la institució i clicar a Guardar", "res/vid3.mp4"));
        contingut.getChildren().add(getApartat("Crear Pla d'Estudis", "Clicar al botó de + a la dreta del desplegable de plans d'estudi per poder accedir a la pantalla de creació. Un cop dintre afegir el nom desitjat del pla i clicar a Guardar", "res/vid4.mp4"));
        contingut.getChildren().add(getApartat("Gestió Assignatura", "Clicar al botó Nova Assignatura sota el desplegable d'assignatures per accedir a la pantalla de creació. Un cop dintre omplir els camps amb el nom de la assignatura i el seu tipus," + System.lineSeparator() + "seguit de les restriccions i els correquisitcs que desitgem. Si el que necessitem es modificar una assignatura existent només cal seleccionar-la del desplegable i clicar al botó ... ", "res/vid5.mp4"));
        contingut.getChildren().add(getApartat("Gestió Grup", "Clicar a Crear grups per accedir a la pantalla de creació. Un cop dintre introduïr el nombre de grups i de subgrups per grup que desitgem. Si desitgem afegir una restricció, clicar a Restriccions ." + System.lineSeparator() + "Per modificar un grup en concret, seleccionar-lo del desplegable", "res/vid6.mp4"));
        contingut.getChildren().add(getApartat("Gestió Aula", "Clicar a Nova Aula per accedir a la pantalla de creació. Un cop dintre introduïr el nom i el tipus d'aula que desitgem, seguit de les restriccions que volguem aplicar, i després cliquem a Guardar per conservar els canvis." + System.lineSeparator() + "En cas de voler modificar una aula seleccionada, clicar al botó ..." , "res/vid7.mp4"));
        contingut.getChildren().add(getApartat("Gestió Horari", "Clicar ... per accedir a la pantalla de configuració. Marquem els dies de la setmana que ens interesa que hi hagi classe i introduïm el marc horari que volguem." + System.lineSeparator() + "Un cop configurat el nostre horari podem guardar-lo clicant a Guardar o guardar-lo i generar-lo amb el botó Generar Horari." + System.lineSeparator()+ "Un cop es mostri l'horari per pantalla, podem anar veient com queda l'horari per cada una de les Aules que tenim utilitzant els botons de desplaçament entre aules situats a sobre l'horari.", "res/vid8.mp4"));
        Label tips = new Label("Tips and tricks");
        contingut.getChildren().add(tips);
        tips.setStyle("-fx-font-size: 20; -fx-font-style: italic");
        contingut.getChildren().add(new Label("✔ Per guardar les dades que has creat utilitza la creu de dalt a la dreta per tancar el programa."));
        contingut.getChildren().add(new Label("✔ Clica dos cops seguits perquè el programa es fiqui en pantalla completa."));
        contingut.getChildren().add(new Label("✔ Clica i manté apretat sobre qualsevol lloc (menys el manual) per arrastrar la finestra on et sembli. "));
        contingut.getChildren().add(new Label("✔ Clica sobre Veure Tot per visualitzar les Aules i les Assignatures que tens creades, amb totes les seves propietats."));
        Label color = new Label("✔ No estàs content amb el color del fons?    ⚠    FUNCIO EXPERIMENTAL    ⚠   ");
        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                Color c = colorPicker.getValue();
                colorFons = String.format( "#%02X%02X%02X",
                        (int)( c.getRed() * 255 ),
                        (int)( c.getGreen() * 255 ),
                        (int)( c.getBlue() * 255 ));
                if (colorFons.contains("123456")){
                    side.setStyle("-fx-background-image: url('horacio.jpg'); fx-background-repeat: repeat;");
                    sp.lookup(".viewport").setStyle("-fx-background-image: url('horacio.jpg'); fx-background-repeat: repeat;");
                }else{
                    side.setStyle("-fx-background-color: "+ colorFons);
                    sp.lookup(".viewport").setStyle("-fx-background-color: "+colorFons);
                    sp.setStyle("-fx-background-color: transparent;-fx-background: "+ colorFons);

                }
            }
        });
        contingut.getChildren().add(new HBox(color, colorPicker));
        contingut.setSpacing(30);
        contingut.setPadding(new Insets(40,40,40,40));
        sp.setContent(contingut);
        side.getChildren().add(sp);

    }

    public void addRestriccioGrup(ActionEvent event) {
        if(CD.getGroups().size()>0){
            Button borrar = new Button("\uD83D\uDDD1");
            borrar.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    eliminarR();
                }
            });
            Button add = new Button("Afegir Restricció");
            Button guardar = new Button("Guardar");
            llistaR = new ChoiceBox();
            HBox HBR = new HBox(llistaR, borrar);
            HBR.setAlignment(Pos.CENTER);
            HBR.setSpacing(10);
            VBox container = new VBox(new Label("Restriccions"), HBR, add, guardar);
            container.setSpacing(10);
            container.setAlignment(Pos.CENTER);
            VBox rest = new VBox();
            HBox cont = new HBox(container, rest);
            cont.setSpacing(50);
            cont.setAlignment(Pos.CENTER);
            side.getChildren().clear();
            side.setAlignment(Pos.CENTER);
            side.getChildren().add(cont);
            add.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    afegirRestriccions(rest);
                }
            });
            guardar.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    CD.addRestriccionsGrup();
                    guardar();
                }
            });

        }
    }

    public void borraAssig(ActionEvent actionEvent) {
        CD.removeAssig();
        assigList.setItems(FXCollections.observableArrayList(CD.getAssigs()));
        if (CD.getAssigs().size()>0) assigList.getSelectionModel().select(0);
        actualitzaAssig(new ActionEvent());
        horariGenerable();
    }


    public void borraAula(ActionEvent actionEvent) {
        CD.chooseAula(aulaList.getSelectionModel().getSelectedIndex());
        CD.removeAula();
        aulaList.setItems(FXCollections.observableArrayList(CD.getAules()));
        if (CD.getAules().size()>0) aulaList.getSelectionModel().select(0);
        actualitzaAula(new ActionEvent());
        horariGenerable();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location.equals(getClass().getClassLoader().getResource("Horari.fxml"))){
            setUpStage();
            if (CD.hasHorari()) {
                BotoHorari.setText("Veure Horari");
                horaIni = CD.getHoraIni();
                horaFi = CD.getHoraFi();
                sdies = CD.getDiesHorari();
                ferHorari = !CD.hasHorari();
            }
            else BotoHorari.setText("Generar Horari");
            displayManual();
        }
    }
}
