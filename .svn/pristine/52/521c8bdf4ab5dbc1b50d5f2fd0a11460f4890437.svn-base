/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alcatraz.client;

import alcatraza2.*;
import at.falb.games.alcatraz.api.IllegalMoveException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author alexa
 */
public interface superClientInt extends Remote {
    public void createGame (alcatraza2.Lobby Lobby, User Username) throws RemoteException;
    public void gameStarting(alcatraza2.Lobby Lobby, User Username) throws RemoteException;
    public void updateMove(Move mv) throws RemoteException, IllegalMoveException;
}
