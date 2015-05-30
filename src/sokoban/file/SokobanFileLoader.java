package sokoban.file;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import sokoban.ui.SokobanUI;

public class SokobanFileLoader {

    private SokobanUI ui;
    private String FilePath = "data/";

    public SokobanFileLoader(SokobanUI initUI) {
        ui = initUI;
    }

    public String[] loadStat() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("stat.txt"));
        String[] res = new String[7];
        int i = 0;
        String line = "";
        line = br.readLine();
        while (line != null) {
            res[i] = line;
            line = br.readLine();
            i++;
        }
        return res;
    }

    public void saveStat(int[] played, int[] wins, int[] loss, int[] fast) throws IOException {
        File writename = new File("stat.txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));
        for (int i = 0; i < 7; i++) {
            out.write("" + played[i]);
            out.write(" " + wins[i]);
            out.write(" " + loss[i]);
            out.write(" " + fast[i] + "\r\n");
        }
        out.flush();
        out.close();
    }

    public DataInputStream readLevelFile(String levelstate) {
        String fileName = FilePath + levelstate;
        File fileToOpen = new File(fileName);
        // LET'S USE A FAST LOADING TECHNIQUE. WE'LL LOAD ALL OF THE
        // BYTES AT ONCE INTO A BYTE ARRAY, AND THEN PICK THAT APART.
        // THIS IS FAST BECAUSE IT ONLY HAS TO DO FILE READING ONCE
        byte[] bytes = new byte[Long.valueOf(fileToOpen.length()).intValue()];
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        FileInputStream fis;
        try {
            fis = new FileInputStream(fileToOpen);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(bytes);
            bis.close();
            DataInputStream dis = new DataInputStream(bais);
            return dis;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
