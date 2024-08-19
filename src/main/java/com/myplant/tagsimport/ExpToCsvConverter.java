package com.myplant.tagsimport;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.myplant.tagsimport.entities.ModbusTag;

public class ExpToCsvConverter {

    private TagsReader expReader;
    private List<ModbusTag> tags = new ArrayList<>();
    private String outPath;

    public ExpToCsvConverter(String inPath, String outPath, int slaveModuleIndex) throws FileNotFoundException {

        expReader = new ExpFileTagsReader(inPath, slaveModuleIndex);
        this.outPath = outPath;

    }

    public void getTags() {
        ModbusTag currentTag = new ModbusTag();

        while (currentTag != null) {

            try {

                currentTag = expReader.getNextTag();
                if (currentTag != null) {
                    tags.add(currentTag);
                }
            } catch (TagParsingErrorException e) {

            }
        }

    }

    public void saveTags() throws IOException {
        String csvString = "";
        csvString=csvString+"Name;Region;Address;DataType;Access;ByteOrder;Comment\r\n";
        for (ModbusTag tag : tags) {
            String curTagString = tag.name + ";" + tag.region + ";" + tag.address + ";" +
                    tag.dataType + ";" + tag.access + ";" + tag.byteOrder + ";" + tag.comment;
            csvString = csvString + curTagString+"\r\n";
        }

        Path path = Paths.get(outPath);

        Files.writeString(path, csvString, StandardCharsets.UTF_8);

    }

}
