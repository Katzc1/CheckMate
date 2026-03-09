// Author: Alby Garcia, Camerun Katz, Kia
// Description: 

import javax.swing.JFrame;

public class CheckMate {
    public static void main(String[] args) {
        NetworkManager nm = new NetworkManager();
        new ConnectionUI(nm);
    }
}