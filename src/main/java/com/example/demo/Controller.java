package com.example.demo;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.Random;
import java.util.Scanner;
public class Controller {
    @FXML
    TextField flnme;
    @FXML
    TextField flnmd;
    @FXML
    TextField keyt;
    @FXML
    TextField flnmed;
    @FXML
    TextField flnmdd;
    @FXML
    ProgressBar progress;
    @FXML
    Button encryptBtn;
    @FXML
    Button decryptBtn;
    @FXML
    BorderPane borderPane;
    @FXML
    Label keyLabel;

    private void loadUI(String ui) throws IOException {
        Parent rootCenter;
        rootCenter = FXMLLoader.load(getClass().getResource(ui+".fxml"));
        borderPane.setCenter(rootCenter);
    }
    @FXML
    private void goToEncryption(MouseEvent event) throws IOException {
        loadUI("encryption");
    }
    @FXML
    private void goToHome(MouseEvent event) throws IOException {
        loadUI("home");
    }
    @FXML
    private void goToDecryption(MouseEvent event) throws IOException {
        loadUI("decryption");

    }


    public void fileExplorerOpenEncrypt(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select source file");
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            flnme.setText(selectedFile.getAbsolutePath());
        }
        else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("File explorer");
            alert.setHeaderText(null);
            alert.setContentText("Select a valid file");

            alert.showAndWait();
        }
    }
    public void fileExplorerOpenDecrypt(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select source file");
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            flnmd.setText(selectedFile.getAbsolutePath());
        }
        else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("File explorer");
            alert.setHeaderText(null);
            alert.setContentText("Select a valid file");

            alert.showAndWait();
        }
    }

    public void encryptFolderOpen(){
        DirectoryChooser directory = new DirectoryChooser();
        directory.setTitle("Select destination folder");
        File destinationFolder = directory.showDialog(null);

        if(destinationFolder != null){
            System.out.println(destinationFolder.getAbsolutePath()+"/");
            fileEncryption(destinationFolder.getAbsolutePath()+"/");
        }else{
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("File explorer");
            alert.setHeaderText(null);
            alert.setContentText("Select a valid destination");

            alert.showAndWait();
        }

    }
    public void decryptFolderOpen(){
        DirectoryChooser directory = new DirectoryChooser();
        directory.setTitle("Select destination folder");
        File destinationFolder = directory.showDialog(null);

        if(destinationFolder != null){
            fileDecryption(destinationFolder.getAbsolutePath()+"/");
        }else{

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("File explorer");
            alert.setHeaderText(null);
            alert.setContentText("Select a valid destination");

            alert.showAndWait();
        }

    }

    public void fileEncryption(String dest) {

        new Thread(()  -> {
            System.out.println("-----ENCRYPTION PROCESS-----");
            StringBuilder fileString = new StringBuilder();
            BufferedInputStream in;
            try {
                in = new BufferedInputStream(new FileInputStream(flnme.getText().trim()));
                for (int i; (i = in.read()) != -1;) {
                    String byteValue = "0000000" + Integer.toBinaryString(i);
                    byteValue = byteValue.substring(byteValue.length() - 8);
                    fileString.append(byteValue);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String binaryFileString = fileString.toString();
            Random generator = new Random();
            System.out.println("string length: "+binaryFileString.length());

            int key = generator.nextInt(binaryFileString.length());

            while(key>binaryFileString.length()/2 || key<binaryFileString.length()/3){
                if(key>binaryFileString.length()/2) {
                    key= key-binaryFileString.length()/2;}
                if(key<binaryFileString.length()/3) {
                    key= key+binaryFileString.length()/3;
                }
            }

            System.out.println("key: "+key);

            for(double i = 0; i<=256; i++) {
                binaryFileString = EncryptionDecryption.encryption(binaryFileString, key);
                final double step =i;
                Platform.runLater(() -> progress.setProgress(step/64));

            }

            int help = 1;
            StringBuilder xe = new StringBuilder();
            for (int b=1; b<=binaryFileString.length()/8;b++) {
                String s = "0000000" + Integer.parseInt(binaryFileString.substring(help-1, help+7));
                s = s.substring(s.length() - 8);
                xe.append(s).append(' ');
                help=help+8;
            }

            BufferedOutputStream out;
            try {
                out = new BufferedOutputStream(new FileOutputStream((dest+flnmed.getText()).trim()+".xe"));
                Scanner sc = new Scanner(xe.toString());
                while (sc.hasNextInt()) {
                    int b = sc.nextInt(2);
                    out.write(b);
                }
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> progress.setProgress(0));
            int keyf = key;
            Platform.runLater(() -> keyLabel.setText(Integer.toString(keyf)));

        }).start();

    }

    public void fileDecryption(String dest) {
        new Thread(()  -> {
            StringBuilder sb = new StringBuilder();
            String file =flnmd.getText().trim();
            System.out.println("-----DECRYPTION PROCESS-----");
            BufferedInputStream is;
            try {
                is = new BufferedInputStream(new FileInputStream(file));
                for (int i; (i = is.read()) != -1;) {
                    String s = "0000000" + Integer.toBinaryString(i);
                    s = s.substring(s.length() - 8);
                    sb.append(s).append(' ');
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            String a = sb.toString();
            a = a.replaceAll("\\s","");

            int key = Integer.parseInt(keyt.getText().trim());
            int help = 1;

            StringBuilder xe = new StringBuilder();
            for (int b=1; b<=a.length()/8;b++) {
                String s = "0000000" + Integer.parseInt(a.substring(help-1, help+7));
                s = s.substring(s.length() - 8);
                xe.append(s).append(' ');
                help=help+8;
            }

            System.out.println("encoded string: "+a);
            System.out.println("byte string: "+xe);

            for(int i = 256; i>=0; i--) {
                StringBuilder z=new StringBuilder(a);
                z.reverse().toString();
                a=z.toString();
                String str3 = a.substring(0,key);
                String str2 = a.substring(key, a.length()-key);
                String str = a.substring(a.length() - key);
                StringBuilder h = new StringBuilder(str);
                h.reverse().toString();
                str=h.toString();
                StringBuilder ed = new StringBuilder();
                ed.append(str2).append(str3).append(str);
                a = ed.toString();

                final double step =i;
                Platform.runLater(() -> progress.setProgress(step/64));
            }
            Platform.runLater(() -> progress.setProgress(0));
            System.out.println("decoded string: "+a);
            int helpr = 1;

            StringBuilder xer = new StringBuilder();
            for (int b=1; b<=a.length()/8;b++) {
                String s = "0000000" + Integer.parseInt(a.substring(helpr-1, helpr+7));
                s = s.substring(s.length() - 8);
                xer.append(s).append(' ');
                helpr=helpr+8;
            }
            System.out.println("byte string: "+xer);
            System.out.println();

            BufferedOutputStream outr;
            try {
                outr = new BufferedOutputStream(new FileOutputStream((dest+flnmdd.getText()).trim()));
                Scanner scr = new Scanner(xer.toString());
                while (scr.hasNextInt()) {
                    int b = scr.nextInt(2);
                    System.out.print(b);
                    outr.write(b);
                }
                outr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }


}
