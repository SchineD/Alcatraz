/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alcatraza2;

import java.io.Serializable;
import java.util.UUID;
import alcatraz.client.superClientInt;

/**
 *
 * @author alexa
 */
public class User implements Serializable {

    private UUID UserID;
    private String UserName;
    private superClientInt Client;
    private int UserIDinGame;

    public void setUserID(UUID id) {
        this.UserID = id;
    }

    public void setUserName(String name) throws UserExistsException {
        this.UserName = name;
    }

    public void setClient(superClientInt client) {
        this.Client = client;
    }

    public void setUserIDinGame(int id) {
        this.UserIDinGame = id;
    }

    public UUID getUserID() {
        return this.UserID;
    }

    public String getUserName() {
        return this.UserName;
    }

    public superClientInt getClient() {
        return this.Client;
    }

    public int getUserIDinGame() {
        return this.UserIDinGame;
    }
}
