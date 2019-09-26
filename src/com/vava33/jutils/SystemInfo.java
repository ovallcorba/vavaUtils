/**
 * 
 */
package com.vava33.jutils;

/**
 * @author ovallcorba
 *
 */
import java.io.File;

public class SystemInfo {

    private Runtime runtime = Runtime.getRuntime();

    public String Info() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.OsInfo()).append("\n");
        sb.append(this.MemInfo()).append("\n");
        sb.append(this.DiskInfo()).append("\n");
        return sb.toString();
    }

    public String OSname() {
        return System.getProperty("os.name");
    }

    public String OSversion() {
        return System.getProperty("os.version");
    }
    public String JAVA32_64() {
        return System.getProperty("sun.arch.data.model");    
    }
    
    public String OsArch() {
        return System.getProperty("os.arch");
    }

    public long totalMem() {
        return Runtime.getRuntime().totalMemory();
    }

    public long usedMem() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    public String MemInfo() {
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        
        return String.format(" Free: %s, Alloc: %s, Max: %s, Total Free: %s",
                freeMemory / 1024,
                allocatedMemory / 1024,
                maxMemory / 1024,
                (freeMemory + (maxMemory - allocatedMemory)) / 1024);
    }

    public String OsInfo() {
        return String.format(" OS: %s, Version: %s (%s), Available processors (cores): %s",
                this.OSname(),
                this.OSversion(),
                this.OsArch(),
                runtime.availableProcessors());
    }

    public String DiskInfo() {
        /* Get a list of all filesystem roots on this system */
        File[] roots = File.listRoots();
        StringBuilder sb = new StringBuilder();

        /* For each filesystem root, print some info */
        for (File root : roots) {
            sb.append(String.format(" File system root: %s, Total space (bytes): %s, Free space (bytes): %s, Usable space (bytes): %s",
                    root.getAbsolutePath(),
                    root.getTotalSpace(),
                    root.getFreeSpace(),
                    root.getUsableSpace()));
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public static void main(final String[] args) {
        SystemInfo si = new SystemInfo();
        System.out.println(si.Info());
        System.out.println(si.JAVA32_64());
    }
}