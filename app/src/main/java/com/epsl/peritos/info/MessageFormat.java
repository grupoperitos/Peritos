package com.epsl.peritos.info;

/**
 * Esta interfaz mantendr� todas constantes que se refieran al formato de la informaci�n en los ficheros de texto que almacenan los mensajes de la aplicaci�n
 * Created by Juan Carlos on 05/05/2016.
 */
public interface MessageFormat {
    public final int POS_TIPO        = 0; //C�digo �nico del mensaje
    public final int POS_CODIGO      = 1; //C�digo �nico del mensaje
    public final int POS_PRIORIDAD   = 2; //Prioridad, por defecto 0 que ser�a la m�xima prioridad, implica que un mensaje se mostrar� m�s a menudo que otros
    public final int POS_LOGRO       = 3; //Puntos de logro que gana el usuario cuando ve el detalle del mensaje
    public final int POS_TITULO      = 4; //t�tulo gen�rico a mostrar cuando sale el mensaje
    public final int POS_MENSAJE     = 5; //Mensaje corto, normalmente una frase
    public final int POS_COMENTARIO  = 6; //Comentario sobre mensaje, un par se frases
    public final int POS_DETALLE     = 7; //Texto extenso
    public final int MESSAGE_FIELDS  = 8;
}
