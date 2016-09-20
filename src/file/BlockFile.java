package file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import statistics.StatisticCenter;

/**
 * This class has the basic functions to write and read blocks of data in a
 * file.
 *
 * @author João B. Rocha-Junior
 */
public class BlockFile {

    private final int pageSize;
    private final String fileName;
    private RandomAccessFile raf;
    private final StatisticCenter statisticCenter;  

    /**
     * Creates a BlockFile.
     *
     * @param fileName the name of the file
     * @param pageSize the size of the page  for this file. All pages in
     * the will have the same size. The pageSize must be a value above zero.
     */
    public BlockFile(String fileName, int pageSize) {
        this(fileName, pageSize, null);
    }

    /**
     * Creates a BlockFile.
     * @param fileName the name of the file
     * @param pageSize the size of the page  for this file. All pages in
     * the will have the same size. The pageSize must be a value above zero.
     * @param statisticCenter  the class that will collect statistic information 
     * about the file usage
     */
    public BlockFile(String fileName, int pageSize, StatisticCenter statisticCenter) {
        this.fileName = fileName;
        this.pageSize = pageSize;
        this.statisticCenter = statisticCenter;        
    }

    /**
     * This method returns the file name.
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * This method returns the statistic center of the given file
     * @return the statistic center of the given file
     */
    public StatisticCenter getStatisticCenter(){
        return statisticCenter;
    }

    /**
     * Open the file. This method should be called before using the operation
     * over the file.
     *
     * @throws IOException if there is an I/O problem during the operation
     */
    public void open() throws IOException {
        this.raf = new RandomAccessFile(fileName, "rwd");
    }

    /**
     * Read the page stored in the given id.
     *
     * @param pageId the id of the page to be read. The pageId is a above zero.
     * @param buffer the place where the bytes of the page will be stored
     * @throws IOException if there is an I/O problem during the operation
     */
    public void read(long pageId, byte[] buffer) throws IOException {
        //the page is not in the rage of pages written in the file
        if (pageId > size()) {
            throw new IOException("Page not found. The pageId is beyond the pages written in this file!");
        }
        if (buffer.length < getBlockSize()) {
            throw new IOException("The buffer size is smaller than the page size!");
        }

        long time = System.currentTimeMillis();

        raf.seek(getFilePosition(pageId));
        raf.readFully(buffer, 0, getBlockSize());

        if (statisticCenter != null) {
            statisticCenter.getCount("blocksRead").inc();
            statisticCenter.getTally("readTime").update(System.currentTimeMillis() - time);
        }
    }

    /**
     * Write the page in the in the given page id
     *
     * @param pageId the id where the page should be stored. The pageId is a
     * above zero.
     * @param buffer the bytes of the page to be stored
     * @throws IOException if there is an I/O problem during the operation
     */
    public void write(long pageId, byte[] buffer) throws IOException {
        if (buffer.length < getBlockSize()) {
            throw new IOException("The buffer size is smaller than the page size!");
        }
        long time = System.currentTimeMillis();

        raf.seek(getFilePosition(pageId));
        raf.write(buffer, 0, getBlockSize());

        if (statisticCenter != null) {
            statisticCenter.getCount("blocksWritten").inc();
            statisticCenter.getTally("writeTime").update(System.currentTimeMillis() - time);
        }
    }

    /**
     * Close the file. This method should be called when the BlockFile is not
     * going to be used any more.
     *
     * @throws IOException if there is an I/O problem during the operation
     */
    public void close() throws IOException {
        if (raf != null) {
            raf.close();
        }
    }

    /**
     * This method returns the number of blocks (pages) stored in the file.
     *
     * @return the number of blocks stored in the file
     * @throws IOException if there is an I/O problem during the operation
     */
    public long size() throws IOException {
        return length() / getBlockSize();
    }

    /**
     * This method returns the page size. The size of the page is fixed for any
     * page.
     *
     * @return the size of the page.
     */
    public int getBlockSize() {
        return pageSize;
    }

    /**
     * This method returns the size of the file in bytes.
     *
     * @return the size of the file in bytes
     * @throws IOException if there is an I/O problem during the operation
     */
    public long length() throws IOException {
        long len = 0;
        File file = new File(this.fileName);
        if (file.exists()) {
            len = file.length();
        }

        return len;
    }

    private long getFilePosition(long blockid) {
        return (blockid - 1) * getBlockSize();
    }
}
