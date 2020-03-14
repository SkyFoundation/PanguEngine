package engine.enginemod.client.gui.game;

import engine.Platform;
import engine.gui.Scene;
import engine.gui.control.Button;
import engine.gui.control.Label;
import engine.gui.control.TextField;
import engine.gui.layout.BorderPane;
import engine.gui.layout.HBox;
import engine.gui.layout.VBox;
import engine.gui.misc.Background;
import engine.gui.misc.Pos;
import engine.util.Color;

public class GuiDirectConnectServer extends BorderPane {
    public GuiDirectConnectServer(){
        var vmain = new VBox();
        vmain.alignment().setValue(Pos.HPos.CENTER);
        var vbox = new VBox();
        vmain.getChildren().add(vbox);
        setAlignment(vmain, Pos.CENTER);
        center().setValue(vmain);
        var label1 = new Label();
        label1.text().setValue("Connect to server");
        var lblAddress = new Label();
        lblAddress.text().setValue("Address");
        var txtboxAddress = new TextField();
        txtboxAddress.getSize().prefHeight().set(23.0f);
        txtboxAddress.getSize().prefWidth().set(200f);
        var hbox = new HBox();
        hbox.spacing().set(10f);
        var butConnect = new Button("Connect");
        butConnect.setOnMouseClicked(e -> {
            var fullAddress = txtboxAddress.text().get();
            var port = 18104;
            var colonIndex = fullAddress.lastIndexOf(":");
            if (colonIndex != -1) {
                try {
                    port = Integer.parseInt(fullAddress.substring(colonIndex + 1));
                } catch (NumberFormatException ex) {

                }
                fullAddress = fullAddress.substring(0, colonIndex);
            }
            Platform.getEngineClient().getGraphicsManager().getGUIManager().show(new Scene(new GuiConnectServer(fullAddress, port)));
        });
        var butBack = new Button("Back");
        butBack.setOnMouseClicked(e -> {
            var guiManager = Platform.getEngineClient().getGraphicsManager().getGUIManager();
            guiManager.showLast();
        });
        hbox.getChildren().addAll(butConnect, butBack);
        vbox.getChildren().addAll(label1, lblAddress, txtboxAddress, hbox);
        background().setValue(Background.fromColor(Color.fromRGB(0x7f7f7f)));
    }
}
