package com.myplant.tagsimport;

public class TagParsingErrorException extends Exception {
    public TagParsingErrorException(String msg){
        super(msg);
        System.out.println("Tag exception:"+msg);
    }
    
}
