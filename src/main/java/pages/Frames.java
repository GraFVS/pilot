package pages;

public enum Frames {
    TOP_WINDOW(null),
    STANDART_ACTIVE_FRAME("//iframe/ancestor::div[contains(@style,'display: block;')]//iframe");

    final String xpath;

    Frames(String xpath){
        this.xpath = xpath;
    }

    public String getXpath(){
        return xpath;
    }
}
