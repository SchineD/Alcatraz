/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alcatraza2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 *
 * @author alexa
 */
public class Lobby implements Serializable {

    private String LobbyName;
    private UUID LobbyID;
    private int MaxAmountOfPlayers;
    private ArrayList<User> UserInLobby;

    public void addUser(User user) throws UserExistsException {
        for (User addUser : UserInLobby) {
            if (addUser.getUserName().equals(user.getUserName())) {
                throw new UserExistsException("User gibts bereits");
            }
        }
        UserInLobby.add(user);
    }

    public void initUserInLobby() {
        this.UserInLobby = new ArrayList<User>();
    }

    public void removeUser(User user) {
        UserInLobby.remove(user);
    }

    public void setLobbyName(String name) {
        this.LobbyName = name;
    }

    public void setLobbyID(UUID id) {
        this.LobbyID = id;
    }

    public void setMaxPlayer(int amount) {
        this.MaxAmountOfPlayers = amount;
    }

    public String getLobbyName() {
        return this.LobbyName;
    }

    public UUID getLobbyID() {
        return this.LobbyID;
    }

    public int getMaxPlayer() {
        return this.MaxAmountOfPlayers;
    }

    public int getPlayerInLobby() {
        int i = UserInLobby.size();
        return i;
    }

    public ArrayList<User> getUserListInLobby() {
        return this.UserInLobby;
    }

    public String getPlayerNamesInLobby() {
        String names = LobbyName + ": ";

        for (User x: UserInLobby)
        {
            names = names + " " + x.getUserName();
        }
        
        return names;
    }

}
