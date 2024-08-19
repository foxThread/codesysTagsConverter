package com.myplant.tagsimport;

import com.myplant.tagsimport.entities.ModbusTag;

public interface TagsReader {

    public ModbusTag getTagByName(String tagName);
    public ModbusTag getNextTag() throws TagParsingErrorException;

}
