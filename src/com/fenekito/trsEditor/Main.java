package com.fenekito.trsEditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {

    public static JFrame frame;
    public static JScrollPane pane;
    public static Translation translation;
    public static JPanel panel;

    public static void main(String[] args) {
    	System.setProperty("file.encoding", "ISO-8859-1");
        frame = new JFrame("AGS Translation Editor");
        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout()); // Use BorderLayout for better placement

        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("AGS Translation Files", "trs");
        chooser.setFileFilter(filter);

        // Create a panel to hold the labels and text fields
        panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 5, 5));

        // Add the panel to a JScrollPane
        pane = new JScrollPane(panel);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Create a panel for the top row of labels ("Original Text" and "Translation")
        JPanel headerPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        JLabel originalLabelHeader = new JLabel("Original Text", JLabel.CENTER);
        JLabel translatedLabelHeader = new JLabel("Translation", JLabel.CENTER);
        headerPanel.add(originalLabelHeader);
        headerPanel.add(translatedLabelHeader);

        JButton openFileButton = new JButton("Open File");
        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = chooser.showOpenDialog(null); // Open file chooser dialog
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    System.out.println("Selected file: " + chooser.getSelectedFile().getPath());
                    try {
                        // Load the translation data from the selected file
                        translation = new Translation(chooser.getSelectedFile().getPath());

                        // Clear the panel before adding new lines (for repeated file openings)
                        panel.removeAll();

                        // Add original and translated lines side by side
                        for (int i = 0; i < translation.getOriginalLines().size(); i++) {
                        	int index = i;
                        	
                            JTextField originalField = new JTextField(translation.getOriginalLines().get(i));
                            originalField.setPreferredSize(new Dimension(150, 40)); // Set a smaller size
                            
                            originalField.getDocument().addDocumentListener(new DocumentListener() {
                                @Override
                                public void insertUpdate(DocumentEvent e) {
                                    updateTranslation(index, originalField.getText());
                                }

                                @Override
                                public void removeUpdate(DocumentEvent e) {
                                    updateTranslation(index, originalField.getText());
                                }

                                @Override
                                public void changedUpdate(DocumentEvent e) {
                                    updateTranslation(index, originalField.getText());
                                }

                                // Method to update the corresponding original translation line
                                private void updateTranslation(int index, String newText) {
                                    translation.getOriginalLines().set(index, newText);
                                }
                            });

                            JTextField translatedField = new JTextField(translation.getTranslatedLines().get(i));
                            translatedField.setPreferredSize(new Dimension(150, 40));
                            
                            translatedField.getDocument().addDocumentListener(new DocumentListener() {
                                @Override
                                public void insertUpdate(DocumentEvent e) {
                                    updateTranslation(index, translatedField.getText());
                                }

                                @Override
                                public void removeUpdate(DocumentEvent e) {
                                    updateTranslation(index, translatedField.getText());
                                }

                                @Override
                                public void changedUpdate(DocumentEvent e) {
                                    updateTranslation(index, translatedField.getText());
                                }

                                // Method to update the corresponding translated translation line
                                private void updateTranslation(int index, String newText) {
                                    translation.getTranslatedLines().set(index, newText);
                                }
                            });

                            panel.add(originalField);
                            panel.add(translatedField);
                        }

                        panel.revalidate();
                        panel.repaint();
                    } catch (IOException ee) {
                        JOptionPane.showMessageDialog(frame, ee.getMessage(), "File Opening Error",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JButton addTextButton = new JButton("Add Text");
        addTextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
                JTextField originalField = new JTextField("New Original Text");
                originalField.setPreferredSize(new Dimension(150, 40));

                JTextField translatedField = new JTextField("New Translated Text");
                translatedField.setPreferredSize(new Dimension(150, 40));

                panel.add(originalField);
                panel.add(translatedField);
                
                if (translation == null) {
                    /*We didn't load any file so there's no translation already in place
                     * So we're creating a new one.
                     */
                    if (translation == null) {
                    	translation = new Translation();
                    	//Increment the index by 2 to only go from original line to original line
                    	try {
                    		for(int i = 0; i < panel.getComponentCount(); i+=2) {
                        		translation.add(originalField.getText(), translatedField.getText());
                        	}	
                    	} catch (Exception ee) {
                    		JOptionPane.showMessageDialog(null, ee.getMessage() ,"Error", JOptionPane.ERROR_MESSAGE);
                    	}
                    }
            	}
                
                /*
                 * The code below is needed to correctly add and dynamically update the translation
                 * In case we have opened a file and are adding new lines.
                 */
                
                final int index = translation.getOriginalLines().size() - 1;

                originalField.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        updateOriginalLine();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        updateOriginalLine();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        updateOriginalLine();
                    }

                    private void updateOriginalLine() {
                        translation.getOriginalLines().set(index, originalField.getText());
                    }
                });

                translatedField.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        updateTranslatedLine();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        updateTranslatedLine();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        updateTranslatedLine();
                    }

                    private void updateTranslatedLine() {
                        translation.getTranslatedLines().set(index, translatedField.getText());
                    }
                });
                
                try {
					translation.add(originalField.getText(),translatedField.getText());
				} catch (Exception ee) {
					ee.printStackTrace();
				}

                panel.revalidate();
                panel.repaint();
            }
        });
        
        JButton removeTextButton = new JButton("Remove Last Line");
        removeTextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int componentCount = panel.getComponentCount();
                
                if (componentCount >= 2) {
                	
                    panel.remove(componentCount - 1);
                    panel.remove(componentCount - 2);
                    
                    translation.removeLast();

                    panel.revalidate();
                    panel.repaint();
                } else {
                    JOptionPane.showMessageDialog(removeTextButton, "No lines left to remove!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JButton saveFileButton = new JButton("Save File");
        saveFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check if there's no text lines
                if (panel.getComponentCount() == 0) {
                    JOptionPane.showMessageDialog(saveFileButton, "No Text available to save!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JFileChooser folderChooser = new JFileChooser();
                folderChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                folderChooser.setDialogTitle("Save Translation File");
                folderChooser.setFileFilter(filter);
                int returnValue = folderChooser.showSaveDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    String selectedFolderPath = folderChooser.getSelectedFile().getAbsolutePath();
                    try {
                        translation.saveFile(selectedFolderPath);

                        JOptionPane.showMessageDialog(saveFileButton, "File saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ee) {
                        JOptionPane.showMessageDialog(saveFileButton, "Error saving file: " + ee.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openFileButton);
        buttonPanel.add(addTextButton);
        buttonPanel.add(removeTextButton);
        buttonPanel.add(saveFileButton);

        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(pane, BorderLayout.CENTER);

        startAutoSave();
        frame.setVisible(true);
    }
    
    public static void startAutoSave() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (translation != null) {
                    String currentDirectory = System.getProperty("user.dir");
                    String autosaveFilePath = currentDirectory + File.separator + "autosave.trs";
                    try {
                        translation.saveFile(autosaveFilePath);
                    } catch (IOException e) {
                        System.err.println("Autosave failed: " + e.getMessage());
                        JOptionPane.showMessageDialog(null, 
                        		"Autosave Failed, Check if the current folder has write permissions", 
                        		"Error",
                        		JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }, 0, 60 * 1000);
    }
}
