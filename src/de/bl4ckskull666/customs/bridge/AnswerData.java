/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.bridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PapaHarni
 */
public class AnswerData extends Thread {
    private final Socket socket;
    private final int clientNumber;

    public AnswerData(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
    }
        
    @Override
    public void run() {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("connected");
                
            while (true) {
                String input = in.readLine();
                String[] args = input.split(" ");
                if(args[0].equalsIgnoreCase("set")) {
                    if(args.length < 4) {
                        
                    }
                        
                } else if(input.startsWith("spend")) {
                        
                } 
            }
                
        } catch (IOException ex) {
            Logger.getLogger(AnswerData.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(in != null)
                    in.close();
            } catch (IOException ex) { }
        }
    }    
}
