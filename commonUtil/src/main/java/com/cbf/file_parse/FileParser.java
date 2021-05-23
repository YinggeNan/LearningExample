package com.cbf.file_parse;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author Sky
 * @version 1.0
 * @date 2021/5/21
 * @description
 */
public class FileParser<T> {
    HashMap<String, String> map;
    T parse(String path, cbfCommon.Util.fileparse.FileType fileType){
        switch (fileType){
            case YAML:
                break;
            case XML:
                break;
            case PROPERTIES:
                break;
            case JSON:
                break;
            default:
        }
        return null;
    }
    public static Properties parseResultProperties(String filePath, cbfCommon.Util.fileparse.FileType fileType){
        switch (fileType){
            case YAML:
                break;
            case XML:
                break;
            case PROPERTIES:
                Properties properties = new Properties();
                try {
                    InputStream is = FileParser.class.getClassLoader().getResourceAsStream(filePath);
                    assert is!=null;
                    properties.load(is);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return properties;
            case JSON:
                break;
            default:
        }
        return null;
    }
}
