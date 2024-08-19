package com.myplant.tagsimport.gui;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import com.myplant.tagsimport.ExpToCsvConverter;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainWindow extends JFrame {
    private JFileChooser expFileChooser = new JFileChooser();
    private JButton expFileChooserButton = new JButton("Выбрать EXP файл");
    private JButton convertButton = new JButton("Преобразовать в CSV");

    private JTextField fileName = new JTextField("                             ");

    private JLabel moduleLabel = new JLabel("Номер slave модуля для конвертирования:");
    private JTextField slaveModuleNumber = new JTextField("             ");
    private JPanel slaveModulePanel = new JPanel();
    private JPanel fileNamePanel=new JPanel();

    private File selectedFile = null;
    ExpToCsvConverter converter = null;
    JPanel myPanel;


    
    

    public MainWindow() {

       super("Конвертер Тегов EXP to CSV");
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       setLayout(new BorderLayout());

       myPanel=new JPanel();
       
       myPanel.setLayout(new GridLayout(0,1) );
    
       //панель с кнопками
        JPanel actionPanel=new JPanel();
        actionPanel.setLayout(new FlowLayout());
        actionPanel.add(expFileChooserButton);
        actionPanel.add(convertButton);

        
        //панель параметров
        JPanel parametersPanel=new JPanel();
        parametersPanel.setLayout(new GridLayout(0,1));
      

        slaveModulePanel.setLayout(new FlowLayout());
        slaveModulePanel.add(moduleLabel);
        slaveModulePanel.add(slaveModuleNumber);

        fileNamePanel.setLayout(new FlowLayout());
        fileNamePanel.add(new JLabel("Выбранный файл:"));
        fileNamePanel.add(fileName);

        parametersPanel.add(slaveModulePanel);
        parametersPanel.add(fileNamePanel);

       

        myPanel.add(actionPanel);
        myPanel.add(parametersPanel);

        add(myPanel,BorderLayout.NORTH);
     

        expFileChooserButton.addActionListener(e -> {
            int result = expFileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = expFileChooser.getSelectedFile();
                fileName.setText(selectedFile.getName());

               

            }

        });

        convertButton.addActionListener(e -> {
            if (selectedFile != null) {
                 try {
                    converter = new ExpToCsvConverter(selectedFile.getPath(), selectedFile.getParent() + "\\" +
                            selectedFile.getName() + "_"+slaveModuleNumber.getText().trim()+".csv", Integer.parseInt(slaveModuleNumber.getText().trim()));
                    converter.getTags();
                    converter.saveTags();
                   
                } catch (NumberFormatException e1) {

                    e1.printStackTrace();
                } catch (FileNotFoundException e1) {

                    e1.printStackTrace();
                } catch (IOException e1) {
                   
                }
              
            }
        });

        
    }

}
