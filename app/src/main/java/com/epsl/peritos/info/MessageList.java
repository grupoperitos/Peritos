package com.epsl.peritos.info;

import java.util.ArrayList;

/**
 * Created by Juan Carlos on 08/05/2016.
 */

public class MessageList extends ArrayList<InformationMessage> {

    public int LAST_ID = 0;

    public InformationMessage getMessageById(int id) {
        InformationMessage result = null;

        if (id < this.size()) {
            result = this.get(id);
            LAST_ID = id;
        }

        return result;
    }

    public InformationMessage getNextMessage() {
        InformationMessage result = null;

        LAST_ID++;
        if (LAST_ID >= this.size()) {
            LAST_ID = 0;
        }

        result = this.get(LAST_ID);

        return result;
    }

    public InformationMessage getPrevMessage() {
        InformationMessage result = null;

        LAST_ID--;
        if (LAST_ID < 0) {
            LAST_ID = this.size()-1;
        }

        result = this.get(LAST_ID);

        return result;
    }

    public MessageList getMessagesByType(int type) {
        MessageList result = new MessageList();


        for (InformationMessage message : this) {
            if (message.getType() == type)
                result.add(message);
        }
        return result;
    }

}
