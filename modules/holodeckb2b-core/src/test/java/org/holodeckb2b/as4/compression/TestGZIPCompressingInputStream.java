/**
 * Copyright (C) 2014 The Holodeck B2B Team, Sander Fieten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.holodeckb2b.as4.compression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Sander Fieten <sander at holodeck-b2b.org>
 */
public class TestGZIPCompressingInputStream {
    private GZIPCompressingInputStream sut;
    private File compressedFile;
    private File decompressedFile;
    private File uncompressedFile;
    private final int BUFFER_SIZE = 512;


    @BeforeClass
    public void beforeClass() {

        /** Melis
         *  Give Longer and more explanatory variable names that one should
         *  not be roll up to know which variable mentioned below.
         */
        compressedFile = new File(this.getClass().getClassLoader().getResource("compression/").getPath() + "compressed.gz");
        decompressedFile = new File(this.getClass().getClassLoader().getResource("compression/").getPath() + "decompressed.xml");
        uncompressedFile = new File(this.getClass().getClassLoader().getResource("compression/uncompressed.xml").getPath());

    }




    /** Melis:
     * use when_what_expected naming convention to give more accurate idea about what is this test all about.
     * */
    @Test
    public void uncompressedFile_CompressFile_SeeCompressedFile() throws IOException {
        /**
         * Melis
         * In Unit Testing you should not use try-catch. Just let exception throws so you can understand what is wrong when testing, especially in automated testings.
         */
        FileOutputStream fileOutputStream = null;
        try {
            sut = new GZIPCompressingInputStream(new FileInputStream(uncompressedFile));
            fileOutputStream = new FileOutputStream(compressedFile);

            byte[] buffer = new byte[BUFFER_SIZE];
            int r = 0;
            while ((r = sut.read(buffer, 0, BUFFER_SIZE)) > 0) {
                fileOutputStream.write(buffer, 0, r);
            }

            Assert.assertNotEquals(compressedFile.length(), 0);
        } finally {
            sut.close();
            fileOutputStream.close();
        }
    }


    @Test(dependsOnMethods = {"uncompressedFile_CompressFile_SeeCompressedFile"})
    public void compressedFileExist_UncompressFile_GetSameFile() throws IOException, NoSuchAlgorithmException {

        GZIPInputStream gzipInputStream = null;
        FileOutputStream fileOutputStream = null;
        /**
         * Melis
         * In Unit Testing you should not use try-catch. Just let exception throws so you can understand what is wrong when testing, especially in automated testings.
         */
        try {
            gzipInputStream = new GZIPInputStream(new FileInputStream(compressedFile));
            fileOutputStream = new FileOutputStream(decompressedFile);

            byte[] buffer = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = gzipInputStream.read(buffer, 0, BUFFER_SIZE)) > 0) {
                fileOutputStream.write(buffer, 0, read);
            }

            boolean haveFilesSameContent = FileUtils.contentEquals(uncompressedFile, decompressedFile);
            Assert.assertEquals(haveFilesSameContent, true);
        } finally {
            gzipInputStream.close();
            fileOutputStream.close();
        }

    }

    @AfterClass
    public void afterClass() {
        if (compressedFile.exists())
            compressedFile.delete();

        if (decompressedFile.exists())
            decompressedFile.delete();
    }
}
