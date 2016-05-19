package com.epsl.peritos.info;

/**
 * Esta interfaz mantendrá todas constantes que se refieran al formato de la información en los ficheros de texto que almacenan los mensajes de la aplicación
 * Created by Juan Carlos on 05/05/2016.
 */
public interface MessageFormat {
    public final int POS_TIPO        = 0; //Código único del mensaje
    public final int POS_CODIGO      = 1; //Código único del mensaje
    public final int POS_PRIORIDAD   = 2; //Prioridad, por defecto 0 que sería la máxima prioridad, implica que un mensaje se mostrará más a menudo que otros
    public final int POS_LOGRO       = 3; //Puntos de logro que gana el usuario cuando ve el detalle del mensaje
    public final int POS_TITULO      = 4; //título genérico a mostrar cuando sale el mensaje
    public final int POS_MENSAJE     = 5; //Mensaje corto, normalmente una frase
    public final int POS_COMENTARIO  = 6; //Comentario sobre mensaje, un par se frases
    public final int POS_DETALLE     = 7; //Texto extenso
    public final int MESSAGE_FIELDS  = 8;
}
