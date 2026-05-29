package com.beatify.view.controller;

import com.beatify.view.util.NavegacionUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PlayerController {

    @FXML private Button btnVolver;

    @FXML
    private void initialize() {
        System.out.println("PlayerController iniciado correctamente");
    }

    @FXML
    private void onVolver() {
        NavegacionUtil.cambiarA("/view/home.fxml", btnVolver);
    }
}