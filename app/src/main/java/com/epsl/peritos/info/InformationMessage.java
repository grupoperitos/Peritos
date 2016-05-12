package com.epsl.peritos.info;

/**
 * Created by Juan Carlos on 05/05/2016.
 */
public class InformationMessage {

    private int code = 0;
    private int type = 0;
    private int priority = 0; //Prioridad, por defecto 0 que seria la maxima prioridad, implica que un mensaje se mostrara mas a menudo que otros
    private int achievement = 1; //Puntos de logro que gana el usuario cuando ve el detalle del mensaje
    private String title = ""; //titulo generico a mostrar cuando sale el mensaje
    private String message = ""; //Mensaje corto, normalmente una frase
    private String commentary = ""; //Comentario sobre mensaje, un par se frases
    private String detail = ""; //mensaje extenso

    private boolean isCorrect = false;

    public InformationMessage(String message) {
        if (message != null) {
            String[] fields = null;

            fields = message.split("\t");
            if (fields.length == MessageFormat.MESSAGE_FIELDS) {
                try {
                    String temp = fields[MessageFormat.POS_TIPO];
                    int tempi = Integer.parseInt(temp);
                    this.setType(tempi);
                    this.setCode(Integer.parseInt(fields[MessageFormat.POS_CODIGO]));
                    this.setPriority(Integer.parseInt(fields[MessageFormat.POS_PRIORIDAD]));
                    this.setAchievement(Integer.parseInt(fields[MessageFormat.POS_LOGRO]));
                    this.setTitle(fields[MessageFormat.POS_TITULO]);
                    this.setMessage(fields[MessageFormat.POS_MENSAJE]);
                    this.setCommentary(fields[MessageFormat.POS_COMENTARIO]);
                    this.setDetail(fields[MessageFormat.POS_DETALLE]);
                    isCorrect = true;
                } catch (NumberFormatException ex) {
                }
            }
        }
    }

    public String toSting() {
        String result = "";
        result = getType() + "\t" + getCode() + "\t" + getPriority() +"\t" + getAchievement() + "\t" + getTitle() + "\t" + getMessage() + "\t" + getCommentary() + "\t" + getDetail();
        return result;
    }

    public String toSummarySting() {
        String result = "";
        result = " T=" + getType() + " C=" + getCode() + " P=" + getPriority() + " A=" + getAchievement() + "\n" + getTitle() + "\n" + getMessage() + "\n" + getCommentary() + "\n" + getDetail();

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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
}
