package com.epsl.peritos.info;

/**
 * Created by Juan Carlos on 05/05/2016.
 */
public class InformationMessage {

    private int code = 0;
    private int type = 0;
    private int subtype = 0; //Prioridad, por defecto 0 que seria la maxima prioridad, implica que un mensaje se mostrara mas a menudo que otros
    private int achievement = 1; //Puntos de logro que gana el usuario cuando ve el detalle del mensaje
    private String title = "-"; //titulo generico a mostrar cuando sale el mensaje
    private String caption = "-"; //Mensaje corto, normalmente una frase
    private String commentary = "-"; //Comentario sobre mensaje, un par se frases
    private String detail = "-"; //mensaje extenso
    private String url = "-"; //url del recurso externo

    private boolean isCorrect = false;

    public InformationMessage(String message) {
        if (message != null) {
            String[] fields = null;

            fields = message.split("\t");
            if (fields.length == MessageFormat.MESSAGE_FIELDS) {
                try {
                    this.setType(Integer.parseInt(fields[MessageFormat.POS_TIPO]));
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
                try {
                    this.setCode(Integer.parseInt(fields[MessageFormat.POS_CODIGO]));
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
                try {
                    this.setSubtype(Integer.parseInt(fields[MessageFormat.POS_PRIORIDAD]));
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
                try {
                    this.setAchievement(Integer.parseInt(fields[MessageFormat.POS_LOGRO]));
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
                this.setTitle(fields[MessageFormat.POS_TITULO]);
                this.setCaption(fields[MessageFormat.POS_MENSAJE]);
                this.setCommentary(fields[MessageFormat.POS_COMENTARIO]);
                this.setDetail(fields[MessageFormat.POS_DETALLE]);
                this.setURL(fields[MessageFormat.POS_URL]);
                isCorrect = true;
        }
    }

}

    @Override
    public String toString() {
        String result = "";
        result = getType() + "\t" + getCode() + "\t" + getSubtype() + "\t" + getAchievement() + "\t" + getTitle() + "\t" + getCaption() + "\t" + getCommentary() + "\t" + getDetail()+"\t" + getURL();
        return result;
    }

    public String toSummarySting() {
        String result = "";
        result = " T=" + getType() + " C=" + getCode() + " P=" + getSubtype() + " A=" + getAchievement() + "\n" + getTitle() + "\n" + getCaption() + "\n" + getCommentary() + "\n" + getDetail()+"\n" + getURL();

        return result;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSubtype() {
        return subtype;
    }

    public void setSubtype(int subtype) {
        this.subtype = subtype;
    }

    public int getAchievement() {
        return achievement;
    }

    public void setAchievement(int achievement) {
        this.achievement = achievement;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setURL(String url) {
        this.url = url;
    }
    public String getURL() {
        return this.url;
    }

}
