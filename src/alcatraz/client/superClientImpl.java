/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alcatraz.client;

import alcatraza2.*;
import alcatraz.server.ServerInt;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.UUID;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import at.falb.games.alcatraz.api.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
/**
 *
 * @author alexa
 */
public class superClientImpl extends Application implements superClientInt, MoveListener {

    final ToggleGroup toggle = new ToggleGroup();
    static ServerInt stub;
    public User SuperUser = new User();
    ListView<String> list = new ListView<>();
    ListView<String> playerlist = new ListView<>();
    ArrayList<alcatraza2.Lobby> lobbylist = new ArrayList();
    alcatraza2.Lobby Lobby = new alcatraza2.Lobby();

    private alcatraza2.Lobby finalLobby;
    private User finalUser;
    private Alcatraz alcatraz;

    public static void main(String[] args) throws RemoteException, NotBoundException, AlreadyBoundException {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws RemoteException, NotBoundException, AlreadyBoundException, IOException {
        BorderPane startPane = new BorderPane();
        Thread thread = new Thread() {
            public void run() {
                while (true) {
                    System.out.println("Check RMIIPs");
                    try {
                        register();
                        Thread.sleep(5000);
                    } catch (InterruptedException | NotBoundException | AlreadyBoundException | IOException ex) {
                        System.out.println("start: register() ");
                    }
                }
            }
        };
        thread.start();
        try {
            register();
        } catch (RemoteException | NotBoundException | AlreadyBoundException ex) {
            System.out.println("Client: exception while register()");
            ex.printStackTrace();
        }

        startPane.setLeft(createLeftPane());
        startPane.setBottom(createBottomPane());
        startPane.setCenter(createCenterPane());
        stage.setTitle("Erstelle dein Spiel!");
        stage.setScene(new Scene(startPane, 350, 350));
        stage.show();
    }

    private void register() throws RemoteException, NotBoundException, AlreadyBoundException, IOException {
        superClientInt player;
        player = new superClientImpl();
        Properties properties = readProperties();
        ArrayList<String> RMIIps = GetRMIIps(properties);
        SuperUser.setUserID(UUID.randomUUID());
        SuperUser.setClient((superClientInt) UnicastRemoteObject.exportObject(player, 0));
        
        for (String ipAdress : RMIIps) {
            System.out.println("IPAdresse:" + ipAdress);
            try {
                Registry reg = LocateRegistry.getRegistry(ipAdress);
                stub = (ServerInt) reg.lookup("Server");
                boolean registerPlayer = stub.registerPlayer(SuperUser.getUserID(), SuperUser.getClient());
                if (registerPlayer) {
                    //System.out.println("Player erfolgreich registriert.");
                    System.out.println("connected to RMI @ " + ipAdress);
                    break;
                } else {
                    System.out.println("Player NICHT registriert.");
                }
            } catch (NotBoundException | AlreadyBoundException | IOException ex) {
                System.out.println("Client: exception while register()");
            }
        }
    }

    private Pane createLeftPane() {
        final VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20, 10, 10, 10));

        RadioButton twoPlayers = new RadioButton("2");
        RadioButton threePlayers = new RadioButton("3");
        RadioButton fourPlayers = new RadioButton("4");

        twoPlayers.setSelected(true);
        twoPlayers.setUserData(2);
        threePlayers.setUserData(3);
        fourPlayers.setUserData(4);

        vbox.getChildren().addAll(twoPlayers, threePlayers, fourPlayers);
        twoPlayers.setToggleGroup(toggle);
        threePlayers.setToggleGroup(toggle);
        fourPlayers.setToggleGroup(toggle);

