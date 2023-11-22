package Object;

import java.io.Serializable;

public class Code implements Serializable{
    private String source;
    private String language;
    private String function;

    public Code() {
        this.source = "";
        this.language = "";
        this.function = "";
    }

    public Code(String source, String language, String function) {
        this.source = source;
        this.language = language;
        this.function = function;
    }

    public Code(String str) {
        str = str.substring(1, str.length() - 1);
        String[] delimeterArray = str.split(",", 3);
        for(int i=0;i<delimeterArray.length;i++){
            String[] keyValuePair=delimeterArray[i].split(":", 2);
            String key = keyValuePair[0];
            System.out.println(key);
            String value = keyValuePair[1];
            System.out.println(value);
            if("function".equals(key)){
                setFunction(value);
            }
            else if("language".equals(key)){
                setLanguage(value);
            }
            else if("source".equals(key)){
                setSource(value);
            }
        }
    }

    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "{function:"+this.getFunction()+",language:"+this.getLanguage()+",source:"+this.getSource()+"}";
    }
}
