package ru.falseresync.packbrew;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.falseresync.packbrew.model.VersionManifest;
import ru.falseresync.packbrew.model.VersionMeta;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

public class Main extends Application {
    public static void main(String[] args) throws IOException, InterruptedException {
//        launch(args);
        var client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build();
        var requestManifest = HttpRequest.newBuilder()
            .uri(URI.create("https://launchermeta.mojang.com/mc/game/version_manifest.json"))
            .header("Accept", "application/json")
            .build();
        var responseManifest = client.send(requestManifest, new JsonBodyHandler<>(VersionManifest.class));
        if (responseManifest.statusCode() == 200) {
            var requestMeta = HttpRequest.newBuilder()
                .uri(responseManifest.body().get().getVersions().get(0).getUrl())
                .header("Accept", "application/json")
                .build();
            var responseMeta = client.send(requestMeta, new JsonBodyHandler<>(VersionMeta.class));
            System.out.println(responseMeta.statusCode());
            System.out.println(responseMeta.body().get().arguments().get(VersionMeta.ArgumentsType.JVM).get(0).rules().get(0).os().name());
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("/view/main.fxml"));
//
//        var scene = new Scene(root);
//        scene.getStylesheets().add(getClass().getResource("/style/global.css").toExternalForm());
//
//        primaryStage.setTitle("JavaFX and Gradle");
//        primaryStage.setScene(scene);
//        primaryStage.show();
    }
}
