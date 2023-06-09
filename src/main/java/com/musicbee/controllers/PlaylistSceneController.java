package com.musicbee.controllers;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
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
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class PlaylistSceneController implements Initializable {

    private final ArrayList<Song> allSongs      = new ArrayList<>();
    private final ArrayList<Song> filteredSongs = new ArrayList<>();
    private final ContextMenu     contextMenu   = new ContextMenu();
    ObservableList<Song> tableList = FXCollections.observableArrayList();
    @FXML
    private BorderPane borderPane;

    @FXML
    private TableView<Song>           table = new TableView<>();
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
    private ControlPanel              controlPanel;
    @FXML
    private MenuButton                menuButton;
    @FXML
    private Label                     name;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        name.setText(State.getCurrentPlaylistName());
        MenuItem item1 = new MenuItem("Remove song from playlist");
        item1.setId("1");
        contextMenu.getItems().add(item1);

        loadMenuButton();
        prepareTableview();
        loadSideBar();
        setHamburger();
        controlPanel = loadControlPanel();
    }

    private ControlPanel loadControlPanel() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FilePaths.CONTROL_PANEL));
        try {
            Parent bottom = fxmlLoader.load();
            borderPane.setBottom(bottom);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fxmlLoader.getController();
    }

    private void setHamburger() {
        HamburgerBasicCloseTransition transition = new HamburgerBasicCloseTransition(myHamburger);

        if (State.getBurgerState() == -1) {
            transition.setRate(-1);
            drawer.toggle();
        } else {
            transition.setRate(1);
            drawer.toggle();
        }
        transition.play();
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
            throw new RuntimeException(e);
        }
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
    }

    @FXML
    public void clickItem(MouseEvent event) {
        contextMenu.hide();
        @SuppressWarnings("unchecked") TableRow<Song> row = (TableRow<Song>) event.getSource();
        if (event.getButton() == MouseButton.PRIMARY) {
            try {
                if (!row.isEmpty() && row.getItem() != null) {
                    Song song = tableList.get(row.getIndex());

                    Jukebox.setCurrentSongIndex(row.getIndex());
                    Jukebox.setSong(song);

                    Jukebox.setSong(song);
                    Jukebox.prepare();
                    Jukebox.play();

                    State.setLastSongID(song.getID());
                    Jukebox.setCurrentList(tableList, row.getIndex());

                    controlPanel.update(song.getName(), song.getArtistName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (event.getButton() == MouseButton.SECONDARY) {
            contextMenu.show(table, event.getScreenX(), event.getScreenY());
            System.out.println(contextMenu.getItems().get(0).getId());
            contextMenu.setOnAction(e -> {
                try {
                    if (!row.isEmpty() && row.getItem() != null) {
                        Database.deleteSongFromPlaylist(row.getItem().getID(), State.getCurrentPlaylistID());
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                makeObservableList(Database.getCurrentPlaylistSongs());
            });
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
    public void onClickProfile(ActionEvent event) throws IOException {
        MenuItem menuItem = (MenuItem) event.getSource();
        Stage stage = (Stage) menuItem.getParentPopup().getOwnerWindow();

        SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.PROFILE, FilePaths.STYLESHEET);
        sceneSwitcher.switchNow(stage);
    }

    public void onClickLogOut(ActionEvent event) throws IOException, SQLException {
        MediaPlayer player = Jukebox.getMediaPlayer();

        if (player != null) {
            Jukebox.clearMediaPlayer();
        }

        Database.savePlaybackPosition();
        Database.logOutCurrentUser();

        MenuItem menuItem = (MenuItem) event.getSource();
        Stage stage = (Stage) menuItem.getParentPopup().getOwnerWindow();

        SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.SIGN_IN, FilePaths.STYLESHEET);
        sceneSwitcher.switchNow(stage);
    }

    private void loadMenuButton() {
        try {
            MenuButton menuButton1 = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FilePaths.MENU_BUTTON)));
            menuButton.getItems().clear();
            menuButton.getItems().addAll(menuButton1.getItems());
            menuButton.setGraphic(menuButton1.getGraphic());
            menuButton.setTooltip(menuButton1.getTooltip());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}