/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package killergame;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;

/**
 *
 * @author dam2a21
 */
public class MyBufferedImage extends BufferedImage {
     private byte[] data;
    private int[] dataInt;
    private Viewer v;

    public MyBufferedImage(BufferedImage bi, Viewer viewer) {
        super(bi.getColorModel(), bi.getRaster(),
                bi.getColorModel().isAlphaPremultiplied(), null);
        this.v = viewer;

        byte[] baDataRasterSource;

        baDataRasterSource = ((DataBufferByte) bi.getData().getDataBuffer()).getData();
        copyByteArray(baDataRasterSource);

        this.convertByteArrayToRaster();
    }

    public byte getData(int i) {
        return data[i];
    }

    public int getDataInt(int b) {
        return this.dataInt[b];
    }

    public int getDataLength() {

        byte[] baDataRasterSource;

        baDataRasterSource = ((DataBufferByte) this.getData().getDataBuffer()).getData();

        return baDataRasterSource.length;
    }

    public byte[] copyByteArray(byte[] baDataRasterSource) {

        this.data = new byte[baDataRasterSource.length];
        this.dataInt = new int[baDataRasterSource.length];

        for (int i = 0; i < baDataRasterSource.length; i++) {
            this.data[i] = baDataRasterSource[i];
            this.dataInt[i] = Byte.toUnsignedInt(this.data[i]);
        }

        return this.data;
    }

    public void resetImage(BufferedImage bi) {
        this.setData(bi.getData());

        byte[] baDataRasterSource;
        Raster ras = this.getData();
        if (ras.getDataBuffer().getDataType() != DataBuffer.TYPE_BYTE) {
            throw new IllegalArgumentException("RGB data type is not BYTE");
        }

        baDataRasterSource = ((DataBufferByte) ras.getDataBuffer()).getData();

        this.copyByteArray(baDataRasterSource);
    }

    private void convertIntegerArrayToByteArray() {
        for (int i = 0; i < this.dataInt.length; i++) {
            this.data[i] = (byte) dataInt[i];
        }
    }

    private void convertByteArrayToRaster() {
        this.setData(Raster.createRaster(this.getSampleModel(),
                new DataBufferByte(this.data, this.data.length), new Point()));
    }

    private int obtenirPosicioPixel(int row, int col) {
        return (row) * this.getWidth() * 3 + (col) * 3;
    }
/*
    public void drawRect(int colInici, int rowInici, int width, int height, Color color) {
        int pos;
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                pos = obtenirPosicioPixel(row+rowInici, col+colInici);
            this.dataInt[pos]=0;
            }
        }

        this.convertIntegerArrayToByteArray();
        this.convertByteArrayToRaster();

    }
    
    public void drawRectwhite(int colInici, int rowInici, int width, int height, Color color) {
        int pos;
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                pos = obtenirPosicioPixel(rowInici, colInici);
                this.dataInt[pos]=255;
            }
        }

        this.convertIntegerArrayToByteArray();
        this.convertByteArrayToRaster();

    }*/

    public int[] getDataInt() {
        return this.dataInt;
    }
    
}
