package example;

import file.BlockFile;
import java.io.IOException;
import statistics.DefaultStatisticCenter;

public class BlockFileExample {

    public static void main(String[] args) throws IOException {
        int pageSize = 4096;
        byte[] buffer = new byte[pageSize];

        BlockFile file = new BlockFile("database.bfl", pageSize, new DefaultStatisticCenter());
        file.open();

        if (file.length() == 0) {//if the file does not exist yet
            buffer[0] = 1;
            file.write(1, buffer);

            System.out.println("BlockFile size in bytes =" + file.length());
            System.out.println("BlockFile size in blocks =" + file.size());


            buffer[0] = 2;
            file.write(1, buffer); //replace data in page id = 1

            System.out.println("BlockFile size in bytes =" + file.length());
            System.out.println("BlockFile size in blocks =" + file.size());


            buffer[0] = 10;
            file.write(10, buffer); //jump several blocks and write, pageId=10

            System.out.println("BlockFile size in bytes =" + file.length());
            System.out.println("BlockFile size in blocks =" + file.size());
        }

        file.read(1, buffer);
        System.out.println("byte 0 at page 1=" + buffer[0]);

        file.read(2, buffer); //note that this page was not written!
        System.out.println("byte 0 at page 2=" + buffer[0]);

        file.read(10, buffer);
        System.out.println("byte 0 at page 10=" + buffer[0]);

        file.close();
       

        System.out.println(file.getStatisticCenter().status());
    }
}