        return vbox;
    }

    private Pane createBottomPane() {
        final HBox hbox = new HBox(10);

        hbox.setPadding(new Insets(10, 10, 10, 10));
        Button join = new Button("Beitreten");
        Button exit = new Button("Verlassen");
        Button actualize = new Button("Lobbies?");

        hbox.getChildren().addAll(join, exit, actualize);

        join.setOnAction((ActionEvent e) -> {
            for (alcatraza2.Lobby ExistingLobby : lobbylist) {
                if (ExistingLobby.getLobbyName().equals(list.getSelectionModel().getSelectedItem())) {
                    if (ExistingLobby.getMaxPlayer() > ExistingLobby.getPlayerInLobby()) {
                        Lobby = ExistingLobby;
                        openUserReg(Lobby);
                        break;
                    } else {
                        System.out.println("Lobby ist voll");
                    }
                }
            }
        });

        exit.setOnAction((ActionEvent e) -> {
            try {
                boolean deregisterPlayer = stub.deregisterPlayer(SuperUser.getUserID());
                if (deregisterPlayer) {
                    System.out.println("Player erfolgreich abgemeldet.");
                } else {
                    System.out.println("Player NICHT abgemeldet.");
                }
            } catch (RemoteException | NotBoundException ex) {
                System.out.println("Client: exception while deregistering player");
                ex.printStackTrace();
            }
            Platform.exit();
        });
        actualize.setOnAction((event) -> {
            try {
                LobbiesPane();
            } catch (RemoteException ex) {
                System.out.println("Client: exception LobbiesPane");
            }
        });

        return hbox;
    }

    private void openUserReg(alcatraza2.Lobby lobby) {
        Stage stage = new Stage();
        final BorderPane borderPane = new BorderPane();

        GridPane gridPane = new GridPane();
        final TextField UserName = new TextField();
        Button CreateUser = new Button("Erstelle User");
        final Label label = new Label();

        borderPane.setCenter(gridPane);
        stage.setScene(new Scene(borderPane, 200, 100));
        stage.setTitle("Wähle Spielernamen");

        gridPane.setPadding(new Insets(20, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        GridPane.setConstraints(UserName, 0, 0);
        GridPane.setConstraints(CreateUser, 0, 1);
        GridPane.setConstraints(label, 1, 0);
        GridPane.setColumnSpan(label, 2);
        gridPane.getChildren().addAll(label, UserName, CreateUser);

        CreateUser.setOnAction((ActionEvent e) -> {
            try {
                SuperUser.setUserName(UserName.getText());
                SuperUser.setUserIDinGame(lobby.getPlayerInLobby());

                int joinLobby = stub.joinLobby(SuperUser, lobby);
                switch (joinLobby) {
                    case 0:
                        System.out.println("Spieler NICHT zur Lobby hinzugefügt.");
                        break;
                    case 1:
                        System.out.println("Spieler erfolgreich zur Lobby hinzugefügt.");
                        break;
                    case 2:
                        createGame(lobby, SuperUser);
                }
                openLobbyWindow(lobby);
                stage.close();
            } catch (RemoteException ex) {
                System.out.println("Client: Remoteexception while joining lobby");
                ex.printStackTrace();
            } catch (UserExistsException ex) {
                System.out.println("Wähle anderen Usernamen...");
                stage.close();
                openUserReg(lobby);
            }
        });
        stage.show();
    }

    private void openLobbyWindow(alcatraza2.Lobby lobby) {
        Stage stage = new Stage();
        final BorderPane borderPane = new BorderPane();

        stage.setTitle("Lobby");
        stage.setScene(new Scene(borderPane, 200, 200));

        borderPane.setCenter(createWaitingPane());
        borderPane.setBottom(createExitLobbyPane(lobby));

        stage.show();
    }

    private BorderPane createWaitingPane() {

        final BorderPane borderPane = new BorderPane();
        borderPane.setCenter(PlayersPane());

        return borderPane;
    }

    private Pane createExitLobbyPane(alcatraza2.Lobby lobby) {
        final HBox hbox = new HBox(10);

        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(10, 0, 10, 0));
        Button exitButton = new Button("Verlasse Lobby");
        Button gameButton = new Button("Erstelle Spiel");
        Button actualize = new Button("Spieler?");

        gameButton.setOnAction((ActionEvent e) -> {
            try {
                System.out.println("Superuser: " + SuperUser.getUserName());
                createGame(lobby, SuperUser);
            } catch (RemoteException ex) {
                System.out.println("Client: Remoteexception while creating a game");
                ex.printStackTrace();
            }
        });

        exitButton.setOnAction((ActionEvent e) -> {
            try {
                boolean leaveLobby = stub.leaveLobby(SuperUser, lobby);
                if (leaveLobby) {
                    System.out.println("User hat Lobby erfolgreich verlassen.");
                } else {
                    System.out.println("User hat Lobby NICHT verlassen.");
                }
            } catch (RemoteException ex) {
                System.out.println("Client: Remoteexception while leaving lobby");
                ex.printStackTrace();
            }
            
        });
        actualize.setOnAction((event) -> PlayersPane());
        hbox.getChildren().addAll(exitButton, gameButton, actualize);

        return hbox;
    }

    private BorderPane createCenterPane() throws RemoteException {
        final BorderPane borderPane = new BorderPane();

        borderPane.setTop(createNewLobbyPane());
        borderPane.setCenter(LobbiesPane());
        return borderPane;

    }

    private ListView LobbiesPane() throws RemoteException {
        ObservableList<String> items = FXCollections.observableArrayList();

        try {
            lobbylist = stub.sendlobby();
        } catch (RemoteException ex) {
            System.out.println("Client: Stub: exception while sendLobby()");
            //ex.printStackTrace();
        }

        for (alcatraza2.Lobby x : lobbylist) {
            items.add(x.getLobbyName());
            System.out.println(x.getLobbyName() + ": " + x.getUserListInLobby().size() + "/" + x.getMaxPlayer());
        }

        list.setItems(items);
        list.setMaxWidth(200);

        return list;
    }

    private ListView PlayersPane() {
        ObservableList<String> items = FXCollections.observableArrayList();

        try {
            lobbylist = stub.sendlobby();
        } catch (RemoteException ex) {
            System.out.println("Client: Stub: Remoteexception while sendLobby()");
            ex.printStackTrace();
        }

        for (alcatraza2.Lobby x : lobbylist) {
            items.add(x.getPlayerNamesInLobby());
        }

        playerlist.setItems(items);
        playerlist.setMaxWidth(200);

        return playerlist;
    }

    private GridPane createNewLobbyPane() {
        GridPane gridPane = new GridPane();
        final TextField LobbyName = new TextField();
        Button CreateLobby = new Button("Erstelle Lobby");

        gridPane.setPadding(new Insets(20, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(LobbyName, 0, 0);
        gridPane.add(CreateLobby, 1, 0);

        CreateLobby.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if ((LobbyName.getText() != null && !LobbyName.getText().isEmpty())) {
                    try {
                        Lobby = stub.createLobby(LobbyName.getText(), Integer.parseInt(toggle.getSelectedToggle().getUserData().toString()));
                        openUserReg(Lobby);
                    } catch (RemoteException ex) {
                        System.out.println("Client: Stub: Remoteexception while createLobby()");
                        ex.printStackTrace();
                    }
                    try {
                        LobbiesPane();
                    } catch (RemoteException ex) {
                        System.out.println("Client: Stub: Remoteexception while LobbiesPane()");
                    }
                }
            }
        });

        return gridPane;
    }

    private ArrayList<String> GetRMIIps(Properties properties) {
        ArrayList<String> RMIIps = new ArrayList<>();

        Enumeration keys = properties.keys();

        while (keys.hasMoreElements()) {
            String key = keys.nextElement().toString();

            if (key.contains("RMIIp")) {
                RMIIps.add(properties.getProperty(key));
            }
        }
        System.out.println("GetRMIIps: RMIIps: " + RMIIps);
        return RMIIps;
    }

    private Properties readProperties() throws FileNotFoundException, IOException {
        File file = new File("config.properties");

        Properties properties = null;

        try (FileInputStream fileInput = new FileInputStream(file)) {
            properties = new Properties();
            properties.load(fileInput);
        } catch (IOException ex) {
            System.out.println("Client: IOException while loading properties");
            ex.printStackTrace();
        }
        return properties;
    }

    @Override
    public void createGame(alcatraza2.Lobby lobby, User Username) throws RemoteException {
        try {
            lobbylist = stub.sendlobby();
        } catch (RemoteException ex) {
            System.out.println("Client: Stub: Remoteexception while sendLobby()");
            ex.printStackTrace();
        }

        for (alcatraza2.Lobby lob : lobbylist) {
            if (lob.getLobbyName().equals(lobby.getLobbyName())) {
                if (lob.getUserListInLobby().get(0).getUserName().equals(Username.getUserName())) {
                    //System.out.println("richtige Lobby gesendet:" + lob.getLobbyName() + "User:" + Username.getUserName());
                    //System.out.println("Spieleranzahl in Lobby:" + lob.getPlayerInLobby());
                }
                for (User u : lob.getUserListInLobby()) {
                    u.getClient().gameStarting(lob, u);
                }
            
            } else {
                System.out.println("Falsche Spieler in Lobby:" +lob.getLobbyName());
            }
            break;
        }
    }

    @Override
    public void gameStarting(alcatraza2.Lobby lobby, User Username) {
        this.setAlcatraz(new Alcatraz());
        this.setFinalLobby(lobby);
        this.setFinalUser(Username);

        //System.out.println("Spieleranzahl in Lobby:" + lobby.getPlayerInLobby());
        //System.out.println("Username in Lobby:" + lobby.getUserListInLobby().get(0).getUserName());

        getAlcatraz().init(lobby.getPlayerInLobby(), Username.getUserIDinGame());
        for (int i = 0; i < lobby.getPlayerInLobby(); i++) {
            getAlcatraz().getPlayer(i).setName(lobby.getUserListInLobby().get(i).getUserName());
        }

        getAlcatraz().showWindow();
        getAlcatraz().addMoveListener(this);
        getAlcatraz().start();
    }

    @Override
    public void moveDone(Player player, Prisoner prisoner, int roworcol, int row, int col) {
        Move mv = new Move(player, prisoner, roworcol, row, col);

        this.getFinalLobby().getUserListInLobby().forEach(u -> {
            if (u.getUserID().equals(this.getFinalUser().getUserID())) {
            } else {
                try {
                    u.getClient().updateMove(mv);
                } catch (RemoteException ex) {
                    System.out.println("Client: Remoteexception while updateMove()");
                    ex.printStackTrace();
                } catch (IllegalMoveException ex) {
                    System.out.println("Client: IllegalMoveException");
                }
            }
        });
    }

    @Override
    public void updateMove(Move mv) throws RemoteException, IllegalMoveException {
        getAlcatraz().doMove(mv.getPlayer(), mv.getPrisoner(), mv.getRowOrCol(), mv.getRow(), mv.getCol());
    }

    @Override
    public void gameWon(Player player) {
        System.out.println("Spieler " + player.getName() + "hat gewonnen.");
    }

    public void setFinalLobby(alcatraza2.Lobby lobby) {
        this.finalLobby = lobby;
    }

    public alcatraza2.Lobby getFinalLobby() {
        return this.finalLobby;
    }

    public void setFinalUser(User usr) {
        this.finalUser = usr;
    }

    public User getFinalUser() {
        return this.finalUser;
    }

    public void setAlcatraz(Alcatraz a2) {
        this.alcatraz = a2;
    }

    public Alcatraz getAlcatraz() {
        return this.alcatraz;
    }

}
