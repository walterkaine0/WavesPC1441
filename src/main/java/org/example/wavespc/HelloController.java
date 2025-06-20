package org.example.wavespc;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.List;

public class HelloController {
    @FXML private TableView<Song> tableView;
    @FXML private TableColumn<Song, String> titleCol;
    @FXML private TableColumn<Song, String> artistCol;
    @FXML private Button playButton;
    @FXML private Button pauseButton;
    @FXML private Label currentSongLabel;

    private SupabaseClient supabaseClient;
    private MediaPlayer mediaPlayer;

    @FXML
    public void initialize() {
        supabaseClient = new SupabaseClient("https://tiwobocbcpqjzcqhektj.supabase.co", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRpd29ib2NiY3BxanpjcWhla3RqIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1MDE1MDkyOSwiZXhwIjoyMDY1NzI2OTI5fQ.xsVC8XRe_dQ5PA8QyESEj3QaBAY3f-PEL1SAPbiaFZY");

        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        artistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        loadSongs();

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                playSong(newSelection);
            }
        });

        playButton.setOnAction(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.play();
            }
        });
        pauseButton.setOnAction(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        });
    }

    private void loadSongs() {
        Task<ObservableList<Song>> fetchSongsTask = new Task<>() {
            @Override
            protected ObservableList<Song> call() throws Exception {
                List<Song> songs = supabaseClient.getSongs();
                return FXCollections.observableArrayList(songs);
            }
        };
        fetchSongsTask.setOnSucceeded(event -> tableView.setItems(fetchSongsTask.getValue()));
        fetchSongsTask.setOnFailed(event -> System.err.println("Ошибка: " + fetchSongsTask.getException()));
        new Thread(fetchSongsTask).start();
    }

    private void playSong(Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        try {
            Media media = new Media(song.getUrl());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnError(() -> {
                System.err.println("Ошибка MediaPlayer: " + mediaPlayer.getError().getMessage());
            });
            mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.stop());
            mediaPlayer.play();
            currentSongLabel.setText("Играет: " + song.getTitle());
        } catch (Exception e) {
            System.err.println("Ошибка при создании Media: " + e.getMessage());
            currentSongLabel.setText("Ошибка воспроизведения: " + song.getTitle());
        }
    }
}