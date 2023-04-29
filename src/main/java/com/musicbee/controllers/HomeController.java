package com.musicbee.controllers;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import com.musicbee.entities.Playlist;
import com.musicbee.entities.Song;
import com.musicbee.utility.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    private final ArrayList<Song> allSongs      = new ArrayList<>();
    private final ArrayList<Song> filteredSongs = new ArrayList<>();
    ContextMenu          contextMenu = new ContextMenu();
    Menu                 child       = new Menu("Add song to playlist");
    ObservableList<Song> tableList   = FXCollections.observableArrayList();
    @FXML
    private TableView<Song>           table;
    @FXML
    private TableColumn<Song, String> Title;
    @FXML
    private TableColumn<Song, String> Artist;
    @FXML
    private TableColumn<Song, String> Album;
    @FXML
    private TableColumn<Song, String> Length;
    @FXML
    private JFXHamburger              myHamburger;
    @FXML
    private JFXDrawer                 drawer;
    @FXML
    private TextField                 searchBar;
    @FXML
    private MenuButton                menuButton;
    @FXML
    private VBox                      bottom;
    @FXML
    private ImageView                 profileIcon;
    private ControlPanel              controlPanel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contextMenu.getItems().clear();
        contextMenu.getItems().add(child);
        loadContextMenuPlaylists();

        menuButton.setText(Database.getCurrentUser().getUsername());
        if (Database.getCurrentUser().getImage() != null) {
            profileIcon.setImage(Database.getCurrentUser().getImage());
        }

        Tools.clipImageview(profileIcon, 25);

        prepareTableview();
        loadSideBar();
        setHamburger();
        controlPanel = loadControlPanel();
    }

    private void prepareTableview() {
        EventHandler<MouseEvent> onClick = this::clickItem;
        table.setRowFactory(param -> {
            TableRow<Song> row = new TableRow<>();
            row.setPrefHeight(40);
            row.setStyle("-fx-font-size: 15");
            row.setOnMouseClicked(onClick);
            return row;
        });

        Title.setCellValueFactory(new PropertyValueFactory<>(Song.NAME_ATTRIBUTE));
        Artist.setCellValueFactory(new PropertyValueFactory<>(Song.ARTIST_NAME_ATTRIBUTE));
        Album.setCellValueFactory(new PropertyValueFactory<>(Song.ALBUM_NAME_ATTRIBUTE));
        Length.setCellValueFactory(new PropertyValueFactory<>(Song.LENGTH_IN_STRING_ATTRIBUTE));

        TableColumn<Song, Integer> indexColumn = new TableColumn<>("#");
        indexColumn.setSortable(false);
        indexColumn.setResizable(false);
        indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(
                table.getItems().indexOf(column.getValue()) + 1
        ));
        table.getColumns().add(0, indexColumn);

        table.setItems(tableList);

        table.getFocusModel().focus(1);
    }

    private ControlPanel loadControlPanel() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FilePaths.CONTROL_PANEL));
        try {
            VBox vBox = fxmlLoader.load();
            bottom.getChildren().clear();
            bottom.getChildren().addAll(vBox.getChildren());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println(getClass().getName() + ": " + getClass().getEnclosingMethod());
        }

        return fxmlLoader.getController();
    }

    private void setHamburger() {
        HamburgerBasicCloseTransition transition = new HamburgerBasicCloseTransition(myHamburger);
        if (State.getBurgerState() == -1) {
            transition.setRate(-1);
            drawer.close();
        } else {
            transition.setRate(1);
            drawer.open();
        }
        addHamburgerEventHandler(transition);
    }

    private void addHamburgerEventHandler(HamburgerBasicCloseTransition transition) {
        myHamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            transition.setRate(transition.getRate() * -1);
            if (transition.getRate() == -1) {
                State.setBurgerState(-1);
            } else {
                State.setBurgerState(1);
            }
            transition.play();
            if (drawer.isOpened() || drawer.isOpening()) {
                drawer.close();
            } else drawer.open();
        });
    }

    private void loadSideBar() {
        try {
            VBox vbox = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FilePaths.SIDE_BAR)));
            drawer.setSidePane(vbox);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(getClass().getName() + ": " + getClass().getEnclosingMethod());
        }
    }

    @FXML
    private void clickItem(MouseEvent event) {
        contextMenu.hide();
        @SuppressWarnings("unchecked") TableRow<Song> row = (TableRow<Song>) event.getSource();
        if (event.getButton() == MouseButton.PRIMARY) {
            try {
                if (!row.isEmpty() && row.getItem() != null) {
                    Song song = tableList.get(row.getIndex());

                    Jukebox.setCurrentSongIndex(row.getIndex());
                    Jukebox.setNowPlaying(song);

                    Jukebox.setSong(song);
                    Jukebox.prepare();
                    Jukebox.play();

                    State.setLastSongID(song.getID());
                    Jukebox.setCurrentList(tableList);
                    controlPanel.update(song.getName(), song.getArtistName());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (event.getButton() == MouseButton.SECONDARY) {
            contextMenu.show(table, event.getScreenX(), event.getScreenY());
            child.getItems().forEach(menuItem -> menuItem.setOnAction(e -> {
                        try {
                            if (!row.isEmpty() && row.getItem() != null) {
                                Database.addSongToPlaylist(row.getItem().getID(), Integer.parseInt(menuItem.getId()));
                            }
                        } catch (SQLException ex) {
                            System.out.println(ex.getErrorCode());
                            System.out.println(getClass().getName() + ": " + getClass().getEnclosingMethod());
                        }
                    }
            ));
        }
    }

    private void loadContextMenuPlaylists() {
        ArrayList<Playlist> a = Database.getAllPlaylists();
        for (int i = 0; i < a.size(); i++) {
            MenuItem item = new MenuItem(a.get(i).getName());
            child.getItems().add(item);
            child.getItems().get(i).setId(String.valueOf(a.get(i).getID()));
        }
    }

    public void makeObservableList(ArrayList<Song> list) {
        allSongs.clear();
        filteredSongs.clear();
        tableList.clear();
        allSongs.addAll(list);
        filteredSongs.addAll(list);
        tableList.addAll(list);
    }

    @FXML
    private void onTypedSearchBar() {
        if (searchBar.getText().trim().isEmpty()) {
            tableList.clear();
            tableList.addAll(allSongs);
        } else {
            filteredSongs.clear();
            for (Song song : allSongs) {
                String substr = searchBar.getText().trim().toLowerCase();
                String name = song.getName().toLowerCase();
                if (name.contains(substr)) {
                    filteredSongs.add(song);
                }
            }
            tableList.clear();
            tableList.addAll(filteredSongs);
        }
    }

    @FXML
    private void onClickProfile(ActionEvent event) throws IOException {
        MenuItem menuItem = (MenuItem) event.getSource();
        Stage myStage = (Stage) menuItem.getParentPopup().getOwnerWindow();

        SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.PROFILE, FilePaths.STYLESHEET);
        sceneSwitcher.switchNow(myStage);
    }

    @FXML
    private void onClickLogOut(ActionEvent event) throws IOException, SQLException {
        MediaPlayer player = Jukebox.getMediaPlayer();

        if (player != null) {
            Jukebox.clear();
        }

        Database.savePlaybackPosition();
        Database.logOutCurrentUser();

        MenuItem menuItem = (MenuItem) event.getSource();
        Stage stage = (Stage) menuItem.getParentPopup().getOwnerWindow();

        SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.SIGN_IN, FilePaths.STYLESHEET);
        sceneSwitcher.switchNow(stage);
    }
}
