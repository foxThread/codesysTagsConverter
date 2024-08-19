package com.myplant.tagsimport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myplant.tagsimport.entities.ModbusTag;

public class ExpFileTagsReader implements TagsReader {

    private String tagExpString;
    private int slaveModuleIndex;
    private Integer currentAddress = 0;

    private String content;
    private Pattern tagPattern;
    private Matcher tagMatcher;

    private RegsFinder slaveIndexFinder;

    private List<ModbusTag> tags=new ArrayList<>();
    


    public void writeContentToFile(String filePath){
        Path path=Paths.get(filePath);
        try{
            Files.writeString(path,this.content,StandardCharsets.UTF_8);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public ExpFileTagsReader(String path,int slaveModuleIndex) throws FileNotFoundException {

        
        this.slaveModuleIndex=slaveModuleIndex;
        Scanner scanner = new Scanner(new File(path));
        this.content = scanner.useDelimiter("\\A").next();
        scanner.close();

        tagExpString = "(?s)[^\\w]_CHANNEL[^\\w].*?_SECTION_NAME:\\s*'([\\w\\s]*)'.*?_INDEX_IN_PARENT:\\s*'[\\w\\s]*'.*?_SYMBOLIC_NAME:\\s*'([\\w\\s]*)'.*?_COMMENT:\\s*'([\\w\\s]*)'"+
        ".*?_CHANNEL_MODE:\\s*'[\\w\\s]*'.*?_IECADR:\\s*%([\\w\\.\\d]+)";

              
              


        tagPattern = Pattern.compile(tagExpString);
        tagMatcher = tagPattern.matcher(this.content);
        slaveIndexFinder=new RegsFinder("\\w+(\\d+)\\.");
       

    }

    @Override
    public ModbusTag getTagByName(String tagName) {

        return null;
    }

    private String normalizeTagType(String tagType) {
        switch (tagType) {
            case ("DWordOutput"):
                return "int32";
            case ("WordOutput"):
                return "int16";
            case ("FloatOutput"):
                return  "float";  
            default:
                return "";
        }

    }

    private int getSlaveAddress(String address) throws TagParsingErrorException {
        int slaveIndex;
        slaveIndexFinder.findFirst(address);
        if (slaveIndexFinder.findNext()) {

            try {
                slaveIndex = Integer.parseInt(slaveIndexFinder.getMatcher().group(1));
                return slaveIndex;

            } catch (IllegalStateException | IndexOutOfBoundsException | NumberFormatException e) {
                throw new TagParsingErrorException("Error parsing tag");
            }
        }
        throw new TagParsingErrorException("Error parsing tag");

    }

    private int getAddressOffset(String normalizedTagType) throws TagParsingErrorException {
        switch (normalizedTagType) {
            case ("int32"):
                return 2;
            case ("int16"):
                return 1;
            case("float"):
                return 2;    
            default:
                throw new TagParsingErrorException("Error parsing address of Tag");
        }

    }

    private ModbusTag normalizeTag(ModbusTag tag) throws TagParsingErrorException {

        tag.dataType = normalizeTagType(tag.dataType);
        tag.address = currentAddress.toString();
           
        currentAddress = currentAddress + getAddressOffset(tag.dataType);
        
       
        tag.name = tag.name;
        tag.access = "ReadOnly";
        tag.byteOrder = "10325476";
        tag.region = "HOLDING_REGISTERS";
        tag.comment = tag.comment;
               
        return tag;

    }

    @Override
    public  ModbusTag getNextTag() throws TagParsingErrorException {

        int slaveIndex;

        if (tagMatcher.find()) {

            ModbusTag tag = new ModbusTag();
            try {
                tag.dataType = tagMatcher.group(1);
                tag.name = tagMatcher.group(2);
                tag.address = tagMatcher.group(4);
                tag.comment = tagMatcher.group(3);

                 slaveIndex = getSlaveAddress(tag.address);
                 if(slaveIndex==slaveModuleIndex){
                    return normalizeTag(tag);
                 } else {
                    throw new TagParsingErrorException("Tag no need to parse because slaveID <> "+slaveModuleIndex);
                 }
                           
            } catch (IllegalStateException | IndexOutOfBoundsException e) {
                throw new TagParsingErrorException("Error parsing tag");

            }
        } else {
            return null;
        }

    }

}
