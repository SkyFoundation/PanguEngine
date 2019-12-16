package nullengine.enginemod.client.gui.game;

import nullengine.Platform;
import nullengine.client.game.GameClientStandalone;
import nullengine.client.gui.Scene;
import nullengine.client.gui.component.Button;
import nullengine.client.gui.component.Label;
import nullengine.client.gui.layout.FlowPane;
import nullengine.client.gui.layout.VBox;
import nullengine.client.gui.misc.Background;
import nullengine.client.gui.misc.Border;
import nullengine.client.gui.misc.Pos;
import nullengine.client.i18n.I18n;
import nullengine.client.rendering.font.Font;
import nullengine.enginemod.client.gui.GuiSettings;
import nullengine.game.GameData;
import nullengine.util.Color;
import nullengine.util.Files2;

import java.nio.file.Path;

public class GUIGameCreation extends FlowPane {

    public GUIGameCreation() {
        alignment().set(Pos.CENTER);

        VBox vBox = new VBox();
        vBox.spacing().set(5);
        vBox.alignment().setValue(Pos.HPos.CENTER);
        getChildren().add(vBox);
        this.background().setValue(new Background(Color.fromRGB(0xAAAAAA)));

        Label text = new Label();
        text.text().setValue(I18n.translate("engine.gui.game_creation.text.name"));
        text.font().setValue(new Font(Font.getDefaultFont(), 20));
        vBox.getChildren().add(text);

        Button buttonCreate = new Button("New Game");
        buttonCreate.border().setValue(new Border(Color.WHITE));
        buttonCreate.setOnMouseClicked(event -> {
            var engine = Platform.getEngineClient();
            engine.getRenderManager().getGuiManager().closeScreen();
            Path gameBasePath = engine.getRunPath().resolve("game");
            Files2.deleteDirectoryIfPresent(gameBasePath);
            engine.startGame(new GameClientStandalone(engine, gameBasePath, GameData.createFromCurrentEnvironment(gameBasePath, "default")));
        });
        vBox.getChildren().add(buttonCreate);

        Button buttonLoad = new Button("Load Game");
        buttonLoad.border().setValue(new Border(Color.WHITE));
        buttonLoad.setOnMouseClicked(event -> {
            var engine = Platform.getEngineClient();
            engine.getRenderManager().getGuiManager().closeScreen();
            Path gameBasePath = engine.getRunPath().resolve("game");
            engine.startGame(new GameClientStandalone(engine, gameBasePath, GameData.createFromGame(gameBasePath)));
        });
        vBox.getChildren().add(buttonLoad);

        var buttonSettings = new Button("Settings");
        buttonSettings.setOnMouseClicked(event ->
                Platform.getEngineClient().getRenderManager().getGuiManager().showScreen(new Scene(new GuiSettings())));
        vBox.getChildren().add(buttonSettings);

        Button buttonExit = new Button("Exit");
        buttonExit.setOnMouseClicked(event -> Platform.getEngine().terminate());
        vBox.getChildren().add(buttonExit);

        var butCS = new Button("MultiPlayer");
        butCS.setOnMouseClicked(e -> {
            Platform.getEngineClient().getRenderManager().getGuiManager().showScreen(new Scene(new GuiDirectConnectServer()));
        });
        vBox.getChildren().add(butCS);
    }


    @Override
    public void requireClose() {
        // Main Menu should not be required close
    }
}
