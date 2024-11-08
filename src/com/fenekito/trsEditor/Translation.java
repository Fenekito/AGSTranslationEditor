package com.fenekito.trsEditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Translation {
    private int gameID;
    private String gameName;

    private final String TRS_ID_TAG = "//#GameId=";
    private final String TRS_GAMENAME_TAG = "//#GameName=";
    private final String TRS_ENCODING_TAG = "//#Encoding=";

    private ArrayList<String> originalLines;
    private ArrayList<String> translatedLines;
    private int length = 0;

    public Translation(String filepath) throws IOException {
        originalLines = new ArrayList<>();
        translatedLines = new ArrayList<>();
        readFile(filepath);
        length = originalLines.size();
    }
    
    public Translation() {
    	originalLines = new ArrayList<>();
    	translatedLines = new ArrayList<>();
    }

    private void readFile(String filepath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            boolean isOriginal = true; // To alternate between original and translated lines
            while ((line = reader.readLine()) != null) {
                // Check for tags first
                if (line.startsWith(TRS_ID_TAG)) {
                    gameID = Integer.parseInt(line.substring(TRS_ID_TAG.length()).trim());
                } else if (line.startsWith(TRS_GAMENAME_TAG)) {
                    gameName = line.substring(TRS_GAMENAME_TAG.length()).trim();
                } else if (line.startsWith(TRS_ENCODING_TAG)) {
                    // Handle encoding if necessary (currently skipped)
                    continue; // Skip this line
                } else if (line.startsWith("//")) {
                    // Ignore any comments after processing the tags
                    continue;
                } else if (!line.isEmpty()) {
                    // Add original and translated lines alternately
                    if (isOriginal) {
                        originalLines.add(line);
                    } else {
                        translatedLines.add(line);
                    }
                    // Toggle between original and translated lines
                    isOriginal = !isOriginal;
                }
            }
        }
    }
    
    public void saveFile(String folderPath) throws IOException {
    	if (!folderPath.endsWith(".trs")) {
    		folderPath+=".trs";
    	}
    	File file = new File(folderPath);
    	if (file.createNewFile()) {
    		System.out.println("No file with this name exists, so we're good to go");
    	} else {
    		System.out.println("There's already a file like this so we're going to overwrite it");
    	}
    	//German special characters don't save properly without this encoding
    	OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.ISO_8859_1);
    	writer.write(TRS_ID_TAG+gameID+"\n");
    	writer.write(TRS_GAMENAME_TAG+gameName+"\n");
    	writer.write(TRS_ENCODING_TAG+"ASCII\n");
    	for (int i = 0; i < length; i++) {
    		writer.write(originalLines.get(i)+"\n");
    		writer.write(translatedLines.get(i)+"\n");
    	}
    	writer.flush();
    	writer.close();
    }
    
    public ArrayList<String> getOriginalLines() {
        return originalLines;
    }

    public ArrayList<String> getTranslatedLines() {
        return translatedLines;
    }

    public void add(String original, String translation) throws Exception {
        if (original.isEmpty() || translation.isEmpty()) {
            throw new Exception("Empty String");
        }

        originalLines.add(original);
        translatedLines.add(translation);
        length++;
    }
}
