/* rather simplistic implementation of PGM reader/writer
 *
 * Paul Rosin, 2002
 *
 * update to use 2 input streams - avoiding deprecation errors
 * Matt Gee, 2004
 *
 * constructor added to allow image size to be specified
 * Graham Daniell, 2004
 */

import java.io.*;
import java.util.*;

public class Image
{
    public int [][] pixels;
    public int depth,width,height;

    public Image()
    {
        pixels = new int[1500][1500];
        depth = width = height = 0;
    }

    public Image(int inDepth, int inWidth, int inHeight)
    {
        pixels = new int[inWidth][inHeight];
        width = inWidth;
        height = inHeight;
        depth = inDepth;
    }

    public void ReadPGM(String fileName)
    {
        String line;
        StringTokenizer st;

        try {
            BufferedReader in =
              new BufferedReader(new InputStreamReader(
                new BufferedInputStream(
                  new FileInputStream(fileName))));

            DataInputStream in2 =
              new DataInputStream(
                new BufferedInputStream(
                  new FileInputStream(fileName)));

            // read PGM image header

            // skip comments
            line = in.readLine();
            in2.skip((line+"\n").getBytes().length);
            do {
                line = in.readLine();
                in2.skip((line+"\n").getBytes().length);
            } while (line.charAt(0) == '#');

            // the current line has dimensions
            st = new StringTokenizer(line);
            width = Integer.parseInt(st.nextToken());
            height = Integer.parseInt(st.nextToken());

            // next line has pixel depth
            line = in.readLine();
            in2.skip((line+"\n").getBytes().length);
            st = new StringTokenizer(line);
            depth = Integer.parseInt(st.nextToken());

            // read pixels now
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    pixels[x][y] = in2.readUnsignedByte();
            
            in.close();
            in2.close();
        } catch(ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: image in "+fileName+" too big");
        } catch(FileNotFoundException e) {
            System.out.println("Error: file "+fileName+" not found");
        } catch(IOException e) {
            System.out.println("Error: end of stream encountered when reading "+fileName);
        }
    }

    public void WritePGM(String fileName)
    {
        String line;
        StringTokenizer st;
        int i;

        try {
            DataOutputStream out =
              new DataOutputStream(
                new BufferedOutputStream(
                  new FileOutputStream(fileName)));

            out.writeBytes("P5\n");
            out.writeBytes("#created by Paul Rosin\n");
            out.writeBytes(width+" "+height+"\n255\n");

            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    out.writeByte((byte)pixels[x][y]);

            out.close();
        } catch(IOException e) {
            System.out.println("ERROR: cannot write output file");
        }
    }
}